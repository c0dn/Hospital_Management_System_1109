package policy;

/**
 * Defines the status of an insurance policy.
 */

public enum InsuranceStatus {
    /** The insurance policy is currently active and in effect. */
    ACTIVE,
    /** The insurance policy has expired and is no longer valid. */
    EXPIRED,
    /** The insurance policy has been canceled before its expiration date. */
    CANCELLED,
    /** The insurance policy has been cancelled and submitted but not processed. */
    PENDING
}
