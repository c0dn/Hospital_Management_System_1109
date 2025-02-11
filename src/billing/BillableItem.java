package billing;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents an item that can be billed in a medical billing system.
 * Implementing classes must define methods to retrieve item details
 * such as price, description, category, and code.
 */
public interface BillableItem {
    BigDecimal getUnsubsidisedCharges();

    String getBillItemDescription();

    String getBillItemCategory();

    String getBillingItemCode();

}