package policy;
import humans.Patient;
import java.time.LocalDate;

/**
 * Represents an insurance policy associated with a patient.
 * It contains details such as insurance provider name, deductible and expiry date.
 */

public class InsurancePolicy {
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
     * Constructs an InsurancePolicy object with the specified details.
     *
     * @param policyId          Unique identifier for the insurance policy.
     * @param insuranceProvider Name of the insurance provider.
     * @param deductible        Deductible amount before coverage applies.
     * @param insuranceStatus   Current status of the insurance policy.
     * @param startDate         Start date of the insurance coverage.
     * @param endDate           End date of the insurance coverage.
     * @param coInsuranceRate   Co-insurance rate, representing the cost-sharing percentage.
     */
    public InsurancePolicy(String policyId, String insuranceProvider, double deductible,
                           InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                           double coInsuranceRate, double premiumAmount, double insurancePayout,
                           String insuranceName, String insuranceDescription) {

        this.policyId = policyId;
        this.insuranceProvider = insuranceProvider;
        this.deductible = deductible;
        this.insuranceStatus = insuranceStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coInsuranceRate = coInsuranceRate;
        this.premiumAmount = premiumAmount;
        this.insurancePayout = insurancePayout;
        this.insuranceName = insuranceName;
        this.insuranceDescription = insuranceDescription;
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

        // line
//        System.out.format("Policy Name: %s%n", insuranceName);
//        System.out.format("Insurance Provider: %s%n", insuranceProvider);
//        System.out.format("Policy ID: %s%n", policyId);
//        System.out.format("Policy Description: %s%n", insuranceDescription);
//        System.out.format("Insurance Status: %s%n", insuranceStatus);
//        System.out.format("Start Date: %s%n", startDate);
//        System.out.format("End Date: %s%n", endDate);
//        System.out.format("Premium Amount: $%.2f%n", premiumAmount);

        // table
//        System.out.printf("%-25s %-27s %-15s %-30s %-20s %-12s %-12s %-20.2f", insuranceName, insuranceProvider, policyId,
//                insuranceDescription, insuranceStatus, startDate, endDate, premiumAmount);

        System.out.printf("%n");
        System.out.println("=====================================================================");
        System.out.printf("                       INSURANCE POLICY DETAILS%n");
        System.out.println("=====================================================================");
        System.out.println("POLICY DETAILS");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Policy ID: " + policyId);
        System.out.println("Policy Name: " + insuranceName);
        System.out.printf("Description: ");
        wrapTextByWords(insuranceDescription, 50);
        System.out.printf("%n");
        System.out.println("INSURANCE DETAILS");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-27s %-19s %-14s%n", "Insurance Provider:", "Start Date:", "End Date:");
        System.out.printf("%-27s %-19s %-14s %n%n", insuranceProvider, startDate, endDate);
        System.out.printf("%-23s %-20s%n", "Insurance Status:", "Premium Amount:");
        System.out.printf("%-23s %-15.2f%n%n", insuranceStatus, premiumAmount);

//        System.out.printf("%n");
//        System.out.println("================================================================================");
//        System.out.printf("INSURANCE POLICY DETAILS%n");
//        System.out.println("================================================================================");
//        System.out.println("INSURANCE DETAILS");
//        System.out.println("--------------------------------------------------------------------------------");
//        System.out.printf("%-31s %-27s%n", "Policy Name:", "Insurance Provider:");
//        System.out.printf("%-31s %-27s%n%n", insuranceName, insuranceProvider);
//        System.out.printf("%-16s %-14s %-23s %-20s%n", "Start Date:", "End Date:", "Insurance Status:", "Premium Amount:");
//        System.out.printf("%-16s %-14s %-23s %-15.2f%n%n", startDate, endDate, insuranceStatus, premiumAmount);
//        System.out.println("POLICY DETAILS");
//        System.out.println("--------------------------------------------------------------------------------");
//        System.out.println("Policy ID: " + policyId);
//        System.out.printf("Description: ");
//        wrapTextByWords(insuranceDescription, 75);
//        System.out.printf("%n");
    }
    //table
//    public void printHeaders() {
//        System.out.printf("%-25s %-27s %-15s %-30s %-20s %-12s %-12s %-20s",
//                "Policy Name", "Insurance Provider", "Policy ID", "Policy Description",
//                "Insurance Status", "Start Date", "End Date", "Premium Amount");
//    }
    public static void wrapTextByWords(String text, int maxLineLength) {
        String[] words = text.split("\\s+"); // Split text into words
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            // Check if adding the next word exceeds maxLineLength
            if (line.length() + word.length() + 1 > maxLineLength) {
                System.out.println(line.toString().trim()); // Print current line
                line.setLength(0); // Clear the line
            }
            line.append(word).append(" "); // Add word to line
        }

        // Print any remaining words in the last line
        if (!line.isEmpty()) {
            System.out.println("             " + line.toString().trim());
        }
    }


}
