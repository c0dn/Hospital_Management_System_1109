package policy;
import humans.Patient;
import java.time.LocalDate;

public class HealthInsurancePolicy extends InsurancePolicy {
    private double hospitalCharges;

    public HealthInsurancePolicy(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus,
                                 LocalDate startDate, LocalDate endDate, double coInsuranceRate,
                                 double premiumAmount, Patient policyHolder, double hospitalCharges) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate,
                endDate, coInsuranceRate, premiumAmount, policyHolder);
        this.hospitalCharges = hospitalCharges;

        this.hospitalCharges = hospitalCharges;
    }

    // Getter
    public double getHospitalCharges() { return hospitalCharges; }

    @Override
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Co-Insurance Rate: %.2f%%%n", getCoInsuranceRate()* 100);
        System.out.format("Deductible: $%.2f%n", getDeductible());
        System.out.format("Hospital Coverage Amount: $%.2f%n", hospitalCharges);
    }

}
