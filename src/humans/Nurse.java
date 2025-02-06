package humans;

import java.time.LocalDate;

public class Nurse extends Staff{
    private String rnid;

    public Nurse(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                  ResidentialStatus residentialStatus, String nationality, String address, Contact contact,
                  Sex sex, BloodType bloodType, boolean isVaccinated, String staffId, String title, String department,
                  String rnid) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus, nationality, address, contact, sex,
                bloodType, isVaccinated, staffId, title, department);

        this.rnid = rnid;
    }

    public String getrnid() { return rnid; }
}
