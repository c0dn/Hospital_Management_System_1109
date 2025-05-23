package org.bee.hms.billing;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.TelemedicineFeeItem;

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

    /** The insurance policy associated with the bill */
    InsurancePolicy insurancePolicy;

    /** The visit associated with the bill */
    private Visit visit;

    /** List of consultations associated with the bill */
    private final List<Consultation> consultations;

    /** Flag indicating whether the bill is for an inpatient service */
    boolean isInpatient;

    /** Flag indicating whether the bill is for an emergency service */
    boolean isEmergency;

    /**
     * The payment method used for the bill
     */
    PaymentMethod paymentMethod;

    /**
     * The amount that has been settled for the bill
     */
    BigDecimal settledAmount;


    Appointment appointment;

    /**
     * Constructs a new {@code BillBuilder} instance.
     * Initializes {@code billId} with a randomly generated UUID and
     * {@code billDate} with the current timestamp. Also initializes
     * {@code settledAmount} to zero.
     */
    public BillBuilder() {
        this.billId = UUID.randomUUID().toString();
        this.billDate = LocalDateTime.now();
        this.consultations = new ArrayList<>();
        this.insurancePolicy = null;
        this.isInpatient = false;
        this.isEmergency = false;
        this.paymentMethod = PaymentMethod.NOT_APPLICABLE;
        this.settledAmount = BigDecimal.ZERO;
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
     * Sets the payment method for this bill.
     *
     * @param paymentMethod The payment method to be used.
     * @return The current instance of {@code BillBuilder} for method chaining.
     */
    public BillBuilder withPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
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


    public BillBuilder withAppointment(Appointment appointment) {
        this.appointment = appointment;
        this.isEmergency = false;
        this.isInpatient = false;
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
     * Builds the {@link Bill} object based on the data in the {@code BillBuilder}.
     *
     * @return The constructed {@link Bill}.
     * @throws IllegalStateException if required fields are not set (e.g., patientId, visit, or consultations).
     */
    public Bill build() {
        validateBuildRequirements();

        Bill bill = new Bill(this);

        if (visit != null) {
            visit.getRelatedBillableItems().forEach(item ->
                    bill.addLineItem(item, 1));
        }

        for (Consultation consultation : consultations) {
            consultation.getRelatedBillableItems().forEach(item ->
                    bill.addLineItem(item, 1));
        }

        if (appointment != null) {
            bill.setSourceAppointmentId(appointment.getAppointmentId());
            bill.addLineItem(new TelemedicineFeeItem(), 1);
            Map<Medication, Integer> prescriptions = appointment.getPrescriptions();
            if (prescriptions != null && !prescriptions.isEmpty()) {
                prescriptions.forEach((medication, quantity) -> {
                    MedicationBillableItem medItem = new MedicationBillableItem(medication, quantity);
                    bill.addLineItem(medItem, quantity);
                });
            }
        }


        return bill;
    }

    /**
     * Validates the required fields for building a {@link Bill}.
     *
     * @throws IllegalStateException if any of the required fields are not set (e.g., patientId, visit, or consultations).
     */
    private void validateBuildRequirements() {
        if (Objects.isNull(patient)) {
            throw new IllegalStateException("Patient is required");
        }

        if (Objects.isNull(visit) && (consultations.isEmpty() && appointment == null)) {
            throw new IllegalStateException("Either a visit or at least one consultation or appointment is required");
        }
    }
}
