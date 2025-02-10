package policy;
import java.time.LocalDate;

public class AccidentInsurance extends InsurancePolicy {
    private AccidentsType accidents;
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

    public AccidentsType getAccidents() {
        return accidents;
    }

    public double getAllowance() {
        return allowance;
    }
    // rejection reason
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
