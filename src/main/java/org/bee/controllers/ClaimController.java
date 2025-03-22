package org.bee.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.Coverage;
import org.bee.utils.DataGenerator;

/**
 * Controller class that manages all insurance claims in the system.
 * Handles loading, saving, and searching of claims.
 * Implemented as a singleton.
 * Extends BaseController to handle JSON persistence.
 */
public class ClaimController extends BaseController<InsuranceClaim> {
    private static ClaimController instance;
    private static final DataGenerator dataGenerator = DataGenerator.getInstance();
    private static final HumanController humanController = HumanController.getInstance();

    protected ClaimController() {
        super();
    }

    public static synchronized ClaimController getInstance() {
        if (instance == null) {
            instance = new ClaimController();
        }
        return instance;
    }

    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/claims.txt";
    }

    @Override
    protected Class<InsuranceClaim> getEntityClass() {
        return InsuranceClaim.class;
    }

    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial claim data...");

        List<Patient> patients = humanController.getAllPatients();
        if (patients.isEmpty()) {
            System.err.println("No patients available to generate claims");
            return;
        }

        // Generate sample claims for each patient
        for (Patient patient : patients) {
            PrivateProvider privateProvider = new PrivateProvider();
            GovernmentProvider governmentProvider = new GovernmentProvider();

            generateValidClaimsForPatient(patient, privateProvider, 2);

            generateValidClaimsForPatient(patient, governmentProvider, 2);
        }

        System.out.println("Generated " + items.size() + " claims.");
    }

    /**
     * Generates valid insurance claims for a patient with a specific provider.
     * This method ensures that the generated claims will be approved by creating
     * appropriate visits and bills that match the policy coverage.
     *
     * @param patient  The patient to generate claims for
     * @param provider The insurance provider to use
     * @param count    The number of claims to generate
     */
    private void generateValidClaimsForPatient(Patient patient, InsuranceProvider provider, int count) {
        // Get a policy for the patient
        provider.getPatientPolicy(patient).ifPresent(policy -> {
            Coverage coverage = policy.getCoverage();

            // Create multiple claims
            for (int i = 0; i < count; i++) {
                List<Doctor> availableDoctors = humanController.getAllDoctors();
                List<Nurse> availableNurses = humanController.getAllNurses();
                
                Visit visit = Visit.createCompatibleVisit(coverage, patient, availableDoctors, availableNurses);

                visit.updateStatus(VisitStatus.DISCHARGED);

                // Create a bill for the patient
                Bill bill = new BillBuilder()
                        .withPatientId(patient.getPatientId())
                        .withVisit(visit)
                        .withInsurancePolicy(policy)
                        .build();


                var coverageResult = bill.calculateInsuranceCoverage();
                if (coverageResult.isApproved()) {
                    coverageResult.claim().ifPresent(items::add);
                } else {
                    System.err.println("Failed to generate approved claim: " +
                            coverageResult.getDenialReason().orElse("Unknown reason"));
                }
            }
        });
    }

    public void addClaim(InsuranceClaim claim) {
        addItem(claim);
    }

    public List<InsuranceClaim> getAllClaims() {
        return getAllItems();
    }

    public Optional<InsuranceClaim> findClaimById(String claimId) {
        return items.stream()
                .filter(claim -> claim.getClaimId().equals(claimId))
                .findFirst();
    }

    public List<InsuranceClaim> getClaimsForPatient(Patient patient) {
        return items.stream()
                .filter(claim -> claim.getPatient().equals(patient))
                .collect(Collectors.toList());
    }

    public List<InsuranceClaim> getClaimsByStatus(ClaimStatus status) {
        return items.stream()
                .filter(claim -> claim.getClaimStatus() == status)
                .collect(Collectors.toList());
    }

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
