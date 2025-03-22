package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HumanSerializationTest {
    private JSONHelper jsonHelper;
    private Patient patient;
    private Doctor doctor;
    private Nurse nurse;
    private Clerk clerk;

    @BeforeEach
    void setUp() {
        jsonHelper = JSONHelper.getInstance();

        // Create test objects
        patient = createTestPatient();
        doctor = createTestDoctor();
        nurse = createTestNurse();
        clerk = createTestClerk();
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
    @DisplayName("Test Patient serialization and deserialization")
    void testPatientSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("patient_test.json").toString();

        // Save to JSON file
        jsonHelper.saveToJsonFile(patient, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Patient deserializedPatient = jsonHelper.loadFromJsonFile(jsonFilePath, Patient.class);

        // Verify fields
        assertEquals(getPrivateField(patient, "patientId", String.class),
                    getPrivateField(deserializedPatient, "patientId", String.class));
        assertEquals(getPrivateField(patient, "name", String.class),
                    getPrivateField(deserializedPatient, "name", String.class));
        assertEquals(getPrivateField(patient, "nricFin", String.class),
                    getPrivateField(deserializedPatient, "nricFin", String.class));
        assertEquals(getPrivateField(patient, "height", Double.class),
                    getPrivateField(deserializedPatient, "height", Double.class));
        assertEquals(getPrivateField(patient, "weight", Double.class),
                    getPrivateField(deserializedPatient, "weight", Double.class));
        assertEquals(getPrivateField(patient, "drugAllergies", List.class),
                    getPrivateField(deserializedPatient, "drugAllergies", List.class));
    }

    @Test
    @DisplayName("Test Doctor serialization and deserialization")
    void testDoctorSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("doctor_test.json").toString();

        // Save to JSON file
        jsonHelper.saveToJsonFile(doctor, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Doctor deserializedDoctor = jsonHelper.loadFromJsonFile(jsonFilePath, Doctor.class);

        // Verify fields
        assertEquals(getPrivateField(doctor, "mcr", String.class),
                    getPrivateField(deserializedDoctor, "mcr", String.class));
        assertEquals(getPrivateField(doctor, "name", String.class),
                    getPrivateField(deserializedDoctor, "name", String.class));
        assertEquals(getPrivateField(doctor, "nricFin", String.class),
                    getPrivateField(deserializedDoctor, "nricFin", String.class));
    }

    @Test
    @DisplayName("Test Nurse serialization and deserialization")
    void testNurseSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("nurse_test.json").toString();

        // Save to JSON file
        jsonHelper.saveToJsonFile(nurse, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Nurse deserializedNurse = jsonHelper.loadFromJsonFile(jsonFilePath, Nurse.class);

        // Verify fields
        assertEquals(getPrivateField(nurse, "rnid", String.class),
                    getPrivateField(deserializedNurse, "rnid", String.class));
        assertEquals(getPrivateField(nurse, "name", String.class),
                    getPrivateField(deserializedNurse, "name", String.class));
        assertEquals(getPrivateField(nurse, "nricFin", String.class),
                    getPrivateField(deserializedNurse, "nricFin", String.class));
    }

    @Test
    @DisplayName("Test Clerk serialization and deserialization")
    void testClerkSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("clerk_test.json").toString();

        // Save to JSON file
        jsonHelper.saveToJsonFile(clerk, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Clerk deserializedClerk = jsonHelper.loadFromJsonFile(jsonFilePath, Clerk.class);

        // Verify basic human fields since Clerk doesn't have additional fields
        assertEquals(getPrivateField(clerk, "name", String.class),
                    getPrivateField(deserializedClerk, "name", String.class));
        assertEquals(getPrivateField(clerk, "nricFin", String.class),
                    getPrivateField(deserializedClerk, "nricFin", String.class));
    }

    private Patient createTestPatient() {
        return Patient.builder()
                .withRandomBaseData()
                .patientId(DataGenerator.getInstance().generatePatientId())
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

    private Clerk createTestClerk() {
        return Clerk.builder()
                .withRandomBaseData()
                .build();
    }
}
