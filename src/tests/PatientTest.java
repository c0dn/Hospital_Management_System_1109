package tests;

import humans.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * A test class for the {@link Patient} class.
 * This class verifies the functionality of patient creation and data access.
 */
public class PatientTest {
    /**
     * Main method to execute tests for {@link Patient}.
     * It tests creating patients manually and through the generator, and accessing patient data.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Patient functionality...\n");

            // Test 1: Create a patient manually
            System.out.println("Test 1 - Creating patient manually:");
            Patient patient1 = new Patient(
                    "John Doe",
                    LocalDate.of(1990, 1, 1),
                    "S9012345A",
                    MaritalStatus.SINGLE,
                    ResidentialStatus.CITIZEN,
                    "Singaporean",
                    "123 Test Street",
                    new Contact("91234567", "61234567", "61234568", "john@test.com"),
                    Sex.MALE,
                    BloodType.A_POSITIVE,
                    true,
                    "P1001",
                    Arrays.asList("Penicillin"),
                    "Jane Doe",
                    "456 Test Street",
                    NokRelation.SPOUSE,
                    1.75,
                    70.0,
                    "Engineer",
                    "Test Company",
                    "789 Test Street"
            );
            System.out.println(patient1);

            // Test 2: Generate a random patient
            System.out.println("\nTest 2 - Generating random patient:");
            Patient patient2 = Patient.Generator.createRandom("P1002");
            System.out.println(patient2);

            // Test 3: Generate multiple random patients
            System.out.println("\nTest 3 - Generating multiple random patients:");
            List<Patient> patients = Patient.Generator.createRandom(3);
            for (Patient patient : patients) {
                System.out.println("Generated patient ID: " + patient.getPatientId());
            }
            System.out.println("Number of patients generated: " + patients.size());

            // Test 4: Verify patient data access
            System.out.println("\nTest 4 - Verifying patient data access:");
            patients.forEach(Patient::displayPatientInfo);


        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}