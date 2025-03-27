package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Human;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.InsurancePolicy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the storage and retrieval of {@link Visit} objects.
 * Handles loading, saving, and management of visit
 * Implemented as a singleton
 * Extends BaseController to handle JSON persistence
 */
public class VisitController extends BaseController<Visit> {

    /**
     * Singleton instance of VisitController
     */
    private static VisitController instance;

    /**
     * Reference to HumanController singleton instance
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Protected constructor to prevent direct modification
     */
    protected VisitController() {
        super();
    }

    /**
     * Returns the singleton instance of VisitController
     * Creates instance if it doesn't exist
     *
     * @return Singleton VisitController instance
     */
    public static synchronized VisitController getInstance() {
        if (instance == null) {
            instance = new VisitController();
        }
        return instance;
    }

    /**
     * Specifies the file path for visits.txt
     *
     * @return Path to visits.txt
     *
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/visits.txt";
    }

    /**
     * Returns the Class for Visit entity
     *
     * @return The Class for Visit
     */
    @Override
    protected Class<Visit> getEntityClass() {
        return Visit.class;
    }

    /**
     * Generates initial visit data for tye healthcare management system
     * Creates visits for all patients
     *
     * Requires existing patients and doctors to generate visits
     * Displays warnings if no patients or doctors are available
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial visit data...");

        List<Patient> patients = humanController.getAllPatients();
        List<Doctor> doctors = humanController.getAllDoctors();
        List<Nurse> nurses = humanController.getAllNurses();
        PolicyController policyController = PolicyController.getInstance();

        if (patients.isEmpty() || doctors.isEmpty()) {
            System.err.println("No patients or doctors available to generate visits");
            return;
        }

        for (Patient patient : patients) {
            List<InsurancePolicy> policies = policyController.getAllPoliciesForPatient(patient);

            if (!policies.isEmpty()) {
                InsurancePolicy policy = policies.getFirst();
                Coverage coverage = policy.getCoverage();

                Visit visit = Visit.createCompatibleVisit(
                        coverage, patient, doctors, nurses);

                visit.updateStatus(VisitStatus.DISCHARGED);

                items.add(visit);
            } else {
                Visit visit = Visit.withRandomData(patient);
                visit.updateStatus(VisitStatus.IN_PROGRESS);
                items.add(visit);
            }
        }

        System.out.println("Generated " + items.size() + " visits.");
    }

    /**
     * Retrieves all visits associated with a specific patient
     *
     * @param patient The patient visits to retrieve
     * @return A list of Visit objects for the specified patient
     */
    public List<Visit> getVisitsForPatient(Patient patient) {
        return getAllItems().stream()
                .filter(visit -> visit.getPatient().equals(patient))
                .collect(Collectors.toList());
    }

}