package medical;

import humans.Patient;
import wards.Ward;

import java.time.LocalDate;
import java.util.List;

/**
 * This class represents a patient's medical record in the insurance system.
 * It stores information such as the patient's name, medical history, and treatments.
 */

public class MedicalRecord {
    private String procedureCode;
    private LocalDate dateOfVisit;
    private TypeOfVisit typeOfVisit;
    private List<String> medication;
    private List<String> drugAllergy;
    private List<String> medicalHistory;
    private Patient patient;
    private String attendingDoctor;
    private String medicalRecordId;
    private Ward ward;
    private HealthcareProvider healthcareProvider;
    private List<String> attendingNurse;

    public String getProcedureCode() { return procedureCode; }

    public LocalDate getDateOfVisit() { return dateOfVisit; }

    public TypeOfVisit getTypeOfVisit() { return typeOfVisit; }

    public List<String> getMedication() { return medication; }

    public List<String> getDrugAllergy() { return drugAllergy; }

    public List<String> getMedicalHistory() { return medicalHistory; }

    public Patient getPatient() { return patient; }

    public String getMedicalRecordId() { return medicalRecordId; }

    public Ward getWard() { return ward; }

    public HealthcareProvider getHealthcareProvider() { return healthcareProvider; }

    public String getAttendingDoctor() { return attendingDoctor; }

    public List<String> getAttendingNurse() { return attendingNurse; }
}
