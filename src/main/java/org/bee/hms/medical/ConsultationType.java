package org.bee.hms.medical;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Defines the type of consultation for an insurance claim.
 * <p>
 * A consultation can be one of the following types:
 * </p>
 * <ul>
 *     <li>{@link #EMERGENCY} - A consultation that occurs in response to an emergency situation.</li>
 *     <li>{@link #REGULAR_CONSULTATION} - A standard consultation with a healthcare provider.</li>
 *     <li>{@link #SPECIALIZED_CONSULTATION} - A consultation with a specialist in a specific field.</li>
 *     <li>{@link #FOLLOW_UP} - A consultation that takes place after initial treatment or diagnosis.</li>
 * </ul>
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ConsultationType {
    /**A consultation that occurs in response to an emergency situation.*/
    EMERGENCY,

    /**A standard consultation with a healthcare provider.*/
    REGULAR_CONSULTATION,

    /*** A consultation with a specialist in a specific field.*/
    SPECIALIZED_CONSULTATION,

    /*** A consultation that takes place after initial treatment or diagnosis.*/
    FOLLOW_UP,

    /**A new patient consultation.*/
    NEW_CONSULTATION,

    /**A routine periodic health examination.*/
    ROUTINE_CHECKUP;

    /**
     * Creates a ConsultationType from a string value.
     *
     * @param value The string value to convert to a ConsultationType enum.
     * @return The corresponding ConsultationType enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static ConsultationType fromString(String value) {
        try {
            return ConsultationType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ConsultationType: " + value);
        }
    }
}