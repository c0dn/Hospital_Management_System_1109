package humans;

import java.time.LocalDate;
import utils.DataGenerator;

abstract class HumanBuilder<T extends HumanBuilder<T>> {
    protected static final DataGenerator dataGenerator = DataGenerator.getInstance();
    
    String name;
    LocalDate dateOfBirth;
    String nricFin;
    MaritalStatus maritalStatus;
    ResidentialStatus residentialStatus;
    String nationality;
    String address;
    Contact contact;
    Sex sex;
    BloodType bloodType;
    boolean isVaccinated;

    HumanBuilder() {}

    /**
     * Returns the current instance of the builder class.
     * Exists so chaining is possible
     *
     * @return The current instance of type T.
     */
    @SuppressWarnings("unchecked")
    public T self() {
        return (T) this;
    }

    public T name(String name) {
        this.name = name;
        return self();
    }

    public T dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return self();
    }

    public T nricFin(String nricFin) {
        this.nricFin = nricFin;
        return self();
    }

    public T maritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return self();
    }

    public T residentialStatus(ResidentialStatus residentialStatus) {
        this.residentialStatus = residentialStatus;
        return self();
    }

    public T nationality(String nationality) {
        this.nationality = nationality;
        return self();
    }

    public T address(String address) {
        this.address = address;
        return self();
    }

    public T contact(Contact contact) {
        this.contact = contact;
        return self();
    }

    public T sex(Sex sex) {
        this.sex = sex;
        return self();
    }

    public T bloodType(BloodType bloodType) {
        this.bloodType = bloodType;
        return self();
    }

    public T isVaccinated(boolean isVaccinated) {
        this.isVaccinated = isVaccinated;
        return self();
    }

    public T withRandomBaseData() {
        this.name = dataGenerator.getRandomElement(dataGenerator.getSgNames());
        this.dateOfBirth = LocalDate.now().minusYears(dataGenerator.generateRandomInt(20, 60)); // Age between 20-60
        this.nricFin = dataGenerator.generateNRICNumber();
        this.maritalStatus = dataGenerator.getRandomEnum(MaritalStatus.class);
        this.residentialStatus = dataGenerator.getRandomEnum(ResidentialStatus.class);
        this.nationality = "Singaporean";
        this.address = dataGenerator.generateSGAddress();
        this.contact = dataGenerator.generateContact();
        this.sex = dataGenerator.getRandomEnum(Sex.class);
        this.bloodType = dataGenerator.getRandomEnum(BloodType.class);
        this.isVaccinated = dataGenerator.generateRandomInt(2) == 1; // 50% chance of being vaccinated
        return self();
    }

    protected abstract Human build();

    // Validate required fields before building
    protected void validateRequiredFields() {
        if (name == null || dateOfBirth == null || nricFin == null ||
                maritalStatus == null || residentialStatus == null ||
                nationality == null || address == null || contact == null ||
                sex == null || bloodType == null) {
            throw new IllegalStateException("All required fields must be set");
        }
    }
}
