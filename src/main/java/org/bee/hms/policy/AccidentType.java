package org.bee.hms.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bee.utils.JSONSerializable;

/**
 * Defines the different types of accidents covered under an insurance policy.
 * <br><br>An accident can be classified into the following types:
 * <ul>
 *     <li>{@link #DEATH} - Accident resulting in death.</li>
 *     <li>{@link #PERMANENT_DISABILITY} - Accident resulting in permanent disability.</li>
 *     <li>{@link #PARTIAL_DISABILITY} - Accident resulting in partial disability.</li>
 *     <li>{@link #TEMPORARY_DISABILITY} - Accident resulting in temporary disability.</li>
 *     <li>{@link #FRACTURE} - Accident resulting in a fracture.</li>
 *     <li>{@link #BURNS} - Accident resulting in burns.</li>
 *     <li>{@link #MEDICAL_EXPENSES} - Accident requiring medical expenses.</li>
 * </ul>
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AccidentType implements JSONSerializable {
    /**
     * Accident resulting in death.
     */
    DEATH,

    /**
     * Accident resulting in permanent disability.
     */
    PERMANENT_DISABILITY,

    /**
     * Accident resulting in partial disability.
     */
    PARTIAL_DISABILITY,

    /**
     * Accident resulting in temporary disability.
     */
    TEMPORARY_DISABILITY,

    /**
     * Accident resulting in a fracture.
     */
    FRACTURE,

    /**
     * Accident resulting in burns.
     */
    BURNS,

    /**
     * Accident requiring medical expenses.
     */
    MEDICAL_EXPENSES;

    /**
     * Creates an AccidentType from a string value.
     *
     * @param value The string value to convert to an AccidentType enum.
     * @return The corresponding AccidentType enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static AccidentType fromString(String value) {
        try {
            return AccidentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown AccidentType: " + value);
        }
    }
}