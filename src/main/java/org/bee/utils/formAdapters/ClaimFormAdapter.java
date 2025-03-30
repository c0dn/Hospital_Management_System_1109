package org.bee.utils.formAdapters;

import org.bee.controllers.ClaimController;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.medical.Medication;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/**
 * Adapter for displaying claim form.
 * This adapter generates form fields for insurance claims and handles
 * the conversion between form data and claim objects.
 */
 public class ClaimFormAdapter implements IObjectFormAdapter<InsuranceClaim> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<FormField<?>> generateFields(InsuranceClaim claim) {
        List<FormField<?>> fields = new ArrayList<>();

        ClaimStatus initialStatus = (ClaimStatus) getFieldValue(claim, "claimStatus");
        String statusPrompt = "Enter Status (" +
                Arrays.stream(ClaimStatus.values())
                        .map(Enum::name)
                        .collect(Collectors.joining(", ")) + "):";
        Predicate<String> statusValidator = FormValidators.notEmpty();
        String statusError = "Status selection is required.";

        fields.add(createEnumField(
                "claimStatus",
                "Claim Status",
                statusPrompt,
                claim,
                ClaimStatus.class,
                statusValidator,
                statusError,
                true,
                initialStatus
        ));

        String initialComments = (String) getFieldValue(claim, "comments");
        String commentsPrompt = "Enter comments (min 3 chars):";
        Predicate<String> commentsValidator = FormValidators.combine(
                FormValidators.notEmpty(),
                FormValidators.minLength(3)
        );
        String commentsError = "Comments are required (minimum 3 characters).";

        fields.add(createTextField(
                "comments",
                "Comments",
                commentsPrompt,
                claim,
                commentsValidator,
                commentsError,
                true,
                initialComments
        ));

        // Get supporting documents from claim
        Map<LocalDateTime, String> initialSupportingDocuments = claim.getSupportingDocuments();
        fields.add(createMapField(
                "supportingDocuments",
                "Supporting Documents",
                "Enter date and description (format: 'yyyy-MM-dd HH.mm:Description'):",  // Changed delimiter to |
                claim,
                input -> {
                    if (input == null || input.trim().isEmpty()) {
                        return false;
                    }

                    // Split on first | instead of :
                    String[] parts = input.trim().split("\\|", 2);  // Escape | for regex
                    if (parts.length != 2) {
                        return false;
                    }

                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");
                        LocalDateTime.parse(parts[0].trim(), formatter);
                        return !parts[1].trim().isEmpty();  // Validate description exists
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                },
                "Invalid format. Use 'yyyy-MM-dd HH.mm:Description' where : separates date and description.",
                key -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");
                    return LocalDateTime.parse(key.trim(), formatter);
                },
                value -> value.trim(),
                false,
                initialSupportingDocuments
        ));

        return fields;
    }

    @Override
    public String getObjectTypeName() {
        return "InsuranceClaim";
    }

    @Override
    public InsuranceClaim applyUpdates(InsuranceClaim claim, Map<String, Object> formData) {
        IObjectFormAdapter.super.applyUpdates(claim, formData);
        return claim;
    }

    @Override
    public boolean saveObject(InsuranceClaim claim) {
        try {
            if (genericSaveObject(claim, ClaimController.class, "saveData")) {
                return true;
            } else {
                System.err.println("Failed to save claim using generic method for ID: " + claim.getClaimId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception during claim saving: " + e.getMessage());
            return false;
        }
    }
}