package policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class BaseCoverage implements Coverage {
    protected CoverageLimit limits;
    protected BigDecimal deductible;
    protected BigDecimal coinsurance;
    protected BigDecimal deathBenefitAmount;
    protected Set<BenefitType> coveredBenefits;
    protected ExclusionCriteria exclusions;

    protected BaseCoverage(Builder builder) {
        this.limits = builder.limits;
        this.deductible = builder.deductible;
        this.coinsurance = builder.coinsurance;
        this.deathBenefitAmount = builder.deathBenefitAmount;
        this.coveredBenefits = builder.coveredBenefits;
        this.exclusions = builder.exclusions;
    }

    public static class Builder {
        private CoverageLimit limits;
        private BigDecimal deductible = BigDecimal.ZERO;
        private BigDecimal coinsurance = BigDecimal.ZERO;
        private BigDecimal deathBenefitAmount = BigDecimal.ZERO;
        private Set<BenefitType> coveredBenefits;
        private ExclusionCriteria exclusions;

        public Builder withLimits(CoverageLimit limits) {
            this.limits = limits;
            return this;
        }

        public Builder withDeductible(BigDecimal deductible) {
            this.deductible = deductible;
            return this;
        }

        public Builder withCoinsurance(BigDecimal coinsurance) {
            this.coinsurance = coinsurance;
            return this;
        }

        public Builder withDeathBenefitAmount(BigDecimal deathBenefitAmount) {
            this.deathBenefitAmount = deathBenefitAmount;
            return this;
        }

        public Builder withCoveredBenefits(Set<BenefitType> coveredBenefits) {
            this.coveredBenefits = coveredBenefits;
            return this;
        }

        public Builder withExclusions(ExclusionCriteria exclusions) {
            this.exclusions = exclusions;
            return this;
        }



        public BaseCoverage build() {
            // Validate required fields
            if (limits == null) {
                throw new IllegalStateException("CoverageLimit must be set");
            }
            if (coveredBenefits == null || coveredBenefits.isEmpty()) {
                throw new IllegalStateException("CoveredBenefits must be set and non-empty");
            }
            if (exclusions == null) {
                throw new IllegalStateException("ExclusionCriteria must be set");
            }

            return new BaseCoverage(this);
        }
    }


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

