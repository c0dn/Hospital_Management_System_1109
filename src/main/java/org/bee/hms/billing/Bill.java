package org.bee.hms.billing;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.*;
import org.bee.utils.JSONSerializable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a bill for a patient, containing billing items, categorized charges,
 * and billing status. Supports adding line items and retrieving total charges by category.
 */
public class Bill implements JSONSerializable {
    /** Unique identifier for the bill. */
    private final String billId;

    /** Tax rate for Singapore, excluded from JSON serialization */
    @JsonIgnore
    private static final BigDecimal SINGAPORE_TAX_RATE = new BigDecimal("0.09");

    /**
     * Unique identifier of the patient associated with the bill.
     */
    private final Patient patient;

    /** Method of payment for the bill */
    private PaymentMethod paymentMethod;

    /** Date and time when the bill was created. */
    private final LocalDateTime billDate;
    /** List of billing line items included in the bill. */
    private List<BillingItemLine> lineItems;
    /** A mapping of categorized charges, where the key is the category name and the value is the total amount for that category. */
    private Map<String, BigDecimal> categorizedCharges;
    /** Current status of the bill, such as DRAFT. */
    private BillingStatus status;

    /** Amount that has been settled for the bill */
    private BigDecimal settledAmount;

    /** Insurance policy associated with the bill */
    private final InsurancePolicy insurancePolicy;

    /** Indicates whether the bill is for an inpatient service */
    private final boolean isInpatient;

