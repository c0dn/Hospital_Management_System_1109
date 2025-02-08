package humans;

import medical.MedicalRecord;
import policy.InsurancePolicy; //reference to patient Insurance Policy

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a patient in the insurance system.
 * Patients have medical records have insurance details.
 */

public class Patient extends Human {

    private final String patientId;
    private List<String> drugAllergies;
    private List<MedicalRecord> medicalRecords;
    private String nokName;
    private String nokAddress;
    private String nokRelation;
    private double height; // in meters
    private double weight; // in kilograms
//    private InsurancePolicy insurancePolicy;
    private String occupation;
    private String companyName;
    private String companyAddress;

    public Patient(String name, LocalDate dateOfBirth, String nricFin,
                   MaritalStatus maritalStatus, ResidentialStatus residentialStatus,
                   String nationality, String address, Contact contact,
                   Sex sex, BloodType bloodType, boolean isVaccinated,
                   String patientId, List<String> drugAllergies,
                   List<MedicalRecord> medicalRecords, String nokName,
                   String nokAddress, String nokRelation,
                   double height, double weight, String occupation,
                   String companyName,String companyAddress) {

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
//        this.insurancePolicy = insurancePolicy;
        this.occupation = occupation;
        this.companyName = companyName;
        this.companyAddress=companyAddress;

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

    public String getOccupation() {
        return occupation;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }


    public void displayPatientInfo() {
            System.out.println("Name: " + getName());
            System.out.println("Patient ID: " + getPatientId());
            System.out.println("Date of Birth:" + getDateOfBirth());
            System.out.println("Height: " + getHeight() + 'm');
            System.out.println("Weight: " + getWeight() + "kg");
            System.out.println("Next of Kin: " + getNokName() + " (" + nokRelation + "), Address: " + getNokAddress());
            System.out.println("Drug Allergies:  " + getDrugAllergies());
    }


    public void displayInsrPatient(){
        System.out.println("Patient ID: " + getPatientId());
        System.out.println("Name of Insured/Covered Member: " + getName());
        System.out.println("NRIC/FIN: " + getNricFin());
        System.out.println("Contact Information: " + getContact());
        System.out.println("Mailing Address: " + getAddress());
        System.out.println("Occupation: " + getOccupation());
        System.out.println("Company Name: " + getCompanyName());
        System.out.println("Company Business Address: " + getCompanyAddress());
    }

        //create an insurance grouping information (all the information needed for insurance claim)
        //if there is a shared method (example displayInfo) for all the extend class, do the super method
}
