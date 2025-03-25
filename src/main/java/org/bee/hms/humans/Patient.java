package org.bee.hms.humans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;

/**
 * Represents a patient in the insurance system.
 * <p>
 * Patients have personal details, medical records, drug allergies, next-of-kin (NOK) information,
 * height, weight, occupation, and company-related details.
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
    private final String companyName;
    /** Represents the business address of the company associated with the patient. */
    private final String companyAddress;

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
     *
     * @return A fully constructed Patient object with all properties set from JSON data
     */
    @JsonCreator
    public static Patient fromJson(
            @JsonProperty("name") String name,
            @JsonProperty("dob") LocalDate dob,
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

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDrugAllergies(List<String> drugAllergies) {
        this.drugAllergies = new ArrayList<>(drugAllergies);
    }

    public void setNokName(String nokName) {
        this.nokName = nokName;
    }

    public void setNokRelation(NokRelation nokRelation) {
        this.nokRelation = nokRelation;
    }

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

    public boolean getPatientConsent() { return patientConsent; }

    public void setPatientConsent(boolean patientConsent) { this.patientConsent = patientConsent; }

    public void setContact(String contact) {}

    public void setAddress(String address) {}

    public void displayHuman() {

        super.displayHuman();
        System.out.printf("%n%n");
        System.out.println("PATIENT DETAILS");
        System.out.println("---------------------------------------------------------------------");

//        System.out.printf("%nName: " + name);
        System.out.printf("Patient ID: " + patientId);
        System.out.printf("\nHeight: %3.2f", height);
        System.out.printf("\nWeight: %3.2f", weight);
        System.out.println("\nDrug Allergies: " + drugAllergies);
        System.out.println("\nNEXT OF KIN (NOK) DETAILS");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Name: " + nokName);
        System.out.printf("\nRelationship: " + nokRelation);
        System.out.printf("\nAddress: " + nokAddress);
        System.out.println();
    }

    /**
     * Returns a string representation of the patient.
     *
     * @return A formatted string containing the patient's name and ID.
     */
    @Override
    public String toString() {
        return "Patient Name: " + name + ", Patient ID: " + patientId;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return nricFin;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Patient other = (Patient) obj;
        return Objects.equals(patientId, other.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }


}
