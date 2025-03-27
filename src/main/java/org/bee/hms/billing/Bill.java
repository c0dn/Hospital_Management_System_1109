package org.bee.hms.billing;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.ResidentialStatus;
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

    @JsonIgnore
    private static final BigDecimal SINGAPORE_TAX_RATE = new BigDecimal("0.09");

    /**
     * Unique identifier of the patient associated with the bill.
     */
    private final Patient patient;

    /** Date and time when the bill was created. */
    private final LocalDateTime billDate;
    /** List of billing line items included in the bill. */
    private List<BillingItemLine> lineItems;
    /** A mapping of categorized charges, where the key is the category name and the value is the total amount for that category. */
    private Map<String, BigDecimal> categorizedCharges;
    /** Current status of the bill, such as DRAFT. */
    private BillingStatus status;
    private final InsurancePolicy insurancePolicy;
    private final boolean isInpatient;
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
     * @param insurancePolicy     The insurance policy associated with the bill
     * @param isInpatient         Flag indicating if this is for an inpatient service
     * @param isEmergency         Flag indicating if this is for an emergency service
     *
     * @return A fully constructed Bill object with all properties set from JSON data
     */
    @JsonCreator
    public static Bill fromJson(
            @JsonProperty("bill_id") String billId,
            @JsonProperty("patient") Patient patient,
            @JsonProperty("bill_date") LocalDateTime billDate,
            @JsonProperty("line_items") List<BillingItemLine> lineItems,
            @JsonProperty("categorized_charges") Map<String, BigDecimal> categorizedCharges,
            @JsonProperty("status") BillingStatus status,
            @JsonProperty("insurance_policy") InsurancePolicy insurancePolicy,
            @JsonProperty("is_inpatient") boolean isInpatient,
            @JsonProperty("is_emergency") boolean isEmergency
    ) {
        BillBuilder builder = new BillBuilder();
        builder.billId = billId;
        builder.patient = patient;
        builder.billDate = billDate;
        builder.insurancePolicy = insurancePolicy;
        builder.isInpatient = isInpatient;
        builder.isEmergency = isEmergency;

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
     * Calculates the insurance coverage based on the associated insurance policy.
     * Updates the {@code insuranceCoverage} and {@code patientResponsibility} values.
     * The billing status is also updated to {@code BillingStatus.INSURANCE_PENDING} if insurance is active.
     */
    public InsuranceCoverageResult calculateInsuranceCoverage() {
        if (insurancePolicy == null) {
            return InsuranceCoverageResult.denied("No insurance policy associated with this bill");
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


    public Patient getPatient() {
        return patient;
    }

    public BillingStatus getStatus() {
        return status;
    }

    public InsurancePolicy getInsurancePolicy() {
        return insurancePolicy;
    }
}
