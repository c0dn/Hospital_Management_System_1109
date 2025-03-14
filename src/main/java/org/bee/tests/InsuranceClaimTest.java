package org.bee.tests;

import java.math.BigDecimal;
import java.time.LocalDate;

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

/**
 * A test class for the {@link InsuranceClaim} class.
 * This class verifies the functionality of insurance claim creation and processing.
 */
public class InsuranceClaimTest {
    /**
     * Main method to execute tests for {@link InsuranceClaim}.
     * It tests creating and processing insurance claims with different providers.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing InsuranceClaim functionality...\n");

            // Test 1: Test claim creation and processing with Government Provider
            System.out.println("Test 1 - Testing Government Provider claim processing:");
            testProviderClaimProcessing(new GovernmentProvider());

            // Test 2: Test claim creation and processing with Private Provider
            System.out.println("\nTest 2 - Testing Private Provider claim processing:");
            testProviderClaimProcessing(new PrivateProvider());

            // Test 3: Test BillBuilder with different medical records
            System.out.println("\nTest 3 - Testing BillBuilder with different medical records:");
            testBillBuilderWithDifferentRecords();

            System.out.println("\nAll tests passed successfully!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testProviderClaimProcessing(InsuranceProvider provider) {
        // Create a test patient
        DataGenerator gen = DataGenerator.getInstance();
        Patient patient = new PatientBuilder()
                .withRandomBaseData()
                .patientId(gen.generatePatientId())
                .residentialStatus(ResidentialStatus.CITIZEN)
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .build();
        // Get policy for patient
        InsurancePolicy policy = provider.getPatientPolicy(patient)
                .orElseThrow(() -> new AssertionError("Failed to get policy for patient"));

        // Create a visit for testing
        Visit visit = Visit.withRandomData();

        visit.updateStatus(VisitStatus.DISCHARGED);
        
        // Create bill using BillBuilder
        Bill bill = new BillBuilder<>()
                .withPatientId(patient.getPatientId())
                .withVisit(visit)
                .build();

        // Create claim
        InsuranceClaim claim = InsuranceClaim.createNew(
                bill,
                provider,
                policy,
                patient,
                bill.getTotalAmount()
        );

        // Verify initial state
        if (!claim.isDraft()) {
            throw new AssertionError("New claim should be in DRAFT status");
        }

        // Submit claim
        claim.submit();
        if (!claim.isSubmitted()) {
            throw new AssertionError("Claim should be in SUBMITTED status after submit()");
        }

        // Process claim
        boolean processed = provider.processClaim(patient, claim);
        if (!processed) {
            throw new AssertionError("Claim processing failed");
        }

        // Verify final state
        if (!claim.isApproved()) {
            throw new AssertionError("Claim should be APPROVED after processing");
        }

        System.out.println("Successfully processed claim with " + provider.getProviderName());
        claim.displayClaimInfo();
    }

    private static void testBillBuilderWithDifferentRecords() {
        Patient patient = Patient.builder()
                .withRandomData("P1002")
                .build();

        // Test with Visit
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);
        Bill visitBill = new BillBuilder<>()
                .withPatientId(patient.getPatientId())
                .withVisit(visit)
                .build();
        System.out.println("Visit Bill Total: $" + visitBill.getTotalAmount());

        // Test with EmergencyVisit
        EmergencyVisit emergencyVisit = EmergencyVisit.withRandomData();
        emergencyVisit.updateStatus(VisitStatus.DISCHARGED);

        Bill emergencyBill = new BillBuilder<EmergencyVisit>()
                .withPatientId(patient.getPatientId())
                .withVisit(emergencyVisit)
                .build();
        System.out.println("Emergency Visit Bill Total: $" + emergencyBill.getTotalAmount());

        // Test with Consultation (using consultation's calculateCharges directly since it's not a Visit)
        Consultation consultation = Consultation.withRandomData();
        BigDecimal consultationCharges = consultation.calculateCharges();
        System.out.println("Consultation Total: $" + consultationCharges);

        // Verify all bills have positive charges
        verifyPositiveCharges("Visit", visitBill.getTotalAmount());
        verifyPositiveCharges("Emergency Visit", emergencyBill.getTotalAmount());
        verifyPositiveCharges("Consultation", consultationCharges);
    }

    private static void verifyPositiveCharges(String type, BigDecimal charges) {
        if (charges.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssertionError(type + " bill should have positive charges");
        }
        System.out.println("Verified " + type + " bill has valid charges: $" + charges);
    }
}
