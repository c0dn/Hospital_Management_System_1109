package humans;

import java.time.LocalDate;

public class Nurse extends Staff{

    /** The Registered Nurse ID (RNID) of a nurse. */
    private String rnid;

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

    @Override
    public void displayStaff(){
        super.displayStaff();
        System.out.println("RNID: " + rnid);
    }
}