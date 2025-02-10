package policy;

import java.time.LocalDate;

/**
 * A builder class for creating Critical Illness Insurance policies.
 */
public class CriticalIllnessBuilder extends InsuranceBuilder<CriticalIllnessBuilder> {

    private CriticalIllnessType coveredIllness;

    @Override
    protected CriticalIllnessBuilder self() {
        return this;
    }

    public CriticalIllnessBuilder coveredIllness(CriticalIllnessType coveredIllness) {
        this.coveredIllness = coveredIllness;
        return self();
    }

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

