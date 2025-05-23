package org.bee.hms.humans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;

/**
 * Represents a patient in the healthcare management system.
 * <p>
 * Patients have personal details, medical records, drug allergies, next-of-kin (NOK) information,
 * height, weight, occupation, and company-related details.
 * </p>
 */
public class Patient extends Human implements SystemUser {
    /** The unique identifier for the patient. */
    private final String patientId;
    /** A list of the patient's drug allergies. */
    private List<String> drugAllergies;
    /** The next of kin's name. */
    private String nokName;
    /** The next of kin's residential address. */
    private String nokAddress;
    /** The relationship of the patient and the next of kin. */
    private NokRelation nokRelation;
    /** The patient's height in metres. */
    private double height;
    /** The patient's weight in kilograms. */
    private double weight;
    /** Represents the occupation of a patient. */
    private String occupation;
    /** Represents the name of the company where the patient is employed. */
    private String companyName;
    /** Represents the business address of the company associated with the patient. */
    private String companyAddress;

    private boolean patientConsent;


    /**
     * Constructs a new {@code Patient} instance using the given {@code PatientBuilder}.
     * This constructor initializes the patient-specific attributes by copying
     * the values provided through the builder.
     *
     * @param builder The {@code PatientBuilder} instance containing the data
     *                to initialize the {@code Patient} object. Fields such as
     *                patientId, drugAllergies, next of kin details, height,
     *                weight, occupation, and company details are expected
     *                to be set in the builder before constructing a {@code Patient}.
     */
    Patient(PatientBuilder builder) {
        super(builder);
        this.patientId = builder.patientId;
        this.drugAllergies = new ArrayList<>(builder.drugAllergies);
        this.nokName = builder.nokName;
        this.nokAddress = builder.nokAddress;
        this.nokRelation = builder.nokRelation;
        this.height = builder.height;
        this.weight = builder.weight;
        this.occupation = builder.occupation;
        this.companyName = builder.companyName;
        this.companyAddress = builder.companyAddress;
        this.patientConsent = builder.patientConsent;
        this.humanType = "patient";
    }

    /**
     * Factory method for deserializing Patient objects from JSON using Jackson.
     * <p>
     * This method provides a way for Jackson to reconstruct Patient objects during
     * deserialization without requiring a default constructor. It preserves the
     * Patient class's builder-based construction pattern while enabling JSON serialization.
     *
     * @param name                The name of the patient
     * @param dob                 The date of birth of the patient
     * @param nricFin             The NRIC/FIN number of the patient
     * @param maritalStatus       The marital status of the patient
     * @param residentialStatus   The residential status of the patient
     * @param nationality         The nationality of the patient
     * @param address             The address of the patient
     * @param contact             The contact information of the patient
     * @param sex                 The sex of the patient
     * @param bloodType           The blood type of the patient
     * @param isVaccinated        Vaccination status of the patient
     * @param patientId           The unique identifier for the patient
     * @param drugAllergies       The list of drug allergies the patient has
     * @param nokName             Name of next of kin
     * @param nokAddress          Address of next of kin
     * @param nokRelation         Relationship with next of kin
     * @param height              The height of the patient
     * @param weight              The weight of the patient
     * @param occupation          The occupation of the patient
     * @param companyName         The company name of the patient
     * @param companyAddress      The company address of the patient
     * @param patientConsent      Patient consent information
     * @param humanType           The type classification of the human
     *
     * @return A fully constructed Patient object with all properties set from JSON data
     */
    @JsonCreator
    public static Patient fromJson(
            @JsonProperty("name") String name,
            @JsonProperty("dateOfBirth") LocalDate dob,
            @JsonProperty("nricFin") String nricFin,
            @JsonProperty("maritalStatus") MaritalStatus maritalStatus,
            @JsonProperty("residentialStatus") ResidentialStatus residentialStatus,
            @JsonProperty("nationality") String nationality,
            @JsonProperty("address") String address,
            @JsonProperty("contact") Contact contact,
            @JsonProperty("sex") Sex sex,
            @JsonProperty("bloodType") BloodType bloodType,
            @JsonProperty("isVaccinated") boolean isVaccinated,
            @JsonProperty("patientId") String patientId,
            @JsonProperty("drugAllergies") List<String> drugAllergies,
            @JsonProperty("nokName") String nokName,
            @JsonProperty("nokAddress") String nokAddress,
            @JsonProperty("nokRelation") NokRelation nokRelation,
            @JsonProperty("height") double height,
            @JsonProperty("weight") double weight,
            @JsonProperty("occupation") String occupation,
            @JsonProperty("companyName") String companyName,
            @JsonProperty("companyAddress") String companyAddress,
            @JsonProperty("patientConsent") boolean patientConsent,
            @JsonProperty("humanType") String humanType
    ) {
        PatientBuilder builder = new PatientBuilder();

        setHumanFields(builder, name, dob, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated, humanType);

        if (drugAllergies != null) {
            builder = builder.drugAllergies(drugAllergies);
        }

        return builder
                .patientId(patientId)
                .nokName(nokName)
                .nokAddress(nokAddress)
                .nokRelation(nokRelation)
                .height(height)
                .weight(weight)
                .occupation(occupation)
                .companyName(companyName)
                .companyAddress(companyAddress)
                .patientConsent(patientConsent)
                .build();
    }

