package org.bee.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.InsuranceCoverageResult;
import org.bee.hms.policy.InsurancePolicy;

/**
 * * Manages the storage and retrieval of {@link InsuranceClaim} objects.
 * Handles loading, saving, and searching of claims.
 * Implemented as a singleton.
 * Extends BaseController to handle JSON persistence.
 */
public class ClaimController extends BaseController<InsuranceClaim> {

    /**
     * Singleton instance of ClaimController
     * Ensures that only one ClaimController exists throughout the application
     * providing management of insurance claims
     */
    private static ClaimController instance;

    /**
     * Instance for managing human-related data
     * This is used to access patient and healthcare provider information
     */
    private static final HumanController humanController = HumanController.getInstance();
    private static final PolicyController policyController = PolicyController.getInstance();

    /**
     * ClaimController is initialized as protected for singleton instance to prevent direct modification
     * <p>
     * It calls the superclass constructor to initialize the base controller functionality
     */
    protected ClaimController() {
        super();
    }

    /**
     * Returns the singleton instance of ClaimController
     * This method ensures that only one instance of ClaimController is created and used throughout the application
     * <p>
     * Creates a new instance if one does not exist
     * @return The singleton instance of ClaimController
     */
    public static synchronized ClaimController getInstance() {
        if (instance == null) {
            instance = new ClaimController();
        }
        return instance;
    }

    /**
     * Provides the file path for storing claim data(claims.txt)
     * This method is used by the BaseController for JSON operations
     * @return The file path for claims.txt
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/claims.txt";
    }

    /**
     * Specifies the class type of InsuranceClaim
     * This method is used by the BaseController for operations and JSON deserialization
     *
     * @return The InsuranceClaim type
     */
    @Override
    protected Class<InsuranceClaim> getEntityClass() {
        return InsuranceClaim.class;
    }

    /**
     * Generates initial insurance claim data for the healthcare management system.
     * This method processes draft bills and create insurance claims for those with valid insurance policies
     * If there is no existing bills, it will print that there is no bills available for generation of claims
     * @throws IllegalStateException if there's an error processing a bill
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial claim data...");

        BillController billController = BillController.getInstance();
        List<Bill> existingBills = billController.getAllItems();

        if (existingBills.isEmpty()) {
            System.err.println("No bills available to generate claims");
            return;
        }


        AtomicInteger claimCount = new AtomicInteger(0);
        AtomicInteger billCounterAtomic = new AtomicInteger(0);

        for (Bill bill : existingBills) {
            int currentBillCount = billCounterAtomic.getAndIncrement();

            Patient patient = bill.getPatient();
            InsurancePolicy policy = bill.getInsurancePolicy();

            if (policy == null) {
                continue; // Skip bills without an insurance policy
            }

            if (bill.getStatus() == BillingStatus.DRAFT) {
                if (currentBillCount % 4 == 3) {
                    try {
                        bill.submitForProcessing();
                        InsuranceProvider provider = policy.getInsuranceProvider();
                        InsuranceCoverageResult coverageResult = bill.calculateInsuranceCoverage();

                        if (coverageResult.isApproved()) {
                            Optional<InsuranceClaim> claimOpt = coverageResult.claim();
                            claimOpt.ifPresent(c -> {
                                items.add(c);
                                claimCount.getAndIncrement();
                                if (currentBillCount % 3 == 0) {
                                    boolean submitted = provider.submitClaim(patient, c);
                                    if (submitted) {
                                        provider.processClaim(patient, c);
                                    }
                                }
                            });
                        }

                    } catch (IllegalStateException e) {
                        System.err.println("Error processing bill " + bill.getBillId() + ": " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("Generated " + claimCount + " claims.");
    }

    /**
     * Processes an insurance claim for a bill if possible
     *
     * @param bill The bill to process
     * @return An optional insurance claim if created
     */
    public Optional<InsuranceClaim> processBillClaim(Bill bill) {
        InsuranceCoverageResult coverageResult = bill.calculateInsuranceCoverage();

        if (coverageResult.isApproved()) {
            Optional<InsuranceClaim> claim = coverageResult.claim();
            claim.ifPresent(this::addClaim);
            return claim;
        }

        return Optional.empty();
    }

