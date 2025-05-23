package org.bee.hms.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.utils.JSONSerializable;

import java.math.BigDecimal;

/**
 * Represents an individual billing item in a bill.
 * Each billing item is associated with a {@link BillableItem}, has a quantity,
 * and maintains its unit price and total price.
 */
public class BillingItemLine implements JSONSerializable {
    /** The billable item associated with this billing entry. */
    private final BillableItem item;
    /** The quantity of the item being billed. */
    private final int quantity;
    /** The unit price of the item. */
    private final BigDecimal unitPrice;
    /** The total price calculated as {@code unitPrice * quantity}. */
    private final BigDecimal totalPrice;

    /**
     * Constructs a new {@code BillingItem} with the specified {@link BillableItem} and quantity.
     * The unit price is retrieved from the {@code BillableItem}, and the total price is computed accordingly.
     *
     * @param item The billable item being added to the bill
     * @param quantity The number of units of the item being billed
     */
    @JsonCreator
    public BillingItemLine(
            @JsonProperty("item") BillableItem item,
            @JsonProperty("quantity") int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = item.getUnsubsidisedCharges();
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Returns a formatted string representing the billing entry.
     * The format includes the item code, description, quantity, unit price, and category.
     *
     * @return A formatted string representing the billing entry
     */
    public String getBillEntry() {
        return String.format("%s %s - %s %s%n%s", item.getBillingItemCode(), item.getBillItemDescription(), quantity, unitPrice, item.getBillItemCategory());
    }

    /**
     * Retrieves the billable item associated with this billing line item
     *
     * @return the associated billable item
     */
    public BillableItem getItem() {
        return item;
    }

    /**
     * Retrieves the category of the billable item.
     *
     * @return The category of the billing item
     */
    public String getCategory() {
        return item.getBillItemCategory();
    }

    /**
     * Retrieves the total price of the billing item.
     *
     * @return The total price, calculated as {@code unitPrice * quantity}
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

}
