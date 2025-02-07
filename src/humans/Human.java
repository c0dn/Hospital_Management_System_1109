package humans;

import java.time.LocalDate;

/**
 * Represents a general human entity in the insurance system.
 * This serves as a base class for patients, doctors and other human-related roles.
 */

public abstract class Human {

    /** The name of the person. */
    private String name;

    /** The date of birth of the person. */
    private LocalDate dateOfBirth;

    /** The NRIC/FIN (identification number) of the person. */
    private String nricFin;

    /** The marital status of the person. */
    private MaritalStatus maritalStatus;

    /** The residential status of the person. */
    private ResidentialStatus residentialStatus;

    /** The nationality of the person. */
    private String nationality;

    /** The residential address of the person. */
    private String address;

    /** The contact details of the person. */
    private Contact contact;

    /** The sex of the person. */
    private Sex sex;

    /** The blood type of the person. */
    private BloodType bloodType;

    /** The vaccination status of the person. */
    private boolean isVaccinated;

    /**
     * Constructs a Human object with the specified attributes.
     *
     * @param name The name of the person.
     * @param dateOfBirth The date of birth of the person.
     * @param nricFin The NRIC/FIN (identification) of the person.
     * @param maritalStatus The marital status of the person.
     * @param residentialStatus The residential status of the person.
     * @param nationality The nationality of the person.
     * @param address The residential address of the person.
     * @param contact The contact details of the person.
     * @param sex The sex of the person.
     * @param bloodType The blood type of the person.
     * @param isVaccinated The vaccination status of the person.
     */
    public Human(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                 ResidentialStatus residentialStatus, String nationality, String address,
                 Contact contact, Sex sex, BloodType bloodType, boolean isVaccinated) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.nricFin = nricFin;
        this.maritalStatus = maritalStatus;
        this.residentialStatus = residentialStatus;
        this.nationality = nationality;
        this.address = address;
        this.contact = contact;
        this.sex = sex;
        this.bloodType = bloodType;
        this.isVaccinated = isVaccinated;
    }

}
