package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.hms.telemed.Session;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for serialization and deserialization of Appointment objects using JSONHelper.
 * This test class verifies that Appointment objects can be properly converted to JSON and back.
 */
public class AppointmentSerializationTest {

    private Appointment originalAppointment;
    
    @BeforeEach
    void setUp() {
        originalAppointment = createTestAppointment();
    }
    
    @Test
    @DisplayName("Test serializing Appointment to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalAppointment);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }
    
    @Test
    @DisplayName("Test deserializing Appointment from JSON string")
    void testDeserializeFromJsonString() throws Exception {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalAppointment);
        
        // Deserialize from JSON string
        Appointment deserializedAppointment = JSONHelper.fromJson(json, Appointment.class);
        
        // Verify all fields match
        verifyFields(originalAppointment, deserializedAppointment);
    }
    
    @Test
    @DisplayName("Test serializing Appointment to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws Exception {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("appointment_test.json").toString();
        
        // Save Appointment to JSON file
        JSONHelper.saveToJsonFile(originalAppointment, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load Appointment from JSON file
        Appointment fileDeserializedAppointment = JSONHelper.loadFromJsonFile(jsonFilePath, Appointment.class);
        
        // Verify all fields match
        verifyFields(originalAppointment, fileDeserializedAppointment);
    }

    /**
     * Helper method to access private fields using reflection from the class or any of its superclasses
     */
    private <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> currentClass = obj.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return fieldType.cast(field.get(obj));
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy of " + obj.getClass().getName());
    }

    /**
     * Helper method to verify all fields between original and deserialized Appointment objects
     */
    private void verifyFields(Appointment original, Appointment deserialized) throws Exception {
        // Verify Appointment fields
        assertEquals(getPrivateField(original, "reason", String.class),
                getPrivateField(deserialized, "reason", String.class),
                "Reason should match");
        assertEquals(getPrivateField(original, "appointmentTime", LocalDateTime.class),
                getPrivateField(deserialized, "appointmentTime", LocalDateTime.class),
                "Appointment time should match");
        assertEquals(getPrivateField(original, "appointmentStatus", AppointmentStatus.class),
                getPrivateField(deserialized, "appointmentStatus", AppointmentStatus.class),
                "Appointment status should match");
        assertEquals(getPrivateField(original, "history", String.class),
                getPrivateField(deserialized, "history", String.class),
                "History should match");
        assertEquals(getPrivateField(original, "doctorNotes", String.class),
                getPrivateField(deserialized, "doctorNotes", String.class),
                "Doctor notes should match");

        // Verify patient reference
        Patient originalPatient = getPrivateField(original, "patient", Patient.class);
        Patient deserializedPatient = getPrivateField(deserialized, "patient", Patient.class);
        assertEquals(originalPatient.getPatientId(), deserializedPatient.getPatientId(),
                "Patient should match");

        // Verify doctor reference if present
        Doctor originalDoctor = getPrivateField(original, "doctor", Doctor.class);
        Doctor deserializedDoctor = getPrivateField(deserialized, "doctor", Doctor.class);
        if (originalDoctor != null) {
            assertEquals(getPrivateField(originalDoctor, "staffId", String.class),
                    getPrivateField(deserializedDoctor, "staffId", String.class),
                    "Doctor should match");
        }

        MedicalCertificate originalMC = getPrivateField(original, "mc", MedicalCertificate.class);
        MedicalCertificate deserializedMC = getPrivateField(deserialized, "mc", MedicalCertificate.class);
        if (originalMC != null) {
            assertEquals(getPrivateField(originalMC, "startDate", LocalDateTime.class),
                    getPrivateField(deserializedMC, "startDate", LocalDateTime.class),
                    "Medical certificate start date should match");
            assertEquals(getPrivateField(originalMC, "endDate", LocalDateTime.class),
                    getPrivateField(deserializedMC, "endDate", LocalDateTime.class),
                    "Medical certificate end date should match");
            assertEquals(getPrivateField(originalMC, "remarks", String.class),
                    getPrivateField(deserializedMC, "remarks", String.class),
                    "Medical certificate remarks should match");
        }

        Session originalSession = getPrivateField(original, "session", Session.class);
        Session deserializedSession = getPrivateField(deserialized, "session", Session.class);
        if (originalSession != null) {
            assertEquals(getPrivateField(originalSession, "id", String.class),
                    getPrivateField(deserializedSession, "id", String.class),
                    "Session ID should match");
        }

        Contact originalContact = getPrivateField(original, "contact", Contact.class);
        Contact deserializedContact = getPrivateField(deserialized, "contact", Contact.class);
        if (originalContact != null) {
            assertEquals(getPrivateField(originalContact, "email", String.class),
                    getPrivateField(deserializedContact, "email", String.class),
                    "Contact email should match");
            assertEquals(getPrivateField(originalContact, "phone", String.class),
                    getPrivateField(deserializedContact, "phone", String.class),
                    "Contact phone should match");
        }
    }
    
    /**
     * Creates a test Appointment object with various properties set.
     */
    private Appointment createTestAppointment() {

        // Create a patient
        Patient patient = Patient.builder()
                .patientId(DataGenerator.generatePatientId())
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
        appointment.approveAppointment(doctor, "https://zoom.us/j/" + DataGenerator.generateRandomInt(10000000, 99999999));
        
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
