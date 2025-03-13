package org.bee.policy;

/**
 * Defines the status of an insurance policy.
 * <p>
 *     An insurance policy can have one of the following statuses:
 * </p>
 * <ul>
 *     <li>{@link #ACTIVE} - The insurance policy is currently active and in effect.</li>
 *     <li>{@link #EXPIRED} - The insurance policy has expired and is no longer valid.</li>
 *     <li>{@link #CANCELLED} - The insurance policy has been canceled before its expiration date.</li>
 *     <li>{@link #PENDING} - The insurance policy has been cancelled and submitted but not processed.</li>
 * </ul>
 */
public enum InsuranceStatus {
    /**
     * The insurance policy is currently active and in effect.
     */
    ACTIVE,

    /**
     * The insurance policy has expired and is no longer valid.
     */
    EXPIRED,

    /**
     * The insurance policy has been canceled before its expiration date.
     */
    CANCELLED,

    /**
     * The insurance policy has been cancelled and submitted but not processed.
     */
    PENDING
}
