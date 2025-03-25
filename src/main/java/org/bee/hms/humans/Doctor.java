package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.medical.Consultation;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a doctor in the insurance system.
 * <p>
 *     A doctor is a specialized type of {@link Staff} with an additional Medical Council Registration (MCR) number.
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
     * @param name                The name of the doctor
     * @param dob                 The date of birth of the doctor
     * @param nricFin             The NRIC/FIN number of the doctor
     * @param maritalStatus       The marital status of the doctor
     * @param residentialStatus   The residential status of the doctor
     * @param nationality         The nationality of the doctor
     * @param address             The address of the doctor
     * @param contact             The contact information of the doctor
     * @param sex                 The sex of the doctor
     * @param bloodType           The blood type of the doctor
     * @param isVaccinated        Vaccination status of the doctor
     * @param staffId             The staff identifier of the doctor
     * @param title               The professional title of the doctor
     * @param department          The department the doctor belongs to
     * @param mcr                 The Medical Council Registration number of the doctor
     *
     * @return A fully constructed Doctor object with all properties set from JSON data
     */
    @JsonCreator
    public static Doctor fromJson(
            @JsonProperty("name") String name,
            @JsonProperty("dob") LocalDate dob,
            @JsonProperty("nric_fin") String nricFin,
            @JsonProperty("marital_status") MaritalStatus maritalStatus,
            @JsonProperty("residential_status") ResidentialStatus residentialStatus,
            @JsonProperty("nationality") String nationality,
            @JsonProperty("address") String address,
            @JsonProperty("contact") Contact contact,
            @JsonProperty("sex") Sex sex,
            @JsonProperty("blood_type") BloodType bloodType,
            @JsonProperty("is_vaccinated") boolean isVaccinated,
            @JsonProperty("staff_id") String staffId,
            @JsonProperty("title") String title,
            @JsonProperty("department") String department,
            @JsonProperty("mcr") String mcr,
            @JsonProperty("humanType") String humanType
    ) {
        DoctorBuilder builder = new DoctorBuilder();

        setHumanFields(builder, name, dob, nricFin, maritalStatus, residentialStatus,
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

    public void printAsAttending() {
        System.out.format("%-20s: %s (ID: %s)%n",
                "Attending Doctor",
                name,
                mcr);
    }


    /**
     * Displays doctor's Medical Council Registration (MCR) number, including the inherited staff details.
     */
    @Override
    public void displayHuman() {
        super.displayHuman();
        System.out.println("\n\nROLE: DOCTOR");
        System.out.println("---------------------------------------------------------------------");
        System.out.format("MCR: %s%n", mcr);
        System.out.println("=====================================================================");
    }

    @Override
    public String getUsername() {
        return staffId;
    }
}
