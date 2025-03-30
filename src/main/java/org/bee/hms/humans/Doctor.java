package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;

import java.time.LocalDate;

/**
 * Represents a doctor in the healthcare management system.
 * <p>
 *     A doctor is a specialized type of {@link Staff} with an additional Medical Council Registration (MCR) number.
 * </p>
 */
public class Doctor extends Staff implements SystemUser {

    /**
     * The Medical Council Registration (MCR) number of the doctor.
     */
    private final String mcr;


    /**
     * Constructs a Doctor object using the provided DoctorBuilder.
     * This constructor initializes the doctor-specific details,
     * including the Medical Council Registration (MCR) number,
     * as well as all inherited attributes from the {@link Staff} and {@link Human} classes.
     *
     * @param builder The {@code DoctorBuilder} instance used to initialize the Doctor object.
     *                Contains the data required to populate both doctor-specific fields such as the MCR number,
     *                and inherited attributes of the Doctor class.
     */
    Doctor(DoctorBuilder builder) {
        super(builder);
        this.mcr = builder.mcr;
        this.humanType = "doctor";

    }


    /**
     * Creates and returns a new {@code DoctorBuilder} instance.
     * This builder can be used to construct instances of the {@code Doctor} class.
     *
     * @return A new {@code DoctorBuilder} instance for building {@code Doctor} objects.
     */
    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }


    /**
     * Factory method for deserializing Doctor objects from JSON using Jackson.
     * <p>
     * This method provides a way for Jackson to reconstruct Doctor objects during
     * deserialization without requiring a default constructor. It preserves the
     * Doctor class's builder-based construction pattern while enabling JSON serialization.
     *
     * @param name                The name of the doctor (from JSON "name")
     * @param dateOfBirth         The date of birth of the doctor (from JSON "dateOfBirth")
     * @param nricFin             The NRIC/FIN number of the doctor (from JSON "nricFin")
     * @param maritalStatus       The marital status of the doctor (from JSON "maritalStatus")
     * @param residentialStatus   The residential status of the doctor (from JSON "residentialStatus")
     * @param nationality         The nationality of the doctor (from JSON "nationality")
     * @param address             The address of the doctor (from JSON "address")
     * @param contact             The contact information of the doctor (from JSON "contact")
     * @param sex                 The sex of the doctor (from JSON "sex")
     * @param bloodType           The blood type of the doctor (from JSON "bloodType")
     * @param isVaccinated        Vaccination status of the doctor (from JSON "isVaccinated")
     * @param staffId             The staff identifier of the doctor (from JSON "staffId")
     * @param title               The professional title of the doctor (from JSON "title")
     * @param department          The department the doctor belongs to (from JSON "department")
     * @param mcr                 The Medical Council Registration number of the doctor (from JSON "mcr")
     * @param humanType           The type of human, e.g., "doctor" (from JSON "humanType")
     *
     * @return A fully constructed Doctor object with all properties set from JSON data
     */
    @JsonCreator
    public static Doctor fromJson(
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
            @JsonProperty("mcr") String mcr,
            @JsonProperty("humanType") String humanType
    ) {
        DoctorBuilder builder = new DoctorBuilder();

        // Pass the corrected dateOfBirth parameter
        setHumanFields(builder, name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated, humanType);

        setStaffFields(builder, staffId, title, department);

        builder.mcr(mcr);

        return new Doctor(builder);
    }


    /**
     * Retrieves the Medical Council Registration (MCR) number of the doctor.
     *
     * @return The MCR number.
     */
    public String getMcr() {
        return mcr;
    }

    /**
     * Returns the staffId as the username
     *
     * @return The staffId
     */
    @Override
    public String getUsername() {
        return staffId;
    }
}
