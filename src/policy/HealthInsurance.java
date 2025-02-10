package policy;
import java.time.LocalDate;

public class HealthInsurance extends InsurancePolicy {
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
