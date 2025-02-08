package humans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a patient in the insurance system.
 * Patients have medical records have insurance details.
 */

public class Patient extends Human {
    /** The unique identifier for the patient. */
    private final String patientId;
    /** A list of the patient's drug allergies. */
    private List<String> drugAllergies;
    /** The next of kin's name. */
    private String nokName;
    /** The next of kin's residential address. */
    private String nokAddress;
    /** The relationship of the patient and the next of kin. */
    private NokRelation nokRelation;
    /** The patient's height in metres. */
    private double height; // in meters
    /** The patient's weight in kilograms. */
    private double weight; // in kilograms
    private String occupation;
    private String companyName;
    private String companyAddress;


    public static class Generator {
        private static final Random random = new Random();
        private static final String[] DRUG_ALLERGIES = {
                "Penicillin", "Aspirin", "Ibuprofen", "Sulfa", "None"
        };

        public static Patient createRandom(String patientId) {
            String name = DataGenerator.getRandomElement(DataGenerator.SG_NAMES);
            LocalDate dob = LocalDate.now().minusYears(20 + random.nextInt(60));

            String nricPrefix = dob.getYear() < 2000 ? "S" : "T";
            String nricFin = String.format("%s%07d%c",
                    nricPrefix, random.nextInt(1000000, 9999999),
                    (char)('A' + random.nextInt(26)));

            List<String> allergies = List.of(DRUG_ALLERGIES[random.nextInt(DRUG_ALLERGIES.length)]);

            NokRelation nokRelation = DataGenerator.getRandomEnum(NokRelation.class);
            String nokName = generateNokName(name, nokRelation);

            return new Patient(
                    name,
                    dob,
                    nricFin,
                    DataGenerator.getRandomEnum(MaritalStatus.class),
                    DataGenerator.getRandomEnum(ResidentialStatus.class),
                    "Singaporean",
                    DataGenerator.generateSGAddress(),
                    DataGenerator.generateContact(),
                    DataGenerator.getRandomEnum(Sex.class),
                    DataGenerator.getRandomEnum(BloodType.class),
                    random.nextBoolean(),
                    patientId,
                    allergies,
                    nokName,
                    DataGenerator.generateSGAddress(),
                    nokRelation,
                    150 + random.nextDouble() * 50,
                    50 + random.nextDouble() * 50,
                    DataGenerator.getRandomElement(DataGenerator.OCCUPATIONS),
                    DataGenerator.getRandomElement(DataGenerator.SG_COMPANIES),
                    DataGenerator.generateCompanyAddress()
            );
        }

        private static String generateNokName(String patientName, NokRelation relation) {
            if (relation == NokRelation.SPOUSE || relation == NokRelation.PARENT ||
                    relation == NokRelation.CHILD || relation == NokRelation.SIBLING) {
                String[] nameParts = patientName.split(" ");
                return DataGenerator.getRandomElement(DataGenerator.SG_NAMES).split(" ")[0] +
                        " " + nameParts[nameParts.length - 1];
            }
            return DataGenerator.getRandomElement(DataGenerator.SG_NAMES);
        }

        public static List<Patient> createRandom(int count) {
            List<Patient> patients = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                patients.add(createRandom("P" + (i + 1000)));
            }
            return patients;
        }
    }



    /**
     * Constructs a Patient object with specified details.
     *
     * @param name The patient's name.
     * @param dateOfBirth The patient's date of birth.
     * @param nricFin The patient's NRIC or FIN number.
     * @param maritalStatus The patient's marital status.
     * @param residentialStatus The patient's residential status.
     * @param nationality The patient's nationality.
     * @param address The patient's residental address.
     * @param contact The patient's contact details.
     * @param sex The patient's sex.
     * @param bloodType The patient's blood type.
     * @param isVaccinated Indicates if the patient is vaccinated.
     * @param patientId The patient's unique ID.
     * @param drugAllergies A list of the patient's drug allergies.
     * @param nokName The next of kin's name.
     * @param nokAddress The next of kin's residential address.
     * @param nokRelation The relationship between the patient and the next of kin.
     * @param height The patient's height in metres.
     * @param weight The patient's weight in kilograms.
     */
    public Patient(String name, LocalDate dateOfBirth, String nricFin,
                   MaritalStatus maritalStatus, ResidentialStatus residentialStatus,
                   String nationality, String address, Contact contact,
                   Sex sex, BloodType bloodType, boolean isVaccinated,
                   String patientId, List<String> drugAllergies, String nokName,
                   String nokAddress, NokRelation nokRelation,
                   double height, double weight,
                   String occupation, String companyName, String companyAddress) {

        super(name, dateOfBirth, nricFin, maritalStatus, residentialStatus,
                nationality, address, contact, sex, bloodType, isVaccinated);

        this.patientId = patientId;
        this.drugAllergies = drugAllergies;
        this.nokName = nokName;
        this.nokAddress = nokAddress;
        this.nokRelation = nokRelation;
        this.height = height;
        this.weight = weight;
        this.occupation = occupation;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    public String getPatientId() {
        return patientId;
    }


    public void displayPatientInfo() {
        System.out.format("Name: %s%n", name);
        System.out.format("Patient ID: %s%n", patientId);
        System.out.format("Date of Birth: %s%n", dateOfBirth);
        System.out.format("Height: %.2fm%n", height);
        System.out.format("Weight: %.2fkg%n", weight);
        System.out.format("Next of Kin: %s (%s), Address: %s%n", nokName, nokRelation, nokAddress);
        System.out.format("Drug Allergies: %s%n%n", drugAllergies);
    }

    public void displayInsrPatient() {
        System.out.format("Patient ID: %s%n", patientId);
        System.out.format("Name of Insured/Covered Member: %s%n", name);
        System.out.format("NRIC/FIN: %s%n", nricFin);
        System.out.format("Contact Information: %s%n", contact);
        System.out.format("Mailing Address: %s%n", address);
        System.out.format("Occupation: %s%n", occupation);
        System.out.format("Company Name: %s%n", companyName);
        System.out.format("Company Business Address: %s%n", companyAddress);
    }

    //create an insurance grouping information (all the information needed for insurance claim)
    //if there is a shared method (example displayInfo) for all the extend class, do the super method
}


