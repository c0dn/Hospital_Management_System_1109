package humans;

import java.time.LocalDate;

public abstract class Human {
    private String name;
    private LocalDate dateOfBirth;
    private String nricFin;
    private MaritalStatus maritalStatus;
    private ResidentialStatus residentialStatus;
    private String nationality;
    private String address;
    private Contact contact;
    private Sex sex;
    private BloodType bloodType;
    private boolean isVaccinated;

    public Human(String name, LocalDate dateOfBirth, String nricFin, MaritalStatus maritalStatus,
                 ResidentialStatus residentialStatus, String nationality, String address,
                 Contact contact, Sex sex, BloodType bloodType, boolean isVaccinated) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.nricFin = nricFin;
        this.maritalStatus = maritalStatus;
        this.residentialStatus = residentialStatus;
        this.nationality = nationality;
        this.address = address;
        this.contact = contact;
        this.sex = sex;
        this.bloodType = bloodType;
        this.isVaccinated = isVaccinated;
    }

}
