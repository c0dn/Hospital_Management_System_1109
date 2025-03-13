package org.bee.hms.tests;

import java.time.LocalDate;

import org.bee.hms.humans.BloodType;
import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.MaritalStatus;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.humans.Sex;

/**
 * A test class for the {@link Doctor} class.
 * This class verifies the functionality of doctor creation and data access.
 */
public class DoctorTest {
    /**
     * Main method to execute tests for {@link Doctor}.
     * It tests creating doctors using both constructor and builder pattern.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Doctor functionality...\n");

            // Test 1: Create a doctor using builder with specific data
            System.out.println("Test 1 - Creating doctor using builder with specific data:");
            Doctor doctor1 = Doctor.builder()
                    .name("Dr. Jane Smith")
                    .dateOfBirth(LocalDate.of(1980, 6, 15))
                    .nricFin("S8012345B")
                    .maritalStatus(MaritalStatus.MARRIED)
                    .residentialStatus(ResidentialStatus.CITIZEN)
                    .nationality("Singaporean")
                    .address("123 Medical Drive")
                    .contact(new Contact("91234567", "62345678", "62345679", "jane.smith@hospital.com"))
                    .sex(Sex.FEMALE)
                    .bloodType(BloodType.O_POSITIVE)
                    .isVaccinated(true)
                    .staffId("D1001")
                    .title("Senior Consultant")
                    .department("Cardiology")
                    .mcr("M12345A")
                    .build();
            doctor1.displayHuman();

            // Test 2: Create a doctor using builder with random data
            System.out.println("\nTest 2 - Creating doctor using builder with random data:");
            Doctor doctor2 = Doctor.builder()
                    .withRandomBaseData()
                    .build();
            doctor2.displayHuman();

            // Test 3: Create another doctor using builder with specific data
            System.out.println("\nTest 3 - Creating doctor using builder with specific data:");
            Doctor doctor3 = Doctor.builder()
                    .name("Dr. John Doe")
                    .dateOfBirth(LocalDate.of(1975, 3, 20))
                    .nricFin("S7512345C")
                    .maritalStatus(MaritalStatus.SINGLE)
                    .residentialStatus(ResidentialStatus.CITIZEN)
                    .nationality("Singaporean")
                    .address("456 Hospital Road")
                    .contact(new Contact("98765432", "63456789", "63456780", "john.doe@hospital.com"))
                    .sex(Sex.MALE)
                    .bloodType(BloodType.B_NEGATIVE)
                    .isVaccinated(true)
                    .staffId("D1002")
                    .title("Consultant")
                    .department("Neurology")
                    .mcr("M54321B")
                    .build();
            doctor3.displayHuman();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
