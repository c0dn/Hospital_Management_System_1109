package humans;

/**
 * Stores contact details of a person, such as phone number and email.
 */

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

}
