package policy;
import humans.Patient;
import java.time.LocalDate;

/**
 * Represents an insurance policy associated with a patient.
 * It contains details such as insurance provider name, deductible and expiry date.
 */

public class InsurancePolicy {
    /** Unique identifier for the insurance policy. */
    private final String policyId;
    /** Name of the insurance provider issuing the policy. */
    private String insuranceProvider;
    /** The deductible amount the policyholder must pay before insurance coverage begins. */
    private double deductible;
    /** The current status of the insurance policy (e.g., active, expired, pending). */
    private InsuranceStatus insuranceStatus;
    /** The start date of the insurance policy. */
    private LocalDate startDate;
    /** The end date of the insurance policy. */
    private LocalDate endDate;
    /** The percentage of costs shared by the insured person after the deductible is met. */
    private double coInsuranceRate;
    /** This is the amount the policyholder pays for the insurance policy */
    private double premiumAmount;

    private Patient policyHolder;

    private double insurancePayout;
    /**
     * Constructs an InsurancePolicy object with the specified details.
     *
     * @param policyId Unique identifier for the insurance policy.
     * @param insuranceProvider Name of the insurance provider.
     * @param deductible Deductible amount before coverage applies.
     * @param insuranceStatus Current status of the insurance policy.
     * @param startDate Start date of the insurance coverage.
     * @param endDate End date of the insurance coverage.
     * @param coInsuranceRate Co-insurance rate, representing the cost-sharing percentage.
     */
    public InsurancePolicy(String policyId, String insuranceProvider, double deductible,
                           InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                           double coInsuranceRate, double premiumAmount, Patient policyHolder, double insurancePayout) {

        this.policyId = policyId;
        this.insuranceProvider = insuranceProvider;
        this.deductible = deductible;
        this.insuranceStatus = insuranceStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coInsuranceRate = coInsuranceRate;
        this.premiumAmount = premiumAmount;
        this.policyHolder = policyHolder;
        this.insurancePayout = insurancePayout;
    }

    // getters
    /**
     * Retrieves the unique policy ID.
     *
     * @return The policy ID as a string.
     */
    public String getPolicyId() { return policyId; }

    /**
     * Retrieves the name of the insurance provider.
     *
     * @return The name of the insurance provider.
     */
    public String getInsuranceProvider() { return insuranceProvider; }

    /**
     * Retrieves the deductible amount of the insurance policy.
     *
     * @return The deductible amount.
     */
    public double getDeductible() { return deductible; }

    /**
     * Retrieves the current status of the insurance policy.
     *
     * @return The insurance status.
     */
    public InsuranceStatus getInsuranceStatus() { return insuranceStatus; }

    /**
     * Retrieves the start date of the insurance policy.
     *
     * @return The start date.
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * Retrieves the end date of the insurance policy.
     *
     * @return The end date.
     */
    public LocalDate getEndDate() { return endDate; }

    /**
     * Retrieves the co-insurance rate.
     *
     * @return The co-insurance rate as a percentage.
     */
    public double getCoInsuranceRate() { return coInsuranceRate; }

    public double getPremiumAmount() { return premiumAmount; }

    public Patient getPolicyholder() { return policyHolder; }

    public double getInsurancePayout() {
        return insurancePayout;
    }

    public void displayPolicyDetails(){
        System.out.format("Name: %s%n", policyHolder);
        System.out.format("Policy ID: %s%n", policyId);
        System.out.format("Insurance Provider: %s%n", insuranceProvider);
        System.out.format("Insurance Status: %s%n", insuranceStatus);
        System.out.format("Start Date: %s%n", startDate);
        System.out.format("End Date: %s%n", endDate);
        System.out.format("Premium Amount: $%.2f%n", premiumAmount);
    }
}
