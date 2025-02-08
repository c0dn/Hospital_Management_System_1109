package humans;

import java.time.LocalDate;
import java.util.Random;

abstract class HumanBuilder<T extends HumanBuilder<T>> {
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

    private String generateNRIC() {
        Random random = new Random();
        String prefix = random.nextBoolean() ? "S" : "T";
        String numbers = String.format("%07d", random.nextInt(10000000));
        // Note: This is a simplified NRIC generation, not following actual checksum rules
        char[] checksum = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        return prefix + numbers + checksum[random.nextInt(checksum.length)];
    }

    public T withRandomBaseData() {
        this.name = DataGenerator.getRandomElement(DataGenerator.SG_NAMES);
        this.dateOfBirth = LocalDate.now().minusYears(20 + new Random().nextInt(40));
        this.nricFin = generateNRIC();
        this.maritalStatus = DataGenerator.getRandomEnum(MaritalStatus.class);
        this.residentialStatus = DataGenerator.getRandomEnum(ResidentialStatus.class);
        this.nationality = "Singaporean";
        this.address = DataGenerator.generateSGAddress();
        this.contact = DataGenerator.generateContact();
        this.sex = DataGenerator.getRandomEnum(Sex.class);
        this.bloodType = DataGenerator.getRandomEnum(BloodType.class);
        this.isVaccinated = new Random().nextBoolean();
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