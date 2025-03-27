package org.bee.hms.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bee.utils.JSONSerializable;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentMethod implements JSONSerializable {
    CASH,
    CREDIT_CARD,
    PAYNOW,
    INSURANCE,
    NOT_APPLICABLE;

    @JsonCreator
    public static PaymentMethod fromString(String value) {
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NOT_APPLICABLE;
        }
    }
}