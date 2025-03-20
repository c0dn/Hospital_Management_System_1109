package org.bee.hms.telemed;

/**
 * Enumerates the possible statuses of an appointment within the system.
 * This enum is used throughout the application to manage and track the progress and state of appointments,
 * enabling systematic handling of appointment workflows and user interactions.
 */
public enum AppointmentStatus {
    /**
     * Indicates that the appointment has been accepted by the healthcare provider.
     * This status is used when an appointment has been reviewed and approved, allowing for further
     * planning and preparation for the appointment.
     */
    ACCEPTED,

    /**
     * Indicates that the appointment is currently pending review.
     * This status applies to newly created appointments awaiting approval or any form of confirmation
     * from either the healthcare provider or administrative staff.
     */
    PENDING,

    /**
     * Indicates that the appointment has been declined.
     * This status is used when an appointment cannot be accommodated due to various reasons such as
     * scheduling conflicts, resource limitations, or patient/provider preferences. It requires that
     * the appointment be rescheduled or cancelled.
     */
    DECLINED,
    /**
     * Indicates that the appointment has concluded.
     * This status is used when the appointment has been manually concluded by the doctor.
     */
    COMPLETED
}

