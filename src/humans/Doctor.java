package humans;

import java.time.LocalDate;

public class Doctor extends Staff{
    private String mcr;

    public Doctor(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                  ResidentialStatus residentialStatus, String nationality, String address, Contact contact,
                  Sex sex, BloodType bloodType, boolean isVaccinated, String staffId, String title, String department,
                  String mcr) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus, nationality, address, contact, sex,
                bloodType, isVaccinated, staffId, title, department);

        this.mcr = mcr;
    }

    public String getmcr() { return mcr; }
}
