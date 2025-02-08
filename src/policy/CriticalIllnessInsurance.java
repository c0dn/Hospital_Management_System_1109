package policy;
import humans.Patient;
import java.time.LocalDate;

/**
 * Represents a Critical Illness Insurance Policy that extends the general InsurancePolicy.
 */
public class CriticalIllnessInsurance extends InsurancePolicy {
    private CriticalIllnessType coveredIllness;

    public CriticalIllnessInsurance(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate, double coInsuranceRate, double premiumAmount, Patient policyHolder, double insurancePayout, CriticalIllnessType coveredIllness) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, policyHolder, insurancePayout);
        this.coveredIllness = coveredIllness;
    }

    @Override
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Covered Illness: %s%n", coveredIllness);
        System.out.format("Insurance Payout: $%.2f%n", getInsurancePayout());
    }
    //if not critical illness = reject claim
}
