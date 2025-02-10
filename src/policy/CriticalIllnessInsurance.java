package policy;
import java.time.LocalDate;

/**
 * Represents a Critical Illness Insurance Policy that extends the general InsurancePolicy.
 */
public class CriticalIllnessInsurance extends InsurancePolicy {
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

    @Override
    public void displayPolicyDetails() {
        super.displayPolicyDetails();
        System.out.format("Covered Illness: %s%n", coveredIllness);
        System.out.format("Insurance Payout: $%.2f%n", getInsurancePayout());
    }
    //if not critical illness = reject claim
}
