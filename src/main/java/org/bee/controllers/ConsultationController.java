package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the storage and retrieval of {@link Consultation} objects.
 * This class provides centralized management of consultations through a list and supports operations such as adding, removing, and searching for consultations.
 * It extends BaseController to handle JSON persistence.
 */
public class ConsultationController extends BaseController<Consultation> {
    /**
     * Singleton instance of the ConsultationController.
     */
    private static ConsultationController instance;

    /**
     * Reference to the HumanController singleton instance
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Protected constructor to enforce singleton pattern.
     */
    protected ConsultationController() {
        super();
    }

    /**
     * Returns the instance of ConsultationController
     * Create instance if it does not exist
     *
     * @return The singleton instance of ConsultationController
     */
    public static synchronized ConsultationController getInstance() {
        if (instance == null) {
            instance = new ConsultationController();
        }
        return instance;
    }

    /**
     * Returns the file path for storing consultation data
     *
     * @return A String representing the path to the consultations data file
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/consultations.txt";
    }

    /**
     * Returns the Class for Consultation entity
     *
     * @return The Class for Consultation
     */
    @Override
    protected Class<Consultation> getEntityClass() {
        return Consultation.class;
    }

    /**
     * Generates initial consultation data for the system
     * This method creates consultations for all patients, considering their insurance policies if available
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial consultations data...");

        List<Patient> patients = humanController.getAllPatients();
        List<Doctor> doctors = humanController.getAllDoctors();
        PolicyController policyController = PolicyController.getInstance();

        if (patients.isEmpty() || doctors.isEmpty()) {
            System.err.println("No patients or doctors available to generate consultations");
            return;
        }

        for (Patient patient : patients) {
            List<InsurancePolicy> policies = policyController.getAllPoliciesForPatient(patient);

            if (!policies.isEmpty()) {
                InsurancePolicy policy = policies.getFirst();
                Coverage coverage = policy.getCoverage();

                Consultation consultation = Consultation.createCompatibleConsultation(
                        coverage, patient, doctors);
                items.add(consultation);
            } else {
                Consultation consultation = Consultation.withRandomData(patient,
                        DataGenerator.getRandomElement(doctors));
                items.add(consultation);
            }
        }

        System.out.format("Generated %d consultations%n", items.size());
    }

     /**
     * Adds a new consultation case to the healthcare management system and saves the data
     *
     * @param consultation The Consultation  to be added
     */
    public void addCase(Consultation consultation) {
        addItem(consultation);
        saveData();
    }

    /**
     * Retrieves all outpatient consultation cases in the healthcare management system.
     *
     * @return A list containing all Consultation
     */
    public List<Consultation> getAllOutpatientCases() {
        return getAllItems();
    }

    /**
     * Removes a consultation from the list and saves to the JSON file.
     *
     * @param consultation The consultation to remove
     * @return true if the consultation was removed, false otherwise
     */
    public boolean removeConsultation(Consultation consultation) {
        boolean removed = items.remove(consultation);
        if (removed) {
            saveData();
        }
        return removed;
    }

    /**
     * Finds a consultation by its consultationID.
     *
     * @param consultationId The ID of the consultation to find
     * @return Consultation if found, null otherwise
     */
    private Consultation findConsultationById(String consultationId) {
        return getAllOutpatientCases().stream()
                .filter(c -> c.getConsultationId().equals(consultationId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all consultations for a specific doctor identified by their ID.
     *
     * @param doctorId The staff ID of the doctor
     * @return A list containing all consultations for the specified doctor
     */
    public List<Consultation> getConsultationsByDoctorId(String doctorId) {
        return getAllOutpatientCases().stream()
                .filter(consultation -> consultation.getDoctor() != null &&
                        consultation.getDoctor().getStaffId().equals(doctorId))
                .collect(Collectors.toList());
    }
}
