package org.bee.hms.medical;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.bee.utils.JSONSerializable;

/**
 * Enums for lab test types
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum LabTestType implements JSONSerializable {
    BLOOD,
    URINE,
    STOOL;


    /**
     * Creates a LabTestType from a string value.
     *
     * @param value The string value to convert to a LabTestType enum.
     * @return The corresponding LabTestType enum value.
     * @throws IllegalArgumentException if the string value cannot be converted.
     */
    @JsonCreator
    public static LabTestType fromString(String value) {
        try {
            return LabTestType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown LabTestType: " + value);
        }
    }
}

