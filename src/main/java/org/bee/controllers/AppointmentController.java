package org.bee.controllers;

import com.google.gson.reflect.TypeToken;
import org.lucas.Globals;
import org.lucas.models.*;
import org.bee.telemed.AppointmentStatus;
import org.bee.util.JarLocation;
import org.lucas.util.Pair;
import org.bee.util.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Manages the storage and retrieval of {@link Appointment} objects.
 * This class provides centralized management of appointments through a static list and supports operations such as adding, removing, and searching for appointments. It also handles persistence by reading from and writing to a file.
 * The appointments are stored in a static list, allowing for easy access and manipulation across different parts of the application without instantiating the controller.
 */
public class AppointmentController {
    private static List<Appointment> appointments = new ArrayList<>();
    private static final String fileName = "appointments.txt";

    /**
     * Adds a new appointment to the list and sorts the list of appointments.
     * This method inserts the specified appointment into the static list and then sorts all appointments based on the recent date ,
     * this simulates a queue system where those who requested for an earlier appointment will be given a priority
     * potentially based on criteria such as date and time or priority (depending on the implementation of the sortAppointment method).
     *
     * @param appointment the appointment to be added to the list; cannot be null
     */

    public void addAppointment(Appointment appointment){
        appointments.add(appointment);
        sortAppointment();
    }

    /**
     * Sorts the list of appointments by appointment time in ascending order.
     * This method uses a comparator based on the appointment time to order the appointments,
     * ensuring that earlier times come first in the list. This is useful for organizing appointments chronologically
     * for display or processing purposes.
     */
    public void sortAppointment(){
        appointments.sort(Comparator.comparing(Appointment::getAppointmentTime));
    }

    /**
     * Retrieves a list of all pending appointments.
     * This method filters through the current list of appointments and returns a new list containing only those
     * appointments whose status is marked as PENDING. It is useful for displaying or processing appointments
     * that are yet to be approved or finalized.
     *
     * @return a list of appointments with the status {@link AppointmentStatus#PENDING}; this list is not null but may be empty if no pending appointments are found
     */
    public List<Appointment> displayPendingAppointments(){
        List<Appointment> appointments = getAppointments();
        List<Appointment> returnList = new ArrayList<>();
        for(Appointment appt: appointments){
            if(appt.getAppointmentStatus() == AppointmentStatus.PENDING) {
                returnList.add(appt);
            };
        }
        return returnList;
    }

    /**
     * Retrieves a list of all appointments associated with a specific doctor.
     * This method filters through the entire list of appointments to find and return those that are linked to the specified doctor.
     * The comparison is based on the {@code equals} method of the {@code Doctor} class, assuming it properly handles equality.
     *
     * @param doctor the doctor whose appointments are to be retrieved; this should not be null
     * @return a list of appointments associated with the given doctor; this list is not null but may be empty if no appointments match the specified doctor
     */
    public List<Appointment> getDoctorAppointments(Doctor doctor){
        List<Appointment> appointments = getAppointments();
        List<Appointment> returnList = new ArrayList<>();
        for(Appointment appt: appointments){
            if(appt.getDoctor().equals(doctor)){
                returnList.add(appt);
            }
        }
        return returnList;
    }

    /**
     * Retrieves a list of all appointments associated with a specific patient.
     * This method filters through the entire list of appointments to find and return those that are associated with the specified patient.
     * The comparison checks for object reference equality, meaning it checks if the appointments are linked to the exact same {@code Patient} object provided as the parameter.
     *
     * @param patient the patient whose appointments are to be retrieved; this should not be null to ensure proper functionality.
     * @return a list of appointments associated with the given patient; this list is not null but may be empty if no appointments are found for the specified patient.
     */
    public List<Appointment> getPatientAppointment(Patient patient){
        List<Appointment> appointments = getAppointments();
        List<Appointment> returnList = new ArrayList<>();
        for(Appointment appt: appointments){
            if(appt.getPatient()== patient){
                returnList.add(appt);
            }
        }
        return returnList;
    }

    /**
     * Sets the list of appointments managed by the AppointmentController.
     * This method allows the replacement of the current list of appointments with a new list provided by the caller.
     * Use this method with caution as it replaces the entire existing list, which could affect other parts of the application that depend on the current state of appointments.
     *
     * @param appointments the new list of appointments to set; this list should not be null to avoid null pointer exceptions during operations on the appointments list.
     */
    public void setAppointments(List<Appointment> appointments){
        AppointmentController.appointments = appointments;
    }

