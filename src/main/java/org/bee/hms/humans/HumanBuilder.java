package org.bee.hms.humans;

import java.time.LocalDate;

import org.bee.utils.DataGenerator;

/**
 * Abstract builder class for constructing instances of {@link Human} or its subclasses.
 * Implements a fluent builder pattern to allow method chaining.
 *
 * @param <T> The specific subclass of {@code HumanBuilder}, ensuring method chaining returns the correct type.
 */
abstract class HumanBuilder<T extends HumanBuilder<T>> {
    protected static final DataGenerator dataGenerator = DataGenerator.getInstance();

    /** The full name of the individual. */
    String name;
    /** The date of birth of the individual. */
    LocalDate dateOfBirth;
    /** The NRIC/FIN (National Registration Identity Card/Foreign Identification Number). */
    String nricFin;
    /** The marital status of the individual. */
    MaritalStatus maritalStatus;
    /** The residential status (e.g., citizen, permanent resident, foreigner). */
    ResidentialStatus residentialStatus;
    /** The nationality of the individual. */
    String nationality;
    /** The residential address of the individual. */
    String address;
    /** The contact information of the individual. */
    Contact contact;
    /** The biological sex of the individual. */
    Sex sex;
    /** The blood type of the individual. */
    BloodType bloodType;
    /** Indicates whether the individual is vaccinated. */
    boolean isVaccinated;
    
    /** The type of human, used for JSON serialization/deserialization. */
    String type;

    /**
     * Default constructor for {@code HumanBuilder}.
     * It is package-private to ensure controlled instantiation by subclasses.
     */
    HumanBuilder() {}

    /**
     * Returns the current instance of the builder class.
     * Exists so chaining is possible.
     *
     * @return The current instance of type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T self() {
        return (T) this;
    }

    /**
     * Sets the name of the individual.
     *
     * @param name The full name.
     * @return The current builder instance.
     */
    public T name(String name) {
        this.name = name;
        return self();
    }

    /**
     * Sets the date of birth of the individual.
     *
     * @param dateOfBirth The date of birth.
     * @return The current builder instance.
     */
    public T dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return self();
    }

    /**
     * Sets the NRIC/FIN of the individual.
     *
     * @param nricFin The NRIC/FIN number.
     * @return The current builder instance.
     */
    public T nricFin(String nricFin) {
        this.nricFin = nricFin;
        return self();
    }

    /**
     * Sets the marital status of the individual.
     *
     * @param maritalStatus The marital status.
     * @return The current builder instance.
     */
    public T maritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return self();
    }

    /**
     * Sets the residential status of the individual.
     *
     * @param residentialStatus The residential status.
     * @return The current builder instance.
     */
    public T residentialStatus(ResidentialStatus residentialStatus) {
        this.residentialStatus = residentialStatus;
        return self();
    }

    /**
     * Sets the nationality of the individual.
     *
     * @param nationality The nationality.
     * @return The current builder instance.
     */
    public T nationality(String nationality) {
        this.nationality = nationality;
        return self();
    }

    /**
     * Sets the address of the individual.
     *
     * @param address The residential address.
     * @return The current builder instance.
     */
    public T address(String address) {
        this.address = address;
        return self();
    }

    /**
     * Sets the contact details of the individual.
     *
     * @param contact The contact details.
     * @return The current builder instance.
     */
    public T contact(Contact contact) {
        this.contact = contact;
        return self();
    }

    /**
     * Sets the biological sex of the individual.
     *
     * @param sex The biological sex.
     * @return The current builder instance.
     */
    public T sex(Sex sex) {
        this.sex = sex;
        return self();
    }

    /**
     * Sets the blood type of the individual.
     *
     * @param bloodType The blood type.
     * @return The current builder instance.
     */
    public T bloodType(BloodType bloodType) {
        this.bloodType = bloodType;
        return self();
    }

    /**
     * Sets the vaccination status of the individual.
     *
     * @param isVaccinated {@code true} if vaccinated, {@code false} otherwise.
     * @return The current builder instance.
     */
    public T isVaccinated(boolean isVaccinated) {
        this.isVaccinated = isVaccinated;
        return self();
    }


    /**
     * Fills the builder with random data using a data generator.
     * This is used to create a fully random {@code Human} instance.
     *
     * @return The current builder instance with random data.
     */
    public T withRandomBaseData() {
        this.name = dataGenerator.getRandomElement(dataGenerator.getSgNames());
        this.dateOfBirth = LocalDate.now().minusYears(dataGenerator.generateRandomInt(20, 60)); // Age between 20-60
        this.nricFin = dataGenerator.generateNRICNumber();
        this.maritalStatus = dataGenerator.getRandomEnum(MaritalStatus.class);
        this.residentialStatus = dataGenerator.getRandomEnum(ResidentialStatus.class);
        this.nationality = "Singaporean";
        this.address = dataGenerator.generateSGAddress();
        this.contact = dataGenerator.generateContact();
        this.sex = dataGenerator.getRandomEnum(Sex.class);
        this.bloodType = dataGenerator.getRandomEnum(BloodType.class);
        this.isVaccinated = dataGenerator.generateRandomInt(2) == 1; // 50% chance of being vaccinated
        return self();
    }

    /**
     * Builds and returns an instance of {@link Human} or its subclass.
     * Subclasses must implement this method to return the appropriate type.
     *
     * @return A constructed instance of {@link Human} or its subclass.
     * @throws IllegalStateException if required fields are missing.
     */
    protected abstract Human build();

    /**
     * Validates that all required fields are set before building an instance.
     * Ensures that fields such as {@code name}, {@code dateOfBirth}, {@code nricFin}, and others are non-null.
     *
     * @throws IllegalStateException If any required fields are missing.
     */
    protected void validateRequiredFields() {
        if (name == null || dateOfBirth == null || nricFin == null ||
                maritalStatus == null || residentialStatus == null ||
                nationality == null || address == null || contact == null ||
                sex == null || bloodType == null) {
            throw new IllegalStateException("All required fields must be set");
        }
    }
}
