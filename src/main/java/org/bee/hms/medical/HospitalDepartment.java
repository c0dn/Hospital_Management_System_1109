package org.bee.hms.medical;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Enums for type of departments
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum HospitalDepartment {
    /** Department specializing in the diagnosis and treatment of adult diseases. */
    INTERNAL_MEDICINE,

    /** Department focusing on surgical procedures and operations. */
    SURGERY,

    /** Department specializing in the care of infants, children, and adolescents. */
    PEDIATRICS,

    /** Department focusing on heart-related diseases and conditions. */
    CARDIOLOGY,

    /** Department specializing in disorders of the nervous system. */
    NEUROLOGY,

    /** Department focusing on musculoskeletal disorders and injuries. */
    ORTHOPEDICS,

    /** Department providing immediate medical care for acute illnesses and injuries. */
    EMERGENCY_MEDICINE,

    /** Department specializing in anesthesia and perioperative care. */
    ANESTHESIOLOGY,

    /** Department focusing on mental health disorders and treatment. */
    PSYCHIATRY;

    /**
     * Creates a DEPARTMENT from a string value.
     *
     * @param value The string value to convert to a DEPARTMENT enum.
     * @return The corresponding DEPARTMENT enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static HospitalDepartment fromString(String value) {
        try {
            return HospitalDepartment.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown HospitalDepartment: " + value);
        }
    }
}