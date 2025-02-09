package tests;

import policy.*;

import java.time.LocalDate;

public class AccidentTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Accident Insurance functionality...");

            // Test: Creating an accident insurance policy using builder with specific data
            System.out.println("\nTest - Creating accident insurance policy using builder with specific data:");
            InsurancePolicy specificPolicy = new AccidentBuilder()
                    .insuranceName("AIA Accident Protection")
                    .policyId("POL987654")
                    .provider("AIA")
                    .insuranceDescription("This policy offers coverage for various types of accidents such as medical, disability, and death.")
                    .deductible(2000.0)
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .coInsuranceRate(0.15)
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
