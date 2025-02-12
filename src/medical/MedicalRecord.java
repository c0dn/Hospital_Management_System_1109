package medical;

import humans.Patient;
import wards.Bed;
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
     * Constructs a new MedicalRecord instance with the provided details.
     *
     * @param procedureCode      The medical procedure code associated with this record.
     * @param dateOfVisit        The date of the patient’s visit.
     * @param medication         The list of medications prescribed.
     * @param drugAllergy        The list of drug allergies the patient has.
     * @param medicalHistory     The patient's past medical conditions.
     * @param patient            The patient associated with this record.
     * @param attendingDoctor    The name of the doctor who attended to the patient.
     * @param medicalRecordId    The unique medical record ID.
     * @param ward               The hospital ward where the patient was admitted.
     * @param healthcareProvider The healthcare provider responsible for the treatment.
     * @param attendingNurse     The list of nurses attending to the patient.
     */
    public MedicalRecord(String procedureCode, LocalDate dateOfVisit,
                         List<String> medication, List<String> drugAllergy, List<String> medicalHistory,
                         Patient patient, String attendingDoctor, String medicalRecordId, Ward ward,
                         HealthcareProvider healthcareProvider, List<String> attendingNurse) {

        this.procedureCode = procedureCode;
        this.dateOfVisit = dateOfVisit;
        this.medication = medication;
        this.drugAllergy = drugAllergy;
        this.medicalHistory = medicalHistory;
        this.patient = patient;
        this.attendingDoctor = attendingDoctor;
        this.medicalRecordId = medicalRecordId;
        this.ward = ward;
        this.healthcareProvider = healthcareProvider;
        this.attendingNurse = attendingNurse;
    }

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

    public void displayMedicalRecord() {
//        System.out.printf("%-20s %-20s %-15s %-20s %-20s %-20s %-15s %-20s %-20s %-20s %-20s%n",
//                "Procedure Code", "Date of Visit", "Type of Visit", "Medication", "Drug Allergy",
//                "Medical History", "Patient", "Attending Doctor", "Medical Record ID", "Ward",
//                "Healthcare Provider");
//
//        System.out.printf("%-20s %-20s %-15s %-20s %-20s %-20s %-15s %-20s %-20s %-20s %-20s%n",
//                procedureCode, dateOfVisit, typeOfVisit, String.join(", ", medication),
//                String.join(", ", drugAllergy), String.join(", ", medicalHistory),
//                patient.getPatientId(), attendingDoctor, medicalRecordId, ward, healthcareProvider);

        System.out.printf("%n%n");
        System.out.println("=====================================================================");
        System.out.printf("                           MEDICAL RECORD%n");
        System.out.println("=====================================================================");
        System.out.println("PATIENT INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Patient ID: " + patient.getPatientId());
        System.out.printf("\t\tMedical Record ID: " + medicalRecordId);
        System.out.println("\n\nVISIT INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Hospital Name: " + healthcareProvider.healthcareProviderName);
        System.out.printf("\t\tHospital Code: " + healthcareProvider.hospitalCode);
        System.out.printf("%n%nDate of Visit: " + dateOfVisit);
        System.out.printf("\n\nWard: " + ward.getWardName());
        System.out.println("\n\nMEDICAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Medical History: " + medicalHistory);
        System.out.printf("\t\tDrug Allergy: " + drugAllergy);
        System.out.println("\n\nDIAGNOSIS INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Attending Doctor: " + attendingDoctor);
        System.out.printf("\t\tProcedure Code: " + procedureCode);
        System.out.println("");
        System.out.println("=====================================================================");
    }



}
