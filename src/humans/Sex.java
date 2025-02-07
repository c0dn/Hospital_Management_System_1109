package humans;

/**
 * Defines the sex of a person.
 */

public enum Sex {
    MALE("M"),
    FEMALE("F"),
    OTHERS("O");

    private final String value;

    Sex(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}