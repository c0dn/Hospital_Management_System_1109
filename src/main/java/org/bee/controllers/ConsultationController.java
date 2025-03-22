package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.utils.DataGenerator;
import org.bee.utils.InfoUpdaters.ConsultationUpdater;

import java.util.List;
import java.util.Scanner;

/**
 * Manages the storage and retrieval of {@link Consultation} objects.
 * This class provides centralized management of consultations through a list and supports operations such as adding, removing, and searching for consultations.
 * It extends BaseController to handle JSON persistence.
 */
public class ConsultationController extends BaseController<Consultation> {
    private static ConsultationController instance;
    private static final DataGenerator dataGenerator = DataGenerator.getInstance();
    private static final HumanController humanController = HumanController.getInstance();

    protected ConsultationController() { super(); }

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

        if (patients.isEmpty() || doctors.isEmpty()) {
            System.err.println("No patients or doctors available to generate appointments");
            return;
        }

        for (int i = 0; i < 10; i++) {
            Patient patient = dataGenerator.getRandomElement(patients);

            Doctor doctor = dataGenerator.getRandomElement(doctors);;

            Consultation consultation = Consultation.withRandomData(patient, doctor);
            items.add(consultation);
        }

        System.out.println("Generated " + items.size() + " consultations.");
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

    /**
     * Updates an existing consultation and saves to the JSON file.
     *
     * @param oldConsultation The consultation to be updated
     * @param newConsultation The updated consultation data
     * @return true if the consultation was updated, false if it was not found
     */
//    public boolean updateConsultation(Consultation oldConsultation, Consultation newConsultation) {
//        int index = items.indexOf(oldConsultation);
//        if (index != -1) {
//            items.set(index, newConsultation);
//            saveData();
//            return true;
//        }
//        return false;
//    }
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
        List<Consultation> consultations = consultationController.getAllOutpatientCases();

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
            System.out.printf("%-8s | %-32s | %-10s | %-15s | %-20s | %-15s | %-20s | %-15s | %-10s | %-10s \n",
                    "Case ID", "Appointment Date", "Patient ID", "Patient Name", "Type", "Status", "Diagnosis",
                    "Doctor Name");
            System.out.println("-".repeat(180));

            for (Consultation consultation : currentPageConsultations) {
                System.out.printf("%-8s %-32s %-10s %-15s %-20s %-15s %-20s %-15s \n",
                        consultation.getConsultationId(), consultation.getConsultationTime(),
                        consultation.getPatient() != null ? consultation.getPatient().getPatientId() : "N/A",
                        consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A",
                        consultation.getConsultationType(), consultation.getStatus(),
                        consultation.getDiagnosis(),
                        consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A");
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
