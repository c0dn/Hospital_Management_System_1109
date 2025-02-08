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
        private static final String[] OCCUPATIONS = {
                "Engineer", "Teacher", "Doctor", "Artist", "Chef", "Programmer"
        };
        private static final String[] SG_NAMES = {
                "Tan Wei Ming", "Lim Mei Ling", "Muhammad Ibrahim", "Siti Nurhaliza",
                "Zhang Wei", "Kumar Ravi", "Abdullah Malik", "Lee Hui Ling"
        };
        private static final String[] SG_STREETS = {
                "Ang Mo Kio Ave", "Tampines St", "Jurong East Ave", "Serangoon Road",
                "Bedok North St", "Woodlands Drive", "Yishun Ring Road", "Punggol Way"
        };
        private static final String[] SG_BUILDINGS = {
                "Plaza", "Tower", "Complex", "Centre", "Building", "Point"
        };
        private static final String[] SG_COMPANIES = {
                "DBS Bank", "Singapore Airlines", "Singtel", "OCBC Bank",
                "CapitaLand", "Keppel Corporation", "ST Engineering", "ComfortDelGro"
        };
        private static final String[] SG_INDUSTRIAL_AREAS = {
                "Jurong Industrial Estate", "Tuas South", "Woodlands Industrial Park",
                "Changi Business Park", "One-North", "Alexandra Business Park"
        };

        private String generateSGAddress() {
            String block = String.format("Blk %d", random.nextInt(100, 999));
            String street = SG_STREETS[random.nextInt(SG_STREETS.length)];
            String unit = String.format("#%02d-%02d",
                    random.nextInt(1, 50), random.nextInt(1, 999));
            return String.format("%s %s %d, %s, Singapore %d",
                    block, street, random.nextInt(1, 12), unit,
                    random.nextInt(460000, 569999));
        }

        private String generateCompanyAddress() {
            String building = SG_BUILDINGS[random.nextInt(SG_BUILDINGS.length)];
            String area = SG_INDUSTRIAL_AREAS[random.nextInt(SG_INDUSTRIAL_AREAS.length)];
            return String.format("%d %s %s, Singapore %d",
                    random.nextInt(1, 100), area, building,
                    random.nextInt(460000, 569999));
        }

        private Contact generateContact() {
            String personalPhone = String.format("9%07d", random.nextInt(0, 9999999));
            String homePhone = String.format("6%07d", random.nextInt(0, 9999999));
            String companyPhone = String.format("6%07d", random.nextInt(0, 9999999));
            String email = String.format("%s%d@%s",
                    "user", random.nextInt(100, 999),
                    random.nextBoolean() ? "gmail.com" : "hotmail.com");
            return new Contact(personalPhone, homePhone, companyPhone, email);
        }

        public static Patient createRandom(String patientId) {
            Generator generator = new Generator();
            String name = SG_NAMES[random.nextInt(SG_NAMES.length)];
            LocalDate dob = LocalDate.now().minusYears(20 + random.nextInt(60));

            String nricPrefix = dob.getYear() < 2000 ? "S" : "T";
            String nricFin = String.format("%s%07d%c",
                    nricPrefix, random.nextInt(1000000, 9999999),
                    (char)('A' + random.nextInt(26)));

            List<String> allergies = List.of(DRUG_ALLERGIES[random.nextInt(DRUG_ALLERGIES.length)]);

            NokRelation nokRelation = NokRelation.values()[random.nextInt(NokRelation.values().length)];
            String nokName;
            if (nokRelation == NokRelation.SPOUSE || nokRelation == NokRelation.PARENT ||
                    nokRelation == NokRelation.CHILD || nokRelation == NokRelation.SIBLING) {
                String[] nameParts = name.split(" ");
                nokName = SG_NAMES[random.nextInt(SG_NAMES.length)].split(" ")[0] +
                        " " + nameParts[nameParts.length - 1];
            } else {
                nokName = SG_NAMES[random.nextInt(SG_NAMES.length)];
            }

            return new Patient(
                    name,
                    dob,
                    nricFin,
                    MaritalStatus.values()[random.nextInt(MaritalStatus.values().length)],
                    ResidentialStatus.values()[random.nextInt(ResidentialStatus.values().length)],
                    "Singaporean",
                    generator.generateSGAddress(),
                    generator.generateContact(),
                    Sex.values()[random.nextInt(Sex.values().length)],
                    BloodType.values()[random.nextInt(BloodType.values().length)],
                    random.nextBoolean(),
                    patientId,
                    allergies,
                    nokName,
                    generator.generateSGAddress(),
                    nokRelation,
                    150 + random.nextDouble() * 50,
                    50 + random.nextDouble() * 50,
                    OCCUPATIONS[random.nextInt(OCCUPATIONS.length)],
                    SG_COMPANIES[random.nextInt(SG_COMPANIES.length)],
                    generator.generateCompanyAddress()
            );
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
        System.out.format("Drug Allergies: %s%n", drugAllergies);
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


