package org.bee.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.*;
import org.bee.utils.InfoUpdaters.PatientUpdater;

/**
 * Controller class that manages all human entities in the system.
 * Handles loading, saving, and searching of doctors, nurses, and patients.
 * Implemented as a singleton.
 * Extends BaseController to handle JSON persistence.
 */
public class HumanController extends BaseController<Human> {
    private static HumanController instance;
    private SystemUser authenticatedUser;

    protected HumanController() {
        super();
    }

    public static synchronized HumanController getInstance() {
        if (instance == null) {
            instance = new HumanController();
        }
        return instance;
    }

    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/humans.txt";
    }

    @Override
    protected Class<Human> getEntityClass() {
        return Human.class;
    }

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
            String patientId = String.format("P%04d", 1001 + i);
            Patient patient = Patient.builder()
                    .withRandomData(patientId)
                    .build();
            items.add(patient);
        }

        System.out.println("Generated " + items.size() + " humans.");
    }

    public void authenticate(SystemUser user) {
        this.authenticatedUser = user;
    }

    public String getUserGreeting() {
        return switch (authenticatedUser) {
            case Doctor doc -> String.format("Welcome back Dr %s MCR No. %s", doc.getName(), doc.getMcr());
            case Patient patient -> String.format("Welcome back %s (%s)", patient.getName(), patient.getPatientId());
            case Nurse nurse -> String.format("Welcome back %s RNID No. %s", nurse.getName(), nurse.getRnid());
            case Clerk clerk -> String.format("Welcome back Clerk %s StaffID No. %s", clerk.getName(), clerk.getStaffId());
            case null, default -> throw new IllegalStateException("There is no logged in user");
        };
    }

    public String getLoginInUser() {
        return switch (authenticatedUser) {
            case Doctor doc -> String.format(doc.getName(), doc.getMcr(), doc.getStaffId());
            case Patient patient -> String.format(patient.getName(), patient.getPatientId(), patient.getNricFin());
            case Nurse nurse -> String.format(nurse.getName(), nurse.getRnid(), nurse.getStaffId());
            case Clerk clerk -> String.format(clerk.getName(), clerk.getStaffId());
            case null, default -> throw new IllegalStateException("There is no logged in user");
        };
    }


    public SystemUser getLoggedInUser() {
        if (authenticatedUser == null) {
            throw new IllegalStateException("There is no logged in user");
        }
        return authenticatedUser;
    }

    public void addHuman(Human human) {
        addItem(human);
    }

    /**
     * Updates a patient using the PatientUpdater.
     *
     * @param patientId ID of the patient to update
     * @param updater   PatientUpdater with the fields to update
     */
    public void updatePatient(String patientId, PatientUpdater updater) {
        Patient patient = findPatientById(patientId);
        updateEntity(patient, updater);
    }


    private Patient findPatientById(String patientId) {
        return getAllPatients().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst()
                .orElse(null);
    }

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

    public List<Human> getAllHumans() {
        return getAllItems();
    }

    public List<Doctor> getAllDoctors() {
        return items.stream()
                .filter(human -> human instanceof Doctor)
                .map(human -> (Doctor) human)
                .collect(Collectors.toList());
    }

    public List<Nurse> getAllNurses() {
        return items.stream()
                .filter(human -> human instanceof Nurse)
                .map(human -> (Nurse) human)
                .collect(Collectors.toList());
    }

    public List<Patient> getAllPatients() {
        return items.stream()
                .filter(human -> human instanceof Patient)
                .map(human -> (Patient) human)
                .collect(Collectors.toList());
    }


    public List<Clerk> getAllClerks() {
        return items.stream()
                .filter(human -> human instanceof Clerk)
                .map(human -> (Clerk) human)
                .collect(Collectors.toList());
    }

}
