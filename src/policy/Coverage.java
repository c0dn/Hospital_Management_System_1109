package policy;

import java.math.BigDecimal;

public interface Coverage {
    boolean isItemCovered(ClaimableItem item, boolean isInpatient);
    BigDecimal calculateAccidentPayout(AccidentType accidentType);
    BigDecimal getDeductibleAmount();
    BigDecimal calculateCoinsurance(BigDecimal claimAmount);
    CoverageLimit getLimits();
}