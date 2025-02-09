package tests;

import policy.*;

import java.time.LocalDate;

public class HealthInsuranceTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Insurance Policy functionality...");

            // Test: Creating insurance policy using builder with specific data
            System.out.println("\nTest - Creating insurance policy using builder with specific data:");
            InsurancePolicy specificPolicy = new HealthInsuranceBuilder()
                    .insuranceName("AIA HealthShield Gold (HSG) Max")
                    .policyId("POL123456")
                    .provider("AIA")
                    .insuranceDescription("AIA HealthShield Gold Max offers protection against medical bills arising from hospitalisation, pre- and post-hospitalisation treatments and selected outpatient treatments.")
                    .deductible(3000.0)
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .coInsuranceRate(0.05)
                    .premium(1199.00)
                    .payout(25000.0)
                    .hospitalCharges(6000)
                    .build();

            specificPolicy.displayPolicyDetails();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

