package humans;

import medical.MedicalRecord;

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
                   String patientId, List<String> drugAllergies, String nokName,
                   String nokAddress, String nokRelation,
                   double height, double weight,
                   String occupation, String companyName, String companyAddress) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated);

        this.patientId = patientId;
        this.drugAllergies = drugAllergies;
        this.nokName = nokName;
        this.nokAddress = nokAddress;
        this.nokRelation = nokRelation;
        this.height = height;
        this.weight = weight;
        this.occupation = occupation;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void displayPatientInfo() {
        System.out.format("Name: %s%n", name);
        System.out.format("Patient ID: %s%n", patientId);
        System.out.format("Date of Birth: %s%n", dateOfBirth);
        System.out.format("Height: %.2fm%n", height);
        System.out.format("Weight: %.2fkg%n", weight);
        System.out.format("Next of Kin: %s (%s), Address: %s%n", nokName, nokRelation, nokAddress);
        System.out.format("Drug Allergies: %s%n", drugAllergies);
    }

    public void displayInsrPatient() {
        System.out.format("Patient ID: %s%n", patientId);
        System.out.format("Name of Insured/Covered Member: %s%n", name);
        System.out.format("NRIC/FIN: %s%n", nricFin);
        System.out.format("Contact Information: %s%n", contact);
        System.out.format("Mailing Address: %s%n", address);
        System.out.format("Occupation: %s%n", occupation);
        System.out.format("Company Name: %s%n", companyName);
        System.out.format("Company Business Address: %s%n", companyAddress);
    }

    //create an insurance grouping information (all the information needed for insurance claim)
    //if there is a shared method (example displayInfo) for all the extend class, do the super method
}


