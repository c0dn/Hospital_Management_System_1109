package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Defines the residential status of a person.
 * <p>
 *     The residential status includes:
 * </p>
 * <ul>
 *     <li>{@link #CITIZEN} - A full citizen of the country.</li>
 *     <li>{@link #PERMANENT_RESIDENT} - A permanent resident of the country.</li>
 *     <li>{@link #WORK_PASS} - A person holding a work permit or employment pass.</li>
 *     <li>{@link #DEPENDENT_PASS} - A dependent of a resident or worker.</li>
 *     <li>{@link #VISITOR} - A temporary visitor.</li>
 * </ul>
 */

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ResidentialStatus {
    /** A full citizen of the country. */
    CITIZEN,
    /** A person with permanent residency status. */
    PERMANENT_RESIDENT,
    /** A person holding a work permit or employment pass. */
    WORK_PASS,
    /** A dependent of a resident or worker. */
    DEPENDENT_PASS,
    /** A temporary visitor. */
    VISITOR;

    /**
     * Creates a ResidentialStatus from a string value.
     *
     * @param value The string value to convert to a ResidentialStatus enum.
     * @return The corresponding ResidentialStatus enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static ResidentialStatus fromString(String value) {
        try {
            return ResidentialStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ResidentialStatus: " + value);
        }
    }
}
