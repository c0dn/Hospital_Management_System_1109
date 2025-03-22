package org.bee.hms.medical;

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
public enum ConsultationType {
    /**A consultation that occurs in response to an emergency situation.*/
    EMERGENCY,

    /**A standard consultation with a healthcare provider.*/
    REGULAR_CONSULTATION,

    /*** A consultation with a specialist in a specific field.*/
    SPECIALIZED_CONSULTATION,

    /*** A consultation that takes place after initial treatment or diagnosis.*/
    FOLLOW_UP,

    NEW_CONSULTATION,

    ROUTINE_CHECKUP
}
