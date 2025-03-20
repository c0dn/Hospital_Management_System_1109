package org.bee.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bee.execeptions.ZoomApiException;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Manages the storage and retrieval of {@link Appointment} objects.
 * This class provides centralized management of appointments through a static list and supports operations such as adding, removing, and searching for appointments. It also handles persistence by reading from and writing to a file.
 * The appointments are stored in a static list, allowing for easy access and manipulation across different parts of the application without instantiating the controller.
 */
public class AppointmentController {
    private static final String DATABASE_DIR = System.getProperty("database.dir", "database");
    private static final String APPOINTMENT_FILE = DATABASE_DIR + "/appointments.txt";

    private static AppointmentController instance;

    private List<Appointment> appointments = new ArrayList<>();
    private final JSONHelper jsonHelper = JSONHelper.getInstance();
    private final DataGenerator dataGenerator = DataGenerator.getInstance();
    private final HumanController humanController = HumanController.getInstance();

    /**
     * Private constructor to enforce a singleton pattern
     */
    private AppointmentController() {
        init();
    }

    /**
     * Gets the singleton instance of AppointmentController
     *
     * @return The AppointmentController instance
     */
    public static synchronized AppointmentController getInstance() {
        if (instance == null) {
            instance = new AppointmentController();
        }
        return instance;
    }

    /**
     * Initializes the database by either loading existing data or generating new data
     */
    private void init() {
        File directory = new File(DATABASE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File appointmentsFile = new File(APPOINTMENT_FILE);
        if (appointmentsFile.exists()) {
            loadAppointments();
        } else {
            generateInitialData();
            saveAppointments();
        }
    }
    
    /**
     * Generates initial data with 10 random appointments.
     * This method is called when the appointments file doesn't exist.
     */
    private void generateInitialData() {
        System.out.println("Generating initial appointment data...");
        
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
            Appointment appointment = dataGenerator.generateRandomAppointment(patient, doctor);
            appointments.add(appointment);
        }
        
        System.out.println("Generated " + appointments.size() + " appointments.");
    }

    /**
     * Loads appointments from the JSON file
     */
    public void loadAppointments() {
        try {
            appointments = jsonHelper.loadListFromJsonFile(APPOINTMENT_FILE, Appointment.class);
            System.out.println("Loaded " + appointments.size() + " appointments from " + APPOINTMENT_FILE);
        } catch (Exception e) {
            System.err.println("Error loading appointments from file: " + e.getMessage());
            appointments = new ArrayList<>();
        }
    }

