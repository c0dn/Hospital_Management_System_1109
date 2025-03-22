package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores contact details of a person, such as phone number and email.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Contact {

    /**
     * The personal phone number of the person.
     */
    private final String personalPhone;

    /**
     * The home phone number of the person.
     */
    private final String homePhone;

    /**
     * The company phone number of the person.
     */
    private final String companyPhone;

    /**
     * The email address of the person.
     */
    private final String email;

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

    @JsonCreator
    public static Contact create(
            @JsonProperty("personalPhone") String personalPhone,
            @JsonProperty("homePhone") String homePhone,
            @JsonProperty("companyPhone") String companyPhone,
            @JsonProperty("email") String email) {
        return new Contact(personalPhone, homePhone, companyPhone, email);
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

}
