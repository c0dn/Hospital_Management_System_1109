package tests;

import humans.*;
import humans.NurseBuilder;
import java.time.LocalDate;

/**
 * A test class for the {@link Nurse} class.
 * This class verifies the functionality of nurse creation and data access.
 */
public class NurseTest {


    public static void main(String[] args) {
        try {
            System.out.println("Testing Nurse functionality...\n");

            // Test 1: Create a nurse using builder with specific data
            System.out.println("Test 1 - Creating nurse using builder with specific data:");
            Nurse nurse1 = Nurse.builder()
                    .name("Sarah Johnson")
                    .dateOfBirth(LocalDate.of(1992, 8, 25))
                    .nricFin("S9212345C")
                    .maritalStatus(MaritalStatus.SINGLE)
                    .residentialStatus(ResidentialStatus.CITIZEN)
                    .nationality("Singaporean")
                    .address("789 Nursing Avenue")
                    .contact(new Contact("92345678", "64567890", "64567891", "sarah.j@hospital.com"))
                    .sex(Sex.FEMALE)
                    .bloodType(BloodType.A_NEGATIVE)
                    .isVaccinated(true)
                    .staffId("N1001")
                    .title("Senior Nurse")
                    .department("Emergency")
                    .rnid("RN12345B")
                    .build();
            nurse1.displayHuman();

            // Test 2: Create a nurse using builder with random data
            System.out.println("\nTest 2 - Creating nurse using builder with random data:");
            Nurse nurse2 = Nurse.builder()
                    .withRandomBaseData()
                    .build();
            nurse2.displayHuman();

            // Test 3: Create a nurse using builder with specific data
            System.out.println("\nTest 3 - Creating nurse using builder with specific data:");
            Nurse nurse3 = Nurse.builder()
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
            nurse3.displayHuman();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}