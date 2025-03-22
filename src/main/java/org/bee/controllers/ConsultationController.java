package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.utils.DataGenerator;
import org.bee.utils.InfoUpdaters.ConsultationUpdater;

import java.util.List;

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
}
