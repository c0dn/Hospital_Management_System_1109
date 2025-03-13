package org.bee.hms.humans;

/**
 * Defines the residential status of a person.
 * <p>
 *     The residential status includes:
 * </p>
 * <ul>
 *     <li>{@link #CITIZEN} - A full citizen of the country.</li>
 *     <li>{@link #PERMANENT_RESIDENT} - A permanent resident of the country.</li>
 *     <li>{@link #WORK_PASS} - A person holding a work permit or employment pass.</li>
 *     <li>{@link #DEPENDENT_PASS} - A dependent of a resident or worker.</li>
 *     <li>{@link #VISITOR} - A temporary visitor.</li>
 * </ul>
 */

public enum ResidentialStatus {

    /** A full citizen of the country. */
    CITIZEN,
    /** A person with permanent residency status. */
    PERMANENT_RESIDENT,
    /** A person holding a work permit or employment pass. */
    WORK_PASS,
    /** A dependent of a resident or worker. */
    DEPENDENT_PASS,
    /** A temporary visitor. */
    VISITOR
}
