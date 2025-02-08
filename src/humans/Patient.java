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

    /** The unique identifier for the patient. */
    private final String patientId;
    /** A list of the patient's drug allergies. */
    private List<String> drugAllergies;
    /** A list of the patient's medical records. */
    private List<MedicalRecord> medicalRecords;
    /** The next of kin's name. */
    private String nokName;
    /** The next of kin's residential address. */
    private String nokAddress;
    /** The relationship of the patient and the next of kin. */
    private String nokRelation;
    /** The patient's height in metres. */
    private double height; // in meters
    /** The patient's weight in kilograms. */
    private double weight; // in kilograms
//    /** The patient's insurance policy details. */
//    private InsurancePolicy insurancePolicy;
    private String occupation;
    private String companyName;
    private String companyAddress;

    /**
     * Constructs a Patient object with specified details.
     *
     * @param name The patient's name.
     * @param dateOfBirth The patient's date of birth.
     * @param nricFin The patient's NRIC or FIN number.
     * @param maritalStatus The patient's marital status.
     * @param residentialStatus The patient's residential status.
     * @param nationality The patient's nationality.
     * @param address The patient's residental address.
     * @param contact The patient's contact details.
     * @param sex The patient's sex.
     * @param bloodType The patient's blood type.
     * @param isVaccinated Indicates if the patient is vaccinated.
     * @param patientId The patient's unique ID.
     * @param drugAllergies A list of the patient's drug allergies.
     * @param medicalRecords A list of the patient's medical records.
     * @param nokName The next of kin's name.
     * @param nokAddress The next of kin's residential address.
     * @param nokRelation The relationship between the patient and the next of kin.
     * @param height The patient's height in metres.
     * @param weight The patient's weight in kilograms.
     */


    public Patient(String name, LocalDate dateOfBirth, String nricFin,
                          MaritalStatus maritalStatus, ResidentialStatus residentialStatus,
                          String nationality, String address, Contact contact,
                          Sex sex, BloodType bloodType, boolean isVaccinated,
                          String patientId, List<String> drugAllergies,
                          List<MedicalRecord> medicalRecords, String nokName,
                          String nokAddress, String nokRelation,
                          double height, double weight,
                          String occupation, String companyName, String companyAddress) {

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
        this.companyAddress = companyAddress;
    }
    /**
     * Retrieves the patient's unique ID.
     *
     * @return The patient ID.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Retrieves the patient's drug allergies.
     *
     * @return A list of patient's know drug allergies.
     */
    public List<String> getDrugAllergies() {
        return drugAllergies;
    }

    /**
     * Retrieves the patient's medical records.
     *
     * @return A list of patient's medical records.
     */
    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    /**
     * Retrieves the next of kin's name.
     *
     * @return The next of kin's name.
     */
    public String getNokName() {
        return nokName;
    }

    /**
     * Retrieves the next of kin's residential address.
     *
     * @return The next of kin's residential address.
     */
    public String getNokAddress() {
        return nokAddress;
    }

    /**
     * Retrieves the relationship between the patient and the next of kin.
     *
     * @return The relationship between the patient and the next of kin.
     */
    public String getNokRelation() {
        return nokRelation;
    }

    /**
     * Retrieves the patient's height in metres.
     *
     * @return The height of the patient in metres.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Retrieves the patient's weight in kilograms.
     *
     * @return The weight of the patient in kilograms.
     */
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

    /**
     * Displays patient information.
     */
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
        // exact job duties is in the form, but can dont incl.
    }

        //create an insurance grouping information (all the information needed for insurance claim)
        //if there is a shared method (example displayInfo) for all the extend class, do the super method
}

