package org.bee.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * Represents a basic coverage plan in the insurance system, including limits, deductibles,
 * coinsurance, death benefit amounts, and covered benefits.
 * <p>
 * This class provides methods to calculate coverage payouts, verify if a claimable item is covered,
 * and compute coinsurance for a given claim amount.
 * </p>
 */
public class BaseCoverage implements Coverage {

    /** The limits associated with the coverage. */
    protected CoverageLimit limits;

    /** The deductible amount for the coverage. */
    protected BigDecimal deductible;

    /** The coinsurance percentage for the coverage. */
    protected BigDecimal coinsurance;

    /** The death benefit amount provided by the coverage. */
    protected BigDecimal deathBenefitAmount;

    /** The set of covered benefits for this coverage plan. */
    protected Set<BenefitType> coveredBenefits;

    /** The criteria for exclusions from the coverage. */
    protected ExclusionCriteria exclusions;

    /**
     * Private constructor for initializing a {@link BaseCoverage} object using a builder.
     *
     * @param builder The builder containing the configuration for the coverage.
     */
    protected BaseCoverage(Builder builder) {
        this.limits = builder.limits;
        this.deductible = builder.deductible;
        this.coinsurance = builder.coinsurance;
        this.deathBenefitAmount = builder.deathBenefitAmount;
        this.coveredBenefits = builder.coveredBenefits;
        this.exclusions = builder.exclusions;
    }

    /**
     * Builder class for constructing a {@link BaseCoverage} object.
     * <p>
     * The builder pattern is used to ensure that all required fields are set before creating an instance of
     * {@link BaseCoverage}.
     * </p>
     * <p>
     * This builder ensures that all fields necessary for creating a valid coverage plan are configured,
     * and it validates the required fields before constructing the {@link BaseCoverage} instance.
     * </p>
     */
    public static class Builder {

        /**
         * The coverage limits for this coverage plan.
         * <p>
         * This defines the maximum amount of coverage that can be provided by the plan
         * for various types of claims such as annual, lifetime, and accident-based limits.
         * </p>
         */
        private CoverageLimit limits;

        /**
         * The deductible amount for this coverage plan.
         * <p>
         * This is the amount that the policyholder must pay out-of-pocket before the insurance coverage kicks in.
         * Defaults to {@code 0.00}.
         * </p>
         */
        private BigDecimal deductible = BigDecimal.ZERO;

        /**
         * The coinsurance percentage for this coverage plan.
         * <p>
         * This represents the percentage of the claim amount that the policyholder must pay after the deductible is met.
         * Defaults to {@code 0.00}.
         * </p>
         */
        private BigDecimal coinsurance = BigDecimal.ZERO;

        /**
         * The death benefit amount for this coverage plan.
         * <p>
         * This defines the amount paid out to beneficiaries in the event of the policyholder's death.
         * Defaults to {@code 0.00}.
         * </p>
         */
        private BigDecimal deathBenefitAmount = BigDecimal.ZERO;

        /**
         * The set of benefits covered by this coverage plan.
         * <p>
         * This defines the types of benefits that are included in the coverage, such as hospitalization, surgeries, etc.
         * </p>
         */
        private Set<BenefitType> coveredBenefits;

        /**
         * The exclusion criteria for this coverage plan.
         * <p>
         * This defines conditions or items that are excluded from the coverage, such as specific diagnoses, procedures,
         * or accident types.
         * </p>
         */
        private ExclusionCriteria exclusions;

        /**
         * Sets the coverage limits for the plan.
         *
         * @param limits The coverage limits.
         * @return The builder instance.
         */
        public Builder withLimits(CoverageLimit limits) {
            this.limits = limits;
            return this;
        }

        /**
         * Sets the deductible amount for the coverage plan.
         *
         * @param deductible The deductible amount.
         * @return The builder instance.
         */
        public Builder withDeductible(BigDecimal deductible) {
            this.deductible = deductible;
            return this;
        }

