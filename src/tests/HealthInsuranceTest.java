package tests;

import java.time.LocalDate;
import policy.*;

/**
 * A test class for the {@link HealthInsuranceBuilder} class.
 * This class verifies the creation and functionality of health insurance policies.
 */
public class HealthInsuranceTest {
    /**
     * The main method executes tests for health insurance policies.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Health Insurance functionality...");

            // Test 1: Creating insurance policy using builder with specific data
            System.out.println("\nTest 1 - Creating health insurance with specific data:\n");
            InsurancePolicy specificPolicy = new HealthInsuranceBuilder()
                    .insuranceName("AIA HealthShield Gold Max")
                    .provider("AIA")
                    .policyId("H123456789")
                    .insuranceDescription("AIA HealthShield Gold Max offers protection against medical bills arising from hospitalisation, pre- and post-hospitalisation treatments and selected outpatient treatments.")
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .coInsuranceRate(0.05)
                    .premium(1199.00)
                    .deductible(3000.0)
                    .hospitalCharges(15000)
                    .build();

            specificPolicy.displayPolicyDetails();

            // Test 2: Creating insurance policy using builder with random data
            System.out.println("\nTest 2 - Creating health insurance with random data:\n");
            InsurancePolicy randomPolicy = new HealthInsuranceBuilder()
                    .withRandomBaseData()
                    .build();

            randomPolicy.displayPolicyDetails();

            // Test 3: Creating multiple random policies to demonstrate variety
            System.out.println("\nTest 3 - Creating multiple random health insurance policies:\n");
            for (int i = 0; i < 3; i++) {
                System.out.println("\nRandom Policy " + (i + 1) + ":");
                InsurancePolicy policy = new HealthInsuranceBuilder()
                        .withRandomBaseData()
                        .build();
                policy.displayPolicyDetails();
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
