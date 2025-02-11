package policy;

import java.time.LocalDate;

/**
 * A builder class for creating instances of {@link CriticalIllnessInsurance}.
 * This follows the Builder design pattern for structured object creation.
 */
public class CriticalIllnessBuilder extends InsuranceBuilder<CriticalIllnessBuilder> {

    /** The critical illness type covered under the insurance policy. */
    private CriticalIllnessType coveredIllness;

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

    /**
     * Builds and returns a {@link CriticalIllnessInsurance} instance.
     * Ensures all required fields are properly initialized.
     * @return A new instance of {@code CriticalIllnessInsurance}.
     * @throws IllegalStateException if {@code coveredIllness} is not set.
     */
    @Override
    public CriticalIllnessInsurance build() {
        validateFields();
        if (coveredIllness == null) {
            throw new IllegalStateException("Covered illness is required for Critical Illness Insurance.");
        }
        return new CriticalIllnessInsurance(
                policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate,
                coInsuranceRate, premiumAmount, insurancePayout, insuranceName, insuranceDescription, coveredIllness
        );
    }
}

