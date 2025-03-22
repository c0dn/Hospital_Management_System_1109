package org.bee.hms.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

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
@JsonFormat(shape = JsonFormat.Shape.STRING)
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
    PENDING;

    /**
     * Creates an InsuranceStatus from a string value.
     *
     * @param value The string value to convert to an InsuranceStatus enum.
     * @return The corresponding InsuranceStatus enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static InsuranceStatus fromString(String value) {
        try {
            return InsuranceStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown InsuranceStatus: " + value);
        }
    }
}
