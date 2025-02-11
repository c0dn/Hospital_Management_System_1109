package billing;

import policy.InsurancePolicy;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A builder class for creating instances of {@link Bill}.
 * This class follows the Builder design pattern to allow
 * step-by-step construction of a Bill object.
 */
public class BillBuilder {
    /** A unique identifier for the bill, generated automatically. */
    String billId;
    /** The unique identifier of the patient associated with the bill. */
    String patientId;
    /** The date and time when the bill was created, set to the current timestamp by default. */
    LocalDateTime billDate;
    /** The insurance policy associated with the bill, if applicable. */
    InsurancePolicy insurancePolicy;

    /**
     * Constructs a new {@code BillBuilder} instance.
     * Initializes {@code billId} with a randomly generated UUID and
     * {@code billDate} with the current timestamp.
     */
    public BillBuilder() {
        this.billId = UUID.randomUUID().toString();
        this.billDate = LocalDateTime.now();
    }

    /**
     * Sets the patient ID for the bill.
     *
     * @param patientId The unique identifier of the patient
     * @return The current instance of {@code BillBuilder} for method chaining
     */
    public BillBuilder withPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    /**
     * Associates an insurance policy with the bill.
     *
     * @param policy the {@link InsurancePolicy} to be linked to the bill
     * @return The current instance of {@code BillBuilder} for method chaining
     */
    public BillBuilder withInsurancePolicy(InsurancePolicy policy) {
        this.insurancePolicy = policy;
        return this;
    }

    /**
     * Builds and returns a {@link Bill} instance using the provided values.
     *
     * @return A new {@code Bill} object with the configured attributes
     */
    public Bill build() {
        return new Bill(this);
    }
}
