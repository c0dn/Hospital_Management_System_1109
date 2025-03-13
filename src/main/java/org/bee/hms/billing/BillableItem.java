package org.bee.hms.billing;

import java.math.BigDecimal;

/**
 * Represents an item that can be billed in a medical billing system.
 * <p>
 * Implementing classes must define methods to retrieve item details such as price, description,
 * category, and code. This interface ensures that all billable items can be processed in a
 * consistent manner in the billing system.
 * </p>
 */
public interface BillableItem {

    /**
     * Retrieves the unsubsidised charges for the billable item.
     * <p>
     * This method provides the price of the item without any subsidies or discounts applied.
     * </p>
     * @return The unsubsidised charges of the item as a {@link BigDecimal}.
     */
    BigDecimal getUnsubsidisedCharges();

    /**
     * Retrieves the description of the billable item for display on the bill.
     * <p>
     * This method provides a brief description of the item, which will be used for display purposes
     * in the billing statement.
     * </p>

     * @return A string containing the description of the billable item.
     */
    String getBillItemDescription();

    /**
     * Retrieves the category of the billable item.
     * <p>
     * This method returns a string representing the category that the item belongs to, such as
     * "MEDICATION", "CONSULTATION", "DIAGNOSTIC", etc. The category helps to organize and
     * classify different types of items in the billing system.
     * </p>
     * @return A string representing the category of the billable item.
     */
    String getBillItemCategory();

    /**
     * Retrieves the billing item code.
     * <p>
     * This method returns a unique code for the item, which is used in the billing system to identify
     * the item for processing and claims.
     * </p>
     * @return A string representing the billing item code.
     */
    String getBillingItemCode();
}
