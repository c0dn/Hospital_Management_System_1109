package policy;
import java.time.LocalDate;

/**
 * Represents a Health Insurance policy that extends {@link InsurancePolicy}.
 */
public class HealthInsurance extends InsurancePolicy {
    /** The maximum coverage amount for hospital charges under this policy. */
    private double hospitalCharges;

    /**
     * Constructs a Health Insurance policy.
     *
     * @param policyId The unique identifier for the policy.
     * @param insuranceProvider The insurance provider.
     * @param deductible The deductible amount.
     * @param insuranceStatus The status of the insurance policy.
     * @param startDate The policy start date.
     * @param endDate The policy end date.
     * @param coInsuranceRate The co-insurance rate.
     * @param premiumAmount The premium amount.
     * @param hospitalCharges The coverage amount for hospital charges.
     * @param insurancePayout The total payout for insurance claims.
     * @param insuranceName The name of the insurance policy.
     * @param insuranceDescription A brief description of the insurance policy.
     */
    public HealthInsurance(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus,
                           LocalDate startDate, LocalDate endDate, double coInsuranceRate,
                           double premiumAmount, double hospitalCharges , double insurancePayout, String insuranceName, String insuranceDescription) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate,
                coInsuranceRate, premiumAmount, insurancePayout, insuranceName, insuranceDescription);

        this.hospitalCharges = hospitalCharges;
    }

    /**
     * Displays the details of the health insurance policy.
     * Prints co-insurance rate, deductible, and hospital coverage amount.
     */
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
