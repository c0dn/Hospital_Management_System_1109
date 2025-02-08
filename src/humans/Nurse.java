package humans;

import java.time.LocalDate;

/**
 * Represents the details of a nurse in the insurance system.
 */
public class Nurse extends Staff{

    /** The Registered Nurse ID (RNID) of a nurse. */
    private String rnid;

    /**
     * Constructs a Nurse object with specified details.\
     *
     * @param name The nurse's name.
     * @param dateOfBirth The nurse's date of birth.
     * @param nricFin The nurse's NRIC or FIN number.
     * @param maritalStatus The nurse's marital status.
     * @param residentialStatus The nurse's residential status.
     * @param nationality The nurse's nationality.
     * @param address The nurse's residential address.
     * @param contact The nurse's contact details.
     * @param sex The nurse's sex.
     * @param bloodType The nurse's blood type.
     * @param isVaccinated Indicates if the nurse is vaccinated.
     * @param staffId The nurse's staff ID.
     * @param title The nurse's job title.
     * @param department The department the nurse works in.
     * @param rnid The nurse's registered nurse ID.
     */
    public Nurse(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                  ResidentialStatus residentialStatus, String nationality, String address, Contact contact,
                  Sex sex, BloodType bloodType, boolean isVaccinated, String staffId, String title, String department,
                  String rnid) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus, nationality, address, contact, sex,
                bloodType, isVaccinated, staffId, title, department);

        this.rnid = rnid;
    }

    /**
     * Gets the nurse's Registered Nurse ID.
     *
     * @return The RNID.
     */
    private String getRnid() { return rnid; }

    /**
     * Displays the nurse's Registered Nurse ID (RNID), including the inherited staff details.
     */
    @Override
    public void displayStaff(){
        super.displayStaff();
        System.out.println("RNID: " + rnid);
    }
}