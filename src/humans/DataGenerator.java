package humans;

import java.util.Random;

/**
 * Utility class for generating common data used across different entities.
 * This includes random names, addresses, company names, and contact details.
 */
public class DataGenerator {
    /** Random instance for generating random values. */
    private static final Random random = new Random();

    // Personal Information
    /** Names used for random generation. */
    public static final String[] SG_NAMES = {
            "Tan Wei Ming", "Lim Mei Ling", "Muhammad Ibrahim", "Siti Nurhaliza",
            "Zhang Wei", "Kumar Ravi", "Abdullah Malik", "Lee Hui Ling"
    };
    /** Occupations used for random selection. */
    public static final String[] OCCUPATIONS = {
            "Engineer", "Teacher", "Doctor", "Artist", "Chef", "Programmer"
    };

    // Address-related constants
    /** Street names used for random address generation. */
    public static final String[] SG_STREETS = {
            "Ang Mo Kio Ave", "Tampines St", "Jurong East Ave", "Serangoon Road",
            "Bedok North St", "Woodlands Drive", "Yishun Ring Road", "Punggol Way"
    };
    /** Building types for address generation. */
    public static final String[] SG_BUILDINGS = {
            "Plaza", "Tower", "Complex", "Centre", "Building", "Point"
    };

    // Company-related constants
    /** List of companies for random selection. */
    public static final String[] SG_COMPANIES = {
            "DBS Bank", "Singapore Airlines", "Singtel", "OCBC Bank",
            "CapitaLand", "Keppel Corporation", "ST Engineering", "ComfortDelGro"
    };
    /** Industrial areas in Singapore. */
    public static final String[] SG_INDUSTRIAL_AREAS = {
            "Jurong Industrial Estate", "Tuas South", "Woodlands Industrial Park",
            "Changi Business Park", "One-North", "Alexandra Business Park"
    };

    /**
     * Generates a random Singapore address.
     * The format of the address is: "BLK {block_number} {street_name} {floor}, {unit_number}, Singapore {postal_code}"
     *
     * @return A randomly generated address string.
     */
    public static String generateSGAddress() {
        String block = String.format("Blk %d", random.nextInt(100, 999));
        String street = SG_STREETS[random.nextInt(SG_STREETS.length)];
        String unit = String.format("#%02d-%02d",
                random.nextInt(1, 50), random.nextInt(1, 999));
        return String.format("%s %s %d, %s, Singapore %d",
                block, street, random.nextInt(1, 12), unit,
                random.nextInt(460000, 569999));
    }

    /**
     * Generates a random company address.
     * The format of the address is: "{building_number} {area} {building}, Singapore {postal_code}".
     *
     * @return A randomly generated company address string.
     */
    public static String generateCompanyAddress() {
        String building = SG_BUILDINGS[random.nextInt(SG_BUILDINGS.length)];
        String area = SG_INDUSTRIAL_AREAS[random.nextInt(SG_INDUSTRIAL_AREAS.length)];
        return String.format("%d %s %s, Singapore %d",
                random.nextInt(1, 100), area, building,
                random.nextInt(460000, 569999));
    }


    /**
     * Gets a random occupation from the OCCUPATIONS array.
     *
     * @return A randomly selected occupation
     */
    public static String getRandomOccupation() {
        return getRandomElement(OCCUPATIONS);
    }

    /**
     * Gets a random company name from the SG_COMPANIES array.
     *
     * @return A randomly selected company name
     */
    public static String getRandomCompanyName() {
        return getRandomElement(SG_COMPANIES);
    }


    /**
     * Generates random contact information.
     *
     * @return A Contact object with randomly generated details.
     */
    public static Contact generateContact() {
        String personalPhone = String.format("9%07d", random.nextInt(0, 9999999));
        String homePhone = String.format("6%07d", random.nextInt(0, 9999999));
        String companyPhone = String.format("6%07d", random.nextInt(0, 9999999));
        String email = String.format("%s%d@%s",
                "user", random.nextInt(100, 999),
                random.nextBoolean() ? "gmail.com" : "hotmail.com");
        return new Contact(personalPhone, homePhone, companyPhone, email);
    }

    /**
     * Gets a random element from an array.
     *
     * @param array The array to pick from.
     * @return A random element from the array.
     */
    public static <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }

    /**
     * Gets a random enum value.
     *
     * @param enumClass The enum class.
     * @return A random enum value.
     */
    public static <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[random.nextInt(values.length)];
    }
}