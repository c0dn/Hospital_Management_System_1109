package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Defines the blood type of a person.
 * <p>
 *     Blood types include different ABO and Rh factor combinations.
 * </p>
 * <ul>
 *     <li>{@link #A_POSITIVE} - A+ blood type.</li>
 *     <li>{@link #A_NEGATIVE} - A- blood type.</li>
 *     <li>{@link #B_POSITIVE} - B+ blood type.</li>
 *     <li>{@link #B_NEGATIVE} - B- blood type.</li>
 *     <li>{@link #O_POSITIVE} - O+ blood type.</li>
 *     <li>{@link #O_NEGATIVE} - O- blood type.</li>
 *     <li>{@link #AB_POSITIVE} - AB+ blood type.</li>
 *     <li>{@link #AB_NEGATIVE} - AB- blood type.</li>
 * </ul>
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BloodType {

    /** A+ blood type. */
    A_POSITIVE("A+"),

    /** A- blood type. */
    A_NEGATIVE("A-"),

    /** B+ blood type. */
    B_POSITIVE("B+"),

    /** B- blood type. */
    B_NEGATIVE("B-"),

    /** O+ blood type. */
    O_POSITIVE("O+"),

    /** O- blood type. */
    O_NEGATIVE("O-"),

    /** AB+ blood type. */
    AB_POSITIVE("AB+"),

    /** AB- blood type. */
    AB_NEGATIVE("AB-");

    private final String value;

    /**
     * Constructs a BloodType enum with its corresponding string representation.
     *
     * @param value The string representation of the blood type.
     */
    BloodType(String value) {
        this.value = value;
    }

    /**
     * Retrieves the string representation of the blood type.
     *
     * @return The blood type as a string.
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a BloodType enum from a string value.
     *
     * @param value The string value to convert to a BloodType enum.
     * @return The corresponding BloodType enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static BloodType fromString(String value) {
        // Try to match by the display value (A+, B-, etc.)
        for (BloodType bloodType : values()) {
            if (bloodType.getValue().equalsIgnoreCase(value)) {
                return bloodType;
            }
        }

        // Try to match by enum name (A_POSITIVE, B_NEGATIVE, etc.)
        try {
            return BloodType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown BloodType: " + value);
        }
    }
}