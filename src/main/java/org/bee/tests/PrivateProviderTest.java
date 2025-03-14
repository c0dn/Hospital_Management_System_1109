package org.bee.tests;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.bee.hms.humans.Patient;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.policy.BaseCoverage;
import org.bee.hms.policy.CoverageLimit;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;

/**
 * A test class for the {@link PrivateProvider} class.
 * This class verifies that getPatientPolicy returns random policies.
 */
public class PrivateProviderTest {
    /**
     * Main method to execute tests for {@link PrivateProvider}.
     * It tests the randomness of generated insurance policies.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing PrivateProvider getPatientPolicy functionality...\n");

            PrivateProvider provider = new PrivateProvider();
            Patient testPatient = Patient.builder().withRandomData(DataGenerator.getInstance().generatePatientId()).build();

            // Test 1: Verify multiple policies are different
            System.out.println("Test 1 - Verifying policy randomness:");
            Set<String> policyIds = new HashSet<>();
            Set<BigDecimal> annualLimits = new HashSet<>();
            Set<BigDecimal> lifetimeLimits = new HashSet<>();

            for (int i = 0; i < 5; i++) {
                Optional<InsurancePolicy> policyOptional = provider.getPatientPolicy(testPatient);

                if (policyOptional.isEmpty()) {
                    throw new AssertionError("Policy should not be empty");
                }

                InsurancePolicy policy = policyOptional.get();
                System.out.println("\nPolicy " + (i + 1) + ":");
                System.out.println("Policy ID: " + policy.getPolicyNumber());
                System.out.println("Provider Name: " + policy.getInsuranceProvider().getProviderName());
                System.out.println("Annual Limit: $" + policy.getCoverage().getLimits().getAnnualLimit());
                System.out.println("Lifetime Limit: $" + policy.getCoverage().getLimits().getLifetimeLimit());

                // Store values for uniqueness check
                policyIds.add(policy.getPolicyNumber());
                annualLimits.add(policy.getCoverage().getLimits().getAnnualLimit());
                lifetimeLimits.add(policy.getCoverage().getLimits().getLifetimeLimit());

                // Verify policy is linked to correct patient
                if (!policy.getPolicyHolder().equals(testPatient)) {
                    throw new AssertionError("Policy holder does not match test patient");
                }
            }

            // Verify we got different values
            if (policyIds.size() < 4) {
                throw new AssertionError("Policy IDs are not sufficiently random");
            }
            if (annualLimits.size() < 3) {
                throw new AssertionError("Annual limits are not sufficiently random");
            }
            if (lifetimeLimits.size() < 3) {
                throw new AssertionError("Lifetime limits are not sufficiently random");
            }

            // Test 2: Verify policy components
            Optional<InsurancePolicy> policyOptional = provider.getPatientPolicy(testPatient);
            InsurancePolicy policy = policyOptional.get();
            BaseCoverage coverage = (BaseCoverage) policy.getCoverage();

            // Check coverage limits
            CoverageLimit limits = coverage.getLimits();
            if (limits.getAnnualLimit().compareTo(BigDecimal.valueOf(100_000)) < 0 ||
                    limits.getAnnualLimit().compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
                throw new AssertionError("Annual limit out of expected range");
            }

            if (limits.getLifetimeLimit().compareTo(BigDecimal.valueOf(1_000_000)) < 0 ||
                    limits.getLifetimeLimit().compareTo(BigDecimal.valueOf(10_000_000)) > 0) {
                throw new AssertionError("Lifetime limit out of expected range");
            }

            System.out.println("\nAll tests passed successfully!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
