package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.utils.JSONSerializable;

import java.util.Optional;

/**
 * Stores contact details of a person, such as phone number and email.
 */
public class Contact implements JSONSerializable {

    /**
     * The personal phone number of the person.
     */
    private String personalPhone;

    /**
     * The home phone number of the person.
     */
    private String homePhone;

    /**
     * The company phone number of the person.
     */
    private String companyPhone;

    /**
     * The email address of the person.
     */
    private String email;

    /**
     * Constructs a Contact object with the specified phone numbers and email.
     *
     * @param personalPhone The personal phone number.
     * @param homePhone     The home phone number.
     * @param companyPhone  The company phone number.
     * @param email         The email address.
     */
    public Contact(String personalPhone, String homePhone, String companyPhone, String email) {
        this.personalPhone = personalPhone;
        this.homePhone = homePhone;
        this.companyPhone = companyPhone;
        this.email = email;
    }

    /**
     * Creates a Contact instance from JSON properties
     *
     * @param personalPhone The personal phone number
     * @param homePhone The home phone number
     * @param companyPhone The company phone number
     * @param email The email address
     * @return A new Contact instance
     */
    @JsonCreator
    public static Contact create(
            @JsonProperty("personalPhone") String personalPhone,
            @JsonProperty("homePhone") String homePhone,
            @JsonProperty("companyPhone") String companyPhone,
            @JsonProperty("email") String email) {
        return new Contact(personalPhone, homePhone, companyPhone, email);
    }

    /**
     * Gets the personal phone number
     *
     * @return The personal phone numbe
     */
    public String getPersonalPhone() {
        return personalPhone;
    }

    /**
     * Returns the home phone number as an Optional.
     * @return Optional containing the home phone number or empty if null
     */
    public Optional<String> getHomePhone() {
        return Optional.ofNullable(homePhone);
    }

    /**
     * Returns the company phone number as an Optional.
     * @return Optional containing the company phone number or empty if null
     */
    public Optional<String> getCompanyPhone() {
        return Optional.ofNullable(companyPhone);
    }

    /**
     * Returns the email as an Optional.
     * @return Optional containing the email or empty if null
     */
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

}
