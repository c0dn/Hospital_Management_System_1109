package tests;

import policy.*;
import humans.Patient;
import humans.PatientBuilder;
import java.time.LocalDate;

public class InsuranceTest {
    public static void main(String[] args) {
        try {
            System.out.println("\nTesting Insurance Policy functionality...\n");

            Patient policyHolder = new PatientBuilder()
                    .withRandomData("PAT123456")
                    .build();

            // Test 1: Creating insurance policy using builder with random data
            System.out.println("Test 1 - Creating insurance policy using builder with random data:");
            InsurancePolicy randomPolicy = new InsuranceBuilder()
                    .withRandomData(policyHolder)
                    .build();

            randomPolicy.displayPolicyDetails();

            // Test 2: Creating insurance policy using builder with specific data
            System.out.println("\nTest 2 - Creating insurance policy using builder with specific data:");
            InsurancePolicy specificPolicy = new InsuranceBuilder()
                    .policyId("POL123456")
                    .provider("Great Eastern")
                    .deductible(2000.0)
                    .status(InsuranceStatus.ACTIVE)
                    .startDate(LocalDate.of(2025, 1, 1))
                    .endDate(LocalDate.of(2025, 12, 31))
                    .coInsuranceRate(0.2)
                    .premium(800.0)
                    .payout(25000.0)
                    .policyHolder(policyHolder)
                    .build();

            specificPolicy.displayPolicyDetails();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

