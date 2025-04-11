package org.bee.tests;

import java.time.LocalDate;
import java.util.Optional;

import org.bee.hms.humans.Patient;
import org.bee.hms.humans.PatientBuilder;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link GovernmentProvider} class.
 * This class verifies the functionality of government insurance policy assignment.
 */
public class GovernmentProviderTest {
    private GovernmentProvider provider;

    @BeforeEach
    void setUp() {
        provider = new GovernmentProvider();
    }

    @Test
    void testNonEligiblePatient() {
        // Test non-eligible patient (foreigner)
        Patient foreigner = Patient.builder()
                .withRandomBaseData()
                .patientId(DataGenerator.generatePatientId())
                .residentialStatus(ResidentialStatus.VISITOR)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        Optional<InsurancePolicy> foreignerPolicy = provider.getPatientPolicy(foreigner);
        assertFalse(foreignerPolicy.isPresent(), 
                "Foreigner should not be eligible for government insurance");
    }

    @Test
    void testOlderSingaporean() {
        // Test Singaporean born before 1980 (only MediShield)
        Patient oldSingaporean = new PatientBuilder()
                .withRandomBaseData()
                .patientId(DataGenerator.generatePatientId())
                .residentialStatus(ResidentialStatus.CITIZEN)
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .build();

        Optional<InsurancePolicy> oldSingaporeanPolicy = provider.getPatientPolicy(oldSingaporean);
        assertTrue(oldSingaporeanPolicy.isPresent(), 
                "Singaporean should be eligible for government insurance");

        InsurancePolicy policy = oldSingaporeanPolicy.get();
        assertEquals("Government base policy", policy.getPolicyName(),
                "Older Singaporean should have correct base policy");
    }

    @Test
    void testYoungPR() {
        // Test PR born after 1980 (both MediShield and CareShield)
        Patient youngPR = new PatientBuilder()
                .withRandomBaseData()
                .patientId(DataGenerator.generatePatientId())
                .residentialStatus(ResidentialStatus.PERMANENT_RESIDENT)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        Optional<InsurancePolicy> youngPRPolicy = provider.getPatientPolicy(youngPR);
        assertTrue(youngPRPolicy.isPresent(),
                "Young PR should be eligible for government insurance");

        InsurancePolicy policy = youngPRPolicy.get();
        assertEquals("Government base policy", policy.getPolicyName(),
                "Young PR should have correct composite policy");
    }

    @Test
    void testPolicyIdFormat() {
        // Create a test patient that will get a policy
        Patient patient = new PatientBuilder()
                .withRandomBaseData()
                .patientId(DataGenerator.generatePatientId())
                .residentialStatus(ResidentialStatus.CITIZEN)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        Optional<InsurancePolicy> policy = provider.getPatientPolicy(patient);
        assertTrue(policy.isPresent(), "Policy should be present");

        String policyId = policy.get().getPolicyNumber();
        assertNotNull(policyId, "Policy ID should not be null");
        assertTrue(policyId.matches("GOVT-\\d{10}-.*"),
                "Policy ID should match format GOVT-XXXXXXXXXX-*: " + policyId);
    }
}
