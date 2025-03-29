package org.bee.utils.formAdapters;

import org.bee.controllers.HumanController;
import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.NokRelation;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientFormAdapter implements IObjectFormAdapter<Patient> {

    @Override
    public List<FormField<?>> generateFields(Patient patient) {
        List<FormField<?>> fields = new ArrayList<>();
        Contact contact = patient.getContact();

        String initialAddress = (String) getFieldValue(patient, "address");
        String initialOccupation = (String) getFieldValue(patient, "occupation");
        String initialCompanyName = (String) getFieldValue(patient, "companyName");
        String initialCompanyAddress = (String) getFieldValue(patient, "companyAddress");
        String initialNokName = (String) getFieldValue(patient, "nokName");
        String initialNokAddress = (String) getFieldValue(patient, "nokAddress");
        NokRelation initialNokRelation = (NokRelation) getFieldValue(patient, "nokRelation");

        String initialPersonalPhone = null;
        String initialHomePhone = null;
        String initialEmail = null;
        String initialCompanyPhone = null;
        if (contact != null) {
            initialPersonalPhone = contact.getPersonalPhone();
            initialHomePhone = contact.getHomePhone().orElse(null);
            initialEmail = contact.getEmail().orElse(null);
            initialCompanyPhone = contact.getCompanyPhone().orElse(null);
        } else {
            System.err.println("Warning: Patient contact details are missing.");
        }

        fields.add(createTextField(
                "address", "Address", "Enter new address:", patient,
                FormValidators.combine(FormValidators.notEmpty(), FormValidators.minLength(5)),
                "Address must be at least 5 characters long.", true, initialAddress
        ));
        fields.add(createTextField(
                "personalPhone", "Phone no.", "Enter 8-digit phone number:", patient,
                FormValidators.combine(FormValidators.notEmpty(), FormValidators.matches("^[89]\\d{7}$")),
                "Phone number must be 8 digits starting with 8 or 9.", true, initialPersonalPhone
        ));

        fields.add(createTextField(
                "nokName", "NOK Name", "Enter Next of Kin's full name:", patient,
                FormValidators.notEmpty(), "Next of Kin name cannot be empty.", true, initialNokName
        ));

        fields.add(createTextField(
                "nokAddress", "NOK Address", "Enter Next of Kin's address:", patient,
                FormValidators.combine(FormValidators.notEmpty(), FormValidators.minLength(5)),
                "NOK Address must be at least 5 characters long.", true, initialNokAddress
        ));

        String nokPrompt = "Enter NOK Relationship (" +
                 Arrays.stream(NokRelation.values())
                       .map(Enum::toString)
                       .collect(Collectors.joining(", ")) + "):";
        fields.add(createEnumField(
                "nokRelation", "NOK Relationship", nokPrompt, patient, NokRelation.class,
                FormValidators.notEmpty(),
                "NOK Relationship cannot be empty.",
                true,
                initialNokRelation
        ));

        fields.add(createTextField(
                "homePhone", "Home number", "Enter home phone number (optional):", patient,
                FormValidators.matches("^[6]\\d{7}$"),
                "Home number must be 8 digits starting with 6.", false, initialHomePhone
        ));

        fields.add(createTextField(
                "email", "Email", "Enter email address (optional):", patient,
                FormValidators.email(),
                "Please enter a valid email address.", false, initialEmail
        ));

        fields.add(createTextField(
                "companyPhone", "Company number", "Enter company phone number (optional):", patient,
                FormValidators.matches("^[6]\\d{7}$"),
                "Company number must be 8 digits starting with 6.", false, initialCompanyPhone
        ));

        fields.add(createTextField(
                "occupation", "Occupation", "Enter occupation (optional):", patient,
                input -> true,
                "",
                false,
                initialOccupation
        ));

        fields.add(createTextField(
                "companyName", "Company Name", "Enter company name (optional):", patient,
                input -> true,
                "",
                false,
                initialCompanyName
        ));

        fields.add(createTextField(
                "companyAddress", "Company Address", "Enter company address (optional):", patient,
                input -> {
                    if (input == null || input.trim().isEmpty()) return true;
                    return input.length() >= 5;
                },
                "Company Address must be at least 5 characters if provided.",
                false,
                initialCompanyAddress
        ));

        return fields;
    }

    @Override
    public Patient applyUpdates(Patient patient, Map<String, Object> formData) {
        formData.forEach((key, value) -> {
            if (!key.equals("personalPhone") && !key.equals("homePhone") && !key.equals("email") && !key.equals("companyPhone")) {
                setFieldValue(patient, key, value);
            }
        });

        Contact contact = patient.getContact();
        if (contact != null) {
            if (formData.containsKey("personalPhone")) {
                setFieldValue(contact, "personalPhone", formData.get("personalPhone"));
            }
                Object homePhoneValue = formData.get("homePhone");
                setFieldValue(contact, "homePhone", (homePhoneValue instanceof String && !((String)homePhoneValue).isEmpty()) ? homePhoneValue : null);
                Object emailValue = formData.get("email");
                setFieldValue(contact, "email", (emailValue instanceof String && !((String)emailValue).isEmpty()) ? emailValue : null);
                Object companyPhoneValue = formData.get("companyPhone");
                setFieldValue(contact, "companyPhone", (companyPhoneValue instanceof String && !((String)companyPhoneValue).isEmpty()) ? companyPhoneValue : null);
            }
        return patient;
    }

    @Override
    public boolean saveObject(Patient patient) {
        try {
            HumanController humanController = HumanController.getInstance();
            Patient existingPatient = humanController.getAllPatients().stream()
                    .filter(p -> p.getPatientId().equals(patient.getPatientId()))
                    .findFirst()
                    .orElse(null);

            if (existingPatient != null) {
                copyNonNullFields(patient, existingPatient);

                Object sourceContactObj = getFieldValue(patient, "contact");
                Object targetContactObj = getFieldValue(existingPatient, "contact");

                if (sourceContactObj instanceof Contact sourceContact && targetContactObj instanceof Contact targetContact) {
                    copyNonNullFields(sourceContact, targetContact);
                }

                humanController.saveData();
                return true;
            } else {
                System.err.println("Patient not found in the system: " + patient.getPatientId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving patient: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getObjectTypeName() {
        return "Patient";
    }
}