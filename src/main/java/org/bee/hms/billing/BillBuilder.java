package org.bee.hms.billing;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.EmergencyVisit;
import org.bee.hms.medical.Visit;
import org.bee.hms.policy.InsurancePolicy;

/**
 * A builder class for creating instances of {@link org.bee.hms.billing.Bill}.
 * This class follows the Builder design pattern to allow
 * step-by-step construction of a {@link Bill} object.
 * The builder supports creating bills from either a {@link org.bee.hms.medical.Visit},
 * {@link org.bee.hms.medical.Consultation}, or both.
 */
public class BillBuilder {
    /**
     * A unique identifier for the bill, generated automatically.
     */
    String billId;
    /**
     * The patient object associated with the bill
     */
    Patient patient;
    /**
     * The date and time when the bill was created, set to the current timestamp by default.
     */
    LocalDateTime billDate;
    InsurancePolicy insurancePolicy;
    private Visit visit;
    private List<Consultation> consultations;
    private List<BillingItemLine> billingItems;
    boolean isInpatient;
    boolean isEmergency;

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
        this.insurancePolicy = null;
        this.isInpatient = false;
        this.isEmergency = false;
    }

    /**
     * Sets patient for this bill
     *
     * @param patient The patient associated with the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder withPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    /**
     * Associates an insurance policy with the bill.
     *
     * @param policy the {@link InsurancePolicy} to be linked to the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder withInsurancePolicy(InsurancePolicy policy) {
        this.insurancePolicy = policy;
        return this;
    }

    /**
     * Associates a visit with the bill.
     *
     * @param visit the visit to be linked to the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     * @throws NullPointerException if visit is null
     * @throws IllegalArgumentException if visit is not finalized
     */
    public BillBuilder withVisit(Visit visit) {
        Objects.requireNonNull(visit, "Visit cannot be null");
        if (!visit.isFinalized()) {
            throw new IllegalArgumentException("Cannot create bill for non-finalized visit");
        }

        if (visit instanceof EmergencyVisit) {
            this.isEmergency = true;
        }

        this.visit = visit;
        this.isInpatient = true;
        return this;
    }

    /**
     * Adds a consultation to the bill.
     *
     * @param consultation the consultation to be added to the bill.
     * @return The current instance of {@code BillBuilder} for method chaining.
     * @throws NullPointerException if consultation is null
     */
    public BillBuilder withConsultation(Consultation consultation) {
        Objects.requireNonNull(consultation, "Consultation cannot be null");
        this.isInpatient = false;
        this.consultations.add(consultation);
        return this;
    }

    /**
     * Processes the visit and adds related billable items to the bill.
     */
    private void processVisit() {
        if (visit != null) {
            visit.getRelatedBillableItems().forEach(item ->
                    billingItems.add(new BillingItemLine(item, 1)));
        }
    }

    /**
     * Processes the consultations and adds related billable items to the bill.
     */
    private void processConsultations() {
        for (Consultation consultation : consultations) {
            consultation.getRelatedBillableItems().forEach(item ->
                    billingItems.add(new BillingItemLine(item, 1)));
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

        return bill;
    }

    /**
     * Validates the required fields for building a {@link Bill}.
     *
     * @throws IllegalStateException if any of the required fields are not set (e.g., patientId, visit, or consultations).
     */
    private void validateBuildRequirements() {
        if (patient == null) {
            throw new IllegalStateException("Patient ID is required");
        }

        if (visit == null && consultations.isEmpty()) {
            throw new IllegalStateException("Either a visit or at least one consultation is required");
        }
    }
}
