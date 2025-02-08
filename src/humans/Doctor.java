package humans;

import java.time.LocalDate;

/**
 * Represents a doctor in the insurance system.
 * <p>
 *     A doctor is a specialized type of {@link Staff} with an additional Medical Council Registration (MCR) number.
 * </p>
 */
public class Doctor extends Staff {

    /**
     * The Medical Council Registration (MCR) number of the doctor.
     */
    private final String mcr;

    /**
     * Constructs a Doctor object with the specified details.
     *
     * @param name              The name of the doctor.
     * @param dateOfBirth       The date of birth of the doctor.
     * @param nricFin           The NRIC/FIN (identification number) of the doctor.
     * @param maritalStatus     The marital status of the doctor.
     * @param residentialStatus The residential status of the doctor.
     * @param nationality       The nationality of the doctor.
     * @param address           The residential address of the doctor.
     * @param contact           The contact details of the doctor.
     * @param sex               The sex of the doctor.
     * @param bloodType         The blood type of the doctor.
     * @param isVaccinated      The vaccination status of the doctor.
     * @param staffId           The staff ID of the doctor.
     * @param title             The title of the doctor (eg. Senior Doctor, Specialist).
     * @param department        The department the doctor belongs to.
     * @param mcr               The Medical Council Registration (MCR) number of the doctor.
     */
    public Doctor(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                  ResidentialStatus residentialStatus, String nationality, String address, Contact contact,
                  Sex sex, BloodType bloodType, boolean isVaccinated, String staffId, String title, String department,
                  String mcr) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus, nationality, address, contact, sex,
                bloodType, isVaccinated, staffId, title, department);

        this.mcr = mcr;
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
     * Displays doctor's Medical Council Registration (MCR) number, including the inherited staff details.
     */
    @Override
    public void displayStaff()
    {
        super.displayStaff();
        System.out.println("MCR: " + mcr);
    }
}
