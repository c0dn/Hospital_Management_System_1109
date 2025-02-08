package billing;

import medical.Consultation;
import medical.Visit;
import policy.InsurancePolicy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillBuilder {
    String billId;
    String patientId;
    LocalDateTime billDate;
    private InsurancePolicy insurancePolicy;
    private Visit visit;
    private List<Consultation> consultations;
    private List<BillingItem> billingItems;

    public BillBuilder() {
        this.billId = UUID.randomUUID().toString();
        this.billDate = LocalDateTime.now();
        this.consultations = new ArrayList<>();
        this.billingItems = new ArrayList<>();
    }

    public BillBuilder withPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public BillBuilder withInsurancePolicy(InsurancePolicy policy) {
        this.insurancePolicy = policy;
        return this;
    }

    public BillBuilder withVisit(Visit visit) {
        this.visit = visit;
        return this;
    }

    public BillBuilder withConsultation(Consultation consultation) {
        this.consultations.add(consultation);
        return this;
    }

    // Additional processing logic in the future?
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
        if (patientId == null) {
            throw new IllegalStateException("Patient ID is required");
        }

        if (visit == null && consultations.isEmpty()) {
            throw new IllegalStateException("Either a visit or at least one consultation is required");
        }
    }
}
