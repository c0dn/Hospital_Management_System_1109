package policy;

/**
 * A builder class for constructing {@link HealthInsurance} instances.
 */
public class HealthInsuranceBuilder extends InsuranceBuilder<HealthInsuranceBuilder> {
    /** The maximum hospital charges covered by the policy. */
    private double hospitalCharges;

    public HealthInsuranceBuilder() {}

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

    @Override
    protected void validateFields() {
        super.validateFields();
        if (hospitalCharges <= 0) {
            throw new IllegalStateException("Hospital charges must be greater than 0");
        }
    }

    @Override
    public HealthInsuranceBuilder withRandomBaseData() {
        super.withRandomBaseData();
        this.hospitalCharges = dataGenerator.generateHospitalCharges();
        this.insuranceName = dataGenerator.getRandomHealthInsuranceName();
        this.insuranceDescription = dataGenerator.generateHealthInsuranceDescription();
        this.policyId = dataGenerator.generateHealthPolicyId();
        return self();
    }

    @Override
    public HealthInsurance build() {
        validateFields();
        return new HealthInsurance(this);
    }
}
