package policy;

import wards.WardClassType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the coverage limits for an insurance policy, including annual and lifetime limits,
 * as well as specific limits for benefits, ward classes, and accidents.
 * <p>
 * This class helps in determining whether a claim amount is within the defined limits based on
 * benefit types, ward types, and accident types. The coverage limits can be set and queried
 * using the builder pattern.
 * </p>
 */
public class CoverageLimit {

    private BigDecimal annualLimit;
    private BigDecimal lifetimeLimit;
    private Map<BenefitType, BigDecimal> benefitLimits;
    private Map<WardClassType, BigDecimal> wardLimits;
    private Map<AccidentType, BigDecimal> accidentSubLimits;

    /**
     * Private constructor that initializes a CoverageLimit using the provided builder.
     *
     * @param builder The builder used to construct this CoverageLimit object.
     */
    private CoverageLimit(Builder builder) {
        this.annualLimit = builder.annualLimit;
        this.lifetimeLimit = builder.lifetimeLimit;
        this.benefitLimits = builder.benefitLimits;
        this.wardLimits = builder.wardLimits;
        this.accidentSubLimits = builder.accidentSubLimits;
    }

    /**
     * Checks if the policy has an annual coverage limit set.
     *
     * @return true if the annual limit is set and greater than zero, false otherwise.
     */
    public boolean hasAnnualLimit() {
        return annualLimit != null && annualLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if the policy has a lifetime coverage limit set.
     *
     * @return true if the lifetime limit is set and greater than zero, false otherwise.
     */
    public boolean hasLifetimeLimit() {
        return lifetimeLimit != null && lifetimeLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Retrieves the benefit limit for a specific benefit type, if it exists.
     *
     * @param type The type of benefit to check the limit for.
     * @return An Optional containing the limit if it exists, or an empty Optional if not.
     */
    public Optional<BigDecimal> getBenefitLimit(BenefitType type) {
        return Optional.ofNullable(benefitLimits.get(type));
    }

    /**
     * Retrieves the ward class limit for a specific ward class type, if it exists.
     *
     * @param wardClass The ward class type to check the limit for.
     * @return An Optional containing the limit if it exists, or an empty Optional if not.
     */
    public Optional<BigDecimal> getWardLimit(WardClassType wardClass) {
        return Optional.ofNullable(wardLimits.get(wardClass));
    }

    /**
     * Retrieves the accident limit for a specific accident type, if it exists.
     *
     * @param type The accident type to check the limit for.
     * @return An Optional containing the limit if it exists, or an empty Optional if not.
     */
    public Optional<BigDecimal> getAccidentLimit(AccidentType type) {
        return Optional.ofNullable(accidentSubLimits.get(type));
    }

    /**
     * Checks if the specified amount is within the annual coverage limit.
     *
     * @param amount The amount to check against the annual limit.
     * @return true if the amount is within the annual limit, false if it exceeds it.
     */
    public boolean isWithinAnnualLimit(BigDecimal amount) {
        return !hasAnnualLimit() || amount.compareTo(annualLimit) <= 0;
    }

    /**
     * Checks if the specified amount is within the lifetime coverage limit.
     *
     * @param amount The amount to check against the lifetime limit.
     * @return true if the amount is within the lifetime limit, false if it exceeds it.
     */
    public boolean isWithinLifetimeLimit(BigDecimal amount) {
        return !hasLifetimeLimit() || amount.compareTo(lifetimeLimit) <= 0;
    }

    /**
     * Checks if the specified amount is within the benefit limit for a specific benefit type.
     *
     * @param type The benefit type to check the limit for.
     * @param amount The amount to check against the benefit limit.
     * @return true if the amount is within the benefit limit, false if it exceeds it.
     */
    public boolean isWithinBenefitLimit(BenefitType type, BigDecimal amount) {
        return getBenefitLimit(type)
                .map(limit -> amount.compareTo(limit) <= 0)
                .orElse(true);
    }

    /**
     * Checks if the specified amount is within the ward class limit for a specific ward class type.
     *
     * @param wardClass The ward class type to check the limit for.
     * @param amount The amount to check against the ward class limit.
     * @return true if the amount is within the ward limit, false if it exceeds it.
     */
    public boolean isWithinWardLimit(WardClassType wardClass, BigDecimal amount) {
        return getWardLimit(wardClass)
                .map(limit -> amount.compareTo(limit) <= 0)
                .orElse(true);
    }

    /**
     * Checks if the specified amount is within the accident limit for a specific accident type.
     *
     * @param type The accident type to check the limit for.
     * @param amount The amount to check against the accident limit.
     * @return true if the amount is within the accident limit, false if it exceeds it.
     */
    public boolean isWithinAccidentLimit(AccidentType type, BigDecimal amount) {
        return getAccidentLimit(type)
                .map(limit -> amount.compareTo(limit) <= 0)
                .orElse(true);
    }

    /**
     * Returns the annual coverage limit.
     *
     * @return The annual coverage limit.
     */
    public BigDecimal getAnnualLimit() {
        return annualLimit;
    }

    /**
     * Returns the lifetime coverage limit.
     *
     * @return The lifetime coverage limit.
     */
    public BigDecimal getLifetimeLimit() {
        return lifetimeLimit;
    }

    /**
     * Builder class for constructing {@link CoverageLimit} instances with specified limits.
     * <p>
     * The builder pattern allows for setting coverage limits for annual, lifetime,
     * benefits, ward classes, and accidents.
     * </p>
     */
    public static class Builder {
        private BigDecimal annualLimit;
        private BigDecimal lifetimeLimit;
        private Map<BenefitType, BigDecimal> benefitLimits = new HashMap<>();
        private Map<WardClassType, BigDecimal> wardLimits = new HashMap<>();
        private Map<AccidentType, BigDecimal> accidentSubLimits = new HashMap<>();

        /**
         * Sets the annual limit for the coverage.
         *
         * @param limit The annual coverage limit.
         * @return The builder instance for method chaining.
         */
        public Builder withAnnualLimit(BigDecimal limit) {
            this.annualLimit = limit;
            return this;
        }

        /**
         * Sets the lifetime limit for the coverage.
         *
         * @param limit The lifetime coverage limit.
         * @return The builder instance for method chaining.
         */
        public Builder withLifetimeLimit(BigDecimal limit) {
            this.lifetimeLimit = limit;
            return this;
        }

        /**
         * Adds a limit for a specific accident type.
         *
         * @param type The accident type to set the limit for.
         * @param limit The coverage limit for the accident type.
         * @return The builder instance for method chaining.
         */
        public Builder addAccidentLimit(AccidentType type, double limit) {
            this.accidentSubLimits.put(type, BigDecimal.valueOf(limit));
            return this;
        }

        /**
         * Adds a limit for a specific benefit type.
         *
         * @param type The benefit type to set the limit for.
         * @param limit The coverage limit for the benefit type.
         * @return The builder instance for method chaining.
         */
        public Builder addBenefitLimit(BenefitType type, double limit) {
            this.benefitLimits.put(type, BigDecimal.valueOf(limit));
            return this;
        }

        /**
         * Adds a limit for a specific ward class type.
         *
         * @param wardClass The ward class type to set the limit for.
         * @param limit The coverage limit for the ward class type.
         * @return The builder instance for method chaining.
         */
        public Builder addWardLimit(WardClassType wardClass, double limit) {
            this.wardLimits.put(wardClass, BigDecimal.valueOf(limit));
            return this;
        }

        /**
         * Builds and returns the {@link CoverageLimit} instance based on the specified limits.
         *
         * @return A new {@link CoverageLimit} object.
         */
        public CoverageLimit build() {
            return new CoverageLimit(this);
        }
    }
}