package tests;

import policy.*;

import java.time.LocalDate;

/**
 * The {@code AccidentTest} class is a test suite for verifying the functionality of accident insurance policies.
 * It tests the creation of an accident insurance policy using the builder pattern and displays the policy details.
 */
public class AccidentTest {
    /**
     * The main method to execute the accident insurance tests.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Accident Insurance functionality...");

            // Test: Creating an accident insurance policy using builder with specific data
            System.out.println("\nTest - Creating accident insurance policy using builder with specific data:");
            /**
             * Creates an accident insurance policy using the {@code AccidentBuilder}.
             */
            InsurancePolicy specificPolicy = new AccidentBuilder()
                    .insuranceName("AIA Accident Protection")
                    .provider("AIA")
                    .policyId("A123456789")
                    .insuranceDescription("This policy offers coverage for various types of accidents such as medical, disability, and death.")
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .premium(1500.0)
                    .payout(50000.0)
                    .accidents(AccidentsType.FRACTURE)
                    .allowance(100.0)  // Daily allowance for medical accidents
                    .build();

            // Display policy details
            specificPolicy.displayPolicyDetails();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
