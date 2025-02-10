package policy;

public class HealthInsuranceBuilder extends InsuranceBuilder<HealthInsuranceBuilder> {
    double hospitalCharges;

    public HealthInsuranceBuilder() {}

    @Override
    protected HealthInsuranceBuilder self() {
        return this;
    }

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
