package org.bee.utils.formAdapters;

import org.bee.controllers.HumanController;
import org.bee.hms.humans.Patient;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientFormAdapter implements IObjectFormAdapter<Patient> {

    @Override
    public List<FormField<?>> generateFields(Patient patient) {
        List<FormField<?>> fields = new ArrayList<>();

        fields.add(createTextField(
                "address",
                "Enter address",
                patient,
                FormValidators.combine(
                        FormValidators.notEmpty(),
                        FormValidators.minLength(5)
                ),
                "Address must be at least 5 characters long."
        ));

        fields.add(createDoubleField(
                "height",
                "Enter height in meters",
                patient,
                input -> {
                    try {
                        if (input.isEmpty()) return true;
                        double height = Double.parseDouble(input);
                        return height > 0 && height < 3;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                "Height must be a positive number less than 3 meters."
        ));

        fields.add(createDoubleField(
                "weight",
                "Enter weight in kg",
                patient,
                input -> {
                    try {
                        if (input.isEmpty()) return true;
                        double weight = Double.parseDouble(input);
                        return weight > 0 && weight < 500;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                "Weight must be a positive number less than 500 kg."
        ));

        Object contactValue = getFieldValue(patient, "contact");
        if (contactValue != null) {
            String currentPhone = (String)getFieldValue(contactValue, "personalPhone");

            fields.add(new FormField<>(
                    "contactPhone",
                    "Enter contact number [Current: " + currentPhone + "]:",
                    FormValidators.combine(
                            FormValidators.notEmpty(),
                            FormValidators.matches("^[0-9]{8}$")
                    ),
                    "Contact number must be 8 digits.",
                    FormValidators.stringParser()
            ));
        }

        return fields;
    }

    @Override
    public Patient applyUpdates(Patient patient, Map<String, Object> formData) {
        IObjectFormAdapter.super.applyUpdates(patient, formData);

        if (formData.containsKey("contactPhone")) {
            Object contact = getFieldValue(patient, "contact");
            if (contact != null) {
                setFieldValue(contact, "personalPhone", formData.get("contactPhone"));
            }
        }

        return patient;
    }

    @Override
    public boolean saveObject(Patient patient) {
        try {
            HumanController humanController = HumanController.getInstance();

            if (genericSaveObject(patient, HumanController.class, "saveData")) {
                return true;
            }

            Patient existingPatient = humanController.getAllPatients().stream()
                    .filter(p -> p.getPatientId().equals(patient.getPatientId()))
                    .findFirst()
                    .orElse(null);

            if (existingPatient != null) {
                copyNonNullFields(patient, existingPatient);

                Object sourceContact = getFieldValue(patient, "contact");
                Object targetContact = getFieldValue(existingPatient, "contact");

                if (sourceContact != null && targetContact != null) {
                    copyNonNullFields(sourceContact, targetContact);
                }

                humanController.saveData();
                return true;
            } else {
                System.err.println("Patient not found in the system");
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