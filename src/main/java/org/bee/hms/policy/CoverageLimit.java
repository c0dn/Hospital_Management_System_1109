package org.bee.hms.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.wards.WardClassType;
import org.bee.utils.JSONSerializable;

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
public class CoverageLimit implements JSONSerializable {

    /**
     * The maximum claimable amount per policy year
     */
    private final BigDecimal annualLimit;

    /**
     * The maximum claimable amount over the policy lifetime
     */
    private final BigDecimal lifetimeLimit;

    /**
     * Type-specific benefit limits mapped by {@link BenefitType}
     */
    private final Map<BenefitType, BigDecimal> benefitLimits;

    /**
     * Ward class-specific limits mapped by {@link WardClassType}
     */
    private final Map<WardClassType, BigDecimal> wardLimits;

    /**
     * Accident-type sublimits mapped by {@link AccidentType}
     */
    private final Map<AccidentType, BigDecimal> accidentSubLimits;

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
     * JSON deserialization factory method for creating CoverageLimit instances
     * @param annualLimit The yearly coverage limit (optional)
     * @param lifetimeLimit The lifetime coverage limit (optional)
     * @param benefitLimits Benefit-type specific limits (optional)
     * @param wardLimits Ward-class specific limits (optional)
     * @param accidentSubLimits Accident-type sublimits (optional)
     * @return New CoverageLimit instance configured with provided values
     */
    @JsonCreator
    public static CoverageLimit create(
            @JsonProperty("annualLimit") BigDecimal annualLimit,
            @JsonProperty("lifetimeLimit") BigDecimal lifetimeLimit,
            @JsonProperty("benefitLimits") Map<BenefitType, BigDecimal> benefitLimits,
            @JsonProperty("wardLimits") Map<WardClassType, BigDecimal> wardLimits,
            @JsonProperty("accidentSubLimits") Map<AccidentType, BigDecimal> accidentSubLimits
    ) {
        Builder builder = new Builder();

        if (annualLimit != null) {
            builder.withAnnualLimit(annualLimit);
        }

        if (lifetimeLimit != null) {
            builder.withLifetimeLimit(lifetimeLimit);
        }

        if (benefitLimits != null) {
            benefitLimits.forEach(builder::addBenefitLimit);
        }

        if (wardLimits != null) {
            wardLimits.forEach(builder::addWardLimit);
        }

        if (accidentSubLimits != null) {
            accidentSubLimits.forEach(builder::addAccidentLimit);
        }

        return builder.build();
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
     * @param type   The benefit type to check the limit for.
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
     * @param amount    The amount to check against the ward class limit.
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
     * @param type   The accident type to check the limit for.
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
     * @return The annual coverage limit, or BigDecimal.ZERO if not set
     */
    public BigDecimal getAnnualLimit() {
        return annualLimit == null ? BigDecimal.ZERO : annualLimit;
    }

    /**
     * Returns the lifetime coverage limit.
     *
     * @return The lifetime coverage limit, or BigDecimal.ZERO if not set
     */
    public BigDecimal getLifetimeLimit() {
        return lifetimeLimit == null ? BigDecimal.ZERO : lifetimeLimit;
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
        private final Map<BenefitType, BigDecimal> benefitLimits = new HashMap<>();
        private final Map<WardClassType, BigDecimal> wardLimits = new HashMap<>();
        private final Map<AccidentType, BigDecimal> accidentSubLimits = new HashMap<>();

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
         * @param type  The accident type to set the limit for.
         * @param limit The coverage limit for the accident type.
         * @return The builder instance for method chaining.
         */
        public Builder addAccidentLimit(AccidentType type, BigDecimal limit) {
            this.accidentSubLimits.put(type, limit);
            return this;
        }

        /**
         * Adds a limit for a specific benefit type.
         *
         * @param type  The benefit type to set the limit for.
         * @param limit The coverage limit for the benefit type.
         * @return The builder instance for method chaining.
         */
        public Builder addBenefitLimit(BenefitType type, BigDecimal limit) {
            this.benefitLimits.put(type, limit);
            return this;
        }

        /**
         * Adds a limit for a specific ward class type.
         *
         * @param wardClass The ward class type to set the limit for.
         * @param limit     The coverage limit for the ward class type.
         * @return The builder instance for method chaining.
         */
        public Builder addWardLimit(WardClassType wardClass, BigDecimal limit) {
            this.wardLimits.put(wardClass, limit);
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
