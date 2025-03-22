package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Defines the sex of a person.
 */

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Sex {
    /** Male sex. */
    MALE("M"),
    /** Female sex. */
    FEMALE("F"),
    /** Other gender identities. */
    OTHERS("O");

    private final String value;

    /**
     * Constructs a new instance of {@code Sex} with the specified value.
     *
     * @param value The string value representing the sex. This value is stored as part of the {@code Sex} object.
     */
    Sex(String value) {
        this.value = value;
    }

    /**
     * Gets the string representation of the sex.
     *
     * @return The string value representing the sex.
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a Sex enum from a string value.
     *
     * @param value The string value to convert to a Sex enum.
     * @return The corresponding Sex enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static Sex fromString(String value) {
        for (Sex sex : values()) {
            if (sex.getValue().equalsIgnoreCase(value)) {
                return sex;
            }
        }

        try {
            return Sex.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown Sex: " + value);
        }
    }
}