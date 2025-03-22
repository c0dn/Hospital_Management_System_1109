package org.bee.hms.medical;

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
public enum VisitStatus {
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

    SCHEDULED,

    COMPLETED
}
