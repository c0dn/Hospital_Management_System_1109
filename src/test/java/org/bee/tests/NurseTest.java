package org.bee.tests;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.bee.hms.humans.BloodType;
import org.bee.hms.humans.Contact;
import org.bee.hms.humans.MaritalStatus;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.humans.Sex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link Nurse} class.
 * This class verifies the functionality of nurse creation and data access.
 */
public class NurseTest {
    private Nurse nurse1;
    private Nurse nurse2;
    private Nurse nurse3;

    @BeforeEach
    void setUp() {
        // Create a nurse using builder with specific data
        nurse1 = Nurse.builder()
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

        // Create a nurse using builder with random data
        nurse2 = Nurse.builder()
                .withRandomBaseData()
                .build();

        // Create another nurse using builder with specific data
        nurse3 = Nurse.builder()
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
    }

    @Test
    void testSpecificNurseCreation() throws Exception {
        assertNotNull(nurse1, "Nurse should not be null");
        assertEquals("Sarah Johnson", nurse1.getName(), "Name should match");
        assertEquals(LocalDate.of(1992, 8, 25), nurse1.getDOB(), "Date of birth should match");
        assertEquals("S9212345C", nurse1.getNricFin(), "NRIC should match");
        
        // Access private fields using reflection
        assertEquals(MaritalStatus.SINGLE, getPrivateField(nurse1, "maritalStatus", MaritalStatus.class),
                "Marital status should match");
        assertEquals(ResidentialStatus.CITIZEN, getPrivateField(nurse1, "residentialStatus", ResidentialStatus.class),
                "Residential status should match");
        assertEquals("Singaporean", getPrivateField(nurse1, "nationality", String.class),
                "Nationality should match");
        assertEquals("789 Nursing Avenue", nurse1.getAddress(), "Address should match");
        assertEquals(Sex.FEMALE, nurse1.getSex(), "Sex should match");
        assertEquals(BloodType.A_NEGATIVE, getPrivateField(nurse1, "bloodType", BloodType.class),
                "Blood type should match");
        assertTrue((boolean)getPrivateField(nurse1, "isVaccinated", Boolean.class), "Should be vaccinated");
        
        // Test nurse-specific fields
        assertEquals("N1001", getPrivateField(nurse1, "staffId", String.class), "Staff ID should match");
        assertEquals("Senior Nurse", getPrivateField(nurse1, "title", String.class), "Title should match");
        assertEquals("Emergency", getPrivateField(nurse1, "department", String.class), "Department should match");
        assertEquals("RN12345B", getPrivateField(nurse1, "rnid", String.class), "RNID should match");
    }

    @Test
    void testRandomNurseCreation() throws Exception {
        assertNotNull(nurse2, "Random nurse should not be null");
        assertNotNull(nurse2.getName(), "Name should not be null");
        assertNotNull(nurse2.getDOB(), "Date of birth should not be null");
        assertNotNull(nurse2.getNricFin(), "NRIC should not be null");
        assertNotNull(getPrivateField(nurse2, "maritalStatus", MaritalStatus.class),
                "Marital status should not be null");
        assertNotNull(getPrivateField(nurse2, "residentialStatus", ResidentialStatus.class),
                "Residential status should not be null");
        assertNotNull(getPrivateField(nurse2, "nationality", String.class),
                "Nationality should not be null");
        assertNotNull(nurse2.getAddress(), "Address should not be null");
        assertNotNull(nurse2.getSex(), "Sex should not be null");
        assertNotNull(getPrivateField(nurse2, "bloodType", BloodType.class),
                "Blood type should not be null");
    }

    @Test
    void testContactInformation() throws Exception {
        Contact contact = getPrivateField(nurse1, "contact", Contact.class);
        assertNotNull(contact, "Contact should not be null");
        
        assertEquals("92345678", getPrivateField(contact, "personalPhone", String.class),
                "Personal phone should match");
        assertEquals("64567890", getPrivateField(contact, "homePhone", String.class),
                "Home phone should match");
        assertEquals("64567891", getPrivateField(contact, "companyPhone", String.class),
                "Company phone should match");
        assertEquals("sarah.j@hospital.com", getPrivateField(contact, "email", String.class),
                "Email should match");
    }

    @Test
    void testNurseSpecificFields() throws Exception {
        assertEquals("RN12345B", getPrivateField(nurse1, "rnid", String.class),
                "RNID field should match");
        assertEquals("Emergency", getPrivateField(nurse1, "department", String.class),
                "Department field should match");
        assertEquals("Senior Nurse", getPrivateField(nurse1, "title", String.class),
                "Title field should match");
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
