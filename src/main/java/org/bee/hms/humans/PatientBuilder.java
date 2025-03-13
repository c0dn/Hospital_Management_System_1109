package org.bee.hms.humans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A builder class for creating instances of {@link Patient}.
 * <p>
 * This class enables step-by-step construction of a {@code Patient} object,
 * ensuring that all required fields are set before finalization.
 * It supports both manual field population and randomized data generation.
 * </p>
 */
public class PatientBuilder extends HumanBuilder<PatientBuilder> {
    /**
     * A shared random number generator used for generating random patient attributes,
     * such as height, weight, drug allergies, and next of kin details.
     * <p>
     * This ensures that all instances of {@code PatientBuilder} use the same
     * random generator, promoting consistency in random data generation.
     * </p>
     */
    private static final Random random = new Random();
    /** List of possible drug allergies for random data generation. */
    private static final String[] DRUG_ALLERGIES = {
            "Penicillin", "Aspirin", "Ibuprofen", "Sulfa", "None"
    };

    /** The unique identifier for the patient. */
    String patientId;
    /** A list of known drug allergies for the patient. */
    List<String> drugAllergies = new ArrayList<>();
    /** The name of the patient's next of kin (NOK). */
    String nokName;
    /** The residential address of the patient's next of kin (NOK). */
    String nokAddress;
    /** The relationship between the patient and their next of kin (NOK). */
    NokRelation nokRelation;
    /** The patient's height in meters. */
    double height;
    /** The patient's weight in kilograms. */
    double weight;
    /** The patient's occupation. */
    String occupation;
    /** The name of the company where the patient is employed. */
    String companyName;
    /** The business address of the patient's company. */
    String companyAddress;

    /**
     * Constructs a new {@code PatientBuilder} instance.
     */
    public PatientBuilder() {}

