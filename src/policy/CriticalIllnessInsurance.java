package policy;
import humans.Patient;
import java.time.LocalDate;

/**
 * Represents a Critical Illness Insurance Policy that extends the general {@link InsurancePolicy}.
 * This policy provides coverage for specified critical illnesses.
 */
public class CriticalIllnessInsurance extends InsurancePolicy {
    /** The specific critical illness covered under this policy. */
    private CriticalIllnessType coveredIllness;

    /**
     * Constructs a Critical Illness Insurance policy.
     *
     * @param policyId The unique identifier for the policy.
     * @param insuranceProvider The insurance provider name.
     * @param deductible The deductible amount for claims.
     * @param insuranceStatus The current status of the insurance policy.
     * @param startDate The start date of the policy.
     * @param endDate The end date of the policy.
     * @param coInsuranceRate The co-insurance rate percentage.
     * @param premiumAmount The premium amount to be paid.
     * @param insurancePayout The payout amount upon claim approval.
     * @param insuranceName The name of the insurance policy.
     * @param insuranceDescription A description of the policy.
     * @param coveredIllness The critical illness type covered under this policy.
     */
    public CriticalIllnessInsurance(String policyId, String insuranceProvider, double deductible,
                                    InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                                    double coInsuranceRate, double premiumAmount, double insurancePayout,
                                    String insuranceName, String insuranceDescription, CriticalIllnessType coveredIllness) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, insurancePayout, insuranceName, insuranceDescription);
        this.coveredIllness = coveredIllness;


    }

    /**
     * Displays the details of the critical illness insurance policy.
     * Prints the covered illness and insurance payout details.
     */
    @Override
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Covered Illness: %s%n", coveredIllness);
        System.out.format("Insurance Payout: $%.2f%n", getInsurancePayout());
    }
    //if not critical illness = reject claim
}
