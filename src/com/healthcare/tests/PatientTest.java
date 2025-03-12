package tests;

import humans.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**
 * A test class for the {@link Patient} class.
 * This class verifies the functionality of patient creation and data access using the builder pattern.
 */
public class PatientTest {
    /**
     * Main method to execute tests for {@link Patient}.
     * It tests creating patients using the builder pattern with both specific and random data.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Patient functionality...\n");

            // Create a patient using builder with specific data
            System.out.println("Test 1 - Creating patient using builder with specific data:");
            Patient patient1 = Patient.builder()
                    .name("John Doe")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .nricFin("S9012345A")
                    .maritalStatus(MaritalStatus.SINGLE)
                    .residentialStatus(ResidentialStatus.CITIZEN)
                    .nationality("Singaporean")
                    .address("123 Test Street")
                    .contact(new Contact("91234567", "61234567", "61234568", "john@test.com"))
                    .sex(Sex.MALE)
                    .bloodType(BloodType.A_POSITIVE)
                    .isVaccinated(true)
                    .patientId("P1001")
                    .drugAllergies(Arrays.asList("Penicillin"))
                    .nokName("Jane Doe")
                    .nokAddress("456 Test Street")
                    .nokRelation(NokRelation.SPOUSE)
                    .height(1.75)
                    .weight(70.0)
                    .occupation("Engineer")
                    .companyName("Test Company")
                    .companyAddress("789 Test Street")
                    .build();
            patient1.displayHuman();

            // Create a patient using builder with random data
            System.out.println("\nTest 2 - Creating patient using builder with random data:");
            Patient patient2 = Patient.builder()
                    .withRandomData("P1002")
                    .build();
            patient2.displayHuman();

            // Generate multiple random patients
            System.out.println("\nTest 3 - Generating multiple random patients:");
            List<Patient> patients = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String patientId = String.format("P%04d", 1003 + i);
                patients.add(Patient.builder()
                        .withRandomData(patientId)
                        .build());
            }

            for (Patient patient : patients) {
                System.out.println("Generated patient ID: " + patient.getPatientId());
            }
            System.out.println("Number of patients generated: " + patients.size());

            //Verify patient data access
            System.out.println("\nTest 4 - Verifying patient data access:");
            patients.forEach(Patient::displayHuman);

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}