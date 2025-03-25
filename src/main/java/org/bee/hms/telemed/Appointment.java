package org.bee.hms.telemed;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
    @JsonCreator
    public Appointment(
            @JsonProperty("patient") Patient patient,
            @JsonProperty("reason") String reason,
            @JsonProperty("appointmentTime") LocalDateTime appointmentTime,
            @JsonProperty("appointmentStatus") AppointmentStatus appointmentStatus) {
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
        Patient patient = Patient.builder()
                .patientId(DataGenerator.generatePatientId())
                .withRandomBaseData()
                .build();

        return withRandomData(patient, null);
    }

    /**
     * Creates an appointment with random data for a specific patient and optional doctor.
     *
     * @param patient The patient for the appointment
     * @param doctor The doctor for the appointment (can be null)
     * @return A randomly generated Appointment object
     */
    public static Appointment withRandomData(Patient patient, Doctor doctor) {
        String[] reasons = {
                "Regular check-up",
                "Flu symptoms",
                "Headache",
                "Skin rash",
                "Fever",
                "Stomach pain",
                "Follow-up consultation",
                "Medication review",
                "Chronic condition management",
                "Mental health consultation"
        };

        String reason = reasons[DataGenerator.generateRandomInt(reasons.length)];

        // Generate a random appointment time between now and 30 days in the future
        LocalDateTime now = LocalDateTime.now();
        int daysToAdd = DataGenerator.generateRandomInt(1, 30);
        int hoursToAdd = DataGenerator.generateRandomInt(9, 16); // 9 AM to 4 PM
        LocalDateTime appointmentTime = now.plusDays(daysToAdd).withHour(hoursToAdd).withMinute(0).withSecond(0);

        // Randomly select an appointment status
        AppointmentStatus[] statuses = AppointmentStatus.values();
        AppointmentStatus status = statuses[DataGenerator.generateRandomInt(statuses.length)];

        Appointment appointment = new Appointment(patient, reason, appointmentTime, status);

        // If doctor is provided, assign it to the appointment
        if (doctor != null) {
            appointment.setDoctor(doctor);

            // If the appointment has a doctor and is ACCEPTED, create a session
            if (status == AppointmentStatus.ACCEPTED) {
                String zoomLink = "https://zoom.us/j/" + (10000000 + DataGenerator.generateRandomInt(90000000));
                appointment.approveAppointment(doctor, zoomLink);
            }

            // If the appointment is COMPLETED, add doctor notes
            if (status == AppointmentStatus.COMPLETED) {
                String[] notes = {
                        "Patient is recovering well.",
                        "Prescribed medication for symptoms.",
                        "Recommended follow-up in 2 weeks.",
                        "Referred to specialist for further evaluation.",
                        "No significant concerns at this time."
                };
                appointment.setDoctorNotes(notes[DataGenerator.generateRandomInt(notes.length)]);
            }
        }
        // If no doctor is provided, randomly decide if a doctor should be assigned (50% chance)
        else if (DataGenerator.generateRandomInt(2) == 1) {
            Doctor randomDoctor = Doctor.builder().withRandomBaseData().build();
            appointment.setDoctor(randomDoctor);

            // If the appointment has a doctor and is ACCEPTED, create a session
            if (status == AppointmentStatus.ACCEPTED) {
                String zoomLink = "https://zoom.us/j/" + (10000000 + DataGenerator.generateRandomInt(90000000));
                appointment.approveAppointment(randomDoctor, zoomLink);
            }

            // If the appointment is COMPLETED, add doctor notes
            if (status == AppointmentStatus.COMPLETED) {
                String[] notes = {
                        "Patient is recovering well.",
                        "Prescribed medication for symptoms.",
                        "Recommended follow-up in 2 weeks.",
                        "Referred to specialist for further evaluation.",
                        "No significant concerns at this time."
                };
                appointment.setDoctorNotes(notes[DataGenerator.generateRandomInt(notes.length)]);
            }
        }

        return appointment;
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
