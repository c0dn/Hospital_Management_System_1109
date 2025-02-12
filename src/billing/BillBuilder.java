package billing;

import humans.Patient;
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
 * step-by-step construction of a Bill object.
 */
public class BillBuilder<T extends Visit> {
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
    private T visit;
    private List<Consultation> consultations;
    private List<BillingItem> billingItems;
    boolean isInpatient;

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
    }

    /**
     * Sets the patient ID for the bill.
     *
     * @param patientId The unique identifier of the patient
     * @return The current instance of {@code BillBuilder} for method chaining
     */
    public BillBuilder<T> withPatientId(String patientId) {
        // logic to get patient from id
        this.patient = Patient.builder()
                .withRandomData(patientId)
                .build();
        return this;
    }

    /**
     * Associates an insurance policy with the bill.
     *
     * @param policy the {@link InsurancePolicy} to be linked to the bill
     * @return The current instance of {@code BillBuilder} for method chaining
     */
    public BillBuilder<T> withInsurancePolicy(InsurancePolicy policy) {
        this.insurancePolicy = policy;
        return this;
    }

    /**
     * Associates a visit with the bill.
     *
     * @param visit the visit to be linked to the bill
     * @return The current instance of {@code BillBuilder} for method chaining
     */
    public BillBuilder<T> withVisit(T visit) {

        if (visit == null) {
            throw new IllegalArgumentException("Visit cannot be null");
        }
        if (!visit.isFinalized()) {
            throw new IllegalArgumentException("Cannot create bill for non-finalized visit");
        }


        this.visit = visit;
        this.isInpatient = true;
        return this;
    }

    /**
     * Adds a consultation to the bill.
     *
     * @param consultation the consultation to be added to the bill
     * @return The current instance of {@code BillBuilder} for method chaining
     */
    public BillBuilder<T> withConsultation(Consultation consultation) {
        this.isInpatient = false;
        this.consultations.add(consultation);
        return this;
    }

    private void processVisit() {
        if (visit != null) {
            visit.getRelatedBillableItems().forEach(item ->
                    billingItems.add(new BillingItem(item, 1)));
        }
    }

    private void processConsultations() {
        for (Consultation consultation : consultations) {
            consultation.getRelatedBillableItems().forEach(item ->
                    billingItems.add(new BillingItem(item, 1)));
        }
    }

    public Bill build() {
        validateBuildRequirements();

        processVisit();
        processConsultations();

        Bill bill = new Bill(this);

        if (visit != null) {
            visit.getRelatedBillableItems().forEach(item ->
                    bill.addLineItem(item, 1));
        }

        for (Consultation consultation : consultations) {
            consultation.getRelatedBillableItems().forEach(item ->
                    bill.addLineItem(item, 1));
        }

        if (insurancePolicy != null) {
            bill.calculateInsuranceCoverage();
        }

        return bill;
    }

    private void validateBuildRequirements() {
        if (patient == null) {
            throw new IllegalStateException("Patient ID is required");
        }

        if (visit == null && consultations.isEmpty()) {
            throw new IllegalStateException("Either a visit or at least one consultation is required");
        }
    }
}