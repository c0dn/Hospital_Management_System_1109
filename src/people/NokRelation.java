package people;

/**
 * Represents the relationship between a patient and their next of kin.
 */
public enum NokRelation {
    /** Spouse of the patient. */
    SPOUSE("Spouse"),
    /** Parent of the patient. */
    PARENT("Parent"),
    /** Child of the patient. */
    CHILD("Child"),
    /** Sibling of the patient. */
    SIBLING("Sibling"),
    /** Grandparent of the patient. */
    GRANDPARENT("Grandparent"),
    /** Grandchild of the patient. */
    GRANDCHILD("Grandchild"),
    /** Legal guardian of the patient. */
    GUARDIAN("Legal Guardian"),
    /** Other relationship types. */
    OTHER("Other");
    /** A human-readable display name for the relationship type. */
    private final String displayName;

    /**
     * Constructs a {@code NokRelation} enum with a user-friendly display name.
     *
     * @param displayName The human-readable name of the relationship.
     */
    NokRelation(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name of the relationship.
     *
     * @return The display name of the next-of-kin relationship.
     */
    @Override
    public String toString() {
        return displayName;
    }
}