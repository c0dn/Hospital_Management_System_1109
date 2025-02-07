package medical;

import humans.Patient;
import wards.Ward;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a patient's medical record in the insurance system.
 * <p>
 *     This class stores information related to a patient's medical visit, including:
 * </p>
 * <ul>
 *     <li>Procedure details</li>
 *     <li>Date of visit</li>
 *     <li>Type of visit</li>
 *     <li>Prescribed medications</li>
 *     <li>Drug allergies</li>
 *     <li>Medical history</li>
 *     <li>Attending doctor and nurses</li>
 *     <li>Hospital ward and healthcare provider</li>
 * </ul>
 */

public class MedicalRecord {
    /** The code representing the medical procedure performed during the visit. */
    private String procedureCode;
    /** The date on which the patient visited the healthcare facility. */
    private LocalDate dateOfVisit;
    /** The type of visit, such as inpatient or outpatient. */
    private TypeOfVisit typeOfVisit;
    /** A list of prescribed medications during the visit. */
    private List<String> medication;
    /** A list of drug allergies that the patient has. */
    private List<String> drugAllergy;
    /** A list of past medical conditions and treatments recorded for the patient. */
    private List<String> medicalHistory;
    /** The patient to whom this medical record belongs. */
    private Patient patient;
    /** The name of the doctor who attended to the patient. */
    private String attendingDoctor;
    /** The unique ID associated with this medical record. */
    private String medicalRecordId;
    /** The hospital ward where the patient was admitted (if applicable). */
    private Ward ward;
    /** The healthcare provider responsible for the patient's treatment. */
    private HealthcareProvider healthcareProvider;
    /** The list of nurses who attended to the patient during their visit. */
    private List<String> attendingNurse;

    /**
     * Retrieves the medical procedure code associated with this record.
     *
     * @return The procedure code.
     */
    public String getProcedureCode() { return procedureCode; }

    /**
     * Retrieves the date of the patient's visit.
     *
     * @return The visit date as a {@link LocalDate}.
     */
    public LocalDate getDateOfVisit() { return dateOfVisit; }

    /**
     * Retrieves the type of visit (eg. inpatient, outpatient).
     *
     * @return The type of visit as a {@link TypeOfVisit}.
     */
    public TypeOfVisit getTypeOfVisit() { return typeOfVisit; }

    /**
     * Retrieves the list of medications prescribed during the visit.
     *
     * @return A list of medication names.
     */
    public List<String> getMedication() { return medication; }

    /**
     * Retrieves the list of the patient's drug allergies.
     *
     * @return A list of known drug allergies.
     */
    public List<String> getDrugAllergy() { return drugAllergy; }

    /**
     * Retrieves the patient's medical history.
     *
     * @return A list of past medical conditions and treatments.
     */
    public List<String> getMedicalHistory() { return medicalHistory; }

    /**
     * Retrieves the patient associated with this medical record.
     *
     * @return The {@link Patient} onject.
     */
    public Patient getPatient() { return patient; }

    /**
     * Retrieves the unique medical record ID.
     *
     * @return The medical record ID.
     */
    public String getMedicalRecordId() { return medicalRecordId; }

    /**
     * Retrieves the hospital ward where the patient was admitted.
     *
     * @return The {@link Ward} associated with this record.
     */
    public Ward getWard() { return ward; }

    /**
     * Retrieves the healthcare provider responsible for the treatment.
     *
     * @return The {@link HealthcareProvider} associated with this record.
     */
    public HealthcareProvider getHealthcareProvider() { return healthcareProvider; }

    /**
     * Retrieves the name of the attending doctor for this visit.
     *
     * @return The name of the attending doctor.
     */
    public String getAttendingDoctor() { return attendingDoctor; }

    /**
     * Retrieves the list of attending nurses for this medical visit.
     *
     * @return A list of names of attending nurses.
     */
    public List<String> getAttendingNurse() { return attendingNurse; }
}
