package policy;

import wards.WardClassType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the coverage limits associated with an insurance policy.
 * <p>
 *     This class defines various coverage limits, such as annual, lifetime, benefit-specific, ward-specific, and
 *     accident-specific limits. It also provides methods to check whether a given amount falls within these limits.
 * </p>
 */
public class CoverageLimit {
    private double annualLimit;
    private double lifetimeLimit;
    private Map<BenefitType, Double> benefitLimits;
    private Map<WardClassType, Double> wardLimits;
    private Map<AccidentType, Double> accidentSubLimits;

    /**
     * Constructor for CoverageLimit.
     *
     * @param builder A builder instance that helps in constructing the CoverageLimit object.
     */
    private CoverageLimit(Builder builder) {
        this.annualLimit = builder.annualLimit;
        this.lifetimeLimit = builder.lifetimeLimit;
        this.benefitLimits = builder.benefitLimits;
        this.wardLimits = builder.wardLimits;
        this.accidentSubLimits = builder.accidentSubLimits;
    }

    /**
     * Checks if an annual limit is defined.
     *
     * @return {@code true} if an annual limit is defined, {@code false} otherwise.
     */
    public boolean hasAnnualLimit() {
        return annualLimit > 0;
    }

    /**
     * Checks if a lifetime limit is defined.
     *
     * @return {@code true} if a lifetime limit is defined, {@code false} otherwise.
     */
    public boolean hasLifetimeLimit() {
        return lifetimeLimit > 0;
    }

    /**
     * Retrieves the benefit limit for a specific benefit type.
     *
     * @param type The benefit type for which the limit is being requested.
     * @return An {@link Optional} containing the limit, or an empty {@code Optional} if no limit is set.
     */
    public Optional<Double> getBenefitLimit(BenefitType type) {
        return Optional.ofNullable(benefitLimits.get(type));
    }

    /**
     * Retrieves the ward limit for a specific ward class.
     *
     * @param wardClass The ward class for which the limit is being requested.
     * @return An {@link Optional} containing the limit, or an empty {@code Optional} if no limit is set.
     */
    public Optional<Double> getWardLimit(WardClassType wardClass) {
        return Optional.ofNullable(wardLimits.get(wardClass));
    }

    /**
     * Retrieves the accident limit for a specific accident type.
     *
     * @param type The accident type for which the limit is being requested.
     * @return An {@link Optional} containing the limit, or an empty {@code Optional} if no limit is set.
     */
    public Optional<Double> getAccidentLimit(AccidentType type) {
        return Optional.ofNullable(accidentSubLimits.get(type));
    }

    /**
     * Checks if a given amount is within the annual limit.
     *
     * @param amount The amount to be checked.
     * @return {@code true} if the amount is within the annual limit, {@code false} otherwise.
     */
    public boolean isWithinAnnualLimit(double amount) {
        return !hasAnnualLimit() || amount <= annualLimit;
    }

    /**
     * Checks if a given amount is within the lifetime limit.
     *
     * @param amount The amount to be checked.
     * @return {@code true} if the amount is within the lifetime limit, {@code false} otherwise.
     */
    public boolean isWithinLifetimeLimit(double amount) {
        return !hasLifetimeLimit() || amount <= lifetimeLimit;
    }

    /**
     * Checks if a given amount is within the limit for a specific benefit type.
     *
     * @param type The benefit type for which the limit is being checked.
     * @param amount The amount to be checked.
     * @return {@code true} if the amount is within the benefit limit, {@code false} otherwise.
     */
    public boolean isWithinBenefitLimit(BenefitType type, double amount) {
        return getBenefitLimit(type)
                .map(limit -> amount <= limit)
                .orElse(true);
    }

    /**
     * Checks if a given amount is within the limit for a specific ward class.
     *
     * @param wardClass The ward class for which the limit is being checked.
     * @param amount The amount to be checked.
     * @return {@code true} if the amount is within the ward limit, {@code false} otherwise.
     */
    public boolean isWithinWardLimit(WardClassType wardClass, double amount) {
        return getWardLimit(wardClass)
                .map(limit -> amount <= limit)
                .orElse(true);
    }

    /**
     * Checks if a given amount is within the limit for a specific accident type.
     *
     * @param type The accident type for which the limit is being checked.
     * @param amount The amount to be checked.
     * @return {@code true} if the amount is within the accident limit, {@code false} otherwise.
     */
    public boolean isWithinAccidentLimit(AccidentType type, double amount) {
        return getAccidentLimit(type)
                .map(limit -> amount <= limit)
                .orElse(true);
    }

    /**
     * Builder class for constructing a {@link CoverageLimit} instance.
     */
    public static class Builder {
        private double annualLimit;
        private double lifetimeLimit;
        private Map<BenefitType, Double> benefitLimits = new HashMap<>();
        private Map<WardClassType, Double> wardLimits = new HashMap<>();
        private Map<AccidentType, Double> accidentSubLimits = new HashMap<>();

        /**
         * Sets the annual limit for the coverage.
         *
         * @param limit The annual limit to set.
         * @return The builder instance.
         */
        public Builder withAnnualLimit(double limit) {
            this.annualLimit = limit;
            return this;
        }

        /**
         * Sets the lifetime limit for the coverage.
         *
         * @param limit The lifetime limit to set.
         * @return The builder instance.
         */
        public Builder withLifetimeLimit(double limit) {
            this.lifetimeLimit = limit;
            return this;
        }

        /**
         * Adds an accident limit for a specific accident type.
         *
         * @param type The accident type.
         * @param limit The limit for the specified accident type.
         * @return The builder instance.
         */
        public Builder addAccidentLimit(AccidentType type, double limit) {
            this.accidentSubLimits.put(type, limit);
            return this;
        }

        /**
         * Adds a benefit limit for a specific benefit type.
         *
         * @param type The benefit type.
         * @param limit The limit for the specified benefit type.
         * @return The builder instance.
         */
        public Builder addBenefitLimit(BenefitType type, double limit) {
            this.benefitLimits.put(type, limit);
            return this;
        }

        /**
         * Adds a ward limit for a specific ward class.
         *
         * @param wardClass The ward class.
         * @param limit The limit for the specified ward class.
         * @return The builder instance.
         */
        public Builder addWardLimit(WardClassType wardClass, double limit) {
            this.wardLimits.put(wardClass, limit);
            return this;
        }

        /**
         * Builds the {@link CoverageLimit} object with the defined settings.
         *
         * @return The {@link CoverageLimit} instance.
         */
        public CoverageLimit build() {
            return new CoverageLimit(this);
        }
    }
}
