package org.bee.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.ConsultationType;
import org.bee.utils.DataGenerator;

/**
 * Manages the storage and retrieval of {@link Consultation} objects.
 * This class provides centralized management of consultations through a list and supports operations such as adding, removing, and searching for consultations.
 * It extends BaseController to handle JSON persistence.
 */
public class ConsultationController extends BaseController<Consultation> {
    private static ConsultationController instance;
    private static final DataGenerator dataGenerator = DataGenerator.getInstance();
    private static final HumanController humanController = HumanController.getInstance();

    private ConsultationController() {
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
        System.out.println("Generating initial consultation data...");
        
        List<Patient> patients = humanController.getAllPatients();
        List<Doctor> doctors = humanController.getAllDoctors();
        
        if (patients.isEmpty()) {
            System.err.println("No patients available to generate consultations");
            return;
        }
        
        for (int i = 0; i < 10; i++) {
            Consultation consultation = Consultation.withRandomData();
            
            Patient patient = patients.get(dataGenerator.generateRandomInt(patients.size()));
            
            Doctor doctor = null;
            if (!doctors.isEmpty() && dataGenerator.generateRandomInt(2) == 0) {
                doctor = doctors.get(dataGenerator.generateRandomInt(doctors.size()));
            }
            
            // Set the patient and doctor
            consultation.getClass().cast(consultation);  // This is a no-op, just to avoid warnings
            
            // Use reflection to set the patient and doctor fields since they're private
            try {
                java.lang.reflect.Field patientField = Consultation.class.getDeclaredField("patient");
                patientField.setAccessible(true);
                patientField.set(consultation, patient);
                
                if (doctor != null) {
                    java.lang.reflect.Field doctorField = Consultation.class.getDeclaredField("doctor");
                    doctorField.setAccessible(true);
                    doctorField.set(consultation, doctor);
                    
                    java.lang.reflect.Field doctorIdField = Consultation.class.getDeclaredField("doctorId");
                    doctorIdField.setAccessible(true);
                    doctorIdField.set(consultation, doctor.getMcr());
                }
            } catch (Exception e) {
                System.err.println("Error setting patient or doctor: " + e.getMessage());
            }
            
            items.add(consultation);
        }
        
        System.out.println("Generated " + items.size() + " consultations.");
    }

    /**
     * Adds a consultation to the list and saves to the JSON file.
     *
     * @param consultation The consultation to add
     */
    public void addConsultation(Consultation consultation) {
        addItem(consultation);
        saveData();
    }

    /**
     * Gets all consultations.
     *
     * @return A list of all consultations
     */
    public List<Consultation> getAllConsultations() {
        return getAllItems();
    }

