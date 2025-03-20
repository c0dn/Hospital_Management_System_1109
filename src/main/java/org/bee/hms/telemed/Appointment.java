package org.bee.hms.telemed;

import java.time.LocalDateTime;
import java.util.Objects;

import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONReadable;
import org.bee.utils.JSONWritable;

/**
 * This class represents an appointment for a telemedicine integeration for a hospital
 * It provides functionalities to manage appointments involving patients and doctors
 * It includes setting and updating appointment time, managing appointment statuses, and handling billing procedures.
 * <p>
 * Example Usage:
 * <p>
 * Appointment appointment = new Appointment(patient, "Routine check-up", time, AppointmentStatus.PENDING, "Initial notes");
 * appointment.setDoctor(doctor);
 * appointment.approve(doctor, "zoomLinkExample");
 */

public class Appointment implements JSONWritable, JSONReadable {
    private Patient patient;
    private String reason;
    private String history;
    private LocalDateTime appointmentTime;
    private Doctor doctor;
    private AppointmentStatus appointmentStatus;
    private Session session;
    private String doctorNotes;
    //private Billing billing;
    private MedicalCertificate mc;
    private Contact contact;

    /**
     * Constructs a new Appointment with the specified details.
     *
     * @param patient           the patient involved in the appointment (must not be null)
     * @param reason            the reason for the appointment (must not be null)
     * @param appointmentTime   the time the appointment is scheduled (must not be null)
     * @param appointmentStatus the initial status of the appointment (must not be null)
     * @throws NullPointerException if any of the parameters are null
     */
    public Appointment(Patient patient, String reason, LocalDateTime appointmentTime, AppointmentStatus appointmentStatus) {
        this.patient = Objects.requireNonNull(patient, "Patient cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
        this.appointmentTime = Objects.requireNonNull(appointmentTime, "Appointment time cannot be null");
        this.appointmentStatus = Objects.requireNonNull(appointmentStatus, "Appointment status cannot be null");
    }


    public Session getSession() {
        return session;
    }

    ;

    public void setSession(Session session) {
        this.session = session;
    }

    ;

    public AppointmentStatus getAppointmentStatus() {
        return this.appointmentStatus;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    ;

    public Doctor getDoctor() {
        return doctor;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Contact getContact() { return contact; }

    public String getHistory() {
        return history;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }


    /**
     * Approves the appointment by assigning a doctor and creating a new session with the provided Zoom link.
     * This method changes the appointment status to ACCEPTED.
     *
     * @param doctor   the doctor who is approving the appointment (must not be null)
     * @param zoomLink the Zoom meeting link for the telehealth session (must not be null)
     * @throws NullPointerException if doctor or zoom link is null
     */
    public void approveAppointment(Doctor doctor, String zoomLink) {
        this.doctor = Objects.requireNonNull(doctor);
        this.appointmentStatus = AppointmentStatus.ACCEPTED;
        this.session = new Session(Objects.requireNonNull(zoomLink));
    }

    /**
     * Completes the appointment process by ending the session, updating the doctor's notes, and triggering billing.
     * - `endSession()`: Marks the session as completed, reflecting its conclusion in the system.
     * - `setDoctorNotes(String)`: Updates the appointment record with the doctor's final observations.
     * This method ensures all aspects of the appointment are concluded and documented properly in the system.
     *
     * @param doctorNotes the doctor's final remarks for the patient, intended for medical records and follow-up care.
     */
    public void finishAppointment(String doctorNotes) {
        this.session.endSession();
        this.doctorNotes = doctorNotes;
        this.appointmentStatus = AppointmentStatus.COMPLETED;
    }

    public void setMedicalCertificate(MedicalCertificate mc) {
        this.mc = mc;
    }

    public MedicalCertificate getMc() {
        return mc;
    }

    /**
     * Creates an appointment with random data.
     * This method generates a random patient, reason, appointment time, and status.
     * It may also randomly assign a doctor and create a session if the status is ACCEPTED.
     *
     * @return A randomly generated Appointment object
     */
    public static Appointment withRandomData() {
        DataGenerator gen = DataGenerator.getInstance();
        return gen.generateRandomAppointment();
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "patient=" + patient +
                ", reason='" + reason + '\'' +
                ", appointmentTime=" + appointmentTime +
                ", doctor=" + doctor +
                ", appointmentStatus=" + appointmentStatus +
                ", session=" + session +
                ", doctorNotes='" + doctorNotes + '\'' +
                '}';
    }
}
