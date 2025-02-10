package tests;

import policy.*;

import java.time.LocalDate;

public class HealthInsuranceTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Insurance Policy functionality...");

            // Test: Creating insurance policy using builder with specific data
            System.out.println("\nTest - Creating insurance policy using builder with specific data:\n");
            InsurancePolicy specificPolicy = new HealthInsuranceBuilder()
                    .insuranceName("AIA HealthShield Gold (HSG) Max")
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

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

