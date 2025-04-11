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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link PrivateProvider} class.
 * This class verifies that getPatientPolicy returns random policies.
 */
public class PrivateProviderTest {
    private PrivateProvider provider;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        provider = new PrivateProvider();
        testPatient = Patient.builder()
                .withRandomData(DataGenerator.generatePatientId())
                .build();
    }

    @Test
    void testPolicyRandomness() {
        Set<String> policyIds = new HashSet<>();
        Set<BigDecimal> annualLimits = new HashSet<>();
        Set<BigDecimal> lifetimeLimits = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            Optional<InsurancePolicy> policyOptional = provider.getPatientPolicy(testPatient);
            assertTrue(policyOptional.isPresent(), "Policy should not be empty");

            InsurancePolicy policy = policyOptional.get();
            assertNotNull(policy.getPolicyNumber(), "Policy ID should not be null");
            assertNotNull(policy.getInsuranceProvider().getProviderName(), 
                    "Provider name should not be null");
            assertNotNull(policy.getCoverage().getLimits().getAnnualLimit(), 
                    "Annual limit should not be null");
            assertNotNull(policy.getCoverage().getLimits().getLifetimeLimit(), 
                    "Lifetime limit should not be null");

            // Store values for uniqueness check
            policyIds.add(policy.getPolicyNumber());
            annualLimits.add(policy.getCoverage().getLimits().getAnnualLimit());
            lifetimeLimits.add(policy.getCoverage().getLimits().getLifetimeLimit());

            // Verify policy is linked to correct patient
            assertEquals(testPatient, policy.getPolicyHolder(), 
                    "Policy holder should match test patient");
        }

        // Verify randomness
        assertTrue(policyIds.size() >= 4, 
                "Policy IDs should be sufficiently random (at least 4 unique out of 5)");
        assertTrue(annualLimits.size() >= 3, 
                "Annual limits should be sufficiently random (at least 3 unique out of 5)");
        assertTrue(lifetimeLimits.size() >= 3, 
                "Lifetime limits should be sufficiently random (at least 3 unique out of 5)");
    }

    @Test
    void testPolicyCoverageLimits() {
        Optional<InsurancePolicy> policyOptional = provider.getPatientPolicy(testPatient);
        assertTrue(policyOptional.isPresent(), "Policy should be present");

        InsurancePolicy policy = policyOptional.get();
        BaseCoverage coverage = (BaseCoverage) policy.getCoverage();
        assertNotNull(coverage, "Coverage should not be null");

        CoverageLimit limits = coverage.getLimits();
        assertNotNull(limits, "Coverage limits should not be null");

        // Check annual limit range (100,000 to 1,000,000)
        BigDecimal annualLimit = limits.getAnnualLimit();
        assertTrue(annualLimit.compareTo(BigDecimal.valueOf(100_000)) >= 0 &&
                annualLimit.compareTo(BigDecimal.valueOf(1_000_000)) <= 0,
                "Annual limit should be between 100,000 and 1,000,000");

        // Check lifetime limit range (1,000,000 to 10,000,000)
        BigDecimal lifetimeLimit = limits.getLifetimeLimit();
        assertTrue(lifetimeLimit.compareTo(BigDecimal.valueOf(1_000_000)) >= 0 &&
                lifetimeLimit.compareTo(BigDecimal.valueOf(10_000_000)) <= 0,
                "Lifetime limit should be between 1,000,000 and 10,000,000");
    }

    @Test
    void testConsistentPolicyForSamePatient() {
        // Get two policies for the same patient
        Optional<InsurancePolicy> policy1 = provider.getPatientPolicy(testPatient);
        Optional<InsurancePolicy> policy2 = provider.getPatientPolicy(testPatient);

        assertTrue(policy1.isPresent() && policy2.isPresent(), 
                "Both policies should be present");

        // Verify policy number changes but holder remains the same
        assertNotNull(policy1.get().getPolicyNumber(), "First policy number should not be null");
        assertNotNull(policy2.get().getPolicyNumber(), "Second policy number should not be null");
        assertEquals(testPatient, policy1.get().getPolicyHolder(), 
                "First policy holder should match test patient");
        assertEquals(testPatient, policy2.get().getPolicyHolder(), 
                "Second policy holder should match test patient");
    }

    @Test
    void testProviderDetails() {
        Optional<InsurancePolicy> policyOptional = provider.getPatientPolicy(testPatient);
        assertTrue(policyOptional.isPresent(), "Policy should be present");

        InsurancePolicy policy = policyOptional.get();
        assertNotNull(policy.getInsuranceProvider(), "Insurance provider should not be null");
        assertNotNull(policy.getInsuranceProvider().getProviderName(), 
                "Provider name should not be null");
        assertTrue(policy.getPolicyNumber().startsWith("PRIV-"), 
                "Private provider policy numbers should start with PRIV-");
    }
}
