package org.bee.utils.formAdapters;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.LabTest;
import org.bee.hms.medical.Medication;
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

//            String initialDiagnosticCode = (String) getFieldValue(consultation, "diagnosticCodes");
//            fields.add(createTextField(
//                    "diagnosticCodes", "Diagnostic Codes", "Enter diagnostic code:", consultation,
//                    FormValidators.notEmpty(),
//                    "Diagnostic code cannot be empty.",
//                    true,
//                    initialDiagnosticCode
//            ));


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

            @SuppressWarnings("unchecked")
            Map<Medication, Integer> initialPrescriptions = (Map<Medication, Integer>) getFieldValue(consultation, "prescriptions");
            Function<String, Medication> medicationParser = medication.createMedicationParser();
            fields.add(createHashMapField("prescriptions", "Prescriptions", "Enter prescription (drugCode:quantity)", consultation,
                    FormValidators.notEmpty(), "Prescriptions cannot be empty.", medicationParser, Integer::parseInt, ":",
                    ",", true, initialPrescriptions != null ? initialPrescriptions : new HashMap<>()));


//            String initialProcedureCode = (String) getFieldValue(consultation, "procedureCodes");
//            fields.add(createTextField(
//                    "procedureCodes", "Procedure Codes", "Enter procedure codes:", consultation,
//                    FormValidators.notEmpty(),
//                    "Procedure codes cannot be empty.",
//                    true,
//                    initialProcedureCode
//            ));

//            String initialTreatments = (String) getFieldValue(consultation, "treatments");
//            fields.add(createTextField(
//                    "treatments", "Treatments", "Enter treatments description:", consultation,
//                    FormValidators.notEmpty(),
//                    "Treatments description cannot be empty.",
//                    true,
//                    initialTreatments
//            ));
//
//            // Get initial lab tests (assuming consultation has getLabTests() method)
//            List<LabTest> initialLabTests = consultation.getLabTests();
//
//// Create parser for LabTest objects
//            FormField.FormInputParser<LabTest> labTestParser = input -> {
//                try {
//                    int id = Integer.parseInt(input.trim());
//                    LabTest test = LabTest.searchLabTestByID(id);
//                    if (test == null) {
//                        throw new IllegalArgumentException("No lab test found with ID: " + id);
//                    }
//                    return test;
//                } catch (NumberFormatException e) {
//                    throw new IllegalArgumentException("Please enter valid lab test IDs (numbers)");
//                }
//            };
//
//// Create the field
//            fields.add(createListField(
//                    "labTests",
//                    "Lab Tests",
//                    "Enter lab test IDs (comma separated):",
//                    consultation,
//                    FormValidators.notEmpty(),
//                    "At least one valid lab test ID is required.",
//                    labTestParser,
//                    false,  // or true if required
//                    initialLabTests != null ? initialLabTests : Collections.emptyList()
//            ));
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