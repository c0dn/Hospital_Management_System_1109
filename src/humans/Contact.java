package humans;

/**
 * Stores contact details of a person, such as phone number and email.
 */

public class Contact {

    /** The personal phone number of the person. */
    private String personalPhone;

    /** The home phone number of the person. */
    private String homePhone;

    /** The company phone number of the person. */
    private String companyPhone;

    /** The email address of the person. */
    private String email;

    /**
     * Constructs a Contact object with the specified phone numbers and email.
     *
     * @param personalPhone The personal phone number.
     * @param homePhone The home phone number.
     * @param companyPhone The company phone number.
     * @param email The email address.
     */
    public Contact(String personalPhone, String homePhone, String companyPhone, String email) {
        this.personalPhone = personalPhone;
        this.homePhone = homePhone;
        this.companyPhone = companyPhone;
        this.email = email;
    }

    /**
     * Retrieves the personal phone number.
     *
     * @return The personal phone number.
     */
    public String getPersonalPhone() {
        return personalPhone;
    }

    /**
     * Retrieves the home phone number.
     *
     * @return The home phone number.
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * Retrieves the company phone number.
     *
     * @return The company phone number.
     */
    public String getCompanyPhone() {
        return companyPhone;
    }

    /**
     * Retrieves the email address.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }


}
