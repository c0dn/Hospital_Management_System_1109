package policy;
import java.time.LocalDate;

/**
 * Represents an insurance policy associated with a patient.
 * It contains details such as insurance provider name, deductible and expiry date.
 */

public abstract class InsurancePolicy {
    /**
     * Unique identifier for the insurance policy.
     */
    private final String policyId;
    /**
     * Name of the insurance provider issuing the policy.
     */
    private String insuranceProvider;
    /**
     * The deductible amount the policyholder must pay before insurance coverage begins.
     */
    private double deductible;
    /**
     * The current status of the insurance policy (e.g., active, expired, pending).
     */
    private InsuranceStatus insuranceStatus;
    /**
     * The start date of the insurance policy.
     */
    private LocalDate startDate;
    /**
     * The end date of the insurance policy.
     */
    private LocalDate endDate;
    /**
     * The percentage of costs shared by the insured person after the deductible is met.
     */
    private double coInsuranceRate;
    /**
     * This is the amount the policyholder pays for the insurance policy
     */
    private double premiumAmount;

    private double insurancePayout;

    private String insuranceName;

    private String insuranceDescription;

    /**
     * Constructs an InsurancePolicy object using the provided InsuranceBuilder.
     * Package-private constructor, only accessible by builders in the same package.
     *
     * @param builder The builder object containing the data used to initialize
     *                the InsurancePolicy instance. Must include all required fields
     *                as validated by the builder's validateFields method.
     */
    InsurancePolicy(InsuranceBuilder<?> builder) {
        this.policyId = builder.policyId;
        this.insuranceProvider = builder.insuranceProvider;
        this.deductible = builder.deductible;
        this.insuranceStatus = builder.insuranceStatus;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.coInsuranceRate = builder.coInsuranceRate;
        this.premiumAmount = builder.premiumAmount;
        this.insurancePayout = builder.insurancePayout;
        this.insuranceName = builder.insuranceName;
        this.insuranceDescription = builder.insuranceDescription;
    }

    // getters

    /**
     * Retrieves the unique policy ID.
     *
     * @return The policy ID as a string.
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Retrieves the name of the insurance provider.
     *
     * @return The name of the insurance provider.
     */
    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    /**
     * Retrieves the deductible amount of the insurance policy.
     *
     * @return The deductible amount.
     */
    public double getDeductible() {
        return deductible;
    }

    /**
     * Retrieves the current status of the insurance policy.
     *
     * @return The insurance status.
     */
    public InsuranceStatus getInsuranceStatus() {
        return insuranceStatus;
    }

    /**
     * Retrieves the start date of the insurance policy.
     *
     * @return The start date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Retrieves the end date of the insurance policy.
     *
     * @return The end date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Retrieves the co-insurance rate.
     *
     * @return The co-insurance rate as a percentage.
     */
    public double getCoInsuranceRate() {
        return coInsuranceRate;
    }

    public double getPremiumAmount() {
        return premiumAmount;
    }

    public double getInsurancePayout() {
        return insurancePayout;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public String getInsuranceDescription() {
        return insuranceDescription;
    }

    public void displayPolicyDetails() {

        System.out.format("Policy Name: %s%n", insuranceName);
        System.out.format("Insurance Provider: %s%n", insuranceProvider);
        System.out.format("Policy ID: %s%n", policyId);
        System.out.format("Policy Description: %s%n", insuranceDescription);
        System.out.format("Insurance Status: %s%n", insuranceStatus);
        System.out.format("Start Date: %s%n", startDate);
        System.out.format("End Date: %s%n", endDate);
        System.out.format("Premium Amount: $%.2f%n", premiumAmount);
    }
}
