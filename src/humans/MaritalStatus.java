package humans;

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
    SINGLE, /** The individual is not married. */
    MARRIED, /** The individual is married. */
    DIVORCED, /** The individual is divorced. */
    WIDOWED /** The individual has lost their spouse due to death. */
}
