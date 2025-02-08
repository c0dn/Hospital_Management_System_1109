package humans;

import java.time.LocalDate;

/**
 * Represents staff in the insurance system.
 * <p>
 * Staff members include:
 * </p>
 * <ul>
 *     <li>Doctors</li>
 *     <li>Nurses</li>
 *     <li>Administrative personnel</li>
 * </ul>
 */

public class Staff extends Human {
    /** The unique identifier for the staff. */
    private String staffId;
    /** The title of the staff. */
    private String title;
    /** The department of the staff. */
    private String department;

    /**
     * Constructs a Staff object with the specified details.
     *
     * @param name The staff member's full name.
     * @param dateOfBirth The staff member's date of birth.
     * @param nricFin The staff member's NRIC or FIN number.
     * @param maritalStatus The staff member's marital status.
     * @param residentialStatus The staff member's residential status.
     * @param nationality The staff member's contact details.
     * @param address The staff member's residential address.
     * @param contact The staff member's contact details.
     * @param sex The staff member's sex.
     * @param bloodType The staff member's blood type.
     * @param isVaccinated Indicates if the staff member is vaccinated.
     * @param staffId The unique identifier for the staff.
     * @param title The staff member's job title.
     * @param department The department the staff member belongs to.
     */
    public Staff(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                 ResidentialStatus residentialStatus, String nationality, String address, Contact contact,
                 Sex sex, BloodType bloodType, boolean isVaccinated, String staffId, String title, String department) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus, nationality, address,
                contact, sex, bloodType, isVaccinated);

        this.staffId = staffId;
        this.title = title;
        this.department = department;
    }

    /**
     * Displays staff information.
     */
    public void displayStaff() {
        System.out.format("Name: %s%n", name);
        System.out.format("Title: %s%n", title);
        System.out.format("Department: %s%n", department);
        System.out.format("Staff ID: %s%n", staffId);
    }
}
