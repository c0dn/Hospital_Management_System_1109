package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Defines the marital status of a human.
 * <p>
 *     The marital status can be one of the following:
 * </p>
 * <ul>
 *     <li>{@link #SINGLE} - The individual is not married.</li>
 *     <li>{@link #MARRIED} - The individual is married.</li>
 *     <li>{@link #DIVORCED} - The individual is divorced.</li>
 *     <li>{@link #WIDOWED} - The individual has lost their spouse due to death.</li>
 * </ul>
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MaritalStatus {
    /** The individual is not married. */
    SINGLE,
    /** The individual is married. */
    MARRIED,
    /** The individual is divorced. */
    DIVORCED,
    /** The individual has lost their spouse due to death. */
    WIDOWED;

    /**
     * Creates a MaritalStatus from a string value.
     *
     * @param value The string value to convert to a MaritalStatus enum.
     * @return The corresponding MaritalStatus enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static MaritalStatus fromString(String value) {
        try {
            return MaritalStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown MaritalStatus: " + value);
        }
    }
}