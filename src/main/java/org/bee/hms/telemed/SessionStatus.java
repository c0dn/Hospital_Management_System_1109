package org.bee.hms.telemed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Enumerates the possible states of a session in the healthcare or service delivery context.
 * This enum is used to manage and track the current state of sessions.
 * consultations, or any interaction that has a distinct start and end.
 */
/**
 * Enumerates the possible states of a session in the healthcare or service delivery context.
 * This enum is used to manage and track the current state of sessions.
 * consultations, or any interaction that has a distinct start and end.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SessionStatus {
    /**
     * Indicates that the session is currently in progress. This status is used to signify that an appointment
     * or consultation has started but has not yet concluded.
     */
    ONGOING,

    /**
     * Indicates that the session has been completed. This status is applied once the appointment or consultation
     * ends, signifying that all planned activities or objectives have been fulfilled.
     */
    COMPLETED;

    /**
     * Creates a SessionStatus from a string value.
     *
     * @param value The string value to convert to a SessionStatus enum.
     * @return The corresponding SessionStatus enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static SessionStatus fromString(String value) {
        try {
            return SessionStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown SessionStatus: " + value);
        }
    }
}