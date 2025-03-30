package org.bee.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.hms.telemed.Session;
import org.bee.utils.DataGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link Appointment} class.
 * This class verifies the functionality of appointment creation, status transitions, and various operations.
 */
public class AppointmentTest {
    
    private DateTimeFormatter formatter;
    private Patient patient;
    private Doctor doctor;
    
    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // Create a patient and doctor for tests
        patient = Patient.builder()
                .patientId(DataGenerator.generatePatientId())
                .withRandomBaseData()
                .build();
        
        doctor = Doctor.builder().withRandomBaseData().build();
    }

    @Test
    @DisplayName("Test appointment state transitions")
    void testAppointmentStateTransitions() {
        Appointment appointment = Appointment.withRandomData();
        
        // Assign doctor
        appointment.setDoctor(doctor);
        assertEquals(doctor, appointment.getDoctor());
        
        // Update status to ACCEPTED
        appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
        assertEquals(AppointmentStatus.ACCEPTED, appointment.getAppointmentStatus());
        
        // Approve appointment with Zoom link
        String zoomLink = "https://zoom.us/j/123456789";
        appointment.approveAppointment(doctor, zoomLink);
        
        // Verify session was created
        assertNotNull(appointment.getSession());
        Session session = appointment.getSession();
        assertEquals(zoomLink, session.getZoomLink());
        
        // Finish appointment
        String doctorNotes = "Patient is doing well. No further action needed.";
        appointment.finishAppointment(doctorNotes);
        
        // Verify final state
        assertEquals(AppointmentStatus.COMPLETED, appointment.getAppointmentStatus());
        assertEquals(doctorNotes, appointment.getDoctorNotes());
    }
    
    @Test
    @DisplayName("Test creating appointment with random data")
    void testCreateAppointmentWithRandomData() {
        // Create an appointment with random data
        Appointment appointment = Appointment.withRandomData();
        
        // Verify appointment has required properties
        assertNotNull(appointment.getPatient());
        assertNotNull(appointment.getReason());
        assertNotNull(appointment.getAppointmentTime());
        assertNotNull(appointment.getAppointmentStatus());
    }
    
    @Test
    @DisplayName("Test medical certificate creation")
    void testMedicalCertificateCreation() {
        // Create an appointment
        Appointment appointment = Appointment.withRandomData();
        appointment.approveAppointment(doctor, "https://zoom.us/j/987654321");
        
        // Create and set medical certificate
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(3);
        String mcRemarks = "Patient needs rest due to severe flu symptoms";
        
        MedicalCertificate mc = new MedicalCertificate(startDate, endDate, mcRemarks);
        appointment.setMedicalCertificate(mc);
        
        // Verify medical certificate properties
        assertNotNull(mc.getId());
        assertEquals(startDate, mc.getStartDate());
        assertEquals(endDate, mc.getEndDate());
        assertEquals(mcRemarks, mc.getRemarks());
        
        // Finish appointment
        appointment.finishAppointment("Prescribed medication for flu. Issued MC for 3 days.");
        assertEquals(AppointmentStatus.COMPLETED, appointment.getAppointmentStatus());
    }
    
    @Test
    @DisplayName("Test error cases and validation")
    void testErrorCasesAndValidation() {
        // Test setting null patient
        assertThrows(NullPointerException.class, () -> {
            Appointment.createNewAppointment(null, "Test", LocalDateTime.now());
        });
        
        // Test setting null doctor in approveAppointment
        Appointment appointment1 = Appointment.createNewAppointment(patient, "Test", LocalDateTime.now());
        assertThrows(NullPointerException.class, () -> {
            appointment1.approveAppointment(null, "https://zoom.us/j/123456789");
        });
        
        // Test setting null zoom link in approveAppointment
        Appointment appointment2 = Appointment.createNewAppointment(patient, "Test", LocalDateTime.now());
        assertThrows(NullPointerException.class, () -> {
            appointment2.approveAppointment(doctor, null);
        });
    }
}
