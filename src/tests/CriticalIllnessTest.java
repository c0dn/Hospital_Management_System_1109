package tests;

import policy.*;

import java.time.LocalDate;

public class CriticalIllnessTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Critical Illness Insurance functionality...");

            // Test: Creating a critical illness insurance policy using builder with specific data
            System.out.println("\nTest - Creating critical illness insurance policy using builder with specific data:");
            InsurancePolicy specificPolicy = new CriticalIllnessBuilder()
                    .insuranceName("AIA Beyond Critical Care")
                    .provider("AIA")
                    .policyId("H123456789")
                    .insuranceDescription("This policy offers protection against critical illnesses like cancer, heart attack, stroke, etc.")
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .premium(2500.0)
                    .payout(150000.0)
                    .coveredIllness(CriticalIllnessType.BENIGN_BRAIN_TUMOUR)
                    .build();

            specificPolicy.displayPolicyDetails();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
