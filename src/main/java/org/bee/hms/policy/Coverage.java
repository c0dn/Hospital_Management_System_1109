package org.bee.hms.policy;

import java.math.BigDecimal;

/**
 * Represents an insurance coverage that defines the methods for checking coverage, calculating payouts,
 * and determining limits and costs associated with coverage.
 * <p>
 * Implementing classes should provide the logic for various coverage-related operations such as determining
 * if an item is covered, calculating the accident payout, computing the deductible and coinsurance, and managing
 * the coverage limits.
 * </p>
 */
public interface Coverage {

    /**
     * Checks if a given item is covered by this coverage.
     * <p>
     * This method determines if the provided claimable item is covered under the terms of this coverage,
     * based on whether the item is for inpatient or outpatient treatment.
     * </p>
     *
     * @param item The claimable item to check for coverage.
     * @param isInpatient A flag indicating whether the item relates to inpatient treatment.
     * @return true if the item is covered, false otherwise.
     */
    boolean isItemCovered(ClaimableItem item, boolean isInpatient);

    /**
     * Calculates the accident payout for a given accident type.
     * <p>
     * This method computes the payout based on the type of accident, taking into account the coverage limits,
     * deductibles, and other factors.
     * </p>
     *
     * @param accidentType The type of accident for which the payout is to be calculated.
     * @return The calculated payout amount for the accident type.
     */
    BigDecimal calculateAccidentPayout(AccidentType accidentType);

    /**
     * Returns the deductible amount for this coverage.
     * <p>
     * The deductible is the amount the policyholder must pay out of pocket before the coverage kicks in.
     * </p>
     *
     * @return The deductible amount for this coverage.
     */
    BigDecimal getDeductibleAmount();

    /**
     * Calculates the coinsurance for a given claim amount.
     * <p>
     * Co-insurance is the percentage of the claim amount that the policyholder is responsible for paying.
     * This method computes the coinsurance based on the claim amount and the specific coverage policy.
     * </p>
     *
     * @param claimAmount The amount for which coinsurance is to be calculated.
     * @return The coinsurance amount based on the claim amount.
     */
    BigDecimal calculateCoinsurance(BigDecimal claimAmount);

    /**
     * Returns the coverage limits for this coverage.
     * <p>
     * The coverage limits define the maximum amounts that can be paid for claims under this coverage.
     * This method retrieves those limits, which may include annual limits, lifetime limits, or limits for specific types of claims.
     * </p>
     *
     * @return The coverage limits associated with this coverage.
     */
    CoverageLimit getLimits();
}
