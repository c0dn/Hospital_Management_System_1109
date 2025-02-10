package tests;

import java.time.LocalDate;
import policy.*;

public class CriticalIllnessTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Critical Illness Insurance functionality...");

            // Test 1: Creating critical illness insurance policy using builder with specific data
            System.out.println("\nTest 1 - Creating critical illness policy with specific data:\n");
            InsurancePolicy specificPolicy = new CriticalIllnessBuilder()
                    .insuranceName("AIA Beyond Critical Care")
                    .provider("AIA")
                    .policyId("C123456789")
                    .insuranceDescription("This policy offers protection against critical illnesses like cancer, heart attack, stroke, etc.")
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .premium(2500.0)
                    .payout(150000.0)
                    .coveredIllness(CriticalIllnessType.BENIGN_BRAIN_TUMOUR)
                    .build();

            specificPolicy.displayPolicyDetails();

            // Test 2: Creating critical illness insurance policy using builder with random data
            System.out.println("\nTest 2 - Creating critical illness policy with random data:\n");
            InsurancePolicy randomPolicy = new CriticalIllnessBuilder()
                    .withRandomBaseData()
                    .build();

            randomPolicy.displayPolicyDetails();

            // Test 3: Creating multiple random policies to demonstrate variety
            System.out.println("\nTest 3 - Creating multiple random critical illness policies:\n");
            for (int i = 0; i < 3; i++) {
                System.out.println("\nRandom Policy " + (i + 1) + ":");
                InsurancePolicy policy = new CriticalIllnessBuilder()
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
