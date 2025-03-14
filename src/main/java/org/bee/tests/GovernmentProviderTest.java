package org.bee.tests;

import java.time.LocalDate;
import java.util.Optional;

import org.bee.hms.humans.Patient;
import org.bee.hms.humans.PatientBuilder;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;

/**
 * A test class for the {@link GovernmentProvider} class.
 * This class verifies the functionality of government insurance policy assignment.
 */
public class GovernmentProviderTest {
    /**
     * Main method to execute tests for {@link GovernmentProvider}.
     * It tests policy assignment based on different patient criteria.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing GovernmentProvider functionality...\n");

            GovernmentProvider provider = new GovernmentProvider();

            // Test 1: Non-eligible patient (foreigner)
            System.out.println("Test 1 - Testing non-eligible patient (foreigner):");
            Patient foreigner = Patient.builder()
                    .withRandomBaseData()
                    .patientId(DataGenerator.getInstance().generatePatientId())
                    .residentialStatus(ResidentialStatus.VISITOR)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .build();
            Optional<InsurancePolicy> foreignerPolicy = provider.getPatientPolicy(foreigner);
            
            if (foreignerPolicy.isPresent()) {
                throw new AssertionError("Foreigner should not be eligible for government insurance");
            }
            System.out.println("Verified foreigner is not eligible for government insurance");

            // Test 2: Singaporean born before 1980 (only MediShield)
            System.out.println("\nTest 2 - Testing Singaporean born before 1980:");
            Patient oldSingaporean = new PatientBuilder()
                    .withRandomBaseData()
                    .patientId(DataGenerator.getInstance().generatePatientId())
                    .residentialStatus(ResidentialStatus.CITIZEN)
                    .dateOfBirth(LocalDate.of(1970, 1, 1))
                    .build();
            Optional<InsurancePolicy> oldSingaporeanPolicy = provider.getPatientPolicy(oldSingaporean);
            
            if (oldSingaporeanPolicy.isEmpty()) {
                throw new AssertionError("Singaporean should be eligible for government insurance");
            }

            String oldPolicyDesc = oldSingaporeanPolicy.get().getPolicyName();
            if (!oldPolicyDesc.equals("Government base policy")) {
                throw new AssertionError("Unexpected policy description: " + oldPolicyDesc);
            }
            System.out.println("Verified older Singaporean has correct base policy");

            // Test 3: PR born after 1980 (both MediShield and CareShield)
            System.out.println("\nTest 3 - Testing PR born after 1980:");
            Patient youngPR = new PatientBuilder()
                    .withRandomBaseData()
                    .patientId(DataGenerator.getInstance().generatePatientId())
                    .residentialStatus(ResidentialStatus.PERMANENT_RESIDENT)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .build();
            Optional<InsurancePolicy> youngPRPolicy = provider.getPatientPolicy(youngPR);
            
            if (youngPRPolicy.isEmpty()) {
                throw new AssertionError("Young PR should be eligible for government insurance");
            }
            
            String youngPolicyDesc = youngPRPolicy.get().getPolicyName();
            if (!youngPolicyDesc.equals("Government base policy")) {
                throw new AssertionError("Unexpected policy description: " + youngPolicyDesc);
            }
            System.out.println("Verified young PR has correct composite policy");

            // Test 4: Verify policy ID format
            System.out.println("\nTest 4 - Verifying policy ID format:");
            String policyId = youngPRPolicy.get().getPolicyNumber();
            if (!policyId.matches("GOVT-\\d{10}-.*")) {
                throw new AssertionError("Invalid policy ID format: " + policyId);
            }
            System.out.println("Verified policy ID format is correct: " + policyId);

            System.out.println("\nAll tests passed successfully!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
