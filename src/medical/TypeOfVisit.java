package medical;

/**
 * Defines the type of visit for a patient.
 * <p>
 *     The type of visits:
 * </p>
 * <ul>
 *     <li>{@link #INPATIENT} - A medical treatment that requires hospital admission.</li>
 *     <li>{@link #OUTPATIENT} - A medical treatment that does not require hospital admission.</li>
 * </ul>
 */

public enum TypeOfVisit {
    /** A medical treatment that requires hospital admission. */
    INPATIENT,
    /** A medical treatment that does not require hospital admission. */
    OUTPATIENT
}
