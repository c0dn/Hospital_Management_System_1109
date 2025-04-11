package org.bee.utils.formAdapters;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.medical.*;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Adapter for displaying Consultation form.
 * This adapter generates form fields for consultation details and handles
 * the conversion between form data and consultation objects.
 */
public class ConsultationFormAdapter implements IObjectFormAdapter<Consultation> {

    private Medication medication;
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final HumanController humanController = HumanController.getInstance();

    @Override
    public List<FormField<?>> generateFields(Consultation consultation) {
        List<FormField<?>> fields = new ArrayList<>();
        SystemUser systemUser = humanController.getLoggedInUser();

        if (systemUser instanceof Clerk) {
            LocalDateTime initialFollowUpDate = (LocalDateTime) getFieldValue(consultation, "followUpDate");

            Predicate<String> followUpValidator = input -> {
                if (input == null || input.trim().isEmpty()) {
                    return true;
                }
                try {
                    LocalDateTime.parse(input.trim(), DATETIME_FORMATTER);
                    return true;
                } catch (DateTimeParseException e) {
                    return false;
                }
            };

            FormField.FormInputParser<LocalDateTime> dateTimeParser = input ->
                    (input == null || input.trim().isEmpty()) ? null : LocalDateTime.parse(input.trim(), DATETIME_FORMATTER);

            fields.add(createField(
                    "followUpDate",
                    "Follow-up Date",
                    "Enter follow-up date (yyyy-MM-dd HH:mm, optional):",
                    consultation,
                    followUpValidator,
                    "Invalid date format. Use yyyy-MM-dd HH:mm or leave empty.",
                    dateTimeParser,
                    false,
                    initialFollowUpDate
            ));

        } else if (systemUser instanceof Doctor) {

            String initialDiagnosis = (String) getFieldValue(consultation, "diagnosis");
            fields.add(createTextField(
                    "diagnosis", "Diagnosis", "Enter diagnosis:", consultation,
                    FormValidators.notEmpty(),
                    "Diagnosis cannot be empty.",
                    true,
                    initialDiagnosis
            ));

            String initialNotes = (String) getFieldValue(consultation, "notes");
            fields.add(createTextField(
                    "notes", "Notes", "Enter notes:", consultation,
                    FormValidators.notEmpty(),
                    "Notes cannot be empty.",
                    true,
                    initialNotes
            ));

            String initialInstructions = (String) getFieldValue(consultation, "instructions");
            fields.add(createTextField(
                    "instructions", "Instructions", "Enter patient instructions:", consultation,
                    FormValidators.notEmpty(),
                    "Instructions cannot be empty.",
                    true,
                    initialInstructions
            ));

            List<DiagnosticCode> initialDiagnosticCodes = consultation.getDiagnosticCodes();
            fields.add(createListField(
                    "diagnosticCodes",
                    "Diagnostic Codes",
                    "Manage diagnostic codes:",
                    consultation,
                    FormValidators.combine(
                            FormValidators.notEmpty(),
                            FormValidators.validDiagnosticCode()
                    ),
                    "Diagnostic code cannot be empty.",
                    input -> DiagnosticCode.createFromCode(input.trim().toUpperCase()),
                    true,
                    initialDiagnosticCodes
            ));

            // Get procedure codes from consultation
            List<ProcedureCode> initialProcedureCodes = consultation.getProcedureCodes();
            fields.add(createListField(
                    "procedureCodes",
                    "Procedure Codes",
                    "Enter procedure code (e.g., 31500):",
                    consultation,
                    input -> {
                        try {
                            if (input == null || input.trim().isEmpty()) {
                                return false;
                            }
                            ProcedureCode.createFromCode(input.trim().toUpperCase());
                            return true;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    },
                    "Invalid procedure code. Please enter a valid code.",
                    input -> ProcedureCode.createFromCode(input.trim().toUpperCase()),
                    false,
                    initialProcedureCodes
            ));

            // Get prescriptions from consultation
            Map<Medication, Integer> initialPrescriptions = consultation.getPrescriptions();
            fields.add(createMapField(
                    "prescriptions",
                    "Prescriptions",
                    "Enter medication code and quantity (e.g., AMOX:30):",
                    consultation,
                    input -> {
                        if (input == null || input.trim().isEmpty()) {
                            return false;
                        }

                        String[] parts = input.trim().split(":", 2);
                        if (parts.length != 2) {
                            return false;
                        }

                        try {
                            String medicationCode = parts[0].trim().toUpperCase();
                            String quantityStr = parts[1].trim();

                            // Check if medication is valid
                            Medication.createFromCode(medicationCode);

                            // Check if quantity is valid
                            try {
                                int quantity = Integer.parseInt(quantityStr);
                                return quantity > 0;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    },
                    "Invalid prescription format. Use 'CODE:QUANTITY' with a valid medication code and positive quantity.",
                    input -> {
                        String medicationCode = input.trim().toUpperCase();
                        return Medication.createFromCode(medicationCode);
                    },
                    input -> {
                        int quantity = Integer.parseInt(input.trim());
                        if (quantity <= 0) {
                            throw new IllegalArgumentException("Quantity must be greater than 0");
                        }
                        return quantity;
                    },
                    false,
                    initialPrescriptions
            ));

        }

        return fields;
    }

    @Override
    public String getObjectTypeName() {
        return "Consultation";
    }

    @Override
    public Consultation applyUpdates(Consultation consultation, Map<String, Object> formData) {
        IObjectFormAdapter.super.applyUpdates(consultation, formData);
        return consultation;
    }

    @Override
    public boolean saveObject(Consultation consultation) {
        try {
            if (genericSaveObject(consultation, ConsultationController.class, "saveData")) {
                return true;
            } else {
                System.err.println("Failed to save consultation using generic method for ID: " + consultation.getConsultationId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception during consultation saving: " + e.getMessage());
            return false;
        }
    }


}