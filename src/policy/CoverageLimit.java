package policy;

import wards.WardClassType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CoverageLimit {
    private double annualLimit;
    private double lifetimeLimit;
    private Map<BenefitType, Double> benefitLimits;
    private Map<WardClassType, Double> wardLimits;
    private Map<AccidentType, Double> accidentSubLimits;

    private CoverageLimit(Builder builder) {
        this.annualLimit = builder.annualLimit;
        this.lifetimeLimit = builder.lifetimeLimit;
        this.benefitLimits = builder.benefitLimits;
        this.wardLimits = builder.wardLimits;
        this.accidentSubLimits = builder.accidentSubLimits;
    }


    public boolean hasAnnualLimit() {
        return annualLimit > 0;
    }

    public boolean hasLifetimeLimit() {
        return lifetimeLimit > 0;
    }


    public Optional<Double> getBenefitLimit(BenefitType type) {
        return Optional.ofNullable(benefitLimits.get(type));
    }

    public Optional<Double> getWardLimit(WardClassType wardClass) {
        return Optional.ofNullable(wardLimits.get(wardClass));
    }

    public Optional<Double> getAccidentLimit(AccidentType type) {
        return Optional.ofNullable(accidentSubLimits.get(type));
    }

    public boolean isWithinAnnualLimit(double amount) {
        return !hasAnnualLimit() || amount <= annualLimit;
    }

    public boolean isWithinLifetimeLimit(double amount) {
        return !hasLifetimeLimit() || amount <= lifetimeLimit;
    }

    public boolean isWithinBenefitLimit(BenefitType type, double amount) {
        return getBenefitLimit(type)
                .map(limit -> amount <= limit)
                .orElse(true);
    }

    public boolean isWithinWardLimit(WardClassType wardClass, double amount) {
        return getWardLimit(wardClass)
                .map(limit -> amount <= limit)
                .orElse(true);
    }

    public boolean isWithinAccidentLimit(AccidentType type, double amount) {
        return getAccidentLimit(type)
                .map(limit -> amount <= limit)
                .orElse(true);
    }



    public static class Builder {
        private double annualLimit;
        private double lifetimeLimit;
        private Map<BenefitType, Double> benefitLimits = new HashMap<>();
        private Map<WardClassType, Double> wardLimits = new HashMap<>();
        private Map<AccidentType, Double> accidentSubLimits = new HashMap<>();

        public Builder withAnnualLimit(double limit) {
            this.annualLimit = limit;
            return this;
        }

        public Builder withLifetimeLimit(double limit) {
            this.lifetimeLimit = limit;
            return this;
        }

        public Builder addAccidentLimit(AccidentType type, double limit) {
            this.accidentSubLimits.put(type, limit);
            return this;
        }

        public Builder addBenefitLimit(BenefitType type, double limit) {
            this.benefitLimits.put(type, limit);
            return this;
        }

        public Builder addWardLimit(WardClassType wardClass, double limit) {
            this.wardLimits.put(wardClass, limit);
            return this;
        }

        public CoverageLimit build() {
            return new CoverageLimit(this);
        }
    }
}
