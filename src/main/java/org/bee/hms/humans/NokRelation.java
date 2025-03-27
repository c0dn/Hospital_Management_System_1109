package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Represents the relationship between a patient and their next of kin.
 */

@JsonFormat(shape = JsonFormat.Shape.STRING)
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

    /**
     * Converts a string to its corresponding NokRelation enum value
     *
     * @param value The string representation of the NokRelation
     * @return The matching NokRelation enum value
     * @throws IllegalArgumentException If no matching NokRelation is found
     */
    @JsonCreator
    public static NokRelation fromString(String value) {
        for (NokRelation relation : values()) {
            if (relation.toString().equalsIgnoreCase(value)) {
                return relation;
            }
        }

        try {
            return NokRelation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown NokRelation: " + value);
        }
    }
}
