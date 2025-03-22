package org.bee.tests;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bee.hms.humans.BloodType;
import org.bee.hms.humans.Contact;
import org.bee.hms.humans.MaritalStatus;
import org.bee.hms.humans.NokRelation;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.humans.Sex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link Patient} class.
 * This class verifies the functionality of patient creation and data access using the builder pattern.
 */
public class PatientTest {
    private Patient patient1;
    private Patient patient2;
    private List<Patient> patients;

    @BeforeEach
    void setUp() {
        // Create a patient using builder with specific data
        patient1 = Patient.builder()
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
                .patientConsent(true)
                .build();

        // Create a patient using builder with random data
        patient2 = Patient.builder()
                .withRandomData("P1002")
                .build();

        // Generate multiple random patients
        patients = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String patientId = String.format("P%04d", 1003 + i);
            patients.add(Patient.builder()
                    .withRandomData(patientId)
                    .build());
        }
    }

    @Test
    void testSpecificPatientCreation() throws Exception {
        assertNotNull(patient1, "Patient should not be null");
        assertEquals("John Doe", patient1.getName(), "Name should match");
        assertEquals(LocalDate.of(1990, 1, 1), patient1.getDOB(), "Date of birth should match");
        assertEquals("S9012345A", patient1.getNricFin(), "NRIC should match");
        assertEquals(Sex.MALE, patient1.getSex(), "Sex should match");
        assertEquals("123 Test Street", patient1.getAddress(), "Address should match");
        
        // Access private fields using reflection
        assertEquals(MaritalStatus.SINGLE, getPrivateField(patient1, "maritalStatus", MaritalStatus.class),
                "Marital status should match");
        assertEquals(ResidentialStatus.CITIZEN, getPrivateField(patient1, "residentialStatus", ResidentialStatus.class),
                "Residential status should match");
        assertEquals("Singaporean", getPrivateField(patient1, "nationality", String.class),
                "Nationality should match");
        assertEquals(BloodType.A_POSITIVE, getPrivateField(patient1, "bloodType", BloodType.class),
                "Blood type should match");
        assertTrue((boolean)getPrivateField(patient1, "isVaccinated", Boolean.class),
                "Should be vaccinated");
    }

    @Test
    void testPatientSpecificFields() throws Exception {
        assertEquals("P1001", getPrivateField(patient1, "patientId", String.class),
                "Patient ID should match");
        assertEquals(Arrays.asList("Penicillin"), getPrivateField(patient1, "drugAllergies", List.class),
                "Drug allergies should match");
        assertEquals("Jane Doe", getPrivateField(patient1, "nokName", String.class),
                "NOK name should match");
        assertEquals("456 Test Street", getPrivateField(patient1, "nokAddress", String.class),
                "NOK address should match");
        assertEquals(NokRelation.SPOUSE, getPrivateField(patient1, "nokRelation", NokRelation.class),
                "NOK relation should match");
        assertEquals(1.75, (double)getPrivateField(patient1, "height", Double.class), 0.001,
                "Height should match");
        assertEquals(70.0, (double)getPrivateField(patient1, "weight", Double.class), 0.001,
                "Weight should match");
        assertEquals("Engineer", getPrivateField(patient1, "occupation", String.class),
                "Occupation should match");
        assertEquals("Test Company", getPrivateField(patient1, "companyName", String.class),
                "Company name should match");
        assertEquals("789 Test Street", getPrivateField(patient1, "companyAddress", String.class),
                "Company address should match");
    }

    @Test
    void testRandomPatientCreation() throws Exception {
        assertNotNull(patient2, "Random patient should not be null");
        assertEquals("P1002", getPrivateField(patient2, "patientId", String.class),
                "Random patient ID should match");
        assertNotNull(patient2.getName(), "Random patient name should not be null");
        assertNotNull(patient2.getNricFin(), "Random patient NRIC/FIN should not be null");
    }

    @Test
    void testMultipleRandomPatients() throws Exception {
        assertEquals(3, patients.size(), "Should create 3 random patients");
        
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            String expectedId = String.format("P%04d", 1003 + i);
            
            assertNotNull(patient, "Generated patient should not be null");
            assertEquals(expectedId, getPrivateField(patient, "patientId", String.class),
                    "Patient ID should match expected format");
            assertNotNull(patient.getName(), "Generated patient name should not be null");
            assertNotNull(patient.getNricFin(), "Generated patient NRIC/FIN should not be null");
            assertNotNull(getPrivateField(patient, "contact", Contact.class),
                    "Generated patient contact should not be null");
        }
    }

    @Test
    void testContactInformation() throws Exception {
        Contact contact = getPrivateField(patient1, "contact", Contact.class);
        assertNotNull(contact, "Contact should not be null");
        
        assertEquals("91234567", getPrivateField(contact, "personalPhone", String.class),
                "Personal phone should match");
        assertEquals("61234567", getPrivateField(contact, "homePhone", String.class),
                "Home phone should match");
        assertEquals("61234568", getPrivateField(contact, "companyPhone", String.class),
                "Company phone should match");
        assertEquals("john@test.com", getPrivateField(contact, "email", String.class),
                "Email should match");
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
