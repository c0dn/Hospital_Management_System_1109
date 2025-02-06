package humans;

import medical.MedicalRecord;

import java.time.LocalDate;
import java.util.List;

public class Patient extends Human {
    private String patientId;
    private List<String> drugAllergies;
    private List<MedicalRecord> medicalRecords;
    private String nokName;
    private String nokAddress;
    private String nokRelation;
    private double height; // in meters
    private double weight; // in kilograms

    public Patient(String name, LocalDate dateOfBirth, String nricFin,
                   MaritalStatus maritalStatus, ResidentialStatus residentialStatus,
                   String nationality, String address, Contact contact,
                   Sex sex, BloodType bloodType, boolean isVaccinated,
                   String patientId, List<String> drugAllergies,
                   List<MedicalRecord> medicalRecords, String nokName,
                   String nokAddress, String nokRelation,
                   double height, double weight) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated);

        this.patientId = patientId;
        this.drugAllergies = drugAllergies;
        this.medicalRecords = medicalRecords;
        this.nokName = nokName;
        this.nokAddress = nokAddress;
        this.nokRelation = nokRelation;
        this.height = height;
        this.weight = weight;
    }


    public String getPatientId() {
        return patientId;
    }

    public List<String> getDrugAllergies() {
        return drugAllergies;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public String getNokName() {
        return nokName;
    }

    public String getNokAddress() {
        return nokAddress;
    }

    public String getNokRelation() {
        return nokRelation;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }
}
