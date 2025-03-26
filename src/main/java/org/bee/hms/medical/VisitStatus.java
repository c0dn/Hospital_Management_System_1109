package org.bee.hms.medical;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bee.utils.JSONSerializable;

/**
 * Defines the status of a patient's visit.
 * <p>
 * A visit can have one of the following statuses:
 * </p>
 * <ul>
 *     <li>{@link #ADMITTED} - The patient has been admitted. </li>
 *     <li>{@link #IN_PROGRESS} - The patient's visit is currently in progress.</li>
 *     <li>{@link #DISCHARGED} - The patient has been discharged.</li>
 *     <li>{@link #CANCELLED} - The visit has been cancelled and will not proceed.</li>
 * </ul>
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum VisitStatus implements JSONSerializable {
    /**
     * The patient has been admitted
     */
    ADMITTED,

    /**
     * The patient's visit is currently in progress.
     */
    IN_PROGRESS,

    /**
     * The patient has been discharged
     */
    DISCHARGED,

    /**
     * The visit has been cancelled and will not proceed.
     */
    CANCELLED,

    /**
     * The visit is planned for a future date/time.
     */
    SCHEDULED,

    /**
     * The visit has been completed.
     */
    COMPLETED;

    /**
     * Creates a VisitStatus from a string value.
     *
     * @param value The string value to convert to a VisitStatus enum.
     * @return The corresponding VisitStatus enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static VisitStatus fromString(String value) {
        try {
            return VisitStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown VisitStatus: " + value);
        }
    }
}
