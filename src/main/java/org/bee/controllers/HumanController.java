package org.bee.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Human;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.utils.JSONHelper;

/**
 * Controller class that manages all human entities in the system.
 * Handles loading, saving, and searching of doctors, nurses, and patients.
 * Implemented as a singleton.
 */
public class HumanController {

    private static final String DATABASE_DIR = System.getProperty("database.dir", "database");
    private static final String HUMANS_FILE = DATABASE_DIR + "/humans.json";

    private static HumanController instance;

    private List<Human> humans = new ArrayList<>();
    private JSONHelper jsonHelper = new JSONHelper();

    private HumanController() {
        init();
    }

    /**
     * Gets the singleton instance of HumanController.
     * Creates the instance if it doesn't exist yet.
     *
     * @return The singleton instance
     */
    public static synchronized HumanController getInstance() {
        if (instance == null) {
            instance = new HumanController();
        }
        return instance;
    }

    /**
     * Initializes the database by either loading existing data or generating new data.
     */
    private void init() {
        File directory = new File(DATABASE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File humansFile = new File(HUMANS_FILE);
        if (humansFile.exists()) {
            loadHumans();
        } else {
            generateInitialData();
            saveHumans();
        }
    }

    /**
     * Loads humans from the JSON file.
     */
    public void loadHumans() {
        try {
            humans = jsonHelper.loadListFromJsonFile(HUMANS_FILE, Human.class);
            System.out.println("Loaded " + humans.size() + " humans from " + HUMANS_FILE);
        } catch (Exception e) {
            System.err.println("Error loading humans from file: " + e.getMessage());
            humans = new ArrayList<>();
        }
    }

    /**
     * Saves humans to the JSON file.
     */
    public void saveHumans() {
        try {
            jsonHelper.saveToJsonFile(humans, HUMANS_FILE);
            System.out.println("Saved " + humans.size() + " humans to " + HUMANS_FILE);
        } catch (Exception e) {
            System.err.println("Error saving humans to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates initial data with 20 doctors, 20 nurses, and 10 patients.
     */
    private void generateInitialData() {
        System.out.println("Generating initial human data...");

        for (int i = 0; i < 20; i++) {
            Doctor doctor = Doctor.builder()
                    .withRandomBaseData()
                    .build();
            humans.add(doctor);
        }

        for (int i = 0; i < 20; i++) {
            Nurse nurse = Nurse.builder()
                    .withRandomBaseData()
                    .build();
            humans.add(nurse);
        }

        for (int i = 0; i < 10; i++) {
            String patientId = String.format("P%04d", 1001 + i);
            Patient patient = Patient.builder()
                    .withRandomData(patientId)
                    .build();
            humans.add(patient);
        }

        System.out.println("Generated " + humans.size() + " humans.");
    }

    /**
     * Adds a human to the controller and saves to the JSON file.
     *
     * @param human The human to add
     */
    public void addHuman(Human human) {
        humans.add(human);
        saveHumans();
    }

    /**
     * Finds a user by username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<SystemUser> findUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }

        return humans.stream()
                .filter(human -> human instanceof SystemUser)
                .map(human -> (SystemUser) human)
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    /**
     * Gets all humans in the system.
     *
     * @return List of all humans
     */
    public List<Human> getAllHumans() {
        return new ArrayList<>(humans);
    }

    /**
     * Gets all doctors in the system.
     *
     * @return List of all doctors
     */
    public List<Doctor> getAllDoctors() {
        return humans.stream()
                .filter(human -> human instanceof Doctor)
                .map(human -> (Doctor) human)
                .collect(Collectors.toList());
    }

    /**
     * Gets all nurses in the system.
     *
     * @return List of all nurses
     */
    public List<Nurse> getAllNurses() {
        return humans.stream()
                .filter(human -> human instanceof Nurse)
                .map(human -> (Nurse) human)
                .collect(Collectors.toList());
    }

    /**
     * Gets all patients in the system.
     *
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        return humans.stream()
                .filter(human -> human instanceof Patient)
                .map(human -> (Patient) human)
                .collect(Collectors.toList());
    }
}