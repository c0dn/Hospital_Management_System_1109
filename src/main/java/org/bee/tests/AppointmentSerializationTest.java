package org.bee.tests;

import java.io.File;
import java.time.LocalDateTime;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.hms.telemed.Session;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;

/**
 * Tests for serialization and deserialization of Appointment objects using JSONHelper.
 * This test class verifies that Appointment objects can be properly converted to JSON and back.
 */
public class AppointmentSerializationTest {

    /**
     * Main method to execute tests for Appointment serialization/deserialization.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Appointment serialization/deserialization...\n");
            
            // Initialize JSONHelper
            JSONHelper jsonHelper = JSONHelper.getInstance();
            
            // Create a test Appointment object
            Appointment originalAppointment = createTestAppointment();
            System.out.println("Created test Appointment object with status: " + originalAppointment.getAppointmentStatus());
            
            // Test 1: Serialize to JSON string
            System.out.println("\nTest 1 - Serializing Appointment to JSON string:");
            String json = jsonHelper.toJson(originalAppointment);
            System.out.println("JSON string length: " + json.length());
            System.out.println("JSON string preview: " + json.substring(0, Math.min(json.length(), 200)) + "...");
            
            // Test 2: Deserialize from JSON string
            System.out.println("\nTest 2 - Deserializing Appointment from JSON string:");
            Appointment deserializedAppointment = jsonHelper.fromJson(json, Appointment.class);
            System.out.println("Deserialized Appointment status: " + deserializedAppointment.getAppointmentStatus());
            
            // Verify key properties match
            boolean statusMatches = originalAppointment.getAppointmentStatus() == deserializedAppointment.getAppointmentStatus();
            boolean reasonMatches = originalAppointment.getReason().equals(deserializedAppointment.getReason());
            System.out.println("Status matches: " + statusMatches);
            System.out.println("Reason matches: " + reasonMatches);
            
            // Test 3: Serialize to file and deserialize from file
            System.out.println("\nTest 3 - Serializing Appointment to file and deserializing:");
            
            // Create a temporary file
            String tempDir = System.getProperty("java.io.tmpdir");
            String jsonFilePath = tempDir + File.separator + "appointment_test.json";
            System.out.println("Using temporary file: " + jsonFilePath);
            
            // Save Appointment to JSON file
            jsonHelper.saveToJsonFile(originalAppointment, jsonFilePath);
            System.out.println("Saved Appointment to JSON file");
            
            // Verify file exists
            File jsonFile = new File(jsonFilePath);
            System.out.println("File exists: " + jsonFile.exists());
            System.out.println("File size: " + jsonFile.length() + " bytes");
            
            // Load Appointment from JSON file
            Appointment fileDeserializedAppointment = jsonHelper.loadFromJsonFile(jsonFilePath, Appointment.class);
            System.out.println("Loaded Appointment from JSON file");
            
            // Verify key properties match
            boolean fileStatusMatches = originalAppointment.getAppointmentStatus() == fileDeserializedAppointment.getAppointmentStatus();
            boolean fileReasonMatches = originalAppointment.getReason().equals(fileDeserializedAppointment.getReason());
            System.out.println("Status matches after file deserialization: " + fileStatusMatches);
            System.out.println("Reason matches after file deserialization: " + fileReasonMatches);
            
            // Clean up
            jsonFile.delete();
            System.out.println("Deleted temporary file");
            
            // Summary
            System.out.println("\nSerialization/Deserialization Test Summary:");
            System.out.println("- Direct serialization/deserialization: " + 
                ((statusMatches && reasonMatches) ? "PASSED" : "FAILED"));
            System.out.println("- File serialization/deserialization: " + 
                ((fileStatusMatches && fileReasonMatches) ? "PASSED" : "FAILED"));
            
            if (statusMatches && reasonMatches && fileStatusMatches && fileReasonMatches) {
                System.out.println("\nAll tests PASSED! Appointment can be properly serialized and deserialized.");
            } else {
                System.out.println("\nSome tests FAILED. Appointment serialization/deserialization needs improvement.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during serialization test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a test Appointment object with various properties set.
     */
    private static Appointment createTestAppointment() {
        DataGenerator gen = DataGenerator.getInstance();
        
        // Create a patient
        Patient patient = Patient.builder()
                .patientId(gen.generatePatientId())
                .withRandomBaseData()
                .build();
        
        // Create a doctor
        Doctor doctor = Doctor.builder().withRandomBaseData().build();
        
        // Create appointment time (future date)
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(3);
        
        // Create an appointment
        Appointment appointment = new Appointment(patient, "Annual checkup", appointmentTime, AppointmentStatus.PENDING);
        
        // Assign doctor and approve appointment
        appointment.setDoctor(doctor);
        appointment.approveAppointment(doctor, "https://zoom.us/j/" + gen.generateRandomInt(10000000, 99999999));
        
        // Set history
        appointment.setHistory("Patient has a history of hypertension and diabetes");
        
        // Set doctor notes
        appointment.setDoctorNotes("Patient's condition is stable. Recommended follow-up in 3 months.");
        
        // Create and set medical certificate
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(2);
        MedicalCertificate mc = new MedicalCertificate(startDate, endDate, "Rest recommended for 2 days");
        appointment.setMedicalCertificate(mc);
        
        // Finish appointment
        appointment.finishAppointment("Follow-up appointment scheduled for next month");
        
        return appointment;
    }
}
