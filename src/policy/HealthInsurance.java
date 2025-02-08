package policy;
import humans.Patient;
import java.time.LocalDate;

public class HealthInsurance extends InsurancePolicy {
    private double hospitalCharges;

    public HealthInsurance(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus,
                           LocalDate startDate, LocalDate endDate, double coInsuranceRate,
                           double premiumAmount, Patient policyHolder, double hospitalCharges , double insurancePayout) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate,
                coInsuranceRate, premiumAmount, policyHolder, insurancePayout);

        this.hospitalCharges = hospitalCharges;
    }

    @Override
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Co-Insurance Rate: %.2f%%%n", getCoInsuranceRate()* 100);
        System.out.format("Deductible: $%.2f%n", getDeductible());
        System.out.format("Hospital Coverage Amount: $%.2f%n", hospitalCharges);

        //if Claim is less than or equal to deductible = reject claim
        //policy limit has reached = reject claim
    }
}
