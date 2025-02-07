package claims;

/**
 * Defines different types of insurance claims.\
 *
 * <p>
 *     A claim can have one of the following types:
 * </p>
 * <ul>
 *     <li>{@link #INPATIENT} - A claim for medical treatment that requires hospital admission.</li>
 *     <li>{@link #OUTPATIENT - A claim for medical treatment that does not require hospital admission.}</li>
 * </ul>
 */

public enum TypeOfClaim {
    /** A claim for medical treatment that requires hospital admission. */
    INPATIENT,
    /** A claim for medical treatment that does not require hospital admission. */
    OUTPATIENT
}
