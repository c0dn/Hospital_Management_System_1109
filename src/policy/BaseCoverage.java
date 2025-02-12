package policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public abstract class BaseCoverage implements Coverage {
    protected CoverageLimit limits;
    protected BigDecimal deductible;
    protected BigDecimal coinsurance;
    protected BigDecimal deathBenefitAmount;
    protected Set<BenefitType> coveredBenefits;
    protected ExclusionCriteria exclusions;

    @Override
    public boolean isItemCovered(ClaimableItem item, boolean isInpatient) {
        return coveredBenefits.contains(item.resolveBenefitType(isInpatient)) &&
                !isExcluded(item, isInpatient);
    }

    private boolean coversAccidentType(AccidentType type) {
        return !exclusions.isExcludedAccident(type);
    }

    public BigDecimal getAccidentCoverageLimit(AccidentType type) {
        return limits.getAccidentLimit(type).orElse(BigDecimal.ZERO);
    }

    protected boolean isExcluded(ClaimableItem item, boolean isInpatient) {
        return exclusions.applies(item, isInpatient);
    }

    @Override
    public BigDecimal calculateAccidentPayout(AccidentType accidentType) {
        if (!coversAccidentType(accidentType)) {
            return BigDecimal.ZERO;
        }

        BigDecimal coverageAmount = getAccidentCoverageLimit(accidentType);
        if (coverageAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (accidentType == AccidentType.DEATH) {
            return this.deathBenefitAmount;
        }

        return coverageAmount;
    }


    @Override
    public CoverageLimit getLimits() {
        return limits;
    }

    @Override
    public BigDecimal getDeductibleAmount() {
        return deductible;
    }

    @Override
    public BigDecimal calculateCoinsurance(BigDecimal claimAmount) {
        return claimAmount.multiply(coinsurance).setScale(2, RoundingMode.HALF_UP);
    }
}

