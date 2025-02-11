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
     * Constructs an AccidentInsurance policy with the specified details.
     *
     * @param policyId          The unique identifier of the policy.
     * @param insuranceProvider The name of the insurance provider.
     * @param deductible        The deductible amount for the policy.
     * @param insuranceStatus   The current status of the insurance policy.
     * @param startDate         The start date of the policy coverage.
     * @param endDate           The end date of the policy coverage.
     * @param coInsuranceRate   The co-insurance rate applied to the policy.
     * @param premiumAmount     The premium amount to be paid.
     * @param insurancePayout   The payout amount provided in case of an accident.
     * @param accidents         The type of accident covered under this policy.
     * @param allowance         The daily allowance provided for medical-related accidents.
     * @param insuranceName     The name of the insurance policy.
     * @param insuranceDescription A description of the insurance policy.
     */
    public AccidentInsurance(String policyId, String insuranceProvider, double deductible, InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate, double coInsuranceRate, double premiumAmount, double insurancePayout, AccidentsType accidents, double allowance, String insuranceName, String insuranceDescription) {
        super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, insurancePayout, insuranceName,  insuranceDescription);
        this.accidents = accidents;
        this.allowance = allowance;
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
    // rejection reason
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