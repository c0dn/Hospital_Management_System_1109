package policy;

import java.time.LocalDate;

/**
 * A builder class for constructing {@link HealthInsurance} instances.
 */
public class HealthInsuranceBuilder extends InsuranceBuilder<HealthInsuranceBuilder> {
    /** The maximum hospital charges covered by the policy. */
    private double hospitalCharges;

    /**
     * Returns the builder instance.
     * @return The current instance of {@code HealthInsuranceBuilder}.
     */
    @Override
    protected HealthInsuranceBuilder self() {
        return this;
    }

    /**
     * Sets the hospital coverage amount for the policy.
     * @param hospitalCharges The maximum coverage amount for hospital expenses.
     * @return The current instance of {@code HealthInsuranceBuilder}.
     */
    public HealthInsuranceBuilder hospitalCharges(double hospitalCharges) {
        this.hospitalCharges = hospitalCharges;
        return self();
    }

    /**
     * Builds and returns a {@link HealthInsurance} instance.
     * Ensures required fields are properly initialized.
     * @return A new {@code HealthInsurance} instance.
     */
    @Override
    public HealthInsurance build() {
        // Validate fields specific to HealthInsurance
        validateFields();

        return new HealthInsurance(
                policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate,
                coInsuranceRate, premiumAmount, hospitalCharges, insurancePayout, insuranceName, insuranceDescription
        );
    }
}

