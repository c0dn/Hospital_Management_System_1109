package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.auth.SystemUser;
import org.bee.utils.JSONSerializable;

import java.time.LocalDate;

/**
 * Represents a clerk in the hospital management system.
 * Clerks are administrative staff members responsible for managing
 * paperwork, appointments, and other administrative tasks.
 */
public class Clerk extends Staff implements SystemUser {
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
     * @param name                The name of the clerk (from JSON "name")
     * @param dateOfBirth         The date of birth of the clerk (from JSON "dateOfBirth")
     * @param nricFin             The NRIC/FIN number of the clerk (from JSON "nricFin")
     * @param maritalStatus       The marital status of the clerk (from JSON "maritalStatus")
     * @param residentialStatus   The residential status of the clerk (from JSON "residentialStatus")
     * @param nationality         The nationality of the clerk (from JSON "nationality")
     * @param address             The address of the clerk (from JSON "address")
     * @param contact             The contact information of the clerk (from JSON "contact")
     * @param sex                 The sex of the clerk (from JSON "sex")
     * @param bloodType           The blood type of the clerk (from JSON "bloodType")
     * @param isVaccinated        Vaccination status of the clerk (from JSON "isVaccinated")
     * @param staffId             The staff identifier of the clerk (from JSON "staffId")
     * @param title               The professional title of the clerk (from JSON "title")
     * @param department          The department the clerk belongs to (from JSON "department")
     * @param humanType           The type of human, e.g., "clerk" (from JSON "humanType")
     *
     * @return A fully constructed Clerk object with all properties set from JSON data
     */
    @JsonCreator
    public static Clerk fromJson(
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
            @JsonProperty("humanType") String humanType
    ) {
        ClerkBuilder builder = ClerkBuilder.builder();

        setHumanFields(builder, name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated, humanType);

        setStaffFields(builder, staffId, title, department);

        return new Clerk(builder);
    }
    @Override
    public String getUsername() {
        return staffId;
    }
}