    /**
     * Saves appointments to the JSON file
     */
    public void saveAppointments() {
        try {
            jsonHelper.saveToJsonFile(appointments, APPOINTMENT_FILE);
            System.out.println("Saved " + appointments.size() + " appointments to " + APPOINTMENT_FILE);
        } catch (Exception e) {
            System.err.println("Error saving appointments to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds an appointment to the controller and saves to the JSON file
     *
     * @param appointment The appointment to add
     */
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        saveAppointments();
    }

    /**
     * Removes an appointment from the controller and saves to the JSON file
     *
     * @param appointment The appointment to remove
     * @return true if the appointment was removed, false otherwise
     */
    public boolean removeAppointment(Appointment appointment) {
        boolean removed = appointments.remove(appointment);
        if (removed) {
            saveAppointments();
        }
        return removed;
    }

    /**
     * Gets all appointments in the system
     *
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    /**
     * Gets appointments for a specific patient
     *
     * @param patient The patient to get appointments for
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsForPatient(Patient patient) {
        return appointments.stream()
                .filter(appointment -> appointment.getPatient().equals(patient))
                .collect(Collectors.toList());
    }

    /**
     * Gets appointments for a specific doctor
     *
     * @param doctor The doctor to get appointments for
     * @return List of appointments for the doctor
     */
    public List<Appointment> getAppointmentsForDoctor(Doctor doctor) {
        return appointments.stream()
                .filter(appointment -> {
                    Doctor appointmentDoctor = appointment.getDoctor();
                    return appointmentDoctor != null && appointmentDoctor.equals(doctor);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets appointments with a specific status
     *
     * @param status The status to filter by
     * @return List of appointments with the specified status
     */
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointments.stream()
                .filter(appointment -> appointment.getAppointmentStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Creates a random appointment with a specific doctor and patient
     *
     * @param patientId The ID of the patient for the appointment
     * @param doctorId The ID of the doctor for the appointment (can be null)
     * @return The created appointment, or null if the patient or doctor was not found
     */
    public Appointment createRandomAppointment(String patientId, String doctorId) {
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
        
        // Generate the appointment
        Appointment appointment = dataGenerator.generateRandomAppointment(patient, doctor);
        
        // Add to the list and save
        addAppointment(appointment);
        
        return appointment;
    }

    /**
     * Creates a random appointment with a randomly selected doctor and patient from the system
     *
     * @return The created appointment, or null if no patients or doctors are available
     */
    public Appointment createRandomAppointment() {
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
        
        Appointment appointment = dataGenerator.generateRandomAppointment(patient, doctor);
        
        addAppointment(appointment);
        
        return appointment;
    }

    /**
     * Updates an existing appointment and saves to the JSON file
     *
     * @param oldAppointment The appointment to be updated
     * @param newAppointment The updated appointment data
     * @return true if the appointment was updated, false if it was not found
     */
    public boolean updateAppointment(Appointment oldAppointment, Appointment newAppointment) {
        int index = appointments.indexOf(oldAppointment);
        if (index != -1) {
            appointments.set(index, newAppointment);
            saveAppointments();
            return true;
        }
        return false;
    }

    /**
     * Finds an appointment by matching patient and appointment time
     *
     * @param patient The patient of the appointment
     * @param appointmentTime The time of the appointment
     * @return Optional containing the appointment if found, empty otherwise
     */
    public Optional<Appointment> findAppointment(Patient patient, LocalDateTime appointmentTime) {
        return appointments.stream()
                .filter(appointment -> 
                    appointment.getPatient().equals(patient) && 
                    appointment.getAppointmentTime().equals(appointmentTime))
                .findFirst();
    }

    /**
     * Generates a Zoom meeting link for an appointment
     *
     * @param appointmentTitle The title of the appointment to use as the meeting topic
     * @param durationMinutes The duration of the meeting in minutes
     * @return The Zoom meeting join URL
     * @throws IllegalArgumentException If input parameters are invalid
     * @throws ZoomApiException If there's an error communicating with the Zoom API
     */
    public String generateZoomLink(String appointmentTitle, int durationMinutes)
            throws IllegalArgumentException, ZoomApiException {

        if (appointmentTitle == null || appointmentTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment title cannot be null or empty");
        }

        if (durationMinutes <= 0 || durationMinutes > 1440) {
            throw new IllegalArgumentException("Duration must be between 1 and 1440 minutes");
        }

        OkHttpClient client = new OkHttpClient();
        Response tokenResponse = null;
        Response meetingResponse = null;

        try {
            // Zoom OAuth credentials - why is this in source code?
            final String ACCOUNT_ID = "I3xVw-4USxmYxw15TByvGQ";
            final String CLIENT_ID = "yXD3vVutRhKASvzAOzBjIw";
            final String CLIENT_SECRET = "DJfAquKIWQ46N9h7i3ODLEUEwZpUKGT4";
            final String TOKEN_URL = "https://zoom.us/oauth/token";

            // Step 1: Get access token
            String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            RequestBody tokenBody = new FormBody.Builder()
                    .add("grant_type", "account_credentials")
                    .add("account_id", ACCOUNT_ID)
                    .build();

            Request tokenRequest = new Request.Builder()
                    .url(TOKEN_URL)
                    .post(tokenBody)
                    .addHeader("Authorization", "Basic " + encodedCredentials)
                    .build();

            tokenResponse = client.newCall(tokenRequest).execute();

            if (!tokenResponse.isSuccessful()) {
                throw new ZoomApiException("Failed to get access token. Response code: " + tokenResponse.code() +
                        ", message: " + tokenResponse.message());
            }

            if (tokenResponse.body() == null) {
                throw new ZoomApiException("Token response body is null");
            }

            String tokenResponseBody = tokenResponse.body().string();
            JsonObject jsonTokenResponse = JsonParser.parseString(tokenResponseBody).getAsJsonObject();

            if (!jsonTokenResponse.has("access_token")) {
                throw new ZoomApiException("Access token not found in response: " + tokenResponseBody);
            }

            String accessToken = jsonTokenResponse.get("access_token").getAsString();

            // create meeting
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("topic", appointmentTitle);
            jsonBody.addProperty("type", 1); // 1 for instant meeting
            jsonBody.addProperty("duration", durationMinutes);
            jsonBody.addProperty("timezone", "UTC");

            RequestBody meetingBody = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json")
            );

            Request meetingRequest = new Request.Builder()
                    .url("https://api.zoom.us/v2/users/me/meetings")
                    .post(meetingBody)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .build();

            meetingResponse = client.newCall(meetingRequest).execute();

            if (!meetingResponse.isSuccessful()) {
                throw new ZoomApiException("Failed to create meeting. Response code: " + meetingResponse.code() +
                        ", message: " + meetingResponse.message());
            }

            if (meetingResponse.body() == null) {
                throw new ZoomApiException("Meeting response body is null");
            }

            String meetingResponseBody = meetingResponse.body().string();
            JsonObject jsonMeetingResponse = JsonParser.parseString(meetingResponseBody).getAsJsonObject();

            if (!jsonMeetingResponse.has("join_url")) {
                throw new ZoomApiException("Join URL not found in response: " + meetingResponseBody);
            }

            return jsonMeetingResponse.get("join_url").getAsString();

        } catch (IOException e) {
            throw new ZoomApiException("Error communicating with Zoom API: " + e.getMessage(), e);
        } finally {
            if (tokenResponse != null && tokenResponse.body() != null) {
                tokenResponse.body().close();
            }
            if (meetingResponse != null && meetingResponse.body() != null) {
                meetingResponse.body().close();
            }
        }
    }
}
