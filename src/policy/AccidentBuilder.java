package policy;

public class AccidentBuilder extends InsuranceBuilder<AccidentBuilder> {

    AccidentsType accidents;
    double allowance;

    public AccidentBuilder() {}

    public AccidentBuilder accidents(AccidentsType accidents) {
        this.accidents = accidents;
        return this;
    }

    public AccidentBuilder allowance(double allowance) {
        this.allowance = allowance;
        return this;
    }

    @Override
    protected AccidentBuilder self() {
        return this;
    }

    @Override
    public AccidentBuilder withRandomBaseData() {
        super.withRandomBaseData();
        this.accidents = dataGenerator.getRandomEnum(AccidentsType.class);
        this.allowance = dataGenerator.generateAccidentAllowance();
        this.insuranceName = dataGenerator.getRandomAccidentInsuranceName();
        this.insuranceDescription = dataGenerator.generateAccidentInsuranceDescription();
        this.policyId = dataGenerator.generateAccidentPolicyId();
        return self();
    }

    @Override
    protected void validateFields() {
        super.validateFields();
        if (accidents == null) {
            throw new IllegalStateException("Accident type is required");
        }
        if (allowance <= 0) {
            throw new IllegalStateException("Allowance must be greater than 0");
        }
    }

    @Override
    public AccidentInsurance build() {
        validateFields();

        return new AccidentInsurance(this);
    }
}