    /**
     * Generates valid insurance claims for a patient with a specific provider
     * This method ensures that the generated claims will be approved by creating
     * appropriate visits and bills that match the policy coverage
     *
     * @param patient  The patient to generate claims for
     * @param provider The insurance provider to use
     * @param count    The number of claims to generate
     */
    private void generateValidClaimsForPatient(Patient patient, InsuranceProvider provider, int count) {
        // Get policy from the policy controller instead of provider directly
        Optional<InsurancePolicy> policyOpt;

        if (provider instanceof GovernmentProvider) {
            policyOpt = policyController.getGovernmentPolicy(patient);
        } else {
            policyOpt = policyController.getPrivatePolicy(patient);
        }

        policyOpt.ifPresent(policy -> {
            Coverage coverage = policy.getCoverage();

            // Create multiple claims
            for (int i = 0; i < count; i++) {
                List<Doctor> availableDoctors = humanController.getAllDoctors();
                List<Nurse> availableNurses = humanController.getAllNurses();

                Visit visit = Visit.createCompatibleVisit(coverage, patient, availableDoctors, availableNurses);

                visit.updateStatus(VisitStatus.DISCHARGED);

                Bill bill = new BillBuilder()
                        .withPatient(patient)
                        .withVisit(visit)
                        .withInsurancePolicy(policy)
                        .build();

                var coverageResult = bill.calculateInsuranceCoverage();
                if (coverageResult.isApproved()) {
                    coverageResult.claim().ifPresent(items::add);
                }
            }
        });
    }

    /**
     * Adds a new insurance claim for the patient
     * This method adds the given claim
     * @param claim The claim to be added
     */
    public void addClaim(InsuranceClaim claim) {
        addItem(claim);
    }

    /**
     * Retrieves all the claim details for all patient
     * @return A List of all InsuranceClaim
     */
    public List<InsuranceClaim> getAllClaims() {
        return getAllItems();
    }

    /**
     * Finds an insurance claim by its claimID
     * @param claimId The unique identifier for claim
     * @return An Optional containing the InsuranceClaim if found, empty otherwise
     *
     */
    public Optional<InsuranceClaim> findClaimById(String claimId) {
        return items.stream()
                .filter(claim -> claim.getClaimId().equals(claimId))
                .findFirst();
    }

    /**
     * Retrieves all insurance claims for a specific patient.
     * @param patient The Patient for whom to retrieve claims
     * @return A List of InsuranceClaim associated with the specified patient
     */
    public List<InsuranceClaim> getClaimsForPatient(Patient patient) {
        return items.stream()
                .filter(claim -> claim.getPatient().equals(patient))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all insurance claims by a specified status.
     * @param status The ClaimStatus to filter claims by
     * @return A List of InsuranceClaim with the specified status
     */
    public List<InsuranceClaim> getClaimsByStatus(ClaimStatus status) {
        return items.stream()
                .filter(claim -> claim.getClaimStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of a specified insurance claim
     * @param claimId The ID of the claim to update
     * @param newStatus The new ClaimStatus
     * @return true if the claim was found and updated, false otherwise
     */
    public boolean updateClaimStatus(String claimId, ClaimStatus newStatus) {
        Optional<InsuranceClaim> claimOpt = findClaimById(claimId);
        if (claimOpt.isPresent()) {
            InsuranceClaim claim = claimOpt.get();
            claim.updateStatus(newStatus);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Processes a partial approval for a specific insurance claim
     * This method updates the claim with the approved amount and reason if found
     *
     * @param claimId The ID of the claim to process
     * @param approvedAmount The partially approved amount
     * @param reason  The reason for partial approval
     * @return  true if the claim was found and processed, false otherwise
     */
    public boolean processPartialApproval(String claimId, BigDecimal approvedAmount, String reason) {
        Optional<InsuranceClaim> claimOpt = findClaimById(claimId);
        if (claimOpt.isPresent()) {
            InsuranceClaim claim = claimOpt.get();
            claim.processPartialApproval(approvedAmount, reason);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Adds a supporting document tfor a specified insurance claim
     * This method finds the claim by ID and adds the document description
     * @param claimId The ID of the claim to update
     * @param documentDescription The description of the supporting document
     * @return true if the claim was found and updated, false otherwise
     */
    public boolean addSupportingDocument(String claimId, String documentDescription) {
        Optional<InsuranceClaim> claimOpt = findClaimById(claimId);
        if (claimOpt.isPresent()) {
            InsuranceClaim claim = claimOpt.get();
            claim.addSupportingDocument(documentDescription);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Updates the comments for a specific insurance claim
     * This method finds the claim by ID and updates its comments
     * @param claimId The ID of the claim to update
     * @param comments The new comments
     * @return true if the claim was found and updated, false otherwise
     */
    public boolean updateComments(String claimId, String comments) {
        Optional<InsuranceClaim> claimOpt = findClaimById(claimId);
        if (claimOpt.isPresent()) {
            InsuranceClaim claim = claimOpt.get();
            claim.updateComments(comments);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Removes a specific insurance claim
     * @param claimId The ID of the claim to remove
     * @return true if the claim was found and removed, false otherwise
     */
    public boolean removeClaim(String claimId) {
        Optional<InsuranceClaim> claimOpt = findClaimById(claimId);
        if (claimOpt.isPresent()) {
            boolean removed = items.remove(claimOpt.get());
            if (removed) {
                saveData();
            }
            return removed;
        }
        return false;
    }
}
