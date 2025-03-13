package org.bee.hms.utils;

import org.bee.hms.humans.Contact;
import org.bee.hms.medical.Medication;
import org.bee.hms.policy.AccidentType;

import java.util.List;
import java.util.Random;

/**
 * Utility class for generating common data used across different entities.
 * Implemented as a singleton to ensure only one instance exists.
 */
public class DataGenerator {
    private static DataGenerator instance;
    private final Random random = new Random();

    // Personal Information
    private final String[] SG_NAMES = {
            "Tan Wei Ming", "Lim Mei Ling", "Muhammad Ibrahim", "Siti Nurhaliza",
            "Zhang Wei", "Kumar Ravi", "Abdullah Malik", "Lee Hui Ling"
    };

    private final String[] OCCUPATIONS = {
            "Engineer", "Teacher", "Doctor", "Artist", "Chef", "Programmer"
    };

    // Address-related constants
    private final String[] SG_STREETS = {
            "Ang Mo Kio Ave", "Tampines St", "Jurong East Ave", "Serangoon Road",
            "Bedok North St", "Woodlands Drive", "Yishun Ring Road", "Punggol Way"
    };

    private final String[] SG_BUILDINGS = {
            "Plaza", "Tower", "Complex", "Centre", "Building", "Point"
    };

    // Company-related constants
    private final String[] SG_COMPANIES = {
            "DBS Bank", "Singapore Airlines", "Singtel", "OCBC Bank",
            "CapitaLand", "Keppel Corporation", "ST Engineering", "ComfortDelGro"
    };


    private final String[] HEALTH_INSURANCE_NAMES = {
            "HealthShield Gold Max", "Enhanced IncomeShield", "PRUShield",
            "MyShield", "Great Eastern Supreme Health", "Elite Health Plus"
    };

    private final String[] ACCIDENT_INSURANCE_NAMES = {
            "Personal Accident Elite", "Accident Protect Plus", "PA Secure",
            "Total Protect", "AccidentCare Plus", "Personal Accident Guard"
    };

    private final String[] CRITICAL_ILLNESS_NAMES = {
            "Early Critical Care", "Critical Illness Plus", "Critical Protect",
            "Crisis Cover", "Critical Care Advantage", "MultiPay Critical Illness"
    };

    private final AccidentType[] ACCIDENT_TYPES = AccidentType.values();


    private DataGenerator() {
    }

    /**
     * Gets the singleton instance of DataGenerator
     *
     * @return The DataGenerator instance
     */
    public static DataGenerator getInstance() {
        if (instance == null) {
            instance = new DataGenerator();
        }
        return instance;
    }

    /**
     * Generates a random integer within a specified range.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return A random integer between min and max
     */
    public int generateRandomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Generates a random integer within range [0, max).
     *
     * @param max The upper bound (exclusive)
     * @return A random integer from 0 to max-1
     */
    public int generateRandomInt(int max) {
        return random.nextInt(max);
    }


    public Medication getRandomMedication() {
        List<String> categories = Medication.getAllCategories();
        String randomCategory = categories.get(random.nextInt(categories.size()));

        // Get up to 5 medications from the category and select one randomly
        List<Medication> medications = Medication.getMedicationsByCategory(
                randomCategory, 5, true);

        if (medications.isEmpty()) {
            throw new IllegalStateException("No medications available in the system");
        }

        return medications.get(0); // First one since the list is already randomized
    }


    /**
     * Gets all available Singapore names
     *
     * @return Array of Singapore names
     */
    public String[] getSgNames() {
        return SG_NAMES;
    }

    // ID Generation Methods
    public String generateStaffId() {
        return String.format("S%05d", generateRandomInt(100000));
    }

    public String generateMCRNumber() {
        return String.format("M%05dA", generateRandomInt(100000));
    }

    public String generateRNIDNumber() {
        return String.format("RN%05dB", generateRandomInt(100000));
    }

    public String generatePatientId() {
        int year = java.time.LocalDate.now().getYear();
        int randomDigits = generateRandomInt(10000, 99999);
        return String.format("P-%d%05d", year, randomDigits);
    }


    public String generateNRICNumber() {
        String prefix = generateRandomInt(2) == 0 ? "S" : "T";
        String numbers = String.format("%07d", generateRandomInt(10000000));
        char[] checksum = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        return prefix + numbers + checksum[generateRandomInt(checksum.length)];
    }

    /**
     * Generates a random Singapore address.
     *
     * @return A randomly generated address string
     */
    public String generateSGAddress() {
        String block = String.format("Blk %d", generateRandomInt(100, 999));
        String street = SG_STREETS[generateRandomInt(SG_STREETS.length)];
        String unit = String.format("#%02d-%02d",
                generateRandomInt(1, 50), generateRandomInt(1, 999));
        return String.format("%s %s %d, %s, Singapore %d",
                block, street, generateRandomInt(1, 12), unit,
                generateRandomInt(460000, 569999));
    }

    /**
     * Gets a random occupation from the OCCUPATIONS array.
     *
     * @return A randomly selected occupation
     */
    public String getRandomOccupation() {
        return getRandomElement(OCCUPATIONS);
    }

    /**
     * Gets a random company name from the SG_COMPANIES array.
     *
     * @return A randomly selected company name
     */
    public String getRandomCompanyName() {
        return getRandomElement(SG_COMPANIES);
    }

    /**
     * Gets all insurance policy names (combined from all types)
     *
     * @return Array of all insurance policy names
     */
    private String[] getInsuranceNames() {
        int totalLength = HEALTH_INSURANCE_NAMES.length +
                ACCIDENT_INSURANCE_NAMES.length +
                CRITICAL_ILLNESS_NAMES.length;

        String[] allNames = new String[totalLength];

        System.arraycopy(HEALTH_INSURANCE_NAMES, 0, allNames, 0,
                HEALTH_INSURANCE_NAMES.length);
        System.arraycopy(ACCIDENT_INSURANCE_NAMES, 0, allNames,
                HEALTH_INSURANCE_NAMES.length,
                ACCIDENT_INSURANCE_NAMES.length);
        System.arraycopy(CRITICAL_ILLNESS_NAMES, 0, allNames,
                HEALTH_INSURANCE_NAMES.length + ACCIDENT_INSURANCE_NAMES.length,
                CRITICAL_ILLNESS_NAMES.length);

        return allNames;
    }


    /**
     * Gets a random insurance policy name
     *
     * @return Random insurance policy name
     */
    public String getRandomInsuranceName() {
        String[] allNames = getInsuranceNames();
        return allNames[random.nextInt(allNames.length)];
    }

    public String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }



    /**
     * Generates random contact information.
     *
     * @return A Contact object with randomly generated details
     */
    public Contact generateContact() {
        String personalPhone = String.format("9%07d", generateRandomInt(0, 9999999));
        String homePhone = String.format("6%07d", generateRandomInt(0, 9999999));
        String companyPhone = String.format("6%07d", generateRandomInt(0, 9999999));
        String email = String.format("%s%d@%s",
                "user", generateRandomInt(100, 999),
                generateRandomInt(2) == 0 ? "gmail.com" : "hotmail.com");
        return new Contact(personalPhone, homePhone, companyPhone, email);
    }

    /**
     * Gets a random element from an array.
     *
     * @param array The array to pick from
     * @return A random element from the array
     */
    public <T> T getRandomElement(T[] array) {
        return array[generateRandomInt(array.length)];
    }

    /**
     * Gets a random enum value.
     *
     * @param enumClass The enum class
     * @return A random enum value
     */
    public <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[generateRandomInt(values.length)];
    }
}
