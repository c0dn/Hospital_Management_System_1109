package policy;

import claims.InsuranceClaim;

public interface Coverage {
    boolean isItemCovered(ClaimableItem item, boolean isInpatient);
    double calculatePayout(InsuranceClaim claim);
    CoverageLimit getLimits();
}