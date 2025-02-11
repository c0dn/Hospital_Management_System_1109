package policy;
import java.time.LocalDate;

/**
 * Represents an accident insurance policy that extends {@link InsurancePolicy}.
 * This policy includes coverage for different types of accidents and provides an optional daily allowance for medical-related accidents.
 */
public class AccidentInsurance extends InsurancePolicy {
    /** The type of accident covered by this insurance policy. */
    private AccidentsType accidents;
    /** The daily allowance amount provided for medical-related accidents. */
    private double allowance;

    /**
     * Package-private constructor that accepts only an AccidentBuilder
     * to enforce the builder pattern.
     *
     * @param builder The builder object containing initialization data
     */
    AccidentInsurance(AccidentBuilder builder) {
        super(builder);
        this.accidents = builder.accidents;
        this.allowance = builder.allowance;
    }

    /**
     * Gets the type of accident covered by this policy.
     *
     * @return The accident type.
     */
    public AccidentsType getAccidents() {
        return accidents;
    }

    /**
     * Gets the daily allowance amount for medical-related accidents.
     *
     * @return The allowance amount.
     */
    public double getAllowance() {
        return allowance;
    }


    /**
     * Displays the details of the accident insurance policy.
     *
     * <p>This method overrides {@link InsurancePolicy#displayPolicyDetails()} to provide additional
     * information about the accident type and allowance if applicable.</p>
     */
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
