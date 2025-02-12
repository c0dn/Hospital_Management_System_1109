package policy;

import humans.Patient;

import java.time.LocalDateTime;

/**
 * Represents the core functionality of an insurance policy.
 * <p>
 * This interface defines the key methods that are required for any insurance policy,
 * including the policy number, the policyholder, coverage details, and the status of the policy
 * (active, expired, cancelled, or pending).
 * </p>
 */
public interface InsurancePolicy {

    /**
     * Retrieves the unique policy number of this insurance policy.
     * <p>
     * The policy number serves as an identifier for the insurance policy.
     * </p>
     *
     * @return The policy number as a {@link String}.
     */
    String getPolicyNumber();

    /**
     * Retrieves the policyholder associated with this insurance policy.
     * <p>
     * The policyholder is the individual or entity who owns the insurance policy.
     * </p>
     *
     * @return The {@link Patient} object representing the policyholder.
     */
    Patient getPolicyHolder();

    /**
     * Retrieves the coverage details associated with this insurance policy.
     * <p>
     * The coverage defines the conditions under which specific claimable items are covered,
     * how payouts are calculated for claims, and the coverage limits.
     * </p>
     *
     * @return The {@link Coverage} object associated with this insurance policy.
     */
    Coverage getCoverage();

    /**
     * Determines if this insurance policy is currently active.
     * <p>
     * A policy is considered active if it is not expired or cancelled.
     * </p>
     *
     * @return {@code true} if the policy is active; {@code false} otherwise.
     */
    boolean isActive();

    /**
     * Determines if the insurance policy is expired.
     * <p>
     * A policy is considered expired if the current date is later than the expiration date
     * of the policy.
     * </p>
     *
     * @param now The current date and time to check against the expiration date.
     * @return {@code true} if the policy is expired; {@code false} otherwise.
     */
    boolean isExpired(LocalDateTime now);

    /**
     * Determines if the insurance policy has been cancelled.
     * <p>
     * A policy is considered cancelled if its status is cancelled or if a cancellation date has been set.
     * </p>
     *
     * @return {@code true} if the policy is cancelled; {@code false} otherwise.
     */
    boolean isCancelled();

    /**
     * Determines if the insurance policy is pending.
     * <p>
     * A policy is considered pending if its status is pending approval or processing.
     * </p>
     *
     * @return {@code true} if the policy is pending; {@code false} otherwise.
     */
    boolean isPending();
}
