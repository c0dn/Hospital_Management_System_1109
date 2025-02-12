package billing;


import policy.InsurancePolicy;
import policy.InsuranceStatus;

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
    private InsurancePolicy insurancePolicy;
    private BigDecimal insuranceCoverage;
    private BigDecimal patientResponsibility;


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
        if (item == null) {
            throw new IllegalArgumentException("BillableItem cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        lineItems.add(new BillingItem(item, quantity));
        recalculateTotals();
    }

    /**
     * Retrieves the total charge for a specified category.
     * @return The total charge for the given category, or {@code BigDecimal.ZERO} if not found.
     */
    public void calculateInsuranceCoverage() {
        if (insurancePolicy == null ||
                insurancePolicy.getStatus() != InsuranceStatus.ACTIVE) {
            patientResponsibility = getTotalAmount();
            insuranceCoverage = BigDecimal.ZERO;
            return;
        }

        BigDecimal totalAmount = getTotalAmount();
        BigDecimal deductible = BigDecimal.valueOf(insurancePolicy.getDeductible());

        // Apply deductible
        BigDecimal afterDeductible = totalAmount.subtract(deductible);
        if (afterDeductible.compareTo(BigDecimal.ZERO) <= 0) {
            insuranceCoverage = BigDecimal.ZERO;
            patientResponsibility = totalAmount;
            return;
        }

        // Calculate insurance portion
        BigDecimal coInsuranceRate = BigDecimal.valueOf(insurancePolicy.getCoInsuranceRate());
        insuranceCoverage = afterDeductible.multiply(coInsuranceRate);
        patientResponsibility = totalAmount.subtract(insuranceCoverage);

        // Update status
        status = BillingStatus.INSURANCE_PENDING;
    }


    public BigDecimal getTotalAmount() {
        return categorizedCharges.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


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