    /**
     * Sets the height of the patient
     *
     * @param height The height value to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Sets the weight of the patient
     *
     * @param weight The weight value to se
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Sets the drug allergies of the patient.
     *
     * @param drugAllergies The list of drug allergies to set
     */
    public void setDrugAllergies(List<String> drugAllergies) {
        this.drugAllergies = new ArrayList<>(drugAllergies);
    }

    /**
     * Sets the name of the next of kin.
     *
     * @param nokName The next of kin name to set
     */
    public void setNokName(String nokName) {
        this.nokName = nokName;
    }

    /**
     * Sets the relationship with the next of kin.
     *
     * @param nokRelation The next of kin relationship to set
     */
    public void setNokRelation(NokRelation nokRelation) {
        this.nokRelation = nokRelation;
    }

    /**
     * Sets the address of the next of kin.
     *
     * @param nokAddress The next of kin address to set
     */
    public void setNokAddress(String nokAddress) {
        this.nokAddress = nokAddress;
    }

    /**
     * Creates and returns a new {@link PatientBuilder} instance.
     *
     * @return A new {@code PatientBuilder} instance.
     */
    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    /**
     * Gets the unique patient identifier.
     *
     * @return The patient's ID.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Gets the patient's full name.
     *
     * @return The patient's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the patient's consent status
     *
     * @return The patient's consent status
     */
    public boolean getPatientConsent() { return patientConsent; }

    /**
     * Sets the patient's consent status
     *
     * @param patientConsent The patient's consent status to set
     */
    public void setPatientConsent(boolean patientConsent) { this.patientConsent = patientConsent; }

    /**
     * Sets the patient's contact information
     *
     * @param contact The contact information to set
     */
    public void setContact(String contact) {}

    /**
     * Sets the patient's address
     *
     * @param address The address to set
     */
    public void setAddress(String address) {}

    /**
     * Returns a string representation of the patient.
     *
     * @return A formatted string containing the patient's name and ID.
     */
    @Override
    public String toString() {
        return "Patient Name: " + name + ", Patient ID: " + patientId;
    }

    /**
     * Gets the username for the patient (NRIC/FIN number)
     *
     * @return The patient's NRIC/FIN number
     */
    @JsonIgnore
    @Override
    public String getUsername() {
        return nricFin;
    }

    /**
     * Gets the patient's height
     *
     * @return The patient's height in m
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the patient's weight.
     *
     * @return The patient's weight measurement in kg
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Retrieves the drug allergies of the patient.
     *
     * @return The list of drug allergies
     */
    public List<String> getDrugAllergies() {
        return drugAllergies;
    }
}
