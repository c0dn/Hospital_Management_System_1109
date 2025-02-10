package humans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatientBuilder extends HumanBuilder<PatientBuilder> {
    private static final Random random = new Random();
    private static final String[] DRUG_ALLERGIES = {
            "Penicillin", "Aspirin", "Ibuprofen", "Sulfa", "None"
    };

    String patientId;
    List<String> drugAllergies = new ArrayList<>();
    String nokName;
    String nokAddress;
    NokRelation nokRelation;
    double height;
    double weight;
    String occupation;
    String companyName;
    String companyAddress;

    public PatientBuilder() {}

    public PatientBuilder patientId(String patientId) {
        this.patientId = patientId;
        return self();
    }

    public PatientBuilder addDrugAllergy(String allergy) {
        this.drugAllergies.add(allergy);
        return self();
    }

    public PatientBuilder drugAllergies(List<String> drugAllergies) {
        this.drugAllergies = new ArrayList<>(drugAllergies);
        return self();
    }

    public PatientBuilder nokName(String nokName) {
        this.nokName = nokName;
        return self();
    }

    public PatientBuilder nokAddress(String nokAddress) {
        this.nokAddress = nokAddress;
        return self();
    }

    public PatientBuilder nokRelation(NokRelation nokRelation) {
        this.nokRelation = nokRelation;
        return self();
    }

    public PatientBuilder height(double height) {
        this.height = height;
        return self();
    }

    public PatientBuilder weight(double weight) {
        this.weight = weight;
        return self();
    }

    public PatientBuilder occupation(String occupation) {
        this.occupation = occupation;
        return self();
    }

    public PatientBuilder companyName(String companyName) {
        this.companyName = companyName;
        return self();
    }

    public PatientBuilder companyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return self();
    }

    @Override
    public PatientBuilder withRandomBaseData() {
        super.withRandomBaseData();

        // Generate height between 1.5m and 2m
        this.height = 1.5 + random.nextDouble() * 0.5;

        // Generate weight between 45kg and 100kg
        this.weight = 45 + random.nextDouble() * 55;

        // Generate NOK relation and name
        this.nokRelation = DataGenerator.getRandomEnum(NokRelation.class);
        this.nokName = generateNokName(this.name, this.nokRelation);

        // Generate addresses and other details
        this.nokAddress = DataGenerator.generateSGAddress();
        this.occupation = DataGenerator.getRandomOccupation();
        this.companyName = DataGenerator.getRandomCompanyName();
        this.companyAddress = DataGenerator.generateSGAddress();

        // Generate drug allergies (0-2 allergies)
        this.drugAllergies.clear();
        int numAllergies = random.nextInt(3);
        for (int i = 0; i < numAllergies; i++) {
            this.drugAllergies.add(DRUG_ALLERGIES[random.nextInt(DRUG_ALLERGIES.length)]);
        }

        return self();
    }

    private String generateNokName(String patientName, NokRelation relation) {
        String[] patientNameParts = patientName.split(" ");
        String familyName = (patientNameParts.length > 1) ?
                patientNameParts[patientNameParts.length - 1] : patientName;

        switch (relation) {
            case SPOUSE, SIBLING, PARENT -> {
                // Keep the same family name
                String[] nokNameParts = DataGenerator.getRandomElement(DataGenerator.SG_NAMES).split(" ");
                return nokNameParts[0] + " " + familyName;
            }
            case CHILD, GRANDCHILD -> {
                // Child/Grandchild should have patient's family name
                String[] nokNameParts = DataGenerator.getRandomElement(DataGenerator.SG_NAMES).split(" ");
                return nokNameParts[0] + " " + familyName;
            }
            case GRANDPARENT -> {
                // Grandparent might have different family name
                return DataGenerator.getRandomElement(DataGenerator.SG_NAMES);
            }
            case GUARDIAN, OTHER -> {
                // Different family name for non-blood relations
                return DataGenerator.getRandomElement(DataGenerator.SG_NAMES);
            }
            default -> {
                return DataGenerator.getRandomElement(DataGenerator.SG_NAMES);
            }
        }
    }

    public PatientBuilder withRandomData(String patientId) {
        withRandomBaseData();
        this.patientId = patientId;
        return self();
    }

    @Override
    public Patient build() {
        validateRequiredFields();
        validatePatientFields();
        return new Patient(this);
    }

    private void validatePatientFields() {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalStateException("Patient ID is required");
        }
        if (nokName == null || nokName.trim().isEmpty()) {
            throw new IllegalStateException("Next of kin name is required");
        }
        if (nokAddress == null || nokAddress.trim().isEmpty()) {
            throw new IllegalStateException("Next of kin address is required");
        }
        if (nokRelation == null) {
            throw new IllegalStateException("Next of kin relation is required");
        }
        if (height <= 0) {
            throw new IllegalStateException("Height must be greater than 0");
        }
        if (weight <= 0) {
            throw new IllegalStateException("Weight must be greater than 0");
        }
    }
}