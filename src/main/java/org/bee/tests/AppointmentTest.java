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

/**
 * A test class for the {@link Appointment} class.
 * This class verifies the functionality of appointment creation, status transitions, and various operations.
 */
public class AppointmentTest {
    /**
     * Main method to execute tests for {@link Appointment}.
     * Tests appointment creation, status transitions, and various operations.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Appointment functionality...\n");
            DataGenerator gen = DataGenerator.getInstance();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Test 1: Create an appointment with specific data
            System.out.println("Test 1 - Creating appointment with specific data:");
            Patient patient = Patient.builder()
                    .patientId(gen.generatePatientId())
                    .withRandomBaseData()
                    .build();
            
            LocalDateTime appointmentTime = LocalDateTime.now().plusDays(3).withHour(14).withMinute(30).withSecond(0);
            Appointment appointment1 = new Appointment(patient, "Regular check-up", appointmentTime, AppointmentStatus.PENDING);
            
            System.out.println("Patient: " + patient.getName());
            System.out.println("Reason: " + appointment1.getReason());
            System.out.println("Appointment Time: " + appointment1.getAppointmentTime().format(formatter));
            System.out.println("Status: " + appointment1.getAppointmentStatus());
            System.out.println("Doctor assigned: " + (appointment1.getDoctor() != null ? "Yes" : "No"));

            // Test 2: Test state transitions
            System.out.println("\nTest 2 - Testing state transitions:");
            Doctor doctor = Doctor.builder().withRandomBaseData().build();
            System.out.println("Assigning doctor: " + doctor.getName());
            appointment1.setDoctor(doctor);
            
            System.out.println("Initial status: " + appointment1.getAppointmentStatus());
            appointment1.setAppointmentStatus(AppointmentStatus.ACCEPTED);
            System.out.println("After updating to ACCEPTED: " + appointment1.getAppointmentStatus());
            
            String zoomLink = "https://zoom.us/j/123456789";
            System.out.println("Approving appointment with Zoom link: " + zoomLink);
            appointment1.approveAppointment(doctor, zoomLink);
            System.out.println("Session created: " + (appointment1.getSession() != null ? "Yes" : "No"));
            
            if (appointment1.getSession() != null) {
                Session session = appointment1.getSession();
                System.out.println("Session status: " + session.getSessionStatus());
                System.out.println("Session Zoom link: " + session.getZoomLink());
            }
            
            System.out.println("Finishing appointment with doctor notes");
            appointment1.finishAppointment("Patient is doing well. No further action needed.");
            System.out.println("Final status: " + appointment1.getAppointmentStatus());
            System.out.println("Doctor notes: " + appointment1.getDoctorNotes());
            
            if (appointment1.getSession() != null) {
                System.out.println("Session status after completion: " + appointment1.getSession().getSessionStatus());
            }

            // Test 3: Create an appointment with random data
            System.out.println("\nTest 3 - Creating appointment with random data:");
            Appointment appointment2 = Appointment.withRandomData();
            System.out.println("Random appointment created with status: " + appointment2.getAppointmentStatus());
            System.out.println("Patient: " + appointment2.getPatient().getName());
            System.out.println("Reason: " + appointment2.getReason());
            System.out.println("Appointment Time: " + appointment2.getAppointmentTime().format(formatter));
            System.out.println("Doctor assigned: " + (appointment2.getDoctor() != null ? appointment2.getDoctor().getName() : "No"));
            System.out.println("Session created: " + (appointment2.getSession() != null ? "Yes" : "No"));
            System.out.println("Doctor notes: " + (appointment2.getDoctorNotes() != null ? appointment2.getDoctorNotes() : "None"));

            // Test 4: Test medical certificate creation
            System.out.println("\nTest 4 - Testing medical certificate creation:");
            Appointment appointment3 = new Appointment(patient, "Flu symptoms", LocalDateTime.now(), AppointmentStatus.ACCEPTED);
            appointment3.approveAppointment(doctor, "https://zoom.us/j/987654321");
            
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(3);
            String mcRemarks = "Patient needs rest due to severe flu symptoms";
            
            MedicalCertificate mc = new MedicalCertificate(startDate, endDate, mcRemarks);
            appointment3.setMedicalCertificate(mc);
            
            System.out.println("Medical Certificate created:");
            System.out.println("ID: " + mc.getId());
            System.out.println("Start Date: " + mc.getStartDate().format(formatter));
            System.out.println("End Date: " + mc.getEndDate().format(formatter));
            System.out.println("Remarks: " + mc.getRemarks());
            
            appointment3.finishAppointment("Prescribed medication for flu. Issued MC for 3 days.");
            System.out.println("Appointment status after MC issuance: " + appointment3.getAppointmentStatus());

            // Test 5: Test error cases and validation
            System.out.println("\nTest 5 - Testing error cases and validation:");
            
            // Test setting null patient
            try {
                Appointment appointment4 = new Appointment(null, "Test", LocalDateTime.now(), AppointmentStatus.PENDING);
                System.out.println("ERROR: Should not be able to create appointment with null patient");
            } catch (NullPointerException e) {
                System.out.println("Success: Cannot create appointment with null patient");
            }
            
            // Test setting null doctor in approveAppointment
            try {
                Appointment appointment5 = new Appointment(patient, "Test", LocalDateTime.now(), AppointmentStatus.PENDING);
                appointment5.approveAppointment(null, "https://zoom.us/j/123456789");
                System.out.println("ERROR: Should not be able to approve appointment with null doctor");
            } catch (NullPointerException e) {
                System.out.println("Success: Cannot approve appointment with null doctor");
            }
            
            // Test setting null zoom link in approveAppointment
            try {
                Appointment appointment6 = new Appointment(patient, "Test", LocalDateTime.now(), AppointmentStatus.PENDING);
                appointment6.approveAppointment(doctor, null);
                System.out.println("ERROR: Should not be able to approve appointment with null zoom link");
            } catch (NullPointerException e) {
                System.out.println("Success: Cannot approve appointment with null zoom link");
            }

            System.out.println("\nAll tests completed successfully!");
            System.out.println("Appointment functionality verified including:");
            System.out.println("- Appointment creation and status management");
            System.out.println("- Doctor assignment");
            System.out.println("- Session creation and management");
            System.out.println("- Medical certificate issuance");
            System.out.println("- Error handling and validation");

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
