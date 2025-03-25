package org.bee.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.bee.hms.humans.*;
import org.bee.hms.medical.*;
import org.bee.hms.policy.AccidentType;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;

/**
 * Utility class for generating common data used across different entities.
 * Implemented as a singleton to ensure only one instance exists.
 */
public class DataGenerator {
    private static DataGenerator instance;
    private final Random random = new Random();

    // Personal Information
    private final String[] STAFF_NAMES = {
            "Tan Wei Ming", "Lim Mei Ling", "Muhammad Ibrahim", "Siti Nurhaliza",
            "Zhang Wei", "Kumar Ravi", "Abdullah Malik", "Lee Hui Ling"
    };

    private final String[] PATIENT_NAMES = {
            "Lim Boon Teck", "Wee Jun Kiat", "Muhammad Danial", "Aisha Fatimah",
            "Ben Tan", "Rohan Aand", "Divya Singh", "Ong Li Ting"
    };

    private final String[] SG_NAMES = {
            "Tan Wei Ming", "Lim Mei Ling", "Muhammad Ibrahim", "Siti Nurhaliza",
            "Zhang Wei", "Kumar Ravi", "Abdullah Malik", "Lee Hui Ling",
            "Lim Boon Teck", "Wee Jun Kiat", "Muhammad Danial", "Aisha Fatimah",
            "Ben Tan", "Rohan Aand", "Divya Singh", "Ong Li Ting"
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
    public String[] getStaffNames() {
        return STAFF_NAMES;
    }

    public String[] getPatientNames() {
        return PATIENT_NAMES;
    }

    public String[] getAllNames(NameType nameType) {
        return switch (nameType) {
            case STAFF -> STAFF_NAMES;
            case PATIENT -> PATIENT_NAMES;
            case ALL -> SG_NAMES;
        };
    }

    public String generateStaffId() {
        String uuid = UUID.randomUUID().toString();
        return "S" + uuid.substring(0, 8).toUpperCase();
    }

    public String generateMCRNumber() {
        return String.format("M%05dA", generateRandomInt(100000));
    }

    public String generateRNIDNumber() {
        return String.format("RN%05dB", generateRandomInt(100000));
    }

    public String generatePatientId() {
        int year = java.time.LocalDate.now().getYear();
        String uuid = UUID.randomUUID().toString();

        return String.format("P-%d%s", year, uuid.substring(0, 8).toUpperCase());
    }


    public String generateNRICNumber() {
        String[] prefixes = {"S", "T", "F", "G"};
        String prefix = prefixes[generateRandomInt(prefixes.length)];
        String numbers = String.format("%07d", generateRandomInt(10000000));
        return prefix + numbers + calculateChecksum(prefix, numbers);
    }

    private char calculateChecksum(String prefix, String numbers) {
        int[] weights = {2, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(numbers.charAt(i)) * weights[i];
        }

        if (prefix.equals("G") || prefix.equals("T")) {
            sum += 4;
        } else if (prefix.equals("M")) {
            sum += 3;
        }

        int remainder = sum % 11;

        int checkDigit = 11 - (remainder + 1);
        if (checkDigit == 11) checkDigit = 0;

        switch (prefix) {
            case "S", "T" -> {
                char[] checksumMap = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'Z', 'J'};
                return checksumMap[checkDigit];
            }
            case "F", "G" -> {
                char[] checksumMap = {'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'T', 'U', 'W', 'X'};
                return checksumMap[checkDigit];
            }
            case "M" -> {
                char[] checksumMap = {'K', 'L', 'J', 'N', 'P', 'Q', 'R', 'T', 'U', 'W', 'X'};
                return checksumMap[checkDigit];
            }
        }

        return '?';
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
     * @return A random element from the array, or null if the array is empty
     * @throws NullPointerException if the array is null
     */
    public <T> T getRandomElement(T[] array) {
        Objects.requireNonNull(array, "Array cannot be null");
        if (array.length == 0) {
            return null;
        }
        return array[generateRandomInt(array.length)];
    }


    /**
     * Gets a random element from a list.
     *
     * @param list The list to pick from
     * @return A random element from the list, or null if the list is empty
     * @throws NullPointerException if the list is null
     */
    public <T> T getRandomElement(List<T> list) {
        Objects.requireNonNull(list, "List cannot be null");
        if (list.isEmpty()) {
            return null;
        }
        return list.get(generateRandomInt(list.size()));
    }

    /**
     * Gets a random element from a set.
     *
     * @param set The set to pick from
     * @return A random element from the set, or null if the set is empty
     * @throws NullPointerException if the set is null
     */
    public <T> T getRandomElement(Set<T> set) {
        Objects.requireNonNull(set, "Set cannot be null");
        if (set.isEmpty()) {
            return null;
        }

        List<T> list = new ArrayList<>(set);
        return list.get(generateRandomInt(list.size()));
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

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generates a random appointment with a patient, reason, time, and status.
     * 
     * @return A randomly generated Appointment object
     */
    public Appointment generateRandomAppointment() {
        Patient patient = Patient.builder()
                .patientId(generatePatientId())
                .withRandomBaseData()
                .build();
        
        return generateRandomAppointment(patient, null);
    }
    
    /**
     * Generates a random appointment with a specific patient, reason, time, and status.
     * 
     * @param patient The patient for the appointment
     * @param doctor The doctor for the appointment (can be null)
     * @return A randomly generated Appointment object
     */
    public Appointment generateRandomAppointment(Patient patient, Doctor doctor) {
        String[] reasons = {
            "Regular check-up", 
            "Flu symptoms", 
            "Headache", 
            "Skin rash", 
            "Fever", 
            "Stomach pain",
            "Follow-up consultation", 
            "Medication review", 
            "Chronic condition management",
            "Mental health consultation"
        };
        
        String reason = reasons[random.nextInt(reasons.length)];
        
        // Generate a random appointment time between now and 30 days in the future
        LocalDateTime now = LocalDateTime.now();
        int daysToAdd = random.nextInt(30) + 1;
        int hoursToAdd = random.nextInt(8) + 9; // 9 AM to 5 PM
        LocalDateTime appointmentTime = now.plusDays(daysToAdd).withHour(hoursToAdd).withMinute(0).withSecond(0);
        
        // Randomly select an appointment status
        AppointmentStatus[] statuses = AppointmentStatus.values();
        AppointmentStatus status = statuses[random.nextInt(statuses.length)];
        
        Appointment appointment = new Appointment(patient, reason, appointmentTime, status);
        
        // If doctor is provided, assign it to the appointment
        if (doctor != null) {
            appointment.setDoctor(doctor);
            
            // If the appointment has a doctor and is ACCEPTED, create a session
            if (status == AppointmentStatus.ACCEPTED) {
                String zoomLink = "https://zoom.us/j/" + (10000000 + random.nextInt(90000000));
                appointment.approveAppointment(doctor, zoomLink);
            }
            
            // If the appointment is COMPLETED, add doctor notes
            if (status == AppointmentStatus.COMPLETED) {
                String[] notes = {
                    "Patient is recovering well.",
                    "Prescribed medication for symptoms.",
                    "Recommended follow-up in 2 weeks.",
                    "Referred to specialist for further evaluation.",
                    "No significant concerns at this time."
                };
                appointment.setDoctorNotes(notes[random.nextInt(notes.length)]);
            }
        } 
        // If no doctor is provided, randomly decide if a doctor should be assigned (50% chance)
        else if (random.nextBoolean()) {
            Doctor randomDoctor = Doctor.builder().withRandomBaseData().build();
            appointment.setDoctor(randomDoctor);
            
            // If the appointment has a doctor and is ACCEPTED, create a session
            if (status == AppointmentStatus.ACCEPTED) {
                String zoomLink = "https://zoom.us/j/" + (10000000 + random.nextInt(90000000));
                appointment.approveAppointment(randomDoctor, zoomLink);
            }
            
            // If the appointment is COMPLETED, add doctor notes
            if (status == AppointmentStatus.COMPLETED) {
                String[] notes = {
                    "Patient is recovering well.",
                    "Prescribed medication for symptoms.",
                    "Recommended follow-up in 2 weeks.",
                    "Referred to specialist for further evaluation.",
                    "No significant concerns at this time."
                };
                appointment.setDoctorNotes(notes[random.nextInt(notes.length)]);
            }
        }
        
        return appointment;
    }

}
