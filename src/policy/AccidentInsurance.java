package policy;

import humans.Patient;

import java.time.LocalDate;

public class AccidentInsurance extends InsurancePolicy{
    private CoveredAccidentsType accidents;
    private double dailyAllowance;

    public AccidentInsurance(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus,
                             LocalDate startDate, LocalDate endDate, double coInsuranceRate, double premiumAmount, Patient policyHolder,
                             CoveredAccidentsType accidents, double dailyAllowance) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, policyHolder);
        this.accidents = accidents;
        this.dailyAllowance = dailyAllowance;
    }
    public CoveredAccidentsType getAccidents() {
        return accidents;
    }

    public double getDailyAllowance() {
        return dailyAllowance;
    }
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Covered Accident Type: %s%n", accidents);
        System.out.format("Daily Allowance: $%.2f%n", dailyAllowance);
    }
}
