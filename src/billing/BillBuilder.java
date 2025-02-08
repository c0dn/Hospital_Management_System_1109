package billing;

import policy.InsurancePolicy;

import java.time.LocalDateTime;
import java.util.UUID;

public class BillBuilder {
    String billId;
    String patientId;
    LocalDateTime billDate;
    InsurancePolicy insurancePolicy;

    public BillBuilder() {
        this.billId = UUID.randomUUID().toString();
        this.billDate = LocalDateTime.now();
    }

    public BillBuilder withPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public BillBuilder withInsurancePolicy(InsurancePolicy policy) {
        this.insurancePolicy = policy;
        return this;
    }

    public Bill build() {
        return new Bill(this);
    }
}
