package policy;

/**
 * A builder class for creating Critical Illness Insurance policies.
 */
public class CriticalIllnessBuilder extends InsuranceBuilder<CriticalIllnessBuilder> {

    CriticalIllnessType coveredIllness;

    public CriticalIllnessBuilder() {}

    @Override
    protected CriticalIllnessBuilder self() {
        return this;
    }

    public CriticalIllnessBuilder coveredIllness(CriticalIllnessType coveredIllness) {
        this.coveredIllness = coveredIllness;
        return self();
    }

    @Override
    protected void validateFields() {
        super.validateFields();
        if (coveredIllness == null) {
            throw new IllegalStateException("Covered illness is required for Critical Illness Insurance.");
        }
    }

    @Override
    public CriticalIllnessBuilder withRandomBaseData() {
        super.withRandomBaseData();
        this.coveredIllness = dataGenerator.getRandomEnum(CriticalIllnessType.class);
        this.insuranceName = dataGenerator.getRandomCriticalIllnessName();
        this.insuranceDescription = dataGenerator.generateCriticalIllnessDescription();
        this.policyId = dataGenerator.generateCriticalIllnessPolicyId();
        return self();
    }

    @Override
    public CriticalIllnessInsurance build() {
        validateFields();
        return new CriticalIllnessInsurance(this);
    }
}
