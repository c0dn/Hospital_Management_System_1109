package policy;
import java.time.LocalDate;

/**
 * Represents a Critical Illness Insurance Policy that extends the general {@link InsurancePolicy}.
 * This policy provides coverage for specified critical illnesses.
 */
public class CriticalIllnessInsurance extends InsurancePolicy {
    /** The specific critical illness covered under this policy. */
    private CriticalIllnessType coveredIllness;

    /**
     * Package-private constructor that accepts only a CriticalIllnessBuilder
     * to enforce the builder pattern.
     *
     * @param builder The builder object containing initialization data
     */
    CriticalIllnessInsurance(CriticalIllnessBuilder builder) {
        super(builder);
        this.coveredIllness = builder.coveredIllness;
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
