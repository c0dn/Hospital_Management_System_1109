package org.bee.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.PatientBuilder;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.EmergencyVisit;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A test class for the {@link InsuranceClaim} class.
 * This class verifies the functionality of insurance claim creation and processing.
 */
public class InsuranceClaimTest {
    private DataGenerator gen;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        gen = DataGenerator.getInstance();
        testPatient = new PatientBuilder()
                .withRandomBaseData()
                .patientId(gen.generatePatientId())
                .residentialStatus(ResidentialStatus.CITIZEN)
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .build();
    }

    static Stream<Arguments> provideInsuranceProviders() {
        return Stream.of(
            Arguments.of(new GovernmentProvider(), "Government Provider"),
            Arguments.of(new PrivateProvider(), "Private Provider")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInsuranceProviders")
    void testProviderClaimProcessing(InsuranceProvider provider, String providerName) {
        // Get policy for patient
        InsurancePolicy policy = provider.getPatientPolicy(testPatient)
                .orElseThrow(() -> new AssertionError("Failed to get policy for patient"));
        assertNotNull(policy, "Policy should not be null");

        // Create a visit and set its status
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);
        
        // Create bill using BillBuilder
        Bill bill = new BillBuilder<>()
                .withPatientId(testPatient.getPatientId())
                .withVisit(visit)
                .build();
        assertNotNull(bill, "Bill should not be null");

        // Create claim
        InsuranceClaim claim = InsuranceClaim.createNew(
                bill,
                provider,
                policy,
                testPatient,
                bill.getTotalAmount()
        );
        assertNotNull(claim, "Claim should not be null");

        // Verify initial state
        assertTrue(claim.isDraft(), "New claim should be in DRAFT status");

        // Submit claim
        claim.submit();
        assertTrue(claim.isSubmitted(), "Claim should be in SUBMITTED status after submit()");

        // Process claim
        boolean processed = provider.processClaim(testPatient, claim);
        assertTrue(processed, "Claim processing should succeed");

        // Verify final state
        assertTrue(claim.isApproved(), "Claim should be APPROVED after processing");
    }

    @Test
    void testBillBuilderWithDifferentRecords() {
        // Test with Visit
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);
        Bill visitBill = new BillBuilder<>()
                .withPatientId(testPatient.getPatientId())
                .withVisit(visit)
                .build();

        verifyBill(visitBill, "Visit");

        // Test with EmergencyVisit
        EmergencyVisit emergencyVisit = EmergencyVisit.withRandomData();
        emergencyVisit.updateStatus(VisitStatus.DISCHARGED);
        Bill emergencyBill = new BillBuilder<EmergencyVisit>()
                .withPatientId(testPatient.getPatientId())
                .withVisit(emergencyVisit)
                .build();

        verifyBill(emergencyBill, "Emergency Visit");

        // Test with Consultation
        Consultation consultation = Consultation.withRandomData();
        BigDecimal consultationCharges = consultation.calculateCharges();
        
        assertNotNull(consultationCharges, "Consultation charges should not be null");
        assertTrue(consultationCharges.compareTo(BigDecimal.ZERO) > 0,
                "Consultation charges should be positive");
    }

    @Test
    void testEmergencyVisitCharges() {
        EmergencyVisit emergencyVisit = EmergencyVisit.withRandomData();
        emergencyVisit.updateStatus(VisitStatus.DISCHARGED);

        Bill emergencyBill = new BillBuilder<EmergencyVisit>()
                .withPatientId(testPatient.getPatientId())
                .withVisit(emergencyVisit)
                .build();

        // Verify emergency-specific charges
        BigDecimal emergencyCharges = emergencyBill.getTotalByCategory("EMERGENCY");
        assertTrue(emergencyCharges.compareTo(BigDecimal.ZERO) > 0,
                "Emergency charges should be present and positive");
    }

    @Test
    void testConsultationOnlyBill() {
        Consultation consultation = Consultation.withRandomData();
        Bill consultationBill = new BillBuilder<Visit>()
                .withPatientId(testPatient.getPatientId())
                .withConsultation(consultation)
                .build();

        // Verify consultation-specific charges
        BigDecimal consultationCharges = consultationBill.getTotalByCategory("CONSULTATION");
        assertTrue(consultationCharges.compareTo(BigDecimal.ZERO) > 0,
                "Consultation charges should be present and positive");
    }

    private void verifyBill(Bill bill, String type) {
        assertNotNull(bill, type + " bill should not be null");
        assertTrue(bill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0,
                type + " bill should have positive charges");
        assertNotNull(bill.getPatient(), type + " bill should have associated patient");
        assertEquals(testPatient.getPatientId(), bill.getPatient().getPatientId(),
                type + " bill should be associated with test patient");
    }
}
