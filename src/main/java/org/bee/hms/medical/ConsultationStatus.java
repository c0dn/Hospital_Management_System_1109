package org.bee.hms.medical;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bee.utils.JSONSerializable;

/**
 * Enums for status types
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ConsultationStatus implements JSONSerializable {
    /** The consultation is scheduled but has not yet occurred. */
    SCHEDULED,

    /** The consultation is currently in progress. */
    IN_PROGRESS,

    /** The consultation has been cancelled. */
    CANCELLED,

    /** The consultation has been completed. */
    COMPLETED;

    /**
     * Creates a ConsultationStatus from a string value.
     *
     * @param value The string value to convert to a ConsultationStatus enum.
     * @return The corresponding ConsultationStatus enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static ConsultationStatus fromString(String value) {
        try {
            return ConsultationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ConsultationStatus: " + value);
        }
    }
}