    /** Indicates whether the bill is for an emergency service */
    private final boolean isEmergency;

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
        this.isEmergency = builder.isEmergency;
        this.paymentMethod = builder.paymentMethod;
        this.settledAmount = builder.settledAmount != null ? builder.settledAmount : BigDecimal.ZERO;
    }

    /**
     * Factory method for deserializing Bill objects from JSON using Jackson.
     * <p>
     * This method provides a way for Jackson to reconstruct Bill objects during
     * deserialization without requiring a default constructor. It preserves the
     * Bill class's builder-based construction pattern while enabling JSON serialization.
     *
     * @param billId              The unique identifier for the bill
     * @param patient             The patient associated with the bill
     * @param billDate            The date and time when the bill was created
     * @param lineItems           The list of billing line items included in the bill
     * @param categorizedCharges  The mapping of charges categorized by category name
     * @param status              The current status of the bill
     * @param settledAmount       The amount already settled for this bill
     * @param insurancePolicy     The insurance policy associated with the bill
     * @param isInpatient         Flag indicating if this is for an inpatient service
     * @param isEmergency         Flag indicating if this is for an emergency service
     * @param paymentMethod       The payment method used for the bill
     *
     * @return A fully constructed Bill object with all properties set from JSON data
     */
    @JsonCreator
    public static Bill fromJson(
            @JsonProperty("billId") String billId,
            @JsonProperty("patient") Patient patient,
            @JsonProperty("billDate") LocalDateTime billDate,
            @JsonProperty("lineItems") List<BillingItemLine> lineItems,
            @JsonProperty("categorizedCharges") Map<String, BigDecimal> categorizedCharges,
            @JsonProperty("status") BillingStatus status,
            @JsonProperty("settledAmount") BigDecimal settledAmount,
            @JsonProperty("insurancePolicy") InsurancePolicy insurancePolicy,
            @JsonProperty("isInpatient") boolean isInpatient,
            @JsonProperty("isEmergency") boolean isEmergency,
            @JsonProperty("paymentMethod") PaymentMethod paymentMethod
    ) {
        BillBuilder builder = new BillBuilder();
        builder.billId = billId;
        builder.patient = patient;
        builder.billDate = billDate;
        builder.insurancePolicy = insurancePolicy;
        builder.isInpatient = isInpatient;
        builder.isEmergency = isEmergency;
        builder.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.NOT_APPLICABLE;
        builder.settledAmount = settledAmount != null ? settledAmount : BigDecimal.ZERO;

        Bill bill = new Bill(builder);

        if (lineItems != null) {
            bill.lineItems = lineItems;
        }
        if (categorizedCharges != null) {
            bill.categorizedCharges = categorizedCharges;
        }
        if (status != null) {
            bill.status = status;
        }

        return bill;
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

        lineItems.add(new BillingItemLine(item, quantity));
        recalculateTotals();
    }


    /**
     * Submits the bill for processing.
     * This changes the bill's status from {@link BillingStatus#DRAFT} to {@link BillingStatus#SUBMITTED}.
     * This action typically signifies that the bill is finalized from the user's perspective and ready for
     * the next stage, like payment processing or insurance claim submission.
     *
     * @throws IllegalStateException if the bill is not currently in the {@link BillingStatus#DRAFT} status.
     */
    public void submitForProcessing() {
        if (this.status == BillingStatus.DRAFT) {
            this.status = BillingStatus.SUBMITTED;
        } else {
            throw new IllegalStateException("Bill cannot be submitted. Current status is '" + this.status.getDisplayName() +
                    "', required status is '" + BillingStatus.DRAFT.getDisplayName() + "'.");
        }
    }

    /**
     * Calculates the insurance coverage based on the associated insurance policy.
     * Updates the {@code insuranceCoverage} and {@code patientResponsibility} values.
     * The billing status is also updated to {@code BillingStatus.INSURANCE_PENDING} if insurance is active.
     * @return {@link InsuranceCoverageResult} containing either:
     *      - An approved insurance claim with coverage details
     *      - A denial reason if coverage cannot be provided
     */

    public InsuranceCoverageResult calculateInsuranceCoverage() {
        if (insurancePolicy == null) {
            return InsuranceCoverageResult.denied("No insurance policy associated with this bill");
        }

        if (this.status == BillingStatus.DRAFT) {
            throw new IllegalStateException("Cannot calculate insurance coverage for a bill in DRAFT status. Submit the bill first.");
        }

        if (lineItems.isEmpty()) {
            return InsuranceCoverageResult.denied("Bill contains no line items");
        }

        if (!insurancePolicy.isActive()) {
            return InsuranceCoverageResult.denied("Insurance policy is not active");
        }

        Coverage coverage = insurancePolicy.getCoverage();
        BigDecimal deductibleAmount = coverage.getDeductibleAmount();
        BigDecimal claimableAmount = BigDecimal.ZERO;
        BigDecimal accidentCoverage = BigDecimal.ZERO;

        // Calculate total claimable amount
        for (BillingItemLine itemLine : lineItems) {
            if (itemLine.getItem() instanceof ClaimableItem claimableItem) {
                if (coverage.isItemCovered(claimableItem, isInpatient)) {
                    BigDecimal itemAmount = itemLine.getTotalPrice();
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

        // If it does not exceed deductible, patient will be paying so...
        if (deductibleAmount.compareTo(BigDecimal.ZERO) > 0 &&
                claimableAmount.compareTo(deductibleAmount) <= 0) {
            return InsuranceCoverageResult.denied("Bill amount does not exceed deductible");
        }

        BigDecimal claimAmount = claimableAmount.subtract(deductibleAmount);

        // Check annual limit
        CoverageLimit limits = coverage.getLimits();
        if (!limits.isWithinAnnualLimit(claimAmount)) {
            // Cap at annual limit if it exceeds annual limit
            claimAmount = limits.getAnnualLimit();
        }

        // Add accident coverage (accidentCoverage will be 0 unless it's emergency (accident))
        BigDecimal totalCoverage = claimAmount.add(accidentCoverage);

        status = BillingStatus.INSURANCE_PENDING;

        // Return claim if there's a valid amount to claim
        if (totalCoverage.compareTo(BigDecimal.ZERO) > 0) {
            InsuranceClaim claim = InsuranceClaim.createNew(
                    this,
                    insurancePolicy.getInsuranceProvider(),
                    insurancePolicy,
                    this.patient,
                    totalCoverage
            );
            return InsuranceCoverageResult.approved(claim);
        }

        return InsuranceCoverageResult.denied("No claimable amount");
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
     * Gets the discount percentage applicable to this patient based on their residential status.
     * Singaporeans and PRs get 30%, others get 0%.
     * @return The discount percentage (e.g., 0.30 for 30%).
     */
    public Optional<Double> getDiscountPercentage() {
        if (patient.isResident()) {
            return Optional.of(0.30);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Calculates the discount amount based on patient's residential status.
     *
     * @return The discount amount
     */
    public BigDecimal getDiscountAmount() {
        return getDiscountPercentage()
                .map(discountRate -> getTotalAmount().multiply(BigDecimal.valueOf(discountRate)))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calculates the total amount after applying the discount.
     *
     * @return The discounted total amount
     */
    public BigDecimal getDiscountedTotal() {
        return getTotalAmount().subtract(getDiscountAmount());
    }

    /**
     * Gets the payment method used for this transaction
     * @return the payment method
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Gets the amount that has been settled
     * Returns {@link BigDecimal#ZERO} if no amount has been settled yet
     * @return the settled amount
     */
    public BigDecimal getSettledAmount() {
        return settledAmount == null ? BigDecimal.ZERO : settledAmount;
    }

    /**
     * Cancels the bill, changing its status to CANCELLED.
     *
     * @throws IllegalStateException if the bill is already in a finalized status
     */
    public void cancelBill() {
        if (this.status.isFinalized()) {
            throw new IllegalStateException("Cannot cancel bill. Bill is already in a finalized state: " + this.status.getDisplayName());
        }
        this.status = BillingStatus.CANCELLED;
    }

    /**
     * Updates the bill's insurance status to INSURANCE_APPROVED.
     *
     * @throws IllegalStateException if the bill is not in INSURANCE_PENDING status
     */
    public void approveInsurance() {
        if (this.status != BillingStatus.INSURANCE_PENDING) {
            throw new IllegalStateException("Cannot approve insurance. Current status is '" +
                    this.status.getDisplayName() + "', required status is 'Insurance Pending'.");
        }
        this.status = BillingStatus.INSURANCE_APPROVED;
    }

    /**
     * Updates the bill's insurance status to INSURANCE_REJECTED.
     *
     * @throws IllegalStateException if the bill is not in INSURANCE_PENDING status
     */
    public void rejectInsurance() {
        if (this.status != BillingStatus.INSURANCE_PENDING) {
            throw new IllegalStateException("Cannot reject insurance. Current status is '" +
                    this.status.getDisplayName() + "', required status is 'Insurance Pending'.");
        }
        this.status = BillingStatus.INSURANCE_REJECTED;
    }

    /**
     * Updates the bill's status to PARTIALLY_PAID, records the payment method, and the settled amount.
     * This method should only be used for payments that are less than the bill's grand total.
     *
     * @param amount          The partial amount that has been paid. Must be greater than zero and less than the grand total.
     * @param paymentMethod   The method used for making the payment.
     * @throws IllegalStateException    if the bill is already in a finalized state (e.g., PAID, CANCELLED).
     * @throws IllegalArgumentException if the payment amount is invalid (null, &lt;= 0, or &gt;= grand total).
     */
    public void recordPartialPayment(BigDecimal amount, PaymentMethod paymentMethod) {
        if (this.status.isFinalized()) {
            throw new IllegalStateException("Cannot record payment. Bill is already in a finalized state: " +
                    this.status.getDisplayName());
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        if (amount.compareTo(getGrandTotal()) >= 0) {
            throw new IllegalArgumentException("Partial payment amount cannot be greater than or equal to the grand total. Use recordFullPayment for full payments.");
        }

        this.paymentMethod = paymentMethod;
        this.settledAmount = this.settledAmount.add(amount); // Record the actual amount paid
        this.status = BillingStatus.PARTIALLY_PAID;
    }

    /**
     * Updates the bill's status to PAID, sets the settled amount to the grand total, and records the payment method.
     * This indicates the bill has been fully settled.
     *
     * @param paymentMethod   The method used for making the full payment.
     * @throws IllegalStateException if the bill is already in a finalized state (e.g., PAID, CANCELLED).
     */
    public void recordFullPayment(PaymentMethod paymentMethod) {
        if (this.status.isFinalized()) {
            throw new IllegalStateException("Cannot record payment. Bill is already in a finalized state: " +
                    this.status.getDisplayName());
        }

        this.paymentMethod = paymentMethod;
        this.settledAmount = getGrandTotal();
        this.status = BillingStatus.PAID;
    }
    /**
     * Initiates a refund process for the bill, changing its status to REFUND_PENDING.
     *
     * @throws IllegalStateException if the bill is not in PAID or PARTIALLY_PAID status
     */
    public void initiateRefund() {
        if (this.status != BillingStatus.PAID && this.status != BillingStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Cannot initiate refund. Current status is '" +
                    this.status.getDisplayName() + "', bill must be paid or partially paid.");
        }

        this.status = BillingStatus.REFUND_PENDING;
    }

    /**
     * Completes the refund process, changing the bill's status to REFUNDED.
     *
     * @throws IllegalStateException if the bill is not in REFUND_PENDING status
     */
    public void completeRefund() {
        if (this.status != BillingStatus.REFUND_PENDING) {
            throw new IllegalStateException("Cannot complete refund. Current status is '" +
                    this.status.getDisplayName() + "', required status is 'Refund Pending'.");
        }

        this.status = BillingStatus.REFUNDED;
        this.settledAmount = BigDecimal.ZERO;
    }

    /**
     * Marks a bill as overdue.
     *
     * @throws IllegalStateException if the bill is in a finalized status or already overdue
     */
    public void markAsOverdue() {
        if (this.status.isFinalized()) {
            throw new IllegalStateException("Cannot mark as overdue. Bill is already in a finalized state: " +
                    this.status.getDisplayName());
        }

        if (this.status == BillingStatus.OVERDUE) {
            throw new IllegalStateException("Bill is already marked as overdue.");
        }

        this.status = BillingStatus.OVERDUE;
    }

    /**
     * Marks a bill as in dispute.
     *
     * @throws IllegalStateException if the bill is in a finalized status or already in dispute
     */
    public void markInDispute() {
        if (this.status.isFinalized()) {
            throw new IllegalStateException("Cannot mark as in dispute. Bill is already in a finalized state: " +
                    this.status.getDisplayName());
        }

        if (this.status == BillingStatus.IN_DISPUTE) {
            throw new IllegalStateException("Bill is already marked as in dispute.");
        }

        this.status = BillingStatus.IN_DISPUTE;
    }

    /**
     * Returns the outstanding balance for this bill.
     *
     * @return The amount still owed on the bill (grand total minus settled amount)
     */
    public BigDecimal getOutstandingBalance() {
        BigDecimal settled = this.settledAmount != null ? this.settledAmount : BigDecimal.ZERO;
        return getGrandTotal().subtract(settled);
    }

    /**
     * Calculates the tax amount based on the Singapore GST rate.
     *
     * @return The tax amount
     */
    public BigDecimal getTaxAmount() {
        return getDiscountedTotal().multiply(SINGAPORE_TAX_RATE);
    }

    /**
     * Calculates the grand total including tax.
     *
     * @return The grand total amount
     */
    public BigDecimal getGrandTotal() {
        return getDiscountedTotal().add(getTaxAmount());
    }

    /**
     * Gets the patient responsibility amount (after insurance if applicable).
     *
     * @param insuranceCoverageAmount The amount covered by insurance
     * @return The amount the patient is responsible for
     */
    public BigDecimal getPatientResponsibility(BigDecimal insuranceCoverageAmount) {
        return getGrandTotal().subtract(
                insuranceCoverageAmount != null ? insuranceCoverageAmount : BigDecimal.ZERO
        );
    }

    /**
     * Recalculates the total charges for each category.
     * This method iterates through all billing line items and updates the categorized charges.
     */
    private void recalculateTotals() {
        categorizedCharges.clear();

        for (BillingItemLine item : lineItems) {
            String category = item.getCategory();
            BigDecimal totalPrice = item.getTotalPrice();
            categorizedCharges.put(category,
                    categorizedCharges.getOrDefault(category, BigDecimal.ZERO).add(totalPrice));
        }
    }

    /**
     * Retrieves the patient associated with the bill
     * @return The {@link Patient} object for the bill
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Gets the current status of the bill
     * @return The {@link BillingStatus} of the bill
     */
    public BillingStatus getStatus() {
        return status;
    }

    /**
     * Retrieves the insurance policy associated with the bill
     * @return The {@link InsurancePolicy} for the bill
     */
    public InsurancePolicy getInsurancePolicy() {
        return insurancePolicy;
    }

    /**
     * Gets the billId the bill
     * @return The billId
     */
    public String getBillId() {
        return billId;
    }

    /**
     * Retrieves the date and time when the bill was created
     * @return The bill date as a {@link LocalDateTime} object
     */
    public LocalDateTime getBillDate() {
        return billDate;
    }

    /**
     * Sets the status of this bill.
     * This method should be used carefully, as it bypasses the business logic
     * in the specific status change methods.
     *
     * @param status The new status to set
     */
    public void setStatus(BillingStatus status) {
        this.status = status;
    }
}
