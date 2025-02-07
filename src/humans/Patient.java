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

    /** Unique identifier for the patient. */
    private String patientId;

    /** The list of drugs the patient is allergic to. */
    private List<String> drugAllergies;

    /** The list of the patient's medical records. */
    private List<MedicalRecord> medicalRecords;

    /** The patient's next of kin name. */
    private String nokName;

    /** The patient's next of kin residential address. */
    private String nokAddress;

    /** The patient's relationship to the next of kin. */
    private String nokRelation;

    /** The height of the patient in metres. */
    private double height; // in meters

    /** The weight of the patient in kilograms. */
    private double weight; // in kilograms

    /** The patient's insurance policy details. */
    private InsurancePolicy insurancePolicy;

    /**
     * Constructs a Patient object with specified details.
     *
     * @param name The patient's full name.
     * @param dateOfBirth The patient's date of birth.
     * @param nricFin The patient's NRIC or FIN number.
     * @param maritalStatus The patient's marital status.
     * @param residentialStatus The patient's residential status.
     * @param nationality The patient's nationality.
     * @param address The patient's residential address.
     * @param contact The patient's contact details.
     * @param sex The patient's sex.
     * @param bloodType The patient's bood type.
     * @param isVaccinated Indicates if the patient is vaccinated,
     * @param patientId The patient's unique ID.
     * @param drugAllergies A list of the patient's drug allergies.
     * @param medicalRecords A list of the patient's medical records.
     * @param nokName The patient's next of kin's name.
     * @param nokAddress The patient's next of kin's residential address.
     * @param nokRelation The patient's relationship to the next of kin.
     * @param height The patient's height in metres.
     * @param weight The patient's weight in metres.
     * @param insurancePolicy The patient's insurance policy details.
     */
    public Patient(String name, LocalDate dateOfBirth, String nricFin,
                   MaritalStatus maritalStatus, ResidentialStatus residentialStatus,
                   String nationality, String address, Contact contact,
                   Sex sex, BloodType bloodType, boolean isVaccinated,
                   String patientId, List<String> drugAllergies,
                   List<MedicalRecord> medicalRecords, String nokName,
                   String nokAddress, String nokRelation,
                   double height, double weight, InsurancePolicy insurancePolicy) {

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
        this.insurancePolicy = insurancePolicy;
    }

    /**
     * Retrieves the unique identifier for the patient.
     *
     * @return The patient ID.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Retrieves the list of patient's drug allergies.
     *
     * @return A list of patient's drug allergies.
     */
    public List<String> getDrugAllergies() {
        return drugAllergies;
    }

    /** Retrieves the list of patient's medical records.
     *
     * @return A list of patient's medical records.
     */
    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    /**
     * Retrieves the patient's next of kin's name.
     *
     * @return The patient's next of kin's name.
     */
    public String getNokName() {
        return nokName;
    }

    /**
     * Retrieves the patient's next of kin's residential address.
     *
     * @return The patient's next of kin's residential address.
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
     * @return The patient's height in metres.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Retrieves the patient's weight in kilograms.
     *
     * @return The patient's weight in kilograms.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Retrieves the patient's insurance policy details.
     *
     * @return The patient's insurance policy details.
     */
    public InsurancePolicy getInsurancePolicy() { return insurancePolicy; }
}
