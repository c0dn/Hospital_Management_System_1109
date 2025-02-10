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
        System.out.printf("\n\n%-20s%-15s%-20s%-15s%-15s%-25s%-20s%-20s%-60s%n",
                "Name", "Patient ID", "Date of Birth", "Height", "Weight", "Drug Allergies", "NOK Name", "NOK Relation", "NOK Address");

        System.out.printf("%-20s%-15s%-20s%-15.2f%-15.2f%-25s%-20s%-20s%-60s%n",
                name, patientId, dateOfBirth, height, weight, String.join(", ", drugAllergies), nokName, nokRelation, nokAddress);
    }

    public void displayInsrPatient() {
        System.out.printf("\n\n%-13s %-30s %-11s %-18s %-24s %-14s %-18s %-30s%n",
                "Patient ID", "Name of Insured", "NRIC/FIN", "Contact", "Mailing Address", "Occupation", "Company Name", "Company Address");
        System.out.printf("%-13s %-30s %-11s %-18s %-24s %-14s %-18s %-30s%n",
                patientId, name, nricFin, contact, address, occupation, companyName, companyAddress);
    }

    @Override
    public String toString() {
        return "Patient Name: " + name + ", Patient ID: " + patientId;
    }
}