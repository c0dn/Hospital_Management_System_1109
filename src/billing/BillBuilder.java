package billing;

import medical.Consultation;
import medical.Visit;
import policy.InsurancePolicy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A builder class for creating instances of {@link Bill}.
 * This class follows the Builder design pattern to allow
 * step-by-step construction of a {@link Bill} object.
 *
 * @param <T> The type of {@link Visit} associated with the bill.
 */
public class BillBuilder<T extends Visit> {
    /** A unique identifier for the bill, generated automatically. */
    String billId;
    /** The unique identifier of the patient associated with the bill. */
    String patientId;
    /** The date and time when the bill was created, set to the current timestamp by default. */
    LocalDateTime billDate;
    private InsurancePolicy insurancePolicy;
    private T visit;
    private List<Consultation> consultations;
    private List<BillingItem> billingItems;

    /**
     * Constructs a new {@code BillBuilder} instance.
     * Initializes {@code billId} with a randomly generated UUID and
     * {@code billDate} with the current timestamp.
     */
    public BillBuilder() {
        this.billId = UUID.randomUUID().toString();
        this.billDate = LocalDateTime.now();
        this.consultations = new ArrayList<>();
        this.billingItems = new ArrayList<>();
    }

    /**
     * Sets the patient ID for the bill.
     *
     * @param patientId The unique identifier of the patient.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder<T> withPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    /**
     * Associates an insurance policy with the bill.
     *
     * @param policy the {@link InsurancePolicy} to be linked to the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder<T> withInsurancePolicy(InsurancePolicy policy) {
        this.insurancePolicy = policy;
        return this;
    }

    /**
     * Associates a visit with the bill.
     *
     * @param visit the visit to be linked to the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder<T> withVisit(T visit) {
        this.visit = visit;
        return this;
    }

    /**
     * Adds a consultation to the bill.
     *
     * @param consultation the consultation to be added to the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder<T> withConsultation(Consultation consultation) {
        this.consultations.add(consultation);
        return this;
    }

    /**
     * Processes the visit and adds related billable items to the bill.
     */
    private void processVisit() {
        if (visit != null) {
            visit.getRelatedBillableItems().forEach(item ->
                    billingItems.add(new BillingItem(item, 1)));
        }
    }

    /**
     * Processes the consultations and adds related billable items to the bill.
     */
    private void processConsultations() {
        for (Consultation consultation : consultations) {
            consultation.getRelatedBillableItems().forEach(item ->
                    billingItems.add(new BillingItem(item, 1)));
        }
    }

    /**
     * Builds the {@link Bill} object based on the data in the {@code BillBuilder}.
     *
     * @return The constructed {@link Bill}.
     * @throws IllegalStateException if required fields are not set (e.g., patientId, visit, or consultations).
     */
    public Bill build() {
        validateBuildRequirements();

        processVisit();
        processConsultations();

        Bill bill = new Bill(this);

        // Add related line items from visit and consultations
        if (visit != null) {
            visit.getRelatedBillableItems().forEach(item ->
                    bill.addLineItem(item, 1));
        }

        for (Consultation consultation : consultations) {
            consultation.getRelatedBillableItems().forEach(item ->
                    bill.addLineItem(item, 1));
        }

        // If insurance policy is set, calculate insurance coverage
        if (insurancePolicy != null) {
            bill.calculateInsuranceCoverage();
        }

        return bill;
    }

    /**
     * Validates the required fields for building a {@link Bill}.
     *
     * @throws IllegalStateException if any of the required fields are not set (e.g., patientId, visit, or consultations).
     */
    private void validateBuildRequirements() {
        if (patientId == null) {
            throw new IllegalStateException("Patient ID is required");
        }

        if (visit == null && consultations.isEmpty()) {
            throw new IllegalStateException("Either a visit or at least one consultation is required");
        }
    }
}