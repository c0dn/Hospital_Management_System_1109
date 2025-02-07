package humans;

import java.time.LocalDate;

/**
 * Represents staff in the insurance system.
 * Staff members includes Doctors, Nurses and members who handle administrative roles.
 */

public class Staff extends Human {
    private String staffId;
    private String title;
    private String department;

    public Staff(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                 ResidentialStatus residentialStatus, String nationality, String address, Contact contact,
                 Sex sex, BloodType bloodType, boolean isVaccinated, String staffId, String title, String department) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus, nationality, address,
                contact, sex, bloodType, isVaccinated);

        this.staffId = staffId;
        this.title = title;
        this.department = department;
    }

    public String getStaffId() { return staffId; }

    public String getTitle() { return title; }

    public String getDepartment() { return department; }
}
