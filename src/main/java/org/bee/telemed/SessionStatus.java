package org.bee.telemed;

/**
 * Enumerates the possible states of a session in the healthcare or service delivery context.
 * This enum is used to manage and track the current state of sessions.
 * consultations, or any interaction that has a distinct start and end.
 */
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
    COMPLETED
}
