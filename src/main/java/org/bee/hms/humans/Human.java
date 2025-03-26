package org.bee.hms.humans;

import java.time.LocalDate;

import org.bee.utils.JSONSerializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "humanType",
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Patient.class, name = "patient"),
    @JsonSubTypes.Type(value = Staff.class, name = "staff"),
    @JsonSubTypes.Type(value = Clerk.class, name = "clerk"),
    @JsonSubTypes.Type(value = Doctor.class, name = "doctor"),
    @JsonSubTypes.Type(value = Nurse.class, name = "nurse")
})
public abstract class Human implements JSONSerializable {

    /**
     * The name of the person.
     */
    protected String name;

    /**
     * JSON serialization
     */
    protected String humanType;

    /**
     * The date of birth of the person.
     */
    protected LocalDate dateOfBirth;

    /**
     * The NRIC/FIN (identification number) of the person.
     */
    protected String nricFin;

    /**
     * The marital status of the person.
     */
    protected MaritalStatus maritalStatus;

    /**
     * The residential status of the person.
     */
    protected ResidentialStatus residentialStatus;

    /**
     * The nationality of the person.
     */
    protected String nationality;

    /**
     * The residential address of the person.
     */
    protected String address;

    /**
     * The contact details of the person.
     */
    protected Contact contact;

    /**
     * The sex of the person.
     */
    protected Sex sex;

    /**
     * The blood type of the person.
     */
    protected BloodType bloodType;

    /**
     * The vaccination status of the person.
     */
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
        this.humanType = builder.humanType;
    }

    /**
     * Utility method to set common Human fields on any builder that extends HumanBuilder.
     * This helps avoid repetition in factory methods for Human subclasses.
     *
     * @param <T> The type of builder extending HumanBuilder
     * @param builder The builder instance
     * @param name The name of the human
     * @param dob The date of birth
     * @param nricFin The NRIC/FIN number
     * @param maritalStatus The marital status
     * @param residentialStatus The residential status
     * @param nationality The nationality
     * @param address The address
     * @param contact The contact information
     * @param sex The sex
     * @param bloodType The blood type
     * @param isVaccinated The vaccination status
     * @return The same builder instance with human fields set
     */
    protected static <T extends HumanBuilder<?>> T setHumanFields(
            T builder,
            String name,
            LocalDate dob,
            String nricFin,
            MaritalStatus maritalStatus,
            ResidentialStatus residentialStatus,
            String nationality,
            String address,
            Contact contact,
            Sex sex,
            BloodType bloodType,
            boolean isVaccinated,
            String humanType
    ) {
        builder.name(name);
        builder.dateOfBirth(dob);
        builder.nricFin(nricFin);
        builder.maritalStatus(maritalStatus);
        builder.residentialStatus(residentialStatus);
        builder.nationality(nationality);
        builder.address(address);
        builder.contact(contact);
        builder.sex(sex);
        builder.bloodType(bloodType);
        builder.isVaccinated(isVaccinated);
        builder.humanType(humanType);

        return builder;
    }


    /**
     * Retrieves the NRIC/FIN (identification number) of the person.
     *
     * @return The NRIC/FIN of the person.
     */
    public String getNricFin() {
        return nricFin;
    }

    public String getAddress() {
        return address;
    }

    public Sex getSex() { return sex; }


    /**
     * Checks if the person is a Singapore citizen.
     *
     * @return true if the person is a citizen, false otherwise
     */
    public boolean isSingaporean() {
        return residentialStatus == ResidentialStatus.CITIZEN;
    }

    /**
     * Checks if the person is a permanent resident.
     *
     * @return true if the person is a permanent resident, false otherwise
     */
    public boolean isPermanentResident() {
        return residentialStatus == ResidentialStatus.PERMANENT_RESIDENT;
    }

    /**
     * Checks if the person is on a work pass.
     *
     * @return true if the person holds a work pass, false otherwise
     */
    public boolean isWorkPassHolder() {
        return residentialStatus == ResidentialStatus.WORK_PASS;
    }

    /**
     * Checks if the person is on a dependent pass.
     *
     * @return true if the person is on a dependent pass, false otherwise
     */
    public boolean isDependentPassHolder() {
        return residentialStatus == ResidentialStatus.DEPENDENT_PASS;
    }

    /**
     * Checks if the person is a visitor.
     *
     * @return true if the person is a visitor, false otherwise
     */
    public boolean isVisitor() {
        return residentialStatus == ResidentialStatus.VISITOR;
    }

    /**
     * Checks if the person is a resident (either citizen or permanent resident).
     *
     * @return true if the person is either a citizen or permanent resident, false otherwise
     */
    public boolean isResident() {
        return residentialStatus == ResidentialStatus.CITIZEN ||
                residentialStatus == ResidentialStatus.PERMANENT_RESIDENT;
    }


    public String getName() {
        return name;
    }

    /**
     * Calculates the current age of the person based on their date of birth.
     *
     * @return the person's current age in years
     */
    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }


    /**
     * Retrieves the date of birth of the person.
     *
     * @return The date of birth as a {@code LocalDate} object.
     */
    public LocalDate getDOB() {
        return dateOfBirth;
    }

    /**
     * Displays the full details of the person including personal, demographic, residential,
     * and medical information.
     */
    public void displayHuman() {
        System.out.printf("%n");
        System.out.println("=====================================================================");
        System.out.printf("                           PERSON DETAILS%n");
        System.out.println("=====================================================================");
        System.out.println("PERSONAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Name: " + name);
        System.out.printf("\nNRIF/FIN: " + nricFin);
        System.out.printf("%nDate of Birth: " + dateOfBirth);
        System.out.printf("\nNationality: " + nationality);
        System.out.println("\n\nDEMOGRAPHIC & RESIDENTIAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Contact: " + contact);
        System.out.printf("\ntMarital Status: " + maritalStatus);
        System.out.printf("%n%nResidential Status: " + residentialStatus);
        System.out.printf("\nAddress: " + address);
        System.out.println("\n\nMEDICAL INFORMATION");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("Sex: " + sex);
        System.out.printf("\nBlood Type: " + bloodType);
        System.out.printf("\nVaccinated: " + isVaccinated);
    }

    public Contact getContact() {
        return contact;
    }
}
