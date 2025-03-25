package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;
import org.bee.utils.JSONReadable;
import org.bee.utils.JSONWritable;

import java.time.LocalDate;

/**
 * Represents a clerk in the hospital management system.
 * Clerks are administrative staff members responsible for managing
 * paperwork, appointments, and other administrative tasks.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Clerk extends Staff implements JSONReadable, JSONWritable, SystemUser {
    /**
     * Creates a new instance of ClerkBuilder to construct a Clerk object.
     *
     * @return A new ClerkBuilder instance
     */
    public static ClerkBuilder builder() {
        return ClerkBuilder.builder();
    }

    /**
     * Constructs a Clerk object using the provided StaffBuilder.
     * Uses the parent Staff class constructor to initialize staff-specific
     * details and inherited human attributes.
     *
     * @param builder The builder instance of type {@code StaffBuilder<?>} used to initialize
     *               the Clerk object.
     */
    Clerk(StaffBuilder<?> builder) {
        super(builder);
        this.humanType = "clerk";
    }


    /**
     * Factory method for deserializing Clerk objects from JSON using Jackson.
     * <p>
     * This method provides a way for Jackson to reconstruct Clerk objects during
     * deserialization without requiring a default constructor. It preserves the
     * Clerk class's builder-based construction pattern while enabling JSON serialization.
     *
     * @param name                The name of the clerk
     * @param dob                 The date of birth of the clerk
     * @param nricFin             The NRIC/FIN number of the clerk
     * @param maritalStatus       The marital status of the clerk
     * @param residentialStatus   The residential status of the clerk
     * @param nationality         The nationality of the clerk
     * @param address             The address of the clerk
     * @param contact             The contact information of the clerk
     * @param sex                 The sex of the clerk
     * @param bloodType           The blood type of the clerk
     * @param isVaccinated        Vaccination status of the clerk
     * @param staffId             The staff identifier of the clerk
     * @param title               The professional title of the clerk
     * @param department          The department the clerk belongs to
     *
     * @return A fully constructed Clerk object with all properties set from JSON data
     */
    @JsonCreator
    public static Clerk fromJson(
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
            @JsonProperty("humanType") String humanType
    ) {
        ClerkBuilder builder = ClerkBuilder.builder();

        setHumanFields(builder, name, dob, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated, humanType);

        setStaffFields(builder, staffId, title, department);

        return new Clerk(builder);
    }

    @Override
    public String getUsername() {
        return staffId;
    }
}
