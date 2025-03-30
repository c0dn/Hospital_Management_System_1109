package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bee.hms.humans.*;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests for serialization and deserialization of Human objects using JSONHelper.
 * This test class verifies that Human objects can be properly converted to JSON and back.
 */
public class HumanSerializationTest {
    private Patient patient;
    private Doctor doctor;
    private Nurse nurse;
    private Clerk clerk;

    private List<Human> humanList;

    @BeforeEach
    void setUp() {
        humanList = new ArrayList<>();

        // Create test objects
        patient = createTestPatient();
        doctor = createTestDoctor();
        nurse = createTestNurse();
        clerk = createTestClerk();
        humanList.add(patient);
        humanList.add(doctor);
        humanList.add(nurse);
        humanList.add(clerk);
        humanList.add(createTestPatient());
    }

    /**
     * Helper method to access private fields using reflection from the class or any of its superclasses
     *
     * @param obj The object instance to access the field from
     * @param fieldName The name of the private field to access
     * @param fieldType The expected type of the field for casting
     * @return The value of the private field cast to the specified type
     * @throws Exception If the field is not found or cannot be accessed
     */
    private <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> currentClass = obj.getClass();

        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return fieldType.cast(field.get(obj));
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy of " + obj.getClass().getName());
    }


    @Test
    @DisplayName("Test polymorphic deserialization of a mixed Human list")
    void testHumanListDeserialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("human_list_test.json").toString();

        // Save the list to a JSON file
        JSONHelper.saveToJsonFile(humanList, jsonFilePath);

        // Verify the file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load the list back from the JSON file as a list of Human objects
        List<Human> deserializedHumans = JSONHelper.loadListFromJsonFile(jsonFilePath, Human.class);

        // Verify the list was deserialized correctly
        assertNotNull(deserializedHumans, "Deserialized list should not be null");
        assertEquals(humanList.size(), deserializedHumans.size(),
                "Deserialized list should have the same number of elements");

        // Count the instances of each subtype in the deserialized list
        int patientCount = 0;
        int doctorCount = 0;
        int nurseCount = 0;
        int clerkCount = 0;

        for (Human human : deserializedHumans) {
            if (human instanceof Patient) {
                patientCount++;
            } else if (human instanceof Doctor) {
                doctorCount++;
            } else if (human instanceof Nurse) {
                nurseCount++;
            } else if (human instanceof Clerk) {
                clerkCount++;
            }
        }

        // Verify we have the correct number of each type
        assertEquals(2, patientCount, "Should have 2 patients");
        assertEquals(1, doctorCount, "Should have 1 doctor");
        assertEquals(1, nurseCount, "Should have 1 nurse");
        assertEquals(1, clerkCount, "Should have 1 clerk");

        // Verify each object was deserialized to the correct type with fields intact
        for (int i = 0; i < humanList.size(); i++) {
            Human original = humanList.get(i);
            Human deserialized = deserializedHumans.get(i);

            // Verify the objects have the same class
            assertEquals(original.getClass(), deserialized.getClass(),
                    "Object at index " + i + " should have same class");

            // Verify some key properties are preserved
            assertEquals(original.getName(), deserialized.getName(),
                    "Name should match for object at index " + i);
            assertEquals(original.getNricFin(), deserialized.getNricFin(),
                    "NRIC/FIN should match for object at index " + i);

            // Verify subtype-specific fields
            switch (original) {
                case Patient originalPatient -> {
                    Patient deserializedPatient = (Patient) deserialized;
                    assertEquals(originalPatient.getPatientId(), deserializedPatient.getPatientId(),
                            "Patient ID should match");
                }
                case Doctor originalDoctor -> {
                    Doctor deserializedDoctor = (Doctor) deserialized;
                    assertEquals(originalDoctor.getMcr(), deserializedDoctor.getMcr(),
                            "Medical Council Registration should match");
                }
                case Nurse originalNurse -> {
                    Nurse deserializedNurse = (Nurse) deserialized;
                    assertEquals(originalNurse.getRnid(), deserializedNurse.getRnid(),
                            "Registered Nurse ID should match");
                }
                case Clerk originalClerk -> {
                    Clerk deserializedClerk = (Clerk) deserialized;
                    assertEquals(getPrivateField(originalClerk, "staffId", String.class),
                            getPrivateField(deserializedClerk, "staffId", String.class));
                }
                default -> {
                }
            }
        }
    }

    @Test
    @DisplayName("Test Patient serialization and deserialization")
    void testPatientSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("patient_test.json").toString();

        // Save to JSON file
        JSONHelper.saveToJsonFile(patient, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Patient deserializedPatient = JSONHelper.loadFromJsonFile(jsonFilePath, Patient.class);

        // Verify fields from Human superclass
        assertEquals(getPrivateField(patient, "name", String.class),
                    getPrivateField(deserializedPatient, "name", String.class));
        assertEquals(getPrivateField(patient, "dateOfBirth", LocalDate.class),
                    getPrivateField(deserializedPatient, "dateOfBirth", LocalDate.class));
        assertEquals(getPrivateField(patient, "nricFin", String.class),
                    getPrivateField(deserializedPatient, "nricFin", String.class));
        assertEquals(getPrivateField(patient, "maritalStatus", MaritalStatus.class),
                    getPrivateField(deserializedPatient, "maritalStatus", MaritalStatus.class));
        assertEquals(getPrivateField(patient, "residentialStatus", ResidentialStatus.class),
                    getPrivateField(deserializedPatient, "residentialStatus", ResidentialStatus.class));
        assertEquals(getPrivateField(patient, "nationality", String.class),
                    getPrivateField(deserializedPatient, "nationality", String.class));
        assertEquals(getPrivateField(patient, "address", String.class),
                    getPrivateField(deserializedPatient, "address", String.class));
        // Verify Contact fields individually
        Contact originalContact = getPrivateField(patient, "contact", Contact.class);
        Contact deserializedContact = getPrivateField(deserializedPatient, "contact", Contact.class);
        verifyContactFields(originalContact, deserializedContact);
        assertEquals(getPrivateField(patient, "sex", Sex.class),
                    getPrivateField(deserializedPatient, "sex", Sex.class));
        assertEquals(getPrivateField(patient, "bloodType", BloodType.class),
                    getPrivateField(deserializedPatient, "bloodType", BloodType.class));
        assertEquals(getPrivateField(patient, "isVaccinated", Boolean.class),
                    getPrivateField(deserializedPatient, "isVaccinated", Boolean.class));

        // Verify Patient-specific fields
        assertEquals(getPrivateField(patient, "patientId", String.class),
                    getPrivateField(deserializedPatient, "patientId", String.class));
        // Verify drugAllergies list contents individually
        List<?> originalAllergies = getPrivateField(patient, "drugAllergies", List.class);
        List<?> deserializedAllergies = getPrivateField(deserializedPatient, "drugAllergies", List.class);
        assertEquals(originalAllergies.size(), deserializedAllergies.size(), "Drug allergies list size should match");
        for (int i = 0; i < originalAllergies.size(); i++) {
            assertEquals(originalAllergies.get(i), deserializedAllergies.get(i), 
                        "Drug allergy at index " + i + " should match");
        }
        assertEquals(getPrivateField(patient, "nokName", String.class),
                    getPrivateField(deserializedPatient, "nokName", String.class));
        assertEquals(getPrivateField(patient, "nokAddress", String.class),
                    getPrivateField(deserializedPatient, "nokAddress", String.class));
        assertEquals(getPrivateField(patient, "nokRelation", NokRelation.class),
                    getPrivateField(deserializedPatient, "nokRelation", NokRelation.class));
        assertEquals(getPrivateField(patient, "height", Double.class),
                    getPrivateField(deserializedPatient, "height", Double.class));
        assertEquals(getPrivateField(patient, "weight", Double.class),
                    getPrivateField(deserializedPatient, "weight", Double.class));
        assertEquals(getPrivateField(patient, "occupation", String.class),
                    getPrivateField(deserializedPatient, "occupation", String.class));
        assertEquals(getPrivateField(patient, "companyName", String.class),
                    getPrivateField(deserializedPatient, "companyName", String.class));
        assertEquals(getPrivateField(patient, "companyAddress", String.class),
                    getPrivateField(deserializedPatient, "companyAddress", String.class));
        assertEquals(getPrivateField(patient, "patientConsent", Boolean.class),
                    getPrivateField(deserializedPatient, "patientConsent", Boolean.class));
    }

    @Test
    @DisplayName("Test Doctor serialization and deserialization")
    void testDoctorSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("doctor_test.json").toString();

        // Save to JSON file
        JSONHelper.saveToJsonFile(doctor, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Doctor deserializedDoctor = JSONHelper.loadFromJsonFile(jsonFilePath, Doctor.class);

        // Verify fields from Human superclass
        assertEquals(getPrivateField(doctor, "name", String.class),
                    getPrivateField(deserializedDoctor, "name", String.class));
        assertEquals(getPrivateField(doctor, "dateOfBirth", LocalDate.class),
                    getPrivateField(deserializedDoctor, "dateOfBirth", LocalDate.class));
        assertEquals(getPrivateField(doctor, "nricFin", String.class),
                    getPrivateField(deserializedDoctor, "nricFin", String.class));
        assertEquals(getPrivateField(doctor, "maritalStatus", MaritalStatus.class),
                    getPrivateField(deserializedDoctor, "maritalStatus", MaritalStatus.class));
        assertEquals(getPrivateField(doctor, "residentialStatus", ResidentialStatus.class),
                    getPrivateField(deserializedDoctor, "residentialStatus", ResidentialStatus.class));
        assertEquals(getPrivateField(doctor, "nationality", String.class),
                    getPrivateField(deserializedDoctor, "nationality", String.class));
        assertEquals(getPrivateField(doctor, "address", String.class),
                    getPrivateField(deserializedDoctor, "address", String.class));
        // Verify Contact fields individually
        Contact originalContact = getPrivateField(doctor, "contact", Contact.class);
        Contact deserializedContact = getPrivateField(deserializedDoctor, "contact", Contact.class);
        verifyContactFields(originalContact, deserializedContact);
        assertEquals(getPrivateField(doctor, "sex", Sex.class),
                    getPrivateField(deserializedDoctor, "sex", Sex.class));
        assertEquals(getPrivateField(doctor, "bloodType", BloodType.class),
                    getPrivateField(deserializedDoctor, "bloodType", BloodType.class));
        assertEquals(getPrivateField(doctor, "isVaccinated", Boolean.class),
                    getPrivateField(deserializedDoctor, "isVaccinated", Boolean.class));

        // Verify Staff superclass fields
        assertEquals(getPrivateField(doctor, "staffId", String.class),
                    getPrivateField(deserializedDoctor, "staffId", String.class));
        assertEquals(getPrivateField(doctor, "title", String.class),
                    getPrivateField(deserializedDoctor, "title", String.class));
        assertEquals(getPrivateField(doctor, "department", String.class),
                    getPrivateField(deserializedDoctor, "department", String.class));

        // Verify Doctor-specific fields
        assertEquals(getPrivateField(doctor, "mcr", String.class),
                    getPrivateField(deserializedDoctor, "mcr", String.class));
    }

    @Test
    @DisplayName("Test Nurse serialization and deserialization")
    void testNurseSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("nurse_test.json").toString();

        // Save to JSON file
        JSONHelper.saveToJsonFile(nurse, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Nurse deserializedNurse = JSONHelper.loadFromJsonFile(jsonFilePath, Nurse.class);

        // Verify fields from Human superclass
        assertEquals(getPrivateField(nurse, "name", String.class),
                    getPrivateField(deserializedNurse, "name", String.class));
        assertEquals(getPrivateField(nurse, "dateOfBirth", LocalDate.class),
                    getPrivateField(deserializedNurse, "dateOfBirth", LocalDate.class));
        assertEquals(getPrivateField(nurse, "nricFin", String.class),
                    getPrivateField(deserializedNurse, "nricFin", String.class));
        assertEquals(getPrivateField(nurse, "maritalStatus", MaritalStatus.class),
                    getPrivateField(deserializedNurse, "maritalStatus", MaritalStatus.class));
        assertEquals(getPrivateField(nurse, "residentialStatus", ResidentialStatus.class),
                    getPrivateField(deserializedNurse, "residentialStatus", ResidentialStatus.class));
        assertEquals(getPrivateField(nurse, "nationality", String.class),
                    getPrivateField(deserializedNurse, "nationality", String.class));
        assertEquals(getPrivateField(nurse, "address", String.class),
                    getPrivateField(deserializedNurse, "address", String.class));
        // Verify Contact fields individually
        Contact originalContact = getPrivateField(nurse, "contact", Contact.class);
        Contact deserializedContact = getPrivateField(deserializedNurse, "contact", Contact.class);
        verifyContactFields(originalContact, deserializedContact);
        assertEquals(getPrivateField(nurse, "sex", Sex.class),
                    getPrivateField(deserializedNurse, "sex", Sex.class));
        assertEquals(getPrivateField(nurse, "bloodType", BloodType.class),
                    getPrivateField(deserializedNurse, "bloodType", BloodType.class));
        assertEquals(getPrivateField(nurse, "isVaccinated", Boolean.class),
                    getPrivateField(deserializedNurse, "isVaccinated", Boolean.class));

        // Verify Staff superclass fields
        assertEquals(getPrivateField(nurse, "staffId", String.class),
                    getPrivateField(deserializedNurse, "staffId", String.class));
        assertEquals(getPrivateField(nurse, "title", String.class),
                    getPrivateField(deserializedNurse, "title", String.class));
        assertEquals(getPrivateField(nurse, "department", String.class),
                    getPrivateField(deserializedNurse, "department", String.class));

        // Verify Nurse-specific fields
        assertEquals(getPrivateField(nurse, "rnid", String.class),
                    getPrivateField(deserializedNurse, "rnid", String.class));
    }

    @Test
    @DisplayName("Test Clerk serialization and deserialization")
    void testClerkSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("clerk_test.json").toString();

        // Save to JSON file
        JSONHelper.saveToJsonFile(clerk, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Clerk deserializedClerk = JSONHelper.loadFromJsonFile(jsonFilePath, Clerk.class);

        // Verify fields from Human superclass
        assertEquals(getPrivateField(clerk, "name", String.class),
                    getPrivateField(deserializedClerk, "name", String.class));
        assertEquals(getPrivateField(clerk, "dateOfBirth", LocalDate.class),
                    getPrivateField(deserializedClerk, "dateOfBirth", LocalDate.class));
        assertEquals(getPrivateField(clerk, "nricFin", String.class),
                    getPrivateField(deserializedClerk, "nricFin", String.class));
        assertEquals(getPrivateField(clerk, "maritalStatus", MaritalStatus.class),
                    getPrivateField(deserializedClerk, "maritalStatus", MaritalStatus.class));
        assertEquals(getPrivateField(clerk, "residentialStatus", ResidentialStatus.class),
                    getPrivateField(deserializedClerk, "residentialStatus", ResidentialStatus.class));
        assertEquals(getPrivateField(clerk, "nationality", String.class),
                    getPrivateField(deserializedClerk, "nationality", String.class));
        assertEquals(getPrivateField(clerk, "address", String.class),
                    getPrivateField(deserializedClerk, "address", String.class));
        // Verify Contact fields individually
        Contact originalContact = getPrivateField(clerk, "contact", Contact.class);
        Contact deserializedContact = getPrivateField(deserializedClerk, "contact", Contact.class);
        verifyContactFields(originalContact, deserializedContact);
        assertEquals(getPrivateField(clerk, "sex", Sex.class),
                    getPrivateField(deserializedClerk, "sex", Sex.class));
        assertEquals(getPrivateField(clerk, "bloodType", BloodType.class),
                    getPrivateField(deserializedClerk, "bloodType", BloodType.class));
        assertEquals(getPrivateField(clerk, "isVaccinated", Boolean.class),
                    getPrivateField(deserializedClerk, "isVaccinated", Boolean.class));

        // Verify Staff superclass fields
        assertEquals(getPrivateField(clerk, "staffId", String.class),
                    getPrivateField(deserializedClerk, "staffId", String.class));
        assertEquals(getPrivateField(clerk, "title", String.class),
                    getPrivateField(deserializedClerk, "title", String.class));
        assertEquals(getPrivateField(clerk, "department", String.class),
                    getPrivateField(deserializedClerk, "department", String.class));
    }

    private Patient createTestPatient() {
        return Patient.builder()
                .withRandomBaseData()
                .patientId(DataGenerator.generatePatientId())
                .build();
    }

    private Doctor createTestDoctor() {
        return Doctor.builder()
                .withRandomBaseData().build();
    }

    private Nurse createTestNurse() {
        return Nurse.builder()
                .withRandomBaseData()
                .build();
    }

    /**
     * Helper method to verify Contact fields individually
     */
    private void verifyContactFields(Contact original, Contact deserialized) throws Exception {
        assertEquals(getPrivateField(original, "personalPhone", String.class),
                    getPrivateField(deserialized, "personalPhone", String.class));
        assertEquals(getPrivateField(original, "homePhone", String.class),
                    getPrivateField(deserialized, "homePhone", String.class));
        assertEquals(getPrivateField(original, "companyPhone", String.class),
                    getPrivateField(deserialized, "companyPhone", String.class));
        assertEquals(getPrivateField(original, "email", String.class),
                    getPrivateField(deserialized, "email", String.class));
    }

    private Clerk createTestClerk() {
        return Clerk.builder()
                .withRandomBaseData()
                .build();
    }
}
