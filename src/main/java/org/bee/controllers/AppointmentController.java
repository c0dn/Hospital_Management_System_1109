package org.bee.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bee.execeptions.ZoomApiException;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.utils.DataGenerator;
import java.util.Scanner;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Manages the storage and retrieval of {@link Appointment} objects
 * This class provides centralized management of appointments through a list and supports operations such as adding, removing, and searching for appointments
 * It extends BaseController to handle JSON persistence
 */
public class AppointmentController extends BaseController<Appointment> {

    /**
     * Singleton instance of the AppointmentController
     */
    private static AppointmentController instance;

    /**
     * Reference to the HumanController singleton instance
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Private to prevent direct modification
     */
    private AppointmentController() {
        super();
    }

    /**
     * Returns the instance of AppointmentController
     * Create instance if it does not exist
     *
     * @return The singleton instance of AppointmentController
     */
    public static synchronized AppointmentController getInstance() {
        if (instance == null) {
            instance = new AppointmentController();
        }
        return instance;
    }

    /**
     * Returns the file path for appointments.txt file
     *
     * @return A String representing the directory to appointments.txt file
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/appointments.txt";
    }

    /**
     * Returns the Class for Appointment
     *
     * @return The Class for Appointment
     */
    @Override
    protected Class<Appointment> getEntityClass() {
        return Appointment.class;
    }

    /**
     * Generates initial appointment data for the healthcare management system
     * This method creates 10 random appointments, assigns them to existing patients
     * and optionally to doctors(50% chance). It uses the HumanController to fetch existing
     * patient and doctor data
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial appointment data...");

        List<Patient> patients = humanController.getAllPatients();
        List<Doctor> doctors = humanController.getAllDoctors();

        if (patients.isEmpty()) {
            System.err.println("No patients available to generate appointments");
            return;
        }

        for (int i = 0; i < 10; i++) {
            // Randomly select a patient
            Patient patient = patients.get(DataGenerator.generateRandomInt(patients.size()));

            // Randomly decide whether to assign a doctor (50% chance)
            Doctor doctor = null;
            if (!doctors.isEmpty() && DataGenerator.generateRandomInt(2) == 0) {
                doctor = doctors.get(DataGenerator.generateRandomInt(doctors.size()));
            }

            // Generate the appointment
            Appointment appointment = DataGenerator.generateRandomAppointment(patient, doctor);
            items.add(appointment);
        }

        System.out.println("Generated " + items.size() + " appointments.");
    }

    /**
     * Removes the specified appointment from the list of appointments
     * If the appointment is removed, save the data
     *
     * @param appointment The Appointment  to be removed
     * @return true if the appointment was successfully removed, false otherwise
     */
    public boolean removeAppointment(Appointment appointment) {
        boolean removed = items.remove(appointment);
        if (removed) {
            saveData();
        }
        return removed;
    }

    /**
     * Retrieves all appointments associated with a specific patient
     *
     * @param patient The Patient whose appointments to retrieved from
     * @return A list of Appointment for the specified patient
     */
    public List<Appointment> getAppointmentsForPatient(Patient patient) {
        return items.stream()
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
        return items.stream()
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
        return items.stream()
                .filter(appointment -> appointment.getAppointmentStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new appointment to the appointment list and saves the data
     *
     * @param appointment The Appointment to be added
     */
    public void addAppointment(Appointment appointment) {
        addItem(appointment);
        saveData();
    }

    /**
     * Retrieves all appointments in the healthcare management system
     *
     * @return A list containing all Appointment
     */
    public List<Appointment> getAllAppointments() {
        return getAllItems();
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
        Appointment appointment = DataGenerator.generateRandomAppointment(patient, doctor);

        // Add to the list and save
        addItem(appointment);

        return appointment;
    }

    /**
     * Displays all appointments in a paginated format
     *
     * This method retrieves all appointments, shows 5 appointments per page and allows the user
     * to navigate between pages or exit to the main menu
     *
     * The displayed information for each appointment includes: "ID", "Patient Name",
     * "Date/Time", "Status", "Reason", "Doctor"
     * If no appointments are found, it displays a message and returns to the main menu
     */
    public void viewAllAppointments() {
        AppointmentController appointmentController = AppointmentController.getInstance();
        List<Appointment> appointments = appointmentController.getAllAppointments();

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return;
        }

        // Pagination variables
        final int PAGE_SIZE = 5; // Number of appointments per page
        int currentPage = 1;
        int totalPages = (int) Math.ceil((double) appointments.size() / PAGE_SIZE);

        boolean exit = false;
        while (!exit) {
            // Calculate start and end indices for the current page
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, appointments.size());

            // Get appointments for the current page
            List<Appointment> currentPageAppointments = appointments.subList(startIndex, endIndex);

            // Display header
            System.out.println("\n=== All Appointments (Page " + currentPage + " of " + totalPages + ") ===");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-5s %-20s %-20s %-15s %-15s %-20s\n",
                    "ID", "Patient Name", "Date/Time", "Status", "Reason", "Doctor");
            System.out.println("--------------------------------------------------");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            // Display appointments for the current page
            for (int i = 0; i < currentPageAppointments.size(); i++) {
                Appointment appointment = currentPageAppointments.get(i);
                String doctorName = appointment.getDoctor() != null ?
                        appointment.getDoctor().getName() : "Not Assigned";

                System.out.printf("%-5d %-20s %-20s %-15s %-15s %-20s\n",
                        startIndex + i + 1,
                        appointment.getPatient().getName(),
                        appointment.getAppointmentTime().format(formatter),
                        appointment.getAppointmentStatus(),
                        appointment.getReason(),
                        doctorName);
            }
            System.out.println("--------------------------------------------------");

            // Display navigation options
            System.out.println("\nNavigation:");
            if (currentPage > 1) {
                System.out.println("P - Previous Page");
            }
            if (currentPage < totalPages) {
                System.out.println("N - Next Page");
            }
            System.out.println("E - Exit to Main Menu");

            // Get user input for navigation
            System.out.print("\nEnter your choice: ");
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

        Patient patient = patients.get(DataGenerator.generateRandomInt(patients.size()));

        Doctor doctor = null;
        if (!doctors.isEmpty() && DataGenerator.generateRandomInt(2) == 0) {
            doctor = doctors.get(DataGenerator.generateRandomInt(doctors.size()));
        }

        Appointment appointment = DataGenerator.generateRandomAppointment(patient, doctor);

        addItem(appointment);

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
        int index = items.indexOf(oldAppointment);
        if (index != -1) {
            items.set(index, newAppointment);
            saveData();
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
        return items.stream()
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
            throws IllegalArgumentException, ZoomApiException, IOException {

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
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonTokenResponse = mapper.readTree(tokenResponseBody);

            if (!jsonTokenResponse.has("access_token")) {
                throw new ZoomApiException("Access token not found in response: " + tokenResponseBody);
            }

            String accessToken = jsonTokenResponse.get("access_token").asText();

            ObjectNode jsonBody = mapper.createObjectNode();
            jsonBody.put("topic", appointmentTitle);
            jsonBody.put("type", 1); // 1 for instant meeting
            jsonBody.put("duration", durationMinutes);
            jsonBody.put("timezone", "UTC");

            RequestBody meetingBody = RequestBody.create(
                    mapper.writeValueAsString(jsonBody),
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
            JsonNode jsonMeetingResponse = mapper.readTree(meetingResponseBody);

            if (!jsonMeetingResponse.has("join_url")) {
                throw new ZoomApiException("Join URL not found in response: " + meetingResponseBody);
            }

            return jsonMeetingResponse.get("join_url").asText();

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