        /**
         * Sets the coinsurance percentage for the coverage plan.
         *
         * @param coinsurance The coinsurance percentage.
         * @return The builder instance.
         */
        public Builder withCoinsurance(BigDecimal coinsurance) {
            this.coinsurance = coinsurance;
            return this;
        }

        /**
         * Sets the death benefit amount for the coverage plan.
         *
         * @param deathBenefitAmount The death benefit amount.
         * @return The builder instance.
         */
        public Builder withDeathBenefitAmount(BigDecimal deathBenefitAmount) {
            this.deathBenefitAmount = deathBenefitAmount;
            return this;
        }

        /**
         * Sets the covered benefits for the coverage plan.
         *
         * @param coveredBenefits The set of covered benefits.
         * @return The builder instance.
         */
        public Builder withCoveredBenefits(Set<BenefitType> coveredBenefits) {
            this.coveredBenefits = coveredBenefits;
            return this;
        }

        /**
         * Sets the exclusion criteria for the coverage plan.
         *
         * @param exclusions The exclusion criteria.
         * @return The builder instance.
         */
        public Builder withExclusions(ExclusionCriteria exclusions) {
            this.exclusions = exclusions;
            return this;
        }

        /**
         * Builds and returns the {@link BaseCoverage} instance.
         *
         * @return A new {@link BaseCoverage} object.
         * @throws IllegalStateException if required fields are not set.
         */
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

    /**
     * Determines whether the specified claimable item is covered by this coverage plan.
     * <p>
     * This method checks if the item type is included in the covered benefits and if it is not excluded.
     * </p>
     *
     * @param item The claimable item to check.
     * @param isInpatient Indicates whether the item is for an inpatient.
     * @return {@code true} if the item is covered; {@code false} otherwise.
     */
    @Override
    public boolean isItemCovered(ClaimableItem item, boolean isInpatient) {
        return coveredBenefits.contains(item.resolveBenefitType(isInpatient)) &&
                !isExcluded(item, isInpatient);
    }

    /**
     * Checks if the coverage plan includes an accident type.
     *
     * @param type The type of accident to check.
     * @return {@code true} if the accident type is covered; {@code false} otherwise.
     */
    private boolean coversAccidentType(AccidentType type) {
        return !exclusions.isExcludedAccident(type);
    }

    /**
     * Retrieves the coverage limit for a specific accident type.
     *
     * @param type The accident type.
     * @return The coverage limit for the given accident type, or {@code 0.00} if no limit is set.
     */
    public BigDecimal getAccidentCoverageLimit(AccidentType type) {
        return limits.getAccidentLimit(type).orElse(BigDecimal.ZERO);
    }

    /**
     * Determines if a claimable item is excluded based on the coverage exclusions.
     *
     * @param item The claimable item.
     * @param isInpatient Indicates whether the item is for an inpatient.
     * @return {@code true} if the item is excluded; {@code false} otherwise.
     */
    protected boolean isExcluded(ClaimableItem item, boolean isInpatient) {
        return exclusions.applies(item, isInpatient);
    }

    /**
     * Calculates the payout for an accident based on the coverage limits and accident type.
     *
     * @param accidentType The type of accident.
     * @return The payout amount for the given accident type.
     */
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

    /**
     * Gets the coverage limits for this plan.
     *
     * @return The coverage limits.
     */
    @Override
    public CoverageLimit getLimits() {
        return limits;
    }

    /**
     * Gets the deductible amount for this coverage plan.
     *
     * @return The deductible amount.
     */
    @Override
    public BigDecimal getDeductibleAmount() {
        return deductible;
    }

    /**
     * Calculates the coinsurance amount for a given claim amount.
     *
     * @param claimAmount The claim amount.
     * @return The coinsurance amount, rounded to two decimal places.
     */
    @Override
    public BigDecimal calculateCoinsurance(BigDecimal claimAmount) {
        return claimAmount.multiply(coinsurance).setScale(2, RoundingMode.HALF_UP);
    }
}
