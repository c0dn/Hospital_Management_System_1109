package org.bee.hms.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bee.utils.JSONSerializable;

/**
 * Represents the various payment methods available for bills in the hospital management system
 * This enum provides a set of predefined payment options and implements JSON serialization
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentMethod implements JSONSerializable {
    CASH,
    CREDIT_CARD,
    PAYNOW,
    INSURANCE,
    NOT_APPLICABLE;

    /**
     * Creates a PaymentMethod enum from a string value
     * This method is used by Jackson for JSON deserialization
     *
     * @param value The payment method
     * @return The corresponding PaymentMethod enum, or NOT_APPLICABLE if not found
     */
    @JsonCreator
    public static PaymentMethod fromString(String value) {
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NOT_APPLICABLE;
        }
    }
}