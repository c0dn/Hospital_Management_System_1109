package policy;
import java.time.LocalDate;

/**
 * Represents a Health Insurance policy that extends {@link InsurancePolicy}.
 */
public class HealthInsurance extends InsurancePolicy {
    /** The maximum coverage amount for hospital charges under this policy. */
    private double hospitalCharges;

    /**
     * Package-private constructor that accepts only a HealthInsuranceBuilder
     * to enforce the builder pattern.
     *
     * @param builder The builder object containing initialization data
     */
    HealthInsurance(HealthInsuranceBuilder builder) {
        super(builder);
        this.hospitalCharges = builder.hospitalCharges;
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
