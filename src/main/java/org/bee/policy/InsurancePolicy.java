package org.bee.policy;

import org.bee.humans.Patient;
import org.bee.insurance.InsuranceProvider;

import java.time.LocalDateTime;

/**
 * Represents an insurance policy in the system. This interface defines the core contract that all insurance policy types
 * must follow, including getting details about the policy number, policyholder, insurance provider, coverage, and the
 * policy's status (active, expired, cancelled, pending).
 */
public interface InsurancePolicy {

    /**
     * Returns the unique policy number for this insurance policy.
     *
     * @return The policy number as a string.
     */
    String getPolicyNumber();

    /**
     * Returns the policyholder associated with this insurance policy.
     *
     * @return The policyholder.
     */
    Patient getPolicyHolder();

    /**
     * Returns the name of the insurance policy.
     *
     * @return The policy name.
     */
    String getPolicyName();

    /**
     * Returns the insurance provider associated with this policy.
     *
     * @return The insurance provider.
     */
    InsuranceProvider getInsuranceProvider();

    /**
     * Returns the coverage details associated with this policy.
     *
     * @return The coverage details.
     */
    Coverage getCoverage();

    /**
     * Determines if the insurance policy is currently active.
     * A policy is active if its status is not expired and not cancelled.
     *
     * @return true if the policy is active, false otherwise.
     */
    boolean isActive();

    /**
     * Determines if the insurance policy has expired.
     *
     * @param now The current date and time.
     * @return true if the policy is expired, false otherwise.
     */
    boolean isExpired(LocalDateTime now);

    /**
     * Determines if the insurance policy has been cancelled.
     *
     * @return true if the policy is cancelled, false otherwise.
     */
    boolean isCancelled();

    /**
     * Determines if the insurance policy is in a pending state.
     *
     * @return true if the policy is pending, false otherwise.
     */
    boolean isPending();
}
