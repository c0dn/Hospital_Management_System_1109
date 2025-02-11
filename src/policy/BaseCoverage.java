package policy;

import java.math.BigDecimal;
import java.util.Set;

public abstract class BaseCoverage implements Coverage {
    protected CoverageLimit limits;
    protected BigDecimal deductible;
    protected BigDecimal coinsurance;
    protected Set<BenefitType> coveredBenefits;
    protected ExclusionCriteria exclusions;

    @Override
    public boolean isItemCovered(ClaimableItem item, boolean isInpatient) {
        return coveredBenefits.contains(item.resolveBenefitType(isInpatient)) &&
                !isExcluded(item, isInpatient);
    }

    public boolean coversAccidentType(AccidentType type) {
        return !exclusions.isExcludedAccident(type);
    }

    public double getAccidentCoverageLimit(AccidentType type) {
        return limits.getAccidentLimit(type).orElse(0.0);
    }

    protected boolean isExcluded(ClaimableItem item, boolean isInpatient) {
        return exclusions.applies(item, isInpatient);
    }

    @Override
    public CoverageLimit getLimits() {
        return limits;
    }
}

