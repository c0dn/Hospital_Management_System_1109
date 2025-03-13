package org.bee.hms.policy;

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
public enum AccidentType {
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
    MEDICAL_EXPENSES
}
