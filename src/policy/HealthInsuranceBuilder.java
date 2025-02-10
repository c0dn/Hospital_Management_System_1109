package policy;

import java.time.LocalDate;

public class HealthInsuranceBuilder extends InsuranceBuilder<HealthInsuranceBuilder> {
    private double hospitalCharges;

    @Override
    protected HealthInsuranceBuilder self() {
        return this;
    }

    public HealthInsuranceBuilder hospitalCharges(double hospitalCharges) {
        this.hospitalCharges = hospitalCharges;
        return self();
    }

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

