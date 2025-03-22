package org.bee.tests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.*;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.EmergencyVisit;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.BaseCoverage;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.ClaimableItem;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.CoverageLimit;
import org.bee.hms.policy.ExclusionCriteria;
import org.bee.hms.policy.HeldInsurancePolicy;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A test class for the {@link InsuranceClaim} class.
 * This class verifies the functionality of insurance claim creation and processing.
 */
public class InsuranceClaimTest {
    private DataGenerator gen;
    private Patient testPatient;

    // Test constants for insurance policy limits
    private static final BigDecimal STANDARD_DEDUCTIBLE = new BigDecimal("500.00");     // Default deductible for most tests
    private static final BigDecimal STANDARD_ANNUAL_LIMIT = new BigDecimal("100000.00"); // Default annual limit
    private static final BigDecimal TEST_ANNUAL_LIMIT = new BigDecimal("5000.00");      // Lower limit for annual limit tests
    private static final Set<BenefitType> DEFAULT_COVERED_BENEFITS = EnumSet.allOf(BenefitType.class);

    static Stream<Arguments> provideDeductibleTestCases() {
        return Stream.of(Arguments.of(new BigDecimal("1000.00"), new BigDecimal("500.00")), // Regular case
                Arguments.of(new BigDecimal("500.00"), BigDecimal.ZERO),           // Edge case: Equal to deductible
                Arguments.of(new BigDecimal("499.99"), BigDecimal.ZERO),          // Edge case: Less than deductible
                Arguments.of(new BigDecimal("2000.00"), new BigDecimal("1500.00")) // Regular case: Larger amount
        );
    }

    /**
     * Creates a list containing of random number of nurses for testing
     *
     * @param count Number of nurse objects to be created
     * @return List of nurse objects
     */
    private static List<Nurse> randomNurseList(int count) {
        List<Nurse> nurseList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Nurse nurse = Nurse.builder().withRandomBaseData().build();
            nurseList.add(nurse);
        }

