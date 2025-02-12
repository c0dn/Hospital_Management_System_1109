package humans;

import java.time.LocalDate;

/**
 * Represents a general human entity in the insurance system.
 * This serves as a base class for patients, doctors and other human-related roles.
 */
public abstract class Human {

    /** The name of the person. */
    protected String name;

    /** The date of birth of the person. */
    protected LocalDate dateOfBirth;

    /** The NRIC/FIN (identification number) of the person. */
    protected String nricFin;

    /** The marital status of the person. */
    protected MaritalStatus maritalStatus;

    /** The residential status of the person. */
    protected ResidentialStatus residentialStatus;

    /** The nationality of the person. */
    protected String nationality;

    /** The residential address of the person. */
    protected String address;

    /** The contact details of the person. */
    protected Contact contact;

    /** The sex of the person. */
    protected Sex sex;

    /** The blood type of the person. */
    protected BloodType bloodType;

    /** The vaccination status of the person. */
    protected boolean isVaccinated;

    /**
     * Constructs a Human object using the provided HumanBuilder, initializing
     * various attributes of the Human entity such as name, date of birth, NRIC,
     * marital status, and other personal details.
     * Package-private constructor, only accessible by builders in the same package
     *
     * @param builder The builder object that contains the data used to initialize
     *                the Human instance. The builder must include fields such as
     *                name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
     *                nationality, address, contact, sex, bloodType, and vaccination
     *                status to properly construct a Human object.
     */
    Human(HumanBuilder<?> builder) {
        this.name = builder.name;
        this.dateOfBirth = builder.dateOfBirth;
        this.nricFin = builder.nricFin;
        this.maritalStatus = builder.maritalStatus;
        this.residentialStatus = builder.residentialStatus;
        this.nationality = builder.nationality;
        this.address = builder.address;
        this.contact = builder.contact;
        this.sex = builder.sex;
        this.bloodType = builder.bloodType;
        this.isVaccinated = builder.isVaccinated;
    }


    /**
     * Retrieves the NRIC/FIN (identification number) of the person.
     *
     * @return The NRIC/FIN of the person.
     */
    public String getNricFin() {
        return nricFin;
    }

    /**
     * Displays the contact information of the person.
     * This method delegates the actual display functionality
     * to the {@code displayContactInfo} method of the {@code Contact} class.
     */
    public void displayContactInformation() {
        contact.displayContactInfo();
    }

    public void displayHuman() {
        System.out.printf("%n%n");
        System.out.println("=====================================================================");
        System.out.printf("                           PERSON DETAILS%n");
        System.out.println("=====================================================================");
        System.out.println("PERSONAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Name: " + name);
        System.out.printf("\t\tNRIF/FIN: " + nricFin);
        System.out.printf("%n%nDate of Birth: " + dateOfBirth);
        System.out.printf("\t\tNationality: " + nationality);
        System.out.println("\n\nDEMOGRAPHIC & RESIDENTIAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Contact: " + contact);
        System.out.printf("\t\tMarital Status: " + maritalStatus);
        System.out.printf("%n%nResidential Status: " + residentialStatus);
        System.out.printf("\n\nAddress: " + address);
        System.out.println("\n\nMEDICAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Sex: " + sex);
        System.out.printf("\t\tBlood Type: " + bloodType);
        System.out.printf("\t\tVaccinated: " + isVaccinated);
    }
}