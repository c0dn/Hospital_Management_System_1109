package policy;

/**
 * Represents the types of accidents covered under an accident insurance policy.
 */
public enum AccidentsType {
    /** Coverage in case of death due to accident. */
    DEATH,
    /** Coverage for permanent disability caused by an accident. */
    PERMANENT_DISABILITY,
    /** Coverage for partial disability caused by an accident. */
    PARTIAL_DISABILITY,
    /** Coverage for temporary disability resulting from an accident. */
    TEMPORARY_DISABILITY,
    /** Coverage for fractures due to an accident. */
    FRACTURE,
    /** Coverage for burns sustained in an accident. */
    BURNS,
    /**
     * Coverage for medical expenses related to accidents.
     * Includes fixed daily allowances based on hospital stay duration,
     * ambulance services, broken bone benefits, and medical evacuation or repatriation.
     */
    MEDICAL,
    // comes in the form of a fixed daily allowance based on the duration of the hospital stay,
    // ambulance services, broken bone benefit and/or medical evacuation and repatriation benefit.
}
