package org.bee.utils.formAdapters;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Human;
import org.bee.hms.medical.Consultation;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ConsultationFormAdapter implements IObjectFormAdapter<Consultation> {

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final HumanController humanController = HumanController.getInstance();

    @Override
    public List<FormField<?>> generateFields(Consultation consultation) {

        List<FormField<?>> fields = new ArrayList<>();

        SystemUser systemUser = humanController.getLoggedInUser();
            if (systemUser instanceof Clerk clerk) {
                LocalDateTime followUpDate = (LocalDateTime)getFieldValue(consultation, "followUpDate");
                String currentFollowUp = followUpDate != null ?
                        followUpDate.format(DATETIME_FORMATTER) : "None";

                fields.add(new FormField<>(
                        "followUpDate",
                        "Enter follow-up date and time (yyyy-MM-dd HH:mm) [Current: " + currentFollowUp + "]:",
                        input -> {
                            if (input.trim().isEmpty()) return true;
                            try {
                                LocalDateTime.parse(input, DATETIME_FORMATTER);
                                return true;
                            } catch (DateTimeParseException e) {
                                return false;
                            }
                        },
                        "Invalid date format. Please use yyyy-MM-dd HH:mm format.",
                        input -> input.trim().isEmpty() ? null :
                                LocalDateTime.parse(input, DATETIME_FORMATTER)
                ));
            } else if (systemUser instanceof Doctor doctor) {
                fields.add(createTextField(
                        "diagnosticCodes",
                        "Enter diagnostic code",
                        consultation,
                        FormValidators.notEmpty(),
                        "Diagnostic code cannot be empty."
                ));

                fields.add(createTextField(
                        "procedureCodes",
                        "Enter procedure code",
                        consultation,
                        FormValidators.notEmpty(),
                        "Procedure code cannot be empty."
                ));

                fields.add(createTextField(
                        "prescriptions",
                        "Enter prescription",
                        consultation,
                        input -> true,
                        "Prescription cannot be empty."
                ));

                fields.add(createTextField(
                        "notes",
                        "Enter notes",
                        consultation,
                        input -> true,
                        ""
                ));

                fields.add(createTextField(
                        "diagnosis",
                        "Enter diagnosis",
                        consultation,
                        input -> true,
                        "Diagnosis cannot be empty."
                ));

                fields.add(createTextField(
                        "instructions",
                        "Enter instructions",
                        consultation,
                        input -> true,
                        ""
                ));

                fields.add(createTextField(
                        "treatments",
                        "Enter treatments" +
                                "",
                        consultation,
                        input -> true,
                        "Treatment cannot be empty."
                ));

                fields.add(createTextField(
                        "labTests",
                        "Enter lab tests" +
                                "",
                        consultation,
                        input -> true,
                        ""
                ));
            }

        // Add more fields as needed...

        return fields;
    }

    @Override
    public boolean saveObject(Consultation consultation) {
        try {
            if (genericSaveObject(consultation, ConsultationController.class, "saveData")) {
                return true;
            }

            ConsultationController controller = ConsultationController.getInstance();

            Consultation existingConsultation = controller.getAllOutpatientCases().stream()
                    .filter(c -> c.getConsultationId().equals(consultation.getConsultationId()))
                    .findFirst()
                    .orElse(null);

            if (existingConsultation != null) {
                copyNonNullFields(consultation, existingConsultation);

                controller.saveData();
                return true;
            } else {
                System.err.println("Consultation not found in the system");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving consultation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getObjectTypeName() {
        return "Consultation";
    }
}