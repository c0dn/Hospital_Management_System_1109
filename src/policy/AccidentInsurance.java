package policy;
import java.time.LocalDate;

public class AccidentInsurance extends InsurancePolicy {
    private AccidentsType accidents;
    private double allowance;

    public AccidentInsurance(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate, double coInsuranceRate, double premiumAmount, double insurancePayout, AccidentsType accidents, double allowance, String insuranceName, String insuranceDescription) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, insurancePayout, insuranceName,  insuranceDescription);
        this.accidents = accidents;
        this.allowance = allowance;
    }

    public AccidentsType getAccidents() {
        return accidents;
    }

    public double getAllowance() {
        return allowance;
    }
    // rejection reason
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Covered Accident Type: %s%n", accidents);
        if (accidents == AccidentsType.MEDICAL) {
            System.out.format("Daily Allowance: $%.2f%n", allowance);
        } else {
            System.out.format("Insurance Payout: $%.2f%n", getInsurancePayout());
        }
    }
}