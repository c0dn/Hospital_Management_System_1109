package policy;

public class AccidentBuilder extends InsuranceBuilder<AccidentBuilder> {

    private AccidentsType accidents;
    private double allowance;

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
    public AccidentInsurance build() {
        validateFields();

        return new AccidentInsurance(
                policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate,
                coInsuranceRate, premiumAmount, insurancePayout, accidents, allowance,
                insuranceName, insuranceDescription
        );
    }
}
