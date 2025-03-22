package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.utils.DataGenerator;

import java.util.List;

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

        if (patients.isEmpty()) {
            System.err.println("No patients available to generate appointments");
            return;
        }

        for (int i = 0; i < 10; i++) {
            // Randomly select a patient
            Patient patient = patients.get(dataGenerator.generateRandomInt(patients.size()));

            // Randomly decide whether to assign a doctor (50% chance)
            Doctor doctor = null;
            if (!doctors.isEmpty() && dataGenerator.generateRandomInt(2) == 0) {
                doctor = doctors.get(dataGenerator.generateRandomInt(doctors.size()));
            }

            // Generate the appointment
            Consultation consultation = dataGenerator.generateRandomConsultation(patient, doctor);
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

}
