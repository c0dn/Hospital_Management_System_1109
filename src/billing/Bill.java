package billing;


import claims.InsuranceClaim;
import humans.Patient;
import policy.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a bill for a patient, containing billing items, categorized charges,
 * and billing status. Supports adding line items and retrieving total charges by category.
 */
public class Bill {
    /** Unique identifier for the bill. */
    private String billId;
    /** Unique identifier of the patient associated with the bill. */
    private Patient patient;
    /** Date and time when the bill was created. */
    private LocalDateTime billDate;
    /** List of billing line items included in the bill. */
    private List<BillingItem> lineItems;
    /** A mapping of categorized charges, where the key is the category name and the value is the total amount for that category. */
    private Map<String, BigDecimal> categorizedCharges;
    /** Current status of the bill, such as DRAFT. */
    private BillingStatus status;
    private InsurancePolicy insurancePolicy;
    private boolean isInpatient;
    private boolean isEmergency;

    /**
     * Constructs a {@code Bill} object using the {@link BillBuilder}.
     * Initializes the bill with default values.
     *
     * @param builder The {@link BillBuilder} object used to construct the bill.
     */
    Bill(BillBuilder builder) {
        this.billId = builder.billId;
        this.patient = builder.patient;
        this.billDate = builder.billDate;
        this.lineItems = new ArrayList<>();
        this.insurancePolicy = builder.insurancePolicy;
        this.categorizedCharges = new HashMap<>();
        this.status = BillingStatus.DRAFT;
        this.isInpatient = builder.isInpatient;
    }

    /**
     * Adds a new line item to the bill.
     * The total charges are automatically recalculated after adding the item.
     *
     * @param item     The {@link BillableItem} being added to the bill.
     * @param quantity The quantity of the item being added.
     * @throws IllegalArgumentException if the {@code item} is null or {@code quantity} is not positive.
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
     * Calculates the insurance coverage based on the associated insurance policy.
     * Updates the {@code insuranceCoverage} and {@code patientResponsibility} values.
     * The billing status is also updated to {@code BillingStatus.INSURANCE_PENDING} if insurance is active.
     */
    public Optional<InsuranceClaim> calculateInsuranceCoverage() {
        if (lineItems.isEmpty()) {
            return Optional.empty();
        }
        if (insurancePolicy.isActive()) {
            Coverage coverage = insurancePolicy.getCoverage();
            BigDecimal deductibleAmount = coverage.getDeductibleAmount();
            BigDecimal claimableAmount = BigDecimal.ZERO;
            BigDecimal accidentCoverage = BigDecimal.ZERO;
            BigDecimal totalCoverage = BigDecimal.ZERO;
            for (BillingItem item : lineItems) {

                if (item instanceof ClaimableItem claimableItem) {
                    if (coverage.isItemCovered(claimableItem, isInpatient)) {
                        BigDecimal itemAmount = item.getTotalPrice();
                        claimableAmount = claimableAmount.add(itemAmount);

                        if (isEmergency && claimableItem.getAccidentSubType() != null) {
                            AccidentType accidentType = claimableItem.getAccidentSubType();
                            BigDecimal accidentPayout = coverage.calculateAccidentPayout(accidentType);

                            CoverageLimit limits = coverage.getLimits();
                            if (limits.isWithinAccidentLimit(accidentType, accidentPayout)) {
                                accidentCoverage = accidentCoverage.add(accidentPayout);
                            }
                        }
                    }

                }

            }
            BigDecimal claimAmount = claimableAmount.subtract(deductibleAmount);
            CoverageLimit limits = coverage.getLimits();
            if (limits.isWithinAnnualLimit(claimAmount)) {
                totalCoverage = totalCoverage.add(claimableAmount);
            }
            totalCoverage = totalCoverage.add(accidentCoverage);
            status = BillingStatus.INSURANCE_PENDING;

            if (totalCoverage.compareTo(BigDecimal.ZERO) > 0) {

                InsuranceClaim claim = InsuranceClaim.createNew(
                        this,
                        insurancePolicy.getInsuranceProvider(),
                        insurancePolicy,
                        this.patient,
                        totalCoverage
                );
                return Optional.of(claim);
            }

        }
        return Optional.empty();

    }

    /**
     * Retrieves the total amount for the bill, summing all categorized charges.
     *
     * @return The total amount for the bill.
     */
    public BigDecimal getTotalAmount() {
        return categorizedCharges.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Retrieves the total charge for a specified category.
     * If the category is not found, {@code BigDecimal.ZERO} is returned.
     *
     * @param category The category for which to retrieve the total charge.
     * @return The total charge for the given category.
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