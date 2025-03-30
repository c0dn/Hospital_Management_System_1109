package org.bee.hms.telemed;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Medication;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONSerializable;
import org.bee.utils.jackson.PrescriptionMapDeserializer;
import org.bee.utils.jackson.PrescriptionMapSerializer;

/**
 * This class represents an appointment for a telemedicine integration for a hospital
 * It provides functionalities to manage appointments involving patients and doctors
 * It includes setting and updating appointment time, managing appointment statuses, and handling billing procedures.
 * <p>
 * Example Usage:
 * <p>
 * Appointment appointment = new Appointment(patient, "Routine check-up", time, AppointmentStatus.PENDING, "Initial notes");
 * appointment.setDoctor(doctor);
 * appointment.approve(doctor, "zoomLinkExample");
 */
public class Appointment implements JSONSerializable {
    /** Unique appointment ID */
    private String appointmentId;

    /** The patient attending the appointment */
    private Patient patient;

    /** Reason/purpose for the appointment */
    private String reason;

    /** Relevant patient medical history for this appointment */
    private String history;

    /** Scheduled date and time of the appointment */
    private LocalDateTime appointmentTime;

    /** The doctor assigned to the appointment */
    private Doctor doctor;

    /** Current status (PENDING, APPROVED, COMPLETED, etc.) */
    private AppointmentStatus appointmentStatus;

    /** Telemedicine session details */
    private Session session;

    /** Doctor's notes from the appointment */
    private String doctorNotes;

    /** Medical certificate issued from this appointment */
    private MedicalCertificate mc;

    /** Patient contact information for the appointment */
    private Contact contact;

    @JsonSerialize(using = PrescriptionMapSerializer.class)
    @JsonDeserialize(using = PrescriptionMapDeserializer.class)
    @JsonProperty("prescriptions")
    private Map<Medication, Integer> prescriptions;


    @JsonCreator
    public Appointment(
            @JsonProperty("appointmentId") String appointmentId,
            @JsonProperty("patient") Patient patient,
            @JsonProperty("reason") String reason,
            @JsonProperty("history") String history,
            @JsonProperty("appointmentTime") LocalDateTime appointmentTime,
            @JsonProperty("doctor") Doctor doctor,
            @JsonProperty("appointmentStatus") AppointmentStatus appointmentStatus,
            @JsonProperty("session") Session session,
            @JsonProperty("doctorNotes") String doctorNotes,
            @JsonProperty("mc") MedicalCertificate mc,
            @JsonProperty("contact") Contact contact,
            @JsonProperty("prescriptions") Map<Medication, Integer> prescriptions) {

        this.appointmentId = (appointmentId != null && !appointmentId.isEmpty())
                ? appointmentId
                : DataGenerator.generateUUID();

        this.patient = Objects.requireNonNull(patient, "Patient cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
        this.appointmentTime = Objects.requireNonNull(appointmentTime, "Appointment time cannot be null");
        this.appointmentStatus = Objects.requireNonNull(appointmentStatus, "Appointment status cannot be null");

        this.history = history;
        this.doctor = doctor;
        this.session = session;
        this.doctorNotes = doctorNotes;
        this.mc = mc;
        this.contact = contact;

        // Initialize prescriptions - using empty HashMap if null
        this.prescriptions = prescriptions != null ? prescriptions : new HashMap<>();
    }

    /**
     * Creates a new blank appointment with default PENDING status.
     * This factory method simplifies creating new appointments by requiring only essential fields.
     *
     * @param patient           the patient for the appointment
     * @param reason            the reason for the appointment
     * @param appointmentTime   the scheduled time for the appointment
     * @return                  a new Appointment instance with the specified parameters and default values for other fields
     * @throws NullPointerException if patient, reason or appointmentTime are null
     */
    public static Appointment createNewAppointment(Patient patient, String reason, LocalDateTime appointmentTime) {
        return new Appointment(
                null,
                patient,
                reason,
                null,
                appointmentTime,
                null,
                AppointmentStatus.PENDING,
                null,
                null,
                null,
                null,
                new HashMap<>()
        );
    }


    /**
     * @return The telemedicine session details
     */
    public Session getSession() {
        return session;
    }

    /**
     * @return The unique appointment ID
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * Sets the telemedicine session details
     * @param session The session information to set
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * @return The current status of the appointment
     */
    public AppointmentStatus getAppointmentStatus() {
        return this.appointmentStatus;
    }

    /**
     * Updates the appointment status
     * @param appointmentStatus The new status to set
     */
    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * @return The doctor assigned to this appointment
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * @return The doctor's notes from the appointment
     */
    public String getDoctorNotes() {
        return doctorNotes;
    }

    /**
     * Updates the doctor's notes
     * @param doctorNotes The notes to record
     */
    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    /**
     * Assigns a doctor to this appointment
     * @param doctor The doctor to assign
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * @return The patient scheduled for this appointment
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient for this appointment
     * @param patient The patient to schedule
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * @return The reason/purpose for the appointment
     */
    public String getReason() {
        return reason;
    }

    /**
     * Updates the appointment reason
     * @param reason The new reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return The scheduled date and time of the appointment
     */
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    /**
     * Updates the patient's relevant medical history for this appointment
     * @param history The medical history to record
     */
    public void setHistory(String history) {
        this.history = history;
    }

    /**
     * @return The patient's contact information
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Reschedules the appointment time
     * @param appointmentTime The new date and time to set
     */
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

    /**
     * Associates a medical certificate with this appointment
     *
     * @param mc The medical certificate containing official diagnosis
     */
    public void setMedicalCertificate(MedicalCertificate mc) {
        this.mc = mc;
    }

    /**
     * Retrieves the medical certificate generated from this appointment
     *
     * @return The associated {@link MedicalCertificate} containing professional
     *         medical assessment, or null if no certificate was issued
     */
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

        var status = DataGenerator.getRandomEnum(AppointmentStatus.class);

        var appointment = Appointment.createNewAppointment(patient, reason, appointmentTime);
        
        appointment.setAppointmentStatus(status);

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
        else if (DataGenerator.generateRandomInt(2) == 1) {
            Doctor randomDoctor = Doctor.builder().withRandomBaseData().build();
            appointment.setDoctor(randomDoctor);

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

    public Map<Medication, Integer> getPrescriptions() {
        if (this.prescriptions == null) {
            this.prescriptions = new HashMap<>();
        }
        return this.prescriptions;
    }

    /**
     * Adds or updates a prescription in this consultation.
     *
     * @param medication The medication to prescribe
     * @param quantity The quantity to prescribe
     */
    public void addPrescription(Medication medication, int quantity) {
        if (medication == null || quantity <= 0) {
            return;
        }
        if (this.prescriptions == null) {
            this.prescriptions = new HashMap<>();
        }
        this.prescriptions.put(medication, quantity);
    }

    /**
     * Removes a prescription from this consultation.
     *
     * @param medication The medication to remove
     * @return true if the prescription was removed successfully
     */
    public boolean removePrescription(Medication medication) {
        if (this.prescriptions == null || medication == null) {
            return false;
        }
        return this.prescriptions.remove(medication) != null;
    }


    /**
     * Returns a string representation of the appointment containing key details:
     * ID, patient/doctor names, reason, time, status, session, notes, and MC status
     *
     * @return Formatted string with essential appointment information
     */
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patient=" + (patient != null ? patient.getName() : "null") +
                ", reason='" + reason + '\'' +
                ", appointmentTime=" + appointmentTime +
                ", doctor=" + (doctor != null ? doctor.getName() : "null") +
                ", appointmentStatus=" + appointmentStatus +
                ", session=" + session +
                ", doctorNotes='" + doctorNotes + '\'' +
                ", mc=" + (mc != null) +
                '}';
    }
}
