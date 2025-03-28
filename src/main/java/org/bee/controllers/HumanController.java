package org.bee.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.*;
import org.bee.hms.medical.Consultation;
import org.bee.utils.DataGenerator;

/**
 * Manages the storage and retrieval of {@link Human} objects.
 * Handles loading, saving, and searching of doctors, nurses, and patients
 * Implemented as a singleton
 * Extends BaseController to handle JSON persistence
 */
public class HumanController extends BaseController<Human> {

    /**
     * Singleton instance of the HumanController
     */
    private static HumanController instance;

    /**
     * The current authenticated user
     */
    private SystemUser authenticatedUser;

    /**
     * Protected constructor to enforce singleton pattern.
     */
    protected HumanController() {
        super();
    }

    /**
     * Returns the instance of HumanController
     * Create instance if it does not exist
     *
     * @return The singleton instance of HumanController
     */
    public static synchronized HumanController getInstance() {
        if (instance == null) {
            instance = new HumanController();
        }
        return instance;
    }

    /**
     * Returns the file path for storing humans data
     *
     * @return A String representing the path to the humans file
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/humans.txt";
    }

    /**
     * Returns the Class for Human entity
     *
     * @return The Class for Human
     */
    @Override
    protected Class<Human> getEntityClass() {
        return Human.class;
    }

    /**
     * Generates initial Humans data for the healthcare management system
     * This method creates all the Humans data for the healthcare management system(Doctors,Nurses,Clerks,Paitents)
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial human data...");

        for (int i = 0; i < 10; i++) {
            Doctor doctor = Doctor.builder()
                    .withRandomBaseData()
                    .build();
            items.add(doctor);
        }

        for (int i = 0; i < 15; i++) {
            Nurse nurse = Nurse.builder()
                    .withRandomBaseData()
                    .build();
            items.add(nurse);
        }

        for (int i = 0; i < 10; i++) {
            Clerk clerk = Clerk.builder()
                    .withRandomBaseData()
                    .build();
            items.add(clerk);
        }

        for (int i = 0; i < 30; i++) {
            Patient patient = Patient.builder()
                    .withRandomData(DataGenerator.generatePatientId())
                    .build();
            items.add(patient);
        }

        System.out.println("Generated " + items.size() + " humans.");
    }

    /**
     * Authenticates a user into the system
     *
     * @param user The authenticated user
     */
    public void authenticate(SystemUser user) {
        this.authenticatedUser = user;
    }

    /**
     * Returns a greeting for the current user
     * The greeting varies based on the role (Doctor, Patient, Nurse, Clerk).
     *
     *  @return A greeting message for the authenticated user
     */
    public String getUserGreeting() {
        return switch (authenticatedUser) {
            case Doctor doc -> String.format("Welcome back Dr %s MCR No. %s", doc.getName(), doc.getMcr());
            case Patient patient -> String.format("Welcome back %s (%s)", patient.getName(), patient.getPatientId());
            case Nurse nurse -> String.format("Welcome back %s RNID No. %s", nurse.getName(), nurse.getRnid());
            case Clerk clerk -> String.format("Welcome back Clerk %s Staff ID No. %s", clerk.getName(), clerk.getStaffId());
            case null, default -> throw new IllegalStateException("There is no logged in user");
        };
    }

    /**
     * Returns information about the logged-on user
     * The information varies based on the user's role (Doctor, Patient, Nurse, Clerk)
     *
     * @return Information containing the user's name and relevant ID
     */
    public String getLoginInUser() {
        return switch (authenticatedUser) {
            case Doctor doc -> String.format(doc.getName(), doc.getMcr(), doc.getStaffId());
            case Patient patient -> String.format(patient.getName(), patient.getPatientId(), patient.getNricFin());
            case Nurse nurse -> String.format(nurse.getName(), nurse.getRnid(), nurse.getStaffId());
            case Clerk clerk -> String.format(clerk.getName(), clerk.getStaffId());
            case null, default -> throw new IllegalStateException("There is no logged in user");
        };
    }

    /**
     * Retrieves the current logged-in user
     *
     * @return The authenticated user
     * @throws IllegalStateException If no user is logged in
     */
    public SystemUser getLoggedInUser() {
        if (authenticatedUser == null) {
            throw new IllegalStateException("There is no logged in user");
        }
        return authenticatedUser;
    }

    /**
     * Finds a patient by their patientId
     *
     * @param patientId The ID of the patient
     * @return The Patient object if found, null otherwise
     */
    private Patient findPatientById(String patientId) {
        return getAllPatients().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Searches for user by their username
     *
     * @param username The username to search for
     * @return An Optional containing the SystemUser if found, empty Optional otherwise
     */
    public Optional<SystemUser> findUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }

        return items.stream()
                .filter(human -> human instanceof SystemUser)
                .map(human -> (SystemUser) human)
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    /**
     * Retrieves all humans in the healthcare management system
     *
     * @return A list of Human
     */
    public List<Human> getAllHumans() {
        return getAllItems();
    }

    /**
     * Retrieves all doctors in the healthcare management system
     *
     * @return A list of Doctor
     */
    public List<Doctor> getAllDoctors() {
        return items.stream()
                .filter(human -> human instanceof Doctor)
                .map(human -> (Doctor) human)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all nurses in the healthcare management system
     *
     * @return A list of Nurse
     */
    public List<Nurse> getAllNurses() {
        return items.stream()
                .filter(human -> human instanceof Nurse)
                .map(human -> (Nurse) human)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all patients in the healthcare management system
     *
     * @return A list of Patient
     */
    public List<Patient> getAllPatients() {
        return items.stream()
                .filter(human -> human instanceof Patient)
                .map(human -> (Patient) human)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all clerks in the healthcare management system
     *
     * @return A list of Clerk
     */
    public List<Clerk> getAllClerks() {
        return items.stream()
                .filter(human -> human instanceof Clerk)
                .map(human -> (Clerk) human)
                .collect(Collectors.toList());
    }


    public String maskNRIC(String nric) {
        // First char + masked middle + last 4 chars
        return nric.charAt(0) +
                "X".repeat(Math.max(0, nric.length() - 5)) +
                nric.substring(nric.length() - 4);
    }

}
