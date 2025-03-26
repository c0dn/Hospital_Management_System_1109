package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;

import java.time.LocalDate;

/**
 * Represents the details of a nurse in the insurance system.
 * <p>
 * This class extends {@link Staff} and adds attributes specific to nurses,
 * such as the Registered Nurse ID (RNID). It is constructed using the
 * {@link NurseBuilder} to ensure proper initialization of required fields.
 * </p>
 */
public class Nurse extends Staff implements SystemUser {

    /** The Registered Nurse ID (RNID) of a nurse. */
    private String rnid;


    /**
     * Constructs a Nurse instance using the provided NurseBuilder.
     * This constructor initializes the attributes of the Nurse class, including
     * the Registered Nurse ID (RNID) specific to nurses, while also inheriting
     * and initializing attributes from the Staff class.
     *
     * @param builder The builder instance of type {@code NurseBuilder} used to initialize
     *                the Nurse object. The builder provides the data necessary
     *                for populating the nurse-specific and inherited fields.
     */
    Nurse(NurseBuilder builder) {
        super(builder);
        this.rnid = builder.rnid;
        this.humanType = "nurse";
    }

    /**
     * Factory method for deserializing Nurse objects from JSON using Jackson.
     * <p>
     * This method provides a way for Jackson to reconstruct Nurse objects during
     * deserialization without requiring a default constructor. It preserves the
     * Nurse class's builder-based construction pattern while enabling JSON serialization.
     *
     * @param name                The name of the nurse (from JSON "name")
     * @param dateOfBirth         The date of birth of the nurse (from JSON "dateOfBirth")
     * @param nricFin             The NRIC/FIN number of the nurse (from JSON "nricFin")
     * @param maritalStatus       The marital status of the nurse (from JSON "maritalStatus")
     * @param residentialStatus   The residential status of the nurse (from JSON "residentialStatus")
     * @param nationality         The nationality of the nurse (from JSON "nationality")
     * @param address             The address of the nurse (from JSON "address")
     * @param contact             The contact information of the nurse (from JSON "contact")
     * @param sex                 The sex of the nurse (from JSON "sex")
     * @param bloodType           The blood type of the nurse (from JSON "bloodType")
     * @param isVaccinated        Vaccination status of the nurse (from JSON "isVaccinated")
     * @param staffId             The staff identifier of the nurse (from JSON "staffId")
     * @param title               The professional title of the nurse (from JSON "title")
     * @param department          The department the nurse belongs to (from JSON "department")
     * @param rnid                The Registered Nurse ID (RNID) of the nurse (from JSON "rnid")
     * @param humanType           The type of human, e.g., "nurse" (from JSON "humanType")
     *
     * @return A fully constructed Nurse object with all properties set from JSON data
     */
    @JsonCreator
    public static Nurse fromJson(
            @JsonProperty("name") String name,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
            @JsonProperty("nricFin") String nricFin,
            @JsonProperty("maritalStatus") MaritalStatus maritalStatus,
            @JsonProperty("residentialStatus") ResidentialStatus residentialStatus,
            @JsonProperty("nationality") String nationality,
            @JsonProperty("address") String address,
            @JsonProperty("contact") Contact contact,
            @JsonProperty("sex") Sex sex,
            @JsonProperty("bloodType") BloodType bloodType,
            @JsonProperty("isVaccinated") boolean isVaccinated,
            @JsonProperty("staffId") String staffId,
            @JsonProperty("title") String title,
            @JsonProperty("department") String department,
            @JsonProperty("rnid") String rnid,
            @JsonProperty("humanType") String humanType
    ) {
        NurseBuilder builder = new NurseBuilder();

        setHumanFields(builder, name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated, humanType);

        setStaffFields(builder, staffId, title, department);

        builder.rnid(rnid);

        return new Nurse(builder);
    }

    /**
     * Creates and returns a new instance of {@code NurseBuilder}.
     * <p>
     * This method provides a fluent interface for constructing
     * a {@code Nurse} object using the builder pattern.
     * </p>
     *
     * @return A new instance of {@link NurseBuilder} to facilitate nurse creation.
     */
    public static NurseBuilder builder() {
        return new NurseBuilder();
    }



    /**
     * Gets the nurse's Registered Nurse ID.
     *
     * @return The RNID.
     */
    public String getRnid() { return rnid; }

     /**
     * Displays the nurse's Registered Nurse ID (RNID) along with the inherited staff details.
     * <p>
     * This method first calls {@code displayStaff()} from the {@link Staff} class
     * and then prints the RNID specific to the nurse.
     * </p>
     */
    @Override
    public void displayHuman() {
        super.displayHuman();
//        System.out.format("RNID: %s%n", rnid);

        System.out.println("\n\nROLE: NURSE");
        System.out.println("---------------------------------------------------------------------");
        System.out.format("RNID: %s%n", rnid);
        System.out.println("=====================================================================");
    }

    public void printAsAttending() {
        System.out.printf("  - Attending Nurse: %s (RNID: %s)%n", name, rnid);
    }

    @Override
    public String getUsername() {
        return staffId;
    }
}
