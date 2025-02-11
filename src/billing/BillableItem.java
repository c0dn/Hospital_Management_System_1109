package billing;

import java.math.BigDecimal;

/**
 * Represents an item that can be billed in a medical billing system.
 * Implementing classes must define methods to retrieve item details
 * such as price, description, category, and code.
 */
public interface BillableItem {
    /**
     * Retrieves the price of the billable item.
     *
     * @return The price of the item as a {@link BigDecimal}.
     */
    BigDecimal getPrice();
    /**
     * Retrieves a brief description of the billable item.
     *
     * @return The description of the item as a {@code String}.
     */
    String getDescription();
    /**
     * Retrieves the category of the billable item.
     * Categories may include "Medication", "Consultation", or "Surgery".
     *
     * @return The category of the item as a {@code String}.
     */
    String getCategory();
    /**
     * Retrieves the unique code assigned to the billable item.
     * This code is typically used for tracking and insurance purposes.
     *
     * @return The code of the item as a {@code String}.
     */
    String getCode();
}