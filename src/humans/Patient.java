package humans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a patient in the insurance system.
 * Patients have medical records have insurance details.
 */

public class Patient extends Human {
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
    private double height; // in meters
    /** The patient's weight in kilograms. */
    private double weight;
    /** Represents the occupation of a patient. */
    private String occupation;
    /** Represents the name of the company where the patient is employed. */
    private String companyName;
    /** Represents the business address of the company associated with the patient. */
    private String companyAddress;


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
    }


    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }


    public void displayPatientInfo() {
        System.out.format("Name: %s%n", name);
        System.out.format("Patient ID: %s%n", patientId);
        System.out.format("Date of Birth: %s%n", dateOfBirth);
        System.out.format("Height: %.2fm%n", height);
        System.out.format("Weight: %.2fkg%n", weight);
        System.out.format("Next of Kin: %s (%s), Address: %s%n", nokName, nokRelation, nokAddress);
        System.out.format("Drug Allergies: %s%n%n", drugAllergies);
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


