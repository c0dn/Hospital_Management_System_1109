package policy;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Abstract class representing the coverage details of an insurance policy.
 * <p>
 *     This class defines the general rules for checking if a claimable item is covered,
 *     calculating the coverage limits for specific accident types, and handling exclusions.
 * </p>
 * <ul>
 *     <li>{@link #isItemCovered(ClaimableItem, boolean)} - Determines if an item is covered by this coverage.</li>
 *     <li>{@link #coversAccidentType(AccidentType)} - Determines if a given accident type is covered.</li>
 *     <li>{@link #getAccidentCoverageLimit(AccidentType)} - Retrieves the coverage limit for a given accident type.</li>
 *     <li>{@link #getLimits()} - Retrieves the coverage limits for this insurance coverage.</li>
 * </ul>
 */
public abstract class BaseCoverage implements Coverage {

    /** Coverage limits associated with this coverage. */
    protected CoverageLimit limits;

    /** Deductible amount for claims under this coverage. */
    protected BigDecimal deductible;

    /** Coinsurance percentage for claims under this coverage. */
    protected BigDecimal coinsurance;

    /** Set of benefits covered by this insurance coverage. */
    protected Set<BenefitType> coveredBenefits;

    /** Exclusion criteria that defines conditions under which items are excluded. */
    protected ExclusionCriteria exclusions;

    /**
     * Determines if the given claimable item is covered under this coverage.
     * This is based on whether the item is eligible based on the policy's criteria and if the patient
     * is inpatient or outpatient.
     *
     * @param item The claimable item to check for coverage.
     * @param isInpatient A flag indicating whether the patient is inpatient.
     * @return {@code true} if the item is covered, {@code false} otherwise.
     */
    @Override
    public boolean isItemCovered(ClaimableItem item, boolean isInpatient) {
        return coveredBenefits.contains(item.resolveBenefitType(isInpatient)) &&
                !isExcluded(item, isInpatient);
    }

    /**
     * Determines if the given accident type is covered by this insurance coverage.
     *
     * @param type The type of the accident to check.
     * @return {@code true} if the accident type is covered, {@code false} otherwise.
     */
    public boolean coversAccidentType(AccidentType type) {
        return !exclusions.isExcludedAccident(type);
    }

    /**
     * Retrieves the coverage limit for a given accident type.
     *
     * @param type The type of the accident.
     * @return The coverage limit for the specified accident type.
     */
    public double getAccidentCoverageLimit(AccidentType type) {
        return limits.getAccidentLimit(type).orElse(0.0);
    }

    /**
     * Checks whether the given claimable item is excluded from coverage based on the exclusion criteria.
     *
     * @param item The claimable item to check.
     * @param isInpatient A flag indicating whether the patient is inpatient.
     * @return {@code true} if the item is excluded, {@code false} otherwise.
     */
    protected boolean isExcluded(ClaimableItem item, boolean isInpatient) {
        return exclusions.applies(item, isInpatient);
    }

    /**
     * Retrieves the coverage limits associated with this insurance coverage.
     *
     * @return The {@link CoverageLimit} object containing the limits of this coverage.
     */
    @Override
    public CoverageLimit getLimits() {
        return limits;
    }
}

