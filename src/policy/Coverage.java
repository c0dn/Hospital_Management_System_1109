package policy;

import claims.InsuranceClaim;

/**
 * Defines the coverage details of an insurance policy, determining what is covered and how claims are processed.
 * <p>
 *     This interface defines methods for checking if a claimable item is covered under the policy, calculating the payout
 *     for an insurance claim, and retrieving the coverage limits.
 * </p>
 */
public interface Coverage {

    /**
     * Determines if the given claimable item is covered under this coverage.
     * <p>
     *     This method evaluates whether a specific claimable item, such as a medical procedure or treatment, is covered
     *     under the policy. The decision is based on various factors, including the benefit type and whether the patient
     *     is an inpatient or outpatient.
     * </p>
     *
     * @param item The claimable item to check for coverage.
     * @param isInpatient A flag indicating whether the patient is an inpatient.
     * @return {@code true} if the item is covered, {@code false} otherwise.
     */
    boolean isItemCovered(ClaimableItem item, boolean isInpatient);

    /**
     * Calculates the payout amount for an insurance claim.
     * <p>
     *     This method computes the payout for a given insurance claim, based on the policy's coverage rules such as
     *     deductibles, coinsurance, and coverage limits.
     * </p>
     *
     * @param claim The insurance claim for which the payout is to be calculated.
     * @return The payout amount for the claim, represented as a {@code double}.
     */
    double calculatePayout(InsuranceClaim claim);

    /**
     * Retrieves the coverage limits associated with this insurance policy.
     * <p>
     *     Coverage limits define the maximum allowable payout for specific treatments, procedures, and conditions,
     *     such as annual, lifetime, or accident-specific limits.
     * </p>
     *
     * @return The {@link CoverageLimit} object that contains the limits of this coverage.
     */
    CoverageLimit getLimits();
}