package org.bee.telemed;

/**
 * Represents the different types of users within the system.
 * This enum is used to categorize users without needing {@code instanceof} checks,
 * facilitating faster and cleaner type validations across the application.
 * It simplifies the process of user role management and decision making in various parts of the system.
 */
public enum UserType {
    /**
     * Represents a patient within the healthcare system.
     * Used for users who are recipients of medical services.
     */
    PATIENT,

    /**
     * Represents a medical doctor or healthcare provider within the system.
     * Used for users who administer or oversee patient care.
     */
    DOCTOR,

    /**
     * Represents an error state or an undefined user type.
     * This type is used as a fallback or default when the user's type cannot be determined or if an error occurs during type assignment.
     */
    ERROR
}
