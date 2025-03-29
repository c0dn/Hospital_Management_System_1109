package org.bee.utils.formAdapters;

import org.bee.controllers.ClaimController;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClaimFormAdapter implements IObjectFormAdapter<InsuranceClaim> {
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

        return fields;
    }

    @Override
    public String getObjectTypeName() {
        return "Insurance Claim";
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