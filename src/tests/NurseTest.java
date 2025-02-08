package tests;

import humans.*;
import humans.builder.NurseBuilder;
import java.time.LocalDate;

/**
 * A test class for the {@link Nurse} class.
 * This class verifies the functionality of nurse creation and data access.
 */
public class NurseTest {
    /**
     * Main method to execute tests for {@link Nurse}.
     * It tests creating nurses using both constructor and builder pattern.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Nurse functionality...\n");

            // Test 1: Create a nurse manually
            System.out.println("Test 1 - Creating nurse manually:");
            Nurse nurse1 = new Nurse(
                    "Sarah Johnson",
                    LocalDate.of(1992, 8, 25),
                    "S9212345C",
                    MaritalStatus.SINGLE,
                    ResidentialStatus.CITIZEN,
                    "Singaporean",
                    "789 Nursing Avenue",
                    new Contact("92345678", "64567890", "64567891", "sarah.j@hospital.com"),
                    Sex.FEMALE,
                    BloodType.A_NEGATIVE,
                    true,
                    "N1001",
                    "Senior Nurse",
                    "Emergency",
                    "RN12345B"
            );
            nurse1.displayStaff();

            // Test 2: Create a nurse using builder with random data
            System.out.println("\nTest 2 - Creating nurse using builder with random data:");
            Nurse nurse2 = new NurseBuilder()
                    .withRandomBaseData()
                    .build();
            nurse2.displayStaff();

            // Test 3: Create a nurse using builder with specific data
            System.out.println("\nTest 3 - Creating nurse using builder with specific data:");
            Nurse nurse3 = new NurseBuilder()
                    .name("Michael Chen")
                    .dateOfBirth(LocalDate.of(1988, 11, 15))
                    .nricFin("S8811345D")
                    .maritalStatus(MaritalStatus.MARRIED)
                    .residentialStatus(ResidentialStatus.CITIZEN)
                    .nationality("Singaporean")
                    .address("321 Care Street")
                    .contact(new Contact("93456789", "65678901", "65678902", "michael.c@hospital.com"))
                    .sex(Sex.MALE)
                    .bloodType(BloodType.O_NEGATIVE)
                    .isVaccinated(true)
                    .staffId("N1002")
                    .title("Nurse Clinician")
                    .department("Pediatrics")
                    .rnid("RN54321C")
                    .build();
            nurse3.displayStaff();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}