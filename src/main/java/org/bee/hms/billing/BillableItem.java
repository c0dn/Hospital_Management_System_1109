package org.bee.hms.billing;

import java.math.BigDecimal;

import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.MedicationBillableItem;
import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.medical.WardStay;
import org.bee.hms.telemed.TelemedicineFeeItem;
import org.bee.utils.JSONSerializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface representing a billable item in the hospital management system.
 * This interface defines the common structure for various types of billable items
 * such as medications, procedures, diagnostics, and ward stays. It provides methods
 * for retrieving essential billing information and supports JSON serialization with
 * type information for proper deserialization of concrete implementations.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MedicationBillableItem.class, name = "medication"),
        @JsonSubTypes.Type(value = ProcedureCode.class, name = "procedure"),
        @JsonSubTypes.Type(value = DiagnosticCode.class, name = "diagnostic"),
        @JsonSubTypes.Type(value = WardStay.class, name = "wardStay"),
        @JsonSubTypes.Type(value = TelemedicineFeeItem.class, name = "telemedicineFee")
})
public interface BillableItem extends JSONSerializable {
    /**
     * Gets the full unsubsidised charges before any insurance or discounts
     *
     * @return the base price as BigDecimal
     */
    BigDecimal getUnsubsidisedCharges();

    /**
     * Gets the bill description
     *
     * @return a descriptive name for display purposes
     */
    String getBillItemDescription();

    /**
     * Gets the category classification for this bill (e.g., "MEDICATION", "PROCEDURE")
     *
     * @return the category string
     */
    String getBillItemCategory();

    /**
     * Gets the unique billing code that identifies this item in the system
     *
     * @return the standardized billing code
     */
    String getBillingItemCode();
}