    /**
     * Retrieves the current list of appointments.
     * If the list is initially empty, it attempts to load appointments from a file before returning the list. This ensures that the method returns up-to-date data by checking and potentially reloading the list from persistent storage.
     * This method is critical for accessing the list of all appointments managed by the AppointmentController.
     *
     * @return a list of appointments; never null but may be empty if no appointments are available or if loading from the file fails.
     */
    public List<Appointment> getAppointments(){
        if(appointments.isEmpty()){
            loadAppointmentFromFile();
        }
        return appointments;
    }

    /**
     * Loads appointments from a file into the appointments list.
     * This method clears the existing list of appointments and attempts to load a new list from a specified file.
     * It reads the file content into a string, then deserializes it into a list of Appointment objects using a JSON parser.
     * If an IOException occurs during file reading or parsing, the error is caught and printed to the standard error stream.
     *
     * Note: This method assumes that the file format is JSON and that it correctly represents a list of Appointment objects.
     * Ensure the file path and format are correct to prevent runtime errors or data corruption.
     */
    private void loadAppointmentFromFile() {
        appointments.clear();
        StringBuilder sb = new StringBuilder();
        String basePath = "";

        // get the jar location
        try {
            basePath = JarLocation.getJarDirectory();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(basePath, fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Type listType = new TypeToken<List<Appointment>>() {
            }.getType();
            appointments = Util.fromJsonString(sb.toString(), listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current list of appointments to a file.
     * This method serializes the list of appointments into JSON format using Gson and writes it to the specified file.
     * The file name is determined by the {@code fileName} class variable. If the writing process encounters an IOException,
     * the exception is caught and the stack trace is printed, which can help in diagnosing the issue.
     *
     * Upon successful completion, a message is printed to the console indicating that the appointments have been saved.
     *
     * @throws IOException if there is an error writing to the file. While the exception is caught internally and a stack trace is printed,
     * it's important for users of the method to be aware that an I/O exception could indicate a failure to save data properly.
     */
    public void saveAppointmentsToFile() {
        String basePath = "";

        // get the jar location
        try {
            basePath = JarLocation.getJarDirectory();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String path = Paths.get(basePath, fileName).toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            String json = Globals.gsonPrettyPrint.toJson(appointments);
            writer.write(json);
            System.out.println("Appointments saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the patient associated with a given appointment has consented to the procedure.
     * <p>
     * This method first retrieves the patient from the appointment. If no patient is associated with the appointment,
     * it logs an error message and returns false. It then checks for a patient consent record. If no consent record is found,
     * it logs a message to indicate that consent is being requested and returns false.
     * If a consent record exists, it evaluates whether consent has been given. If consent is given, it logs a confirmation
     * message and allows the appointment to proceed by returning true. If consent is not given, it logs a denial message
     * and prevents the appointment from proceeding by returning false.
     * </p>
     *
     * @param appointment the appointment to check for patient consent; must not be null
     * @return true if consent has been given by the patient, false otherwise
     */
    public static boolean isConsented(Appointment appointment){
        Patient patient = appointment.getPatient();

        if (patient == null) {
            System.out.println("Error: No patient associated with this appointment.");
            return false;
        }

        PatientConsent consent = patient.getPatientConsent();

        if (consent == null) {
            System.out.println("No consent record found. Requesting patient consent...");
            return false;
        }

        if (consent.isConsentGiven()) {
            System.out.println("Consent is given. Appointment can proceed.");
            return true;
        } else {
            System.out.println("Patient has NOT consented! Appointment cannot proceed.");
            return false;
        }
    }


    /**
     * Generates dummy data for testing and demonstrating purposes
     * @param numberOfAppointments indicate the number of appointment to be created
     */
    public void generateRandomAppointmentData(int numberOfAppointments) {
        Random random = new Random();
        // Get the list of dummy patients
        Pair<List<Patient>, List<Doctor>> patientDoctor = Globals.userController.generateDummyUsers();
        List<Doctor> doctors = patientDoctor.second;
        List<Patient> patients = patientDoctor.first;

        String[] Reasons = {"Flu", "Cough", "Migraine", "Vertigo"};
        for (int i = 0; i < numberOfAppointments; i++) {
            Patient patient = patients.get(i % patients.size()); // Cycle through the available patients
            Doctor doctor = doctors.get(random.nextInt(doctors.size())); // Choose a random doctor

            // Generate random data for the appointment
            String reason = Reasons[random.nextInt(Reasons.length)];
            LocalDateTime appointmentTime = LocalDateTime.now().plusMinutes(random.nextInt(1440));
            AppointmentStatus appointmentStatus = AppointmentStatus.PENDING;

            // Create the Appointment object
            Appointment appointment = new Appointment(patient, reason, appointmentTime, appointmentStatus);
            appointment.setDoctor(doctor);

            // Add the appointment to the list
            appointments.add(appointment);
        }

        saveAppointmentsToFile();
    }
}

