package utils;

import humans.Contact;
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

    private final String[] SG_INDUSTRIAL_AREAS = {
            "Jurong Industrial Estate", "Tuas South", "Woodlands Industrial Park",
            "Changi Business Park", "One-North", "Alexandra Business Park"
    };

    // Insurance-related constants
    private final String[] INSURANCE_COMPANIES = {
            "AIA", "Great Eastern", "NTUC Income", "Prudential", "Aviva",
            "Singlife", "HSBC Insurance", "Manulife"
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

    private DataGenerator() {}

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

    public String generateNRICNumber() {
        String prefix = generateRandomInt(2) == 0 ? "S" : "T";
        String numbers = String.format("%07d", generateRandomInt(10000000));
        char[] checksum = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        return prefix + numbers + checksum[generateRandomInt(checksum.length)];
    }

    public String generateHealthPolicyId() {
        return String.format("H%08d", generateRandomInt(100000000));
    }

    public String generateAccidentPolicyId() {
        return String.format("A%08d", generateRandomInt(100000000));
    }

    public String generateCriticalIllnessPolicyId() {
        return String.format("C%08d", generateRandomInt(100000000));
    }

    // Insurance-related Generators
    public double generateDeductible() {
        // Most policies have deductibles between 1,500-3,750
        return 1500 + random.nextDouble() * 2250;
    }

    public double generateHospitalCharges() {
        // Based on actual bill sizes from Income Shield data
        // Basic treatments: 5k-15k
        // Major treatments: 15k-50k
        // Critical conditions: 50k-200k
        double[] tiers = {15000, 50000, 200000};
        int tier = generateRandomInt(3);
        return 5000 + random.nextDouble() * tiers[tier];
    }

    public double generatePremium() {
        // Basic plans: 500-1000
        // Enhanced plans: 1000-2500
        // Premium plans: 2500-5000
        return 500 + random.nextDouble() * 4500;
    }

    public double generateCoInsuranceRate() {
        // Typical rates are 5%, 10%, or 20%
        double[] rates = {0.05, 0.10, 0.20};
        return rates[generateRandomInt(rates.length)];
    }

    public double generateAccidentAllowance() {
        // Daily allowances typically range from 50-300
        return 50 + random.nextDouble() * 250;
    }

    public String generateHealthInsuranceDescription() {
        String[] templates = {
            "Comprehensive medical coverage including pre and post hospitalization care with coverage up to %d per year",
            "Protection against medical bills arising from hospitalization with coverage up to %d per policy year",
            "All-rounded health protection plan covering hospital stays and surgical expenses up to %d annually",
            "Premium healthcare coverage with as-charged benefits for hospitalization up to %d yearly"
        };
        int coverage = 100000 * (1 + generateRandomInt(15)); // 100k to 1.5M coverage
        return String.format(templates[generateRandomInt(templates.length)], coverage);
    }

    public String generateAccidentInsuranceDescription() {
        String[] templates = {
            "24/7 worldwide personal accident coverage up to %d with daily hospital income benefit",
            "Comprehensive accident protection with coverage up to %d and medical expense reimbursement",
            "Total accident coverage up to %d with optional riders for enhanced protection"
        };
        int coverage = 10000 * (1 + generateRandomInt(20)); // 10k to 200k coverage
        return String.format(templates[generateRandomInt(templates.length)], coverage);
    }

    public String generateCriticalIllnessDescription() {
        String[] templates = {
            "Protection against critical illnesses with coverage up to %d for early to advanced stages",
            "Comprehensive critical illness coverage up to %d with multiple claim feature",
            "Financial protection against critical illnesses with sum assured up to %d"
        };
        int coverage = 50000 * (1 + generateRandomInt(30)); // 50k to 1.5M coverage
        return String.format(templates[generateRandomInt(templates.length)], coverage);
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
     * Generates a random company address.
     *
     * @return A randomly generated company address string
     */
    public String generateCompanyAddress() {
        String building = SG_BUILDINGS[generateRandomInt(SG_BUILDINGS.length)];
        String area = SG_INDUSTRIAL_AREAS[generateRandomInt(SG_INDUSTRIAL_AREAS.length)];
        return String.format("%d %s %s, Singapore %d",
                generateRandomInt(1, 100), area, building,
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
     * Gets a random insurance company name.
     *
     * @return A randomly selected insurance company name
     */
    public String getRandomInsuranceCompany() {
        return getRandomElement(INSURANCE_COMPANIES);
    }

    /**
     * Gets a random health insurance product name.
     *
     * @return A randomly selected health insurance name
     */
    public String getRandomHealthInsuranceName() {
        return getRandomElement(HEALTH_INSURANCE_NAMES);
    }

    /**
     * Gets a random accident insurance product name.
     *
     * @return A randomly selected accident insurance name
     */
    public String getRandomAccidentInsuranceName() {
        return getRandomElement(ACCIDENT_INSURANCE_NAMES);
    }

    /**
     * Gets a random critical illness insurance product name.
     *
     * @return A randomly selected critical illness insurance name
     */
    public String getRandomCriticalIllnessName() {
        return getRandomElement(CRITICAL_ILLNESS_NAMES);
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