    /**
     * Sets the patient ID.
     *
     * @param patientId The unique identifier for the patient.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder patientId(String patientId) {
        this.patientId = patientId;
        return self();
    }

    /**
     * Adds a drug allergy to the patient's known allergies.
     *
     * @param allergy The name of the drug allergy.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder addDrugAllergy(String allergy) {
        this.drugAllergies.add(allergy);
        return self();
    }

    /**
     * Sets the patient's list of drug allergies.
     *
     * @param drugAllergies A list of drug allergies.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder drugAllergies(List<String> drugAllergies) {
        this.drugAllergies = new ArrayList<>(drugAllergies);
        return self();
    }

    /**
     * Sets the next of kin's name.
     *
     * @param nokName The name of the patient's next of kin.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder nokName(String nokName) {
        this.nokName = nokName;
        return self();
    }

    /**
     * Sets the next of kin's address.
     *
     * @param nokAddress The residential address of the next of kin.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder nokAddress(String nokAddress) {
        this.nokAddress = nokAddress;
        return self();
    }

    /**
     * Sets the patient's next of kin relationship.
     *
     * @param nokRelation The relationship between the patient and their next of kin.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder nokRelation(NokRelation nokRelation) {
        this.nokRelation = nokRelation;
        return self();
    }

    /**
     * Sets the patient's height.
     *
     * @param height The patient's height in meters.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder height(double height) {
        this.height = height;
        return self();
    }

    /**
     * Sets the patient's weight.
     *
     * @param weight The patient's weight in kilograms.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder weight(double weight) {
        this.weight = weight;
        return self();
    }

    /**
     * Sets the patient's occupation.
     *
     * @param occupation The patient's occupation.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder occupation(String occupation) {
        this.occupation = occupation;
        return self();
    }

    /**
     * Sets the patient's company name.
     *
     * @param companyName The name of the patient's employer.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder companyName(String companyName) {
        this.companyName = companyName;
        return self();
    }

    /**
     * Sets the business address of the company where the patient is employed.
     *
     * @param companyAddress The patient's company address.
     * @return The current {@code PatientBuilder} instance.
     */
    public PatientBuilder companyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return self();
    }

    /**
     * Populates the builder with randomized data.
     *
     * @return The current {@code PatientBuilder} instance.
     */
    @Override
    public PatientBuilder withRandomBaseData() {
        super.withRandomBaseData();

        // Generate height between 1.5m and 2m
        this.height = 1.5 + random.nextDouble() * 0.5;

        // Generate weight between 45kg and 100kg
        this.weight = 45 + random.nextDouble() * 55;

        // Generate NOK relation and name
        this.nokRelation = dataGenerator.getRandomEnum(NokRelation.class);
        this.nokName = generateNokName(this.name, this.nokRelation);

        // Generate addresses and other details
        this.nokAddress = dataGenerator.generateSGAddress();
        this.occupation = dataGenerator.getRandomOccupation();
        this.companyName = dataGenerator.getRandomCompanyName();
        this.companyAddress = dataGenerator.generateSGAddress();

        // Generate drug allergies (0-2 allergies)
        this.drugAllergies.clear();
        int numAllergies = random.nextInt(3);
        for (int i = 0; i < numAllergies; i++) {
            this.drugAllergies.add(DRUG_ALLERGIES[random.nextInt(DRUG_ALLERGIES.length)]);
        }

        return self();
    }

    /**
     * Generates a Next of Kin (NOK) name based on the patient's family name and relationship.
     *
     * @param patientName The name of the patient.
     * @param relation    The relationship of the NOK to the patient.
     * @return A generated NOK name.
     */
    private String generateNokName(String patientName, NokRelation relation) {
        String[] patientNameParts = patientName.split(" ");
        String familyName = (patientNameParts.length > 1) ?
                patientNameParts[patientNameParts.length - 1] : patientName;

        switch (relation) {
            case SPOUSE, SIBLING, PARENT -> {
                // Keep the same family name
                String[] nokNameParts = dataGenerator.getRandomElement(dataGenerator.getSgNames()).split(" ");
                return nokNameParts[0] + " " + familyName;
            }
            case CHILD, GRANDCHILD -> {
                // Child/Grandchild should have patient's family name
                String[] nokNameParts = dataGenerator.getRandomElement(dataGenerator.getSgNames()).split(" ");
                return nokNameParts[0] + " " + familyName;
            }
            case GRANDPARENT -> {
                // Grandparent might have different family name
                return dataGenerator.getRandomElement(dataGenerator.getSgNames());
            }
            case GUARDIAN, OTHER -> {
                // Different family name for non-blood relations
                return dataGenerator.getRandomElement(dataGenerator.getSgNames());
            }
            default -> {
                return dataGenerator.getRandomElement(dataGenerator.getSgNames());
            }
        }
    }

    /**
     * Populates the {@code PatientBuilder} with randomly generated data while
     * assigning a specific patient ID.
     * <p>
     * This method first invokes {@link #withRandomBaseData()} to fill in the
     * patient's attributes with randomized values and then explicitly sets
     * the {@code patientId}.
     * </p>
     *
     * @param patientId The unique identifier to be assigned to the patient.
     * @return The current instance of {@code PatientBuilder} to allow method chaining.
     */
    public PatientBuilder withRandomData(String patientId) {
        withRandomBaseData();
        this.patientId = patientId;
        return self();
    }

    /**
     * Builds and returns a {@code Patient} instance.
     *
     * @return A fully constructed {@code Patient} object.
     */
    @Override
    public Patient build() {
        validateRequiredFields();
        validatePatientFields();
        return new Patient(this);
    }

    /**
     * Validates required patient-specific fields before building the object.
     *
     * @throws IllegalStateException If any required field is missing or invalid.
     */
    private void validatePatientFields() {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalStateException("Patient ID is required");
        }
        if (nokName == null || nokName.trim().isEmpty()) {
            throw new IllegalStateException("Next of kin name is required");
        }
        if (nokAddress == null || nokAddress.trim().isEmpty()) {
            throw new IllegalStateException("Next of kin address is required");
        }
        if (nokRelation == null) {
            throw new IllegalStateException("Next of kin relation is required");
        }
        if (height <= 0) {
            throw new IllegalStateException("Height must be greater than 0");
        }
        if (weight <= 0) {
            throw new IllegalStateException("Weight must be greater than 0");
        }
    }
}