    /**
     * Gets consultations for a specific patient.
     *
     * @param patient The patient to get consultations for
     * @return List of consultations for the patient
     */
    public List<Consultation> getConsultationsForPatient(Patient patient) {
        return items.stream()
                .filter(consultation -> {
                    Patient consultationPatient = consultation.getPatient();
                    return consultationPatient != null && consultationPatient.equals(patient);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets consultations for a specific doctor.
     *
     * @param doctor The doctor to get consultations for
     * @return List of consultations for the doctor
     */
    public List<Consultation> getConsultationsForDoctor(Doctor doctor) {
        return items.stream()
                .filter(consultation -> {
                    Doctor consultationDoctor = consultation.getDoctor();
                    return consultationDoctor != null && consultationDoctor.equals(doctor);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets consultations of a specific type.
     *
     * @param type The consultation type to filter by
     * @return List of consultations of the specified type
     */
    public List<Consultation> getConsultationsByType(ConsultationType type) {
        return items.stream()
                .filter(consultation -> consultation.getConsultationType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Finds a consultation by its ID.
     *
     * @param consultationId The ID of the consultation to find
     * @return Optional containing the consultation if found, empty otherwise
     */
    public Optional<Consultation> findConsultationById(String consultationId) {
        return items.stream()
                .filter(consultation -> consultation.getConsultationId().equals(consultationId))
                .findFirst();
    }

    /**
     * Creates a random consultation with a specific patient and doctor.
     *
     * @param patientId The ID of the patient for the consultation
     * @param doctorId The ID of the doctor for the consultation (can be null)
     * @return The created consultation, or null if the patient or doctor was not found
     */
    public Consultation createRandomConsultation(String patientId, String doctorId) {
        // Find the patient
        Optional<Patient> patientOpt = humanController.getAllPatients().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst();
        
        if (patientOpt.isEmpty()) {
            System.err.println("Patient with ID " + patientId + " not found");
            return null;
        }
        
        Patient patient = patientOpt.get();
        Doctor doctor = null;
        
        // Find the doctor if provided
        if (doctorId != null && !doctorId.isEmpty()) {
            Optional<Doctor> doctorOpt = humanController.getAllDoctors().stream()
                    .filter(d -> d.getMcr().equals(doctorId))
                    .findFirst();
            
            if (doctorOpt.isEmpty()) {
                System.err.println("Doctor with MCR " + doctorId + " not found");
                return null;
            }
            
            doctor = doctorOpt.get();
        }
        
        // Generate the consultation
        Consultation consultation = Consultation.withRandomData();
        
        // Set the patient and doctor using reflection
        try {
            java.lang.reflect.Field patientField = Consultation.class.getDeclaredField("patient");
            patientField.setAccessible(true);
            patientField.set(consultation, patient);
            
            if (doctor != null) {
                java.lang.reflect.Field doctorField = Consultation.class.getDeclaredField("doctor");
                doctorField.setAccessible(true);
                doctorField.set(consultation, doctor);
                
                java.lang.reflect.Field doctorIdField = Consultation.class.getDeclaredField("doctorId");
                doctorIdField.setAccessible(true);
                doctorIdField.set(consultation, doctor.getMcr());
            }
        } catch (Exception e) {
            System.err.println("Error setting patient or doctor: " + e.getMessage());
            return null;
        }
        
        // Add to the list and save
        addItem(consultation);
        
        return consultation;
    }

    /**
     * Creates a random consultation with a randomly selected patient and doctor from the system.
     *
     * @return The created consultation, or null if no patients or doctors are available
     */
    public Consultation createRandomConsultation() {
        List<Patient> patients = humanController.getAllPatients();
        List<Doctor> doctors = humanController.getAllDoctors();
        
        if (patients.isEmpty()) {
            System.err.println("No patients available in the system");
            return null;
        }
        
        Patient patient = patients.get(dataGenerator.generateRandomInt(patients.size()));
        
        Doctor doctor = null;
        if (!doctors.isEmpty() && dataGenerator.generateRandomInt(2) == 0) {
            doctor = doctors.get(dataGenerator.generateRandomInt(doctors.size()));
        }
        
        // Generate the consultation
        Consultation consultation = Consultation.withRandomData();
        
        // Set the patient and doctor using reflection
        try {
            java.lang.reflect.Field patientField = Consultation.class.getDeclaredField("patient");
            patientField.setAccessible(true);
            patientField.set(consultation, patient);
            
            if (doctor != null) {
                java.lang.reflect.Field doctorField = Consultation.class.getDeclaredField("doctor");
                doctorField.setAccessible(true);
                doctorField.set(consultation, doctor);
                
                java.lang.reflect.Field doctorIdField = Consultation.class.getDeclaredField("doctorId");
                doctorIdField.setAccessible(true);
                doctorIdField.set(consultation, doctor.getMcr());
            }
        } catch (Exception e) {
            System.err.println("Error setting patient or doctor: " + e.getMessage());
            return null;
        }
        
        // Add to the list and save
        addItem(consultation);
        
        return consultation;
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
    public boolean updateConsultation(Consultation oldConsultation, Consultation newConsultation) {
        int index = items.indexOf(oldConsultation);
        if (index != -1) {
            items.set(index, newConsultation);
            saveData();
            return true;
        }
        return false;
    }
}
