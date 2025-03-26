package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;
import org.bee.utils.InfoUpdaters.ConsultationUpdater;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the storage and retrieval of {@link Consultation} objects.
 * This class provides centralized management of consultations through a list and supports operations such as adding, removing, and searching for consultations.
 * It extends BaseController to handle JSON persistence.
 */
public class ConsultationController extends BaseController<Consultation> {
    private static ConsultationController instance;
    private static final HumanController humanController = HumanController.getInstance();

    protected ConsultationController() {
        super();
    }

    public static synchronized ConsultationController getInstance() {
        if (instance == null) {
            instance = new ConsultationController();
        }
        return instance;
    }

    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/consultations.txt";
    }

    @Override
    protected Class<Consultation> getEntityClass() {
        return Consultation.class;
    }

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

    public void addCase(Consultation consultation) {
        addItem(consultation);
        saveData();
    }

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


    public void updateConsultation(String consultationId, ConsultationUpdater updater) {
        Consultation consultation = findConsultationById(consultationId);
        updateEntity(consultation, updater);
    }

    private Consultation findConsultationById(String consultationId) {
        return getAllOutpatientCases().stream()
                .filter(c -> c.getConsultationId().equals(consultationId))
                .findFirst()
                .orElse(null);
    }

    public void viewAllOutpatientCases() {
        ConsultationController consultationController = ConsultationController.getInstance();
        List<Consultation> allConsultations = consultationController.getAllOutpatientCases();

        List<Consultation> consultations = allConsultations;
        if (humanController.getLoggedInUser() instanceof Doctor) {
            // If user is a doctor, filter to only show their consultations
            consultations = allConsultations.stream()
                    .filter(consultation -> consultation.getDoctor() != null &&
                            consultation.getDoctor().getStaffId().equals(
                                    ((Doctor) humanController.getLoggedInUser()).getStaffId()))
                    .collect(Collectors.toList());
        }

        if (consultations.isEmpty()) {
            System.out.println("No outpatient cases found.");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return;
        }

        final int PAGE_SIZE = 7;
        int currentPage = 1;
        int totalPages = (int) Math.ceil((double) consultations.size() / PAGE_SIZE);

        boolean exit = false;
        while (!exit) {
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, consultations.size());

            List<Consultation> currentPageConsultations = consultations.subList(startIndex, endIndex);
            System.out.printf("%-8s | %-32s | %-10s | %-15s | %-20s | %-15s | %-20s | %-15s \n",
                    "Case ID", "Appointment Date", "Patient ID", "Patient Name", "Type", "Status", "Diagnosis",
                    "Doctor Name");
            System.out.println("-".repeat(180));

            for (Consultation consultation : currentPageConsultations) {
                System.out.printf("%-8s %-32s %-10s %-15s %-20s %-15s %-20s %-15s \n",
                        consultation.getConsultationId(), consultation.getConsultationTime(),
                        consultation.getPatient().getPatientId(),
                        consultation.getPatient().getName(),
                        consultation.getConsultationType(), consultation.getStatus(),
                        consultation.getDiagnosis(),
                        consultation.getDoctor().getName());
            }

            System.out.println("\nNavigation:");
            if (currentPage > 1) {
                System.out.println("P - Previous Page");
            }
            if (currentPage < totalPages) {
                System.out.println("N - Next Page");
            }
            System.out.println("E - Exit to Main Menu");

            System.out.println("\nEnter your choice: ");
            String choice = new Scanner(System.in).nextLine().toUpperCase();

            switch (choice) {
                case "P":
                    if (currentPage > 1) {
                        currentPage--;
                    }
                    break;
                case "N":
                    if (currentPage < totalPages) {
                        currentPage++;
                    }
                    break;
                case "E":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
