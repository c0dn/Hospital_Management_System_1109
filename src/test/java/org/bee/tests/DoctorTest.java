package org.bee.tests;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.bee.hms.humans.BloodType;
import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.MaritalStatus;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.humans.Sex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link Doctor} class.
 * This class verifies the functionality of doctor creation and data access.
 */
public class DoctorTest {
    private Doctor doctor1;
    private Doctor doctor2;
    private Doctor doctor3;

    @BeforeEach
    void setUp() {
        // Create doctors with specific data
        doctor1 = Doctor.builder()
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

        // Create a doctor with random data
        doctor2 = Doctor.builder()
                .withRandomBaseData()
                .build();

        // Create another doctor with specific data
        doctor3 = Doctor.builder()
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
    }

    @Test
    void testSpecificDoctorCreation() throws Exception {
        assertNotNull(doctor1, "Doctor should not be null");
        assertEquals("Dr. Jane Smith", doctor1.getName(), "Name should match");
        assertEquals(LocalDate.of(1980, 6, 15), doctor1.getDOB(), "Date of birth should match");
        assertEquals("S8012345B", doctor1.getNricFin(), "NRIC should match");
        // Access private fields for marital status, residential status using reflection
        assertEquals(MaritalStatus.MARRIED, getPrivateField(doctor1, "maritalStatus", MaritalStatus.class), 
                "Marital status should match");
        assertEquals(ResidentialStatus.CITIZEN, getPrivateField(doctor1, "residentialStatus", ResidentialStatus.class), 
                "Residential status should match");
        assertEquals("Singaporean", getPrivateField(doctor1, "nationality", String.class), 
                "Nationality should match");
        assertEquals("123 Medical Drive", doctor1.getAddress(), "Address should match");
        assertEquals(Sex.FEMALE, doctor1.getSex(), "Sex should match");
        assertEquals(BloodType.O_POSITIVE, getPrivateField(doctor1, "bloodType", BloodType.class), 
                "Blood type should match");
        assertTrue((boolean)getPrivateField(doctor1, "isVaccinated", Boolean.class), "Should be vaccinated");
        assertEquals("D1001", getPrivateField(doctor1, "staffId", String.class), "Staff ID should match");
        assertEquals("Senior Consultant", getPrivateField(doctor1, "title", String.class), "Title should match");
        assertEquals("Cardiology", getPrivateField(doctor1, "department", String.class), "Department should match");
        assertEquals("M12345A", doctor1.getMcr(), "MCR should match");
    }

    @Test
    void testRandomDoctorCreation() throws Exception {
        assertNotNull(doctor2, "Random doctor should not be null");
        assertNotNull(doctor2.getName(), "Name should not be null");
        assertNotNull(doctor2.getDOB(), "Date of birth should not be null");
        assertNotNull(doctor2.getNricFin(), "NRIC should not be null");
        assertNotNull(getPrivateField(doctor2, "maritalStatus", MaritalStatus.class), 
                "Marital status should not be null");
        assertNotNull(getPrivateField(doctor2, "residentialStatus", ResidentialStatus.class), 
                "Residential status should not be null");
        assertNotNull(getPrivateField(doctor2, "nationality", String.class), 
                "Nationality should not be null");
        assertNotNull(doctor2.getAddress(), "Address should not be null");
        assertNotNull(doctor2.getSex(), "Sex should not be null");
        assertNotNull(getPrivateField(doctor2, "bloodType", BloodType.class), 
                "Blood type should not be null");
        assertNotNull(getPrivateField(doctor2, "staffId", String.class), 
                "Staff ID should not be null");
        assertNotNull(getPrivateField(doctor2, "title", String.class), 
                "Title should not be null");
        assertNotNull(getPrivateField(doctor2, "department", String.class), 
                "Department should not be null");
        assertNotNull(doctor2.getMcr(), "MCR should not be null");
    }

    @Test
    void testContactInformation() throws Exception {
        Contact contact = getPrivateField(doctor1, "contact", Contact.class);
        assertNotNull(contact, "Contact should not be null");
        assertEquals("91234567", getPrivateField(contact, "personalPhone", String.class), 
                "Personal phone should match");
        assertEquals("62345678", getPrivateField(contact, "homePhone", String.class), 
                "Home phone should match");
        assertEquals("62345679", getPrivateField(contact, "companyPhone", String.class), 
                "Company phone should match");
        assertEquals("jane.smith@hospital.com", getPrivateField(contact, "email", String.class), 
                "Email should match");
    }

    @Test
    void testDoctorSpecificFields() throws Exception {
        // Test doctor-specific fields using reflection to verify private fields
        assertEquals("Senior Consultant", getPrivateField(doctor1, "title", String.class),
                "Title field should match");
        assertEquals("Cardiology", getPrivateField(doctor1, "department", String.class),
                "Department field should match");
        assertEquals("M12345A", getPrivateField(doctor1, "mcr", String.class),
                "MCR field should match");
    }

    /**
     * Utility method to access private fields using reflection.
     * 
     * @param <T> The type of the field
     * @param object The object containing the field
     * @param fieldName The name of the field to access
     * @param fieldType The class of the field type
     * @return The value of the field
     * @throws Exception If reflection fails
     */
    private <T> T getPrivateField(Object object, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> currentClass = object.getClass();
        Field field = null;
        
        // Search through the class hierarchy
        while (currentClass != null) {
            try {
                field = currentClass.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        
        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        }
        
        field.setAccessible(true);
        return fieldType.cast(field.get(object));
    }
}
