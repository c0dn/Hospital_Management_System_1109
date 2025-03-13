package org.bee.humans;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient in the insurance system.
 * <p>
 * Patients have personal details, medical records, drug allergies, next-of-kin (NOK) information,
 * height, weight, occupation, and company-related details.
 * </p>
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
    private double height;
    /** The patient's weight in kilograms. */
    private double weight;
    /** Represents the occupation of a patient. */
    private String occupation;
    /** Represents the name of the company where the patient is employed. */
    private final String companyName;
    /** Represents the business address of the company associated with the patient. */
    private final String companyAddress;


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


    public void displayHuman() {

        super.displayHuman();
        System.out.printf("%n%n");
        System.out.println("PATIENT DETAILS");
        System.out.println("---------------------------------------------------------------------");

//        System.out.printf("%nName: " + name);
        System.out.printf("Patient ID: " + patientId);
        System.out.printf("\nHeight: %3.2f", height);
        System.out.printf("\ntWeight: %3.2f", weight);
        System.out.println("\nDrug Allergies: " + drugAllergies);
        System.out.println("\nNEXT OF KIN (NOK) DETAILS");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Name: " + nokName);
        System.out.printf("\nRelationship: " + nokRelation);
        System.out.printf("\nAddress: " + nokAddress);
        System.out.println();
    }

    /**
     * Displays the patient's insurance-related information.
     * <p>
     * This method prints formatted details relevant to insurance,
     * including patient ID, insured name, NRIC/FIN, contact information,
     * mailing address, occupation, and company details.
     * </p>
     */

    public void displayInsrPatient() {
        System.out.printf("\n\n%-13s %-30s %-11s %-18s %-24s %-14s %-18s %-30s%n",
                "Patient ID", "Name of Insured", "NRIC/FIN", "Contact", "Mailing Address", "Occupation", "Company Name", "Company Address");
        System.out.printf("%-13s %-30s %-11s %-18s %-24s %-14s %-18s %-30s%n",
                patientId, name, nricFin, contact, address, occupation, companyName, companyAddress);

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
}
