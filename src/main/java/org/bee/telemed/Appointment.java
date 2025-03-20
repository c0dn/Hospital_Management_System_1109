package org.bee.telemed;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;

import java.time.LocalDateTime;

/**
 * This class represents an appointment for a telemedicine integeration for a hospital
 * It provides functionalities to manage appointments involving patients and doctors
 * It includes setting and updating appointment time, managing appointment statuses, and handling billing procedures.
 *
 * Example Usage:
 *
 *     Appointment appointment = new Appointment(patient, "Routine check-up", time, AppointmentStatus.PENDING, "Initial notes");
 *     appointment.setDoctor(doctor);
 *     appointment.approve(doctor, "zoomLinkExample");
 */

public class Appointment {
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

    /**
     * Constructs a new Appointment with the specified details.
     *
     *  @param patient the patient involved in the appointment
     *  @param reason the reason for the appointment
     *  @param appointmentTime the time the appointment is scheduled
     *  @param appointmentStatus the initial status of the appointment
     */
    public Appointment(Patient patient, String reason, LocalDateTime appointmentTime, AppointmentStatus appointmentStatus) {
        this.patient = patient;
        this.reason = reason;
        this.appointmentTime = appointmentTime;
        this.appointmentStatus = appointmentStatus;
    }


    public Session getSession() {return session;};
    public void setSession(Session session) {this.session = session;};
    public AppointmentStatus getAppointmentStatus() {return this.appointmentStatus;}
    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {this.appointmentStatus= appointmentStatus;};
    public Doctor getDoctor(){return doctor;}
    public String getDoctorNotes(){return doctorNotes;}
    public void setDoctorNotes(String doctorNotes){this.doctorNotes= doctorNotes;}
    public void setDoctor(Doctor doctor){
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

    public String getHistory() {
        return history;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }


    //public Billing getBilling()  return billing; }

    //public void setBilling(Billing billing) { this.billing = billing;}

    /**
     * Approves the appointment and initiates a session with the specified details.
     * This method changes the status of the appointment to 'ACCEPTED', creates a new session object for appointments that are approved, and initializes billing procedures. It invokes the initialBill() method to process the initial bill based on the assigned doctor's rates.
     *
     * @param doctor the doctor who is taking the appointment; this doctor is assigned to the appointment and responsible for the session
     * @param zoomlink the Zoom link that will be used for the virtual meeting during the appointment; this link is stored in the session details
     */
    public void approveAppointment(Doctor doctor, String zoomlink) {
        this.doctor = doctor;
        this.appointmentStatus = AppointmentStatus.ACCEPTED;
        this.session = new Session(zoomlink);
        //this.billing = new Billing();
        //this.billing.initialBill(doctor);
    }

    /**
     * Completes the appointment process by ending the session, updating the doctor's notes, and triggering billing.
     * - `endSession()`: Marks the session as completed, reflecting its conclusion in the system.
     * - `setDoctorNotes(String)`: Updates the appointment record with the doctor's final observations.
     * This method ensures all aspects of the appointment are concluded and documented properly in the system.
     *
     * @param doctorNotes the doctor's final remarks for the patient, intended for medical records and follow-up care.
     */
    public void finishAppointment(String doctorNotes){
        this.session.endSession();
        this.doctorNotes = doctorNotes;
        this.appointmentStatus = AppointmentStatus.COMPLETED;
    }

    public void setMedicalCertificate(MedicalCertificate mc){
        this.mc = mc;
    }

    public MedicalCertificate getMc() {
        return mc;
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