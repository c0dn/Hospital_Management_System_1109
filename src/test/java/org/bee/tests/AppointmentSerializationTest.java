package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for serialization and deserialization of Appointment objects using JSONHelper.
 * This test class verifies that Appointment objects can be properly converted to JSON and back.
 */
public class AppointmentSerializationTest {

    private JSONHelper jsonHelper;
    private Appointment originalAppointment;
    
    @BeforeEach
    void setUp() {
        // Initialize JSONHelper
        jsonHelper = JSONHelper.getInstance();
        
        // Create a test Appointment object
        originalAppointment = createTestAppointment();
    }
    
    @Test
    @DisplayName("Test serializing Appointment to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalAppointment);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }
    
    @Test
    @DisplayName("Test deserializing Appointment from JSON string")
    void testDeserializeFromJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalAppointment);
        
        // Deserialize from JSON string
        Appointment deserializedAppointment = jsonHelper.fromJson(json, Appointment.class);
        
        // Verify key properties match
        assertEquals(originalAppointment.getAppointmentStatus(), deserializedAppointment.getAppointmentStatus());
        assertEquals(originalAppointment.getReason(), deserializedAppointment.getReason());
    }
    
    @Test
    @DisplayName("Test serializing Appointment to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("appointment_test.json").toString();
        
        // Save Appointment to JSON file
        jsonHelper.saveToJsonFile(originalAppointment, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load Appointment from JSON file
        Appointment fileDeserializedAppointment = jsonHelper.loadFromJsonFile(jsonFilePath, Appointment.class);
        
        // Verify key properties match
        assertEquals(originalAppointment.getAppointmentStatus(), fileDeserializedAppointment.getAppointmentStatus());
        assertEquals(originalAppointment.getReason(), fileDeserializedAppointment.getReason());
    }
    
    /**
     * Creates a test Appointment object with various properties set.
     */
    private Appointment createTestAppointment() {
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
