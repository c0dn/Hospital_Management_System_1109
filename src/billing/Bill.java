package billing;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a bill for a patient, containing billing items, categorized charges,
 * and billing status. Supports adding line items and retrieving total charges by category.
 */
public class Bill {
    /** Unique identifier for the bill. */
    private String billId;
    /** Unique identifier of the patient associated with the bill. */
    private String patientId;
    /** Date and time when the bill was created. */
    private LocalDateTime billDate;
    /** List of billing line items included in the bill. */
    private List<BillingItem> lineItems;
    /** A mapping of categorized charges, where the key is the category name and the value is the total amount for that category. */
    private Map<String, BigDecimal> categorizedCharges;
    /** Current status of the bill, such as DRAFT. */
    private BillingStatus status;

    /**
     * Constructs a {@code Bill} object using the {@link BillBuilder}.
     * Initializes the bill with default values.
     *
     * @param builder The {@link BillBuilder} object used to construct the bill.
     */
    Bill(BillBuilder builder) {
        this.billId = builder.billId;
        this.patientId = builder.patientId;
        this.billDate = builder.billDate;
        this.lineItems = new ArrayList<>();
        this.categorizedCharges = new HashMap<>();
        this.status = BillingStatus.DRAFT;
    }

    /**
     * Adds a new line item to the bill.
     * The total charges are automatically recalculated after adding the item.
     *
     * @param item     The {@link BillableItem} being added to the bill.
     * @param quantity The quantity of the item being added.
     */
    public void addLineItem(BillableItem item, int quantity) {
        lineItems.add(new BillingItem(item, quantity));
        recalculateTotals();
    }

    /**
     * Retrieves the total charge for a specified category.
     * If the category does not exist, returns {@code BigDecimal.ZERO}.
     *
     * @param category The category name whose total charge is to be retrieved.
     * @return The total charge for the given category, or {@code BigDecimal.ZERO} if not found.
     */
    public BigDecimal getTotalByCategory(String category) {
        return categorizedCharges.getOrDefault(category, BigDecimal.ZERO);
    }

    /**
     * Recalculates the total charges for each category.
     * This method iterates through all billing line items and updates the categorized charges.
     */
    private void recalculateTotals() {
        categorizedCharges.clear();

        for (BillingItem item : lineItems) {
            String category = item.getCategory();
            BigDecimal totalPrice = item.getTotalPrice();
            categorizedCharges.put(category,
                    categorizedCharges.getOrDefault(category, BigDecimal.ZERO).add(totalPrice));
        }
    }
}