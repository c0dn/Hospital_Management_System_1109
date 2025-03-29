package org.bee.utils.formAdapters;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.medical.Consultation;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ConsultationFormAdapter implements IObjectFormAdapter<Consultation> {

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

            String initialTreatments = (String) getFieldValue(consultation, "treatments");
            fields.add(createTextField(
                    "treatments", "Treatments", "Enter treatments description:", consultation,
                    FormValidators.notEmpty(),
                    "Treatments description cannot be empty.",
                    true,
                    initialTreatments
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