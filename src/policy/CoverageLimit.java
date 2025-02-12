package policy;

import wards.WardClassType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CoverageLimit {
    private BigDecimal annualLimit;
    private BigDecimal lifetimeLimit;
    private Map<BenefitType, BigDecimal> benefitLimits;
    private Map<WardClassType, BigDecimal> wardLimits;
    private Map<AccidentType, BigDecimal> accidentSubLimits;

    private CoverageLimit(Builder builder) {
        this.annualLimit = builder.annualLimit;
        this.lifetimeLimit = builder.lifetimeLimit;
        this.benefitLimits = builder.benefitLimits;
        this.wardLimits = builder.wardLimits;
        this.accidentSubLimits = builder.accidentSubLimits;
    }

    public boolean hasAnnualLimit() {
        return annualLimit != null && annualLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasLifetimeLimit() {
        return lifetimeLimit != null && lifetimeLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    public Optional<BigDecimal> getBenefitLimit(BenefitType type) {
        return Optional.ofNullable(benefitLimits.get(type));
    }

    public Optional<BigDecimal> getWardLimit(WardClassType wardClass) {
        return Optional.ofNullable(wardLimits.get(wardClass));
    }

    public Optional<BigDecimal> getAccidentLimit(AccidentType type) {
        return Optional.ofNullable(accidentSubLimits.get(type));
    }

    public boolean isWithinAnnualLimit(BigDecimal amount) {
        return !hasAnnualLimit() || amount.compareTo(annualLimit) <= 0;
    }

    public boolean isWithinLifetimeLimit(BigDecimal amount) {
        return !hasLifetimeLimit() || amount.compareTo(lifetimeLimit) <= 0;
    }

    public boolean isWithinBenefitLimit(BenefitType type, BigDecimal amount) {
        return getBenefitLimit(type)
                .map(limit -> amount.compareTo(limit) <= 0)
                .orElse(true);
    }

    public boolean isWithinWardLimit(WardClassType wardClass, BigDecimal amount) {
        return getWardLimit(wardClass)
                .map(limit -> amount.compareTo(limit) <= 0)
                .orElse(true);
    }

    public boolean isWithinAccidentLimit(AccidentType type, BigDecimal amount) {
        return getAccidentLimit(type)
                .map(limit -> amount.compareTo(limit) <= 0)
                .orElse(true);
    }

    public static class Builder {
        private BigDecimal annualLimit;
        private BigDecimal lifetimeLimit;
        private Map<BenefitType, BigDecimal> benefitLimits = new HashMap<>();
        private Map<WardClassType, BigDecimal> wardLimits = new HashMap<>();
        private Map<AccidentType, BigDecimal> accidentSubLimits = new HashMap<>();

        public Builder withAnnualLimit(double limit) {
            this.annualLimit = BigDecimal.valueOf(limit);
            return this;
        }

        public Builder withLifetimeLimit(double limit) {
            this.lifetimeLimit = BigDecimal.valueOf(limit);
            return this;
        }

        public Builder addAccidentLimit(AccidentType type, double limit) {
            this.accidentSubLimits.put(type, BigDecimal.valueOf(limit));
            return this;
        }

        public Builder addBenefitLimit(BenefitType type, double limit) {
            this.benefitLimits.put(type, BigDecimal.valueOf(limit));
            return this;
        }

        public Builder addWardLimit(WardClassType wardClass, double limit) {
            this.wardLimits.put(wardClass, BigDecimal.valueOf(limit));
            return this;
        }

        public CoverageLimit build() {
            return new CoverageLimit(this);
        }
    }
}