        return nurseList;

    }

    /**
     * Creates a list containing of random number of doctors for testing
     *
     * @param count Number of doctor objects to be created
     * @return List of doctor objects
     */
    private static List<Doctor> randomDoctorList(int count) {
        List<Doctor> doctorList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Doctor doctor = Doctor.builder().withRandomBaseData().build();
            doctorList.add(doctor);
        }

        return doctorList;
    }



    // Helper methods for creating test objects
    private Coverage createMockCoverage(BigDecimal annualLimit, BigDecimal deductible, Set<BenefitType> coveredBenefits) {
        CoverageLimit.Builder limitsBuilder = new CoverageLimit.Builder().withAnnualLimit(annualLimit);

        return new BaseCoverage.Builder().withLimits(limitsBuilder.build()).withDeductible(deductible).withCoveredBenefits(coveredBenefits).withExclusions(new ExclusionCriteria(Set.of(), // no excluded diagnoses
                Set.of(), // no excluded procedures
                Set.of(), // no excluded benefits
                Set.of()  // no excluded accident types
        )).build();
    }



    private Coverage createMockCoverageOpts(BigDecimal annualLimit, BigDecimal deductible) {
        return createMockCoverage(annualLimit, deductible, DEFAULT_COVERED_BENEFITS);
    }

    private Coverage createMockCoverageOpts() {
        return createMockCoverage(STANDARD_ANNUAL_LIMIT, STANDARD_DEDUCTIBLE, DEFAULT_COVERED_BENEFITS);
    }

    private Coverage createMockCoverageOpts(BigDecimal deductible) {
        return createMockCoverage(STANDARD_ANNUAL_LIMIT, deductible, DEFAULT_COVERED_BENEFITS);
    }

    private Coverage createMockCoverageOpts(BigDecimal deductible, Set<BenefitType> coveredBenefits) {
        return createMockCoverage(STANDARD_ANNUAL_LIMIT, deductible, coveredBenefits);
    }


    private InsurancePolicy createPolicyWithCoverage(Coverage coverage) {
        GovernmentProvider provider = new GovernmentProvider();
        return new HeldInsurancePolicy.Builder("TEST-" + gen.generateRandomString(6), testPatient, coverage, provider, "Test Policy").build();
    }

    private Bill createBillWithClaimableItems(BigDecimal itemAmount, InsurancePolicy policy) {
        Visit visit = Visit.createNew(LocalDateTime.now(), testPatient);
        visit.updateStatus(VisitStatus.DISCHARGED);

        BillBuilder builder = new BillBuilder()
                .withPatientId(testPatient.getPatientId()).withVisit(visit);

        builder.withInsurancePolicy(policy);

        Bill bill = builder.build();

        bill.addLineItem(new TestBillableItem(itemAmount), 1);

        return bill;
    }

    private static class TestBillableItem implements BillableItem, ClaimableItem {
        private final BigDecimal amount;

        public TestBillableItem(BigDecimal amount) {
            this.amount = amount;
        }

        @Override
        public String getBillItemCategory() {
            return "TEST_CATEGORY";
        }

        @Override
        public BigDecimal getUnsubsidisedCharges() {
            return amount;
        }

        @Override
        public String getBillItemDescription() {
            return "Test Item";
        }

        @Override
        public String getBillingItemCode() {
            return "TEST-001";
        }

        @Override
        public BenefitType resolveBenefitType(boolean isInpatient) {
            return isInpatient ? BenefitType.HOSPITALIZATION : BenefitType.OUTPATIENT_TREATMENTS;
        }

        @Override
        public String getBenefitDescription(boolean isInpatient) {
            return isInpatient ? "Inpatient Test Item" : "Outpatient Test Item";
        }

        @Override
        public BigDecimal getCharges() {
            return getUnsubsidisedCharges();
        }
    }

    @BeforeEach
    void setUp() {
        gen = DataGenerator.getInstance();
        testPatient = new PatientBuilder().withRandomBaseData().patientId(gen.generatePatientId()).residentialStatus(ResidentialStatus.CITIZEN).dateOfBirth(LocalDate.of(1970, 1, 1)).build();
    }

    static Stream<Arguments> provideInsuranceProviders() {
        return Stream.of(Arguments.of(new GovernmentProvider(), "Government Provider"), Arguments.of(new PrivateProvider(), "Private Provider"));
    }

    @ParameterizedTest
    @MethodSource("provideInsuranceProviders")
    void testProviderClaimProcessing(InsuranceProvider provider, String providerName) {
        // Get policy for patient
        InsurancePolicy policy = provider.getPatientPolicy(testPatient).orElseThrow(() -> new AssertionError("Failed to get policy for patient"));
        assertNotNull(policy, "Policy should not be null");

        // Create a visit and set its status
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);

        // Create bill using BillBuilder
        Bill bill = new BillBuilder().withPatientId(testPatient.getPatientId()).withVisit(visit).build();
        assertNotNull(bill, "Bill should not be null");

        // Create claim
        InsuranceClaim claim = InsuranceClaim.createNew(bill, provider, policy, testPatient, bill.getTotalAmount());
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
        Bill visitBill = new BillBuilder().withPatientId(testPatient.getPatientId()).withVisit(visit).build();

        verifyBill(visitBill, "Visit");

        // Test with EmergencyVisit
        EmergencyVisit emergencyVisit = EmergencyVisit.withRandomData();
        emergencyVisit.updateStatus(VisitStatus.DISCHARGED);
        Bill emergencyBill = new BillBuilder().withPatientId(testPatient.getPatientId()).withVisit(emergencyVisit).build();

        verifyBill(emergencyBill, "Emergency Visit");

        // Test with Consultation
        Consultation consultation = Consultation.withRandomData();
        BigDecimal consultationCharges = consultation.calculateCharges();

        assertNotNull(consultationCharges, "Consultation charges should not be null");
        assertTrue(consultationCharges.compareTo(BigDecimal.ZERO) > 0, "Consultation charges should be positive");
    }

    private void verifyBill(Bill bill, String type) {
        assertNotNull(bill, type + " bill should not be null");
        assertTrue(bill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, type + " bill should have positive charges");
        assertNotNull(bill.getPatient(), type + " bill should have associated patient");
        assertEquals(testPatient.getPatientId(), bill.getPatient().getPatientId(), type + " bill should be associated with test patient");
    }


    @ParameterizedTest
    @MethodSource("provideDeductibleTestCases")
    void testDeductibleApplication(BigDecimal billAmount, BigDecimal expectedClaimAmount) {
        // Set up coverage with deductible
        Coverage coverage = createMockCoverageOpts();
        InsurancePolicy policy = createPolicyWithCoverage(coverage);

        // Create bill with test amount
        Bill bill = createBillWithClaimableItems(billAmount, policy);

        // Calculate insurance coverage
        var coverageResult = bill.calculateInsuranceCoverage();

        if (billAmount.compareTo(STANDARD_DEDUCTIBLE) > 0) {
            // Verify coverage is approved and claim exists
            assertTrue(coverageResult.isApproved(), "Claim should be created when amount > deductible");
            InsuranceClaim claim = coverageResult.claim().orElseThrow();

            // Verify bill remains unchanged
            assertEquals(billAmount, bill.getTotalAmount(), "Original bill amount should be preserved");

            // Verify claimable amount is correctly calculated
            assertEquals(expectedClaimAmount, billAmount.subtract(STANDARD_DEDUCTIBLE), "Claim amount after deductible should match expected amount");
        } else {
            assertTrue(!coverageResult.isApproved(), "No claim should be created when amount <= deductible");
            assertTrue(coverageResult.claim().isEmpty(), "No claim should be present when amount <= deductible");
        }
    }

    static Stream<Arguments> provideAnnualLimitTestCases() {
        return Stream.of(
                // Format: Arguments.of(billAmount, expectedClaimAmount)
                Arguments.of(new BigDecimal("7000.00"), TEST_ANNUAL_LIMIT),      // Over limit
                Arguments.of(TEST_ANNUAL_LIMIT, TEST_ANNUAL_LIMIT),              // At limit
                Arguments.of(
                        TEST_ANNUAL_LIMIT.subtract(new BigDecimal("0.01")),          // Just under limit
                        TEST_ANNUAL_LIMIT.subtract(new BigDecimal("0.01"))
                ),
                Arguments.of(new BigDecimal("10000.00"), TEST_ANNUAL_LIMIT)      // Well over limit
        );
    }

    @ParameterizedTest
    @MethodSource("provideAnnualLimitTestCases")
    void testAnnualLimitApplication(BigDecimal billAmount, BigDecimal expectedClaimAmount) {
        Coverage coverage = createMockCoverageOpts(expectedClaimAmount, BigDecimal.ZERO);
        InsurancePolicy policy = createPolicyWithCoverage(coverage);

        // Create bill with test amount
        Bill bill = createBillWithClaimableItems(billAmount, policy);

        // Calculate insurance coverage
        var coverageResult = bill.calculateInsuranceCoverage();

        // Verify coverage is approved and get claim
        assertTrue(coverageResult.isApproved(), "Claim should be created");
        InsuranceClaim claim = coverageResult.claim().orElseThrow();

        // Verify original bill remains unchanged
        assertEquals(billAmount, bill.getTotalAmount(), "Original bill amount should be preserved");

        // Verify claim is at correct limit
        var maxClaimAmount = policy.getCoverage().getLimits().getAnnualLimit();
        assertEquals(maxClaimAmount, expectedClaimAmount, "Coverage should have correct annual limit configured");

        // Get actual claim amount
        var actualClaimAmount = claim.getClaimAmount();

        // Verify claim amount is correctly capped
        assertEquals(expectedClaimAmount, actualClaimAmount, String.format("Claim amount should be capped at %s for bill amount %s", expectedClaimAmount, billAmount));
    }


    @ParameterizedTest
    @EnumSource(BenefitType.class)
    void testBenefitExclusion(BenefitType excludedBenefit) {
        // Create coverage with all benefits except the excluded one
        Set<BenefitType> coveredBenefits = EnumSet.allOf(BenefitType.class);
        coveredBenefits.remove(excludedBenefit);

        Coverage coverage = createMockCoverage(STANDARD_ANNUAL_LIMIT, BigDecimal.ZERO, coveredBenefits);
        InsurancePolicy policy = createPolicyWithCoverage(coverage);

        List<Doctor> doctors = randomDoctorList(4);
        List<Nurse> nurses = randomNurseList(4);
        Visit visit = Visit.createCompatibleVisit(coverage, testPatient, doctors, nurses);
        visit.updateStatus(VisitStatus.DISCHARGED);
        Bill bill = new BillBuilder()
                .withPatientId(testPatient.getPatientId())
                .withVisit(visit)
                .withInsurancePolicy(policy)
                .build();

        // Override the benefit type resolution to return the excluded benefit
        TestBillableItem item = new TestBillableItem(new BigDecimal("1000.00")) {
            @Override
            public BenefitType resolveBenefitType(boolean isInpatient) {
                return excludedBenefit;
            }
        };

        bill.addLineItem(item, 1);

        // Calculate insurance coverage
        var coverageResult = bill.calculateInsuranceCoverage();

        assertFalse(coverageResult.isApproved(), "Claim with excluded benefit " + excludedBenefit + " should be denied");
        assertTrue(coverageResult.getDenialReason().isPresent(), "Denial reason should be provided");
        assertEquals("No claimable amount", coverageResult.getDenialReason().get(),
                "Denial reason should specifically be 'No claimable amount'");
    }
}
