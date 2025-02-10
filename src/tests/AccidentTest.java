package tests;

import java.time.LocalDate;
import policy.*;

public class AccidentTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Accident Insurance functionality...");

            // Test 1: Creating accident insurance policy using builder with specific data
            System.out.println("\nTest 1 - Creating accident insurance policy with specific data:\n");
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

            specificPolicy.displayPolicyDetails();

            // Test 2: Creating accident insurance policy using builder with random data
            System.out.println("\nTest 2 - Creating accident insurance policy with random data:\n");
            InsurancePolicy randomPolicy = new AccidentBuilder()
                    .withRandomBaseData()
                    .build();

            randomPolicy.displayPolicyDetails();

            // Test 3: Creating multiple random policies to demonstrate variety
            System.out.println("\nTest 3 - Creating multiple random accident policies:\n");
            for (int i = 0; i < 3; i++) {
                System.out.println("\nRandom Policy " + (i + 1) + ":");
                InsurancePolicy policy = new AccidentBuilder()
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
