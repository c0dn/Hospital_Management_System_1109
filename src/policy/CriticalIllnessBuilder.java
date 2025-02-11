package policy;

/**
 * A builder class for creating instances of {@link CriticalIllnessInsurance}.
 * This follows the Builder design pattern for structured object creation.
 */
public class CriticalIllnessBuilder extends InsuranceBuilder<CriticalIllnessBuilder> {

    /** The critical illness type covered under the insurance policy. */
    CriticalIllnessType coveredIllness;

    public CriticalIllnessBuilder() {}

    /**
     * Returns the builder instance.
     * @return The current instance of {@code CriticalIllnessBuilder}.
     */
    @Override
    protected CriticalIllnessBuilder self() {
        return this;
    }

    /**
     * Sets the covered illness for the insurance policy.
     * @param coveredIllness The type of critical illness covered.
     * @return The current instance of {@code CriticalIllnessBuilder}.
     */
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
