package humans;

/**
 * Represents the relationship between a patient and their next of kin.
 */
public enum NokRelation {
    SPOUSE("Spouse"),
    PARENT("Parent"),
    CHILD("Child"),
    SIBLING("Sibling"),
    GRANDPARENT("Grandparent"),
    GRANDCHILD("Grandchild"),
    GUARDIAN("Legal Guardian"),
    OTHER("Other");

    private final String displayName;

    NokRelation(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}