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
        System.out.println("ACCIDENT INSURANCE");
        System.out.println("---------------------------------------------------------------------");
        if (accidents == AccidentsType.MEDICAL) {
            System.out.printf("%-27s %-20s%n", "Covered Accident Type:", "Daily Allowance:");
            System.out.printf("%-27s %-20.2f", accidents, allowance);
        } else {
            System.out.printf("%-27s %-20s%n", "Covered Accident Type:", "Insurance Payout:");
            System.out.printf("%-27s %-20.2f%n",  accidents, getInsurancePayout());
        }
        System.out.println("=====================================================================");
        // line
//        System.out.format("Covered Accident Type: %s%n", accidents);
//        if (accidents == AccidentsType.MEDICAL) {
//            System.out.format("Daily Allowance: $%.2f%n", allowance);
//        } else {
//            System.out.format("Insurance Payout: $%.2f%n", getInsurancePayout());
//        }
//        System.out.printf("%-30s%n", "Covered Accident Type");
//        System.out.printf("%-30s", accidents);

        // table
//        if (accidents == AccidentsType.MEDICAL) {
//            System.out.printf("%-30s %-20.2f", accidents, allowance);
//        } else {
//            System.out.printf("%-30s %-20.2f",  accidents, getInsurancePayout());
//        }
//    }
//    // table
//    public void printHeaders() {
//        super.printHeaders();
//        if (accidents == AccidentsType.MEDICAL) {
//            System.out.printf("%-30s %-20s%n", "Covered Accident Type", "Daily Allowance");
//        } else {
//            System.out.printf("%-30s %-20s%n", "Covered Accident Type", "Insurance Payout");
//        }
    }
}