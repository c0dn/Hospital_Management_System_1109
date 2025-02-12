package people;

/**
 * Defines the marital status of a human.
 * <p>
 *     The marital status can be one of the following:
 * </p>
 * <ul>
 *     <li>{@link #SINGLE} - The individual is not married.</li>
 *     <li>{@link #MARRIED} - The individual is married.</li>
 *     <li>{@link #DIVORCED} - The individual is divorced.</li>
 *     <li>{@link #WIDOWED} - The individual has lost their spouse due to death.</li>
 * </ul>
 */

public enum MaritalStatus {
    /** The individual is not married. */
    SINGLE,
    /** The individual is married. */
    MARRIED,
    /** The individual is divorced. */
    DIVORCED,
    /** The individual has lost their spouse due to death. */
    WIDOWED
}
