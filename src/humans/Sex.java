package humans;

/**
 * Defines the sex of a person.
 */

public enum Sex {
    /** Male sex. */
    MALE("M"),
    /** Female sex. */
    FEMALE("F"),
    /** Other gender identities. */
    OTHERS("O");

    private final String value;

    /**
     *Constructs a new instance of {@code Sex} with the specified value.
     *
     * @param value The string value representing the sex. This value is stored as part of the {@code Sex} object.
     */
    Sex(String value) {
        this.value = value;
    }

    /**
     * Gets the string representation of the sex.
     *
     */
    public String getValue() {
        return value;
    }
}