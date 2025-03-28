package org.bee.utils.formAdapters;

import org.bee.controllers.ClaimController;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.forms.IObjectFormAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClaimFormAdapter implements IObjectFormAdapter<InsuranceClaim> {
    @Override
    public List<FormField<?>> generateFields(InsuranceClaim claim) {
        List<FormField<?>> fields = new ArrayList<>();

        fields.add(createEnumField(
                "claimStatus",
                "Select Status\n(DRAFT, SUBMITTED, IN_REVIEW, PENDING_INFORMATION, APPROVED\nPARTIALLY_APPROVED, DENIED, APPEALED, PAID, CANCELLED, EXPIRED)",
                claim,
                ClaimStatus.class,
                FormValidators.notEmpty(),
                "Status selection is required"
        ));

        fields.add(createTextField(
                "comments",
                "Enter Comments",
                claim,
                FormValidators.combine(
                        FormValidators.notEmpty(),
                        FormValidators.minLength(3)
                ),
                "Comments must be at least 3 characters long."
        ));

        return fields;
    }

    @Override
    public String getObjectTypeName() {
        return "Insurance Claim";
    }

    @Override
    public boolean saveObject(InsuranceClaim claim) {
        try {
            ClaimController controller = ClaimController.getInstance();

            if (genericSaveObject(claim, ClaimController.class, "saveData")) {
                return true;
            }

            InsuranceClaim existingClaim = controller.getAllClaims().stream()
                    .filter(i -> i.getClaimId().equals(claim.getClaimId()))
                    .findFirst()
                    .orElse(null);

            if (existingClaim != null) {
                copyNonNullFields(claim, existingClaim);

                controller.saveData();
                return true;
            } else {
                System.err.println("Claim not found in the system");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving claim: " + e.getMessage());
            return false;
        }
    }

    @Override
    public InsuranceClaim applyUpdates(InsuranceClaim claim, Map<String, Object> formData) {
        IObjectFormAdapter.super.applyUpdates(claim, formData);

        return claim;
    }
}
