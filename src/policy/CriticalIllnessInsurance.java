package policy;
import humans.Patient;
import java.time.LocalDate;

/**
 * Represents a Critical Illness Insurance Policy that extends the general InsurancePolicy.
 */
public class CriticalIllnessInsurance extends InsurancePolicy {
    private CriticalIllnessType coveredIllness;

    public CriticalIllnessInsurance(String policyId, String insuranceProvider, double deductible,
                                    InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                                    double coInsuranceRate, double premiumAmount, double insurancePayout,
                                    String insuranceName, String insuranceDescription, CriticalIllnessType coveredIllness) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, insurancePayout, insuranceName, insuranceDescription);
        this.coveredIllness = coveredIllness;


    }

    @Override
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
//        System.out.format("Covered Illness: %s%n", coveredIllness);
//        System.out.format("Insurance Payout: $%.2f%n", getInsurancePayout());
        System.out.println("CRITICAL INSURANCE");
        System.out.println("---------------------------------------------------------------------");
            System.out.printf("%-20s %-30s%n", "Insurance Payout:", "Covered Illness:");
            System.out.printf("%-20.2f %-30s%n", getInsurancePayout(),  coveredIllness);
        System.out.println("=====================================================================");

//        System.out.println("CRITICAL INSURANCE");
//        System.out.println("-----------------------------------------------------------------");
//        System.out.printf("%-27s%n", "Covered Illness:");
//        System.out.printf("%-27s%n%n",  coveredIllness);
//        System.out.printf("%-20s%n", "Insurance Payout:");
//        System.out.printf("%-20.2f%n", getInsurancePayout());
//        System.out.println("=================================================================");
    }
    //if not critical illness = reject claim
}
