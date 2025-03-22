package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.utils.JSONHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for serialization and deserialization of Consultation objects using Gson.
 * This test class verifies that Consultation objects can be properly converted to JSON and back.
 * Uses reflection to access private fields since many getters are missing.
 */
public class ConsultationSerializationTest {

    private JSONHelper jsonHelper;
    private Consultation originalConsultation;

    @BeforeEach
    void setUp() {
        // Initialize Gson with necessary type adapters
        jsonHelper = JSONHelper.getInstance();

        // Create a Consultation object
        originalConsultation = createTestConsultation();
    }

    /**
     * Helper method to access private fields using reflection, searching both the class and its superclasses
     */
    private <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = null;

        // Try to find the field in the class or any of its superclasses
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // Field not found in current class, check the superclass
                clazz = clazz.getSuperclass();
            }
        }

        if (field == null) {
            throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy of " + obj.getClass().getName());
        }

        field.setAccessible(true);
        return fieldType.cast(field.get(obj));
    }

    @Test
    @DisplayName("Test serializing Consultation to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalConsultation);

        // Verify JSON string is not empty
        assertNotNull(json, "Serialized JSON should not be null");
        assertFalse(json.isEmpty(), "Serialized JSON should not be empty");

        // Optionally print the JSON for debugging purposes
        System.out.println("Serialized Consultation JSON: " + json);
    }

    @Test
    @DisplayName("Test deserializing Consultation from JSON string")
    void testDeserializeFromJsonString() throws Exception {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalConsultation);

        // Deserialize from JSON string
        Consultation deserializedConsultation = jsonHelper.fromJson(json, Consultation.class);

        verifyFields(originalConsultation, deserializedConsultation);
    }

    @Test
    @DisplayName("Test serializing Consultation to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws Exception {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("consultation_test.json").toString();

        // Save Consultation to JSON file
        jsonHelper.saveToJsonFile(originalConsultation, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load Consultation from JSON file
        Consultation fileDeserializedConsultation = jsonHelper.loadFromJsonFile(jsonFilePath, Consultation.class);

        verifyFields(originalConsultation, fileDeserializedConsultation);
    }

    /**
     * Helper method to verify all fields between original and deserialized Consultation objects
     */
    private void verifyFields(Consultation original, Consultation deserialized) throws Exception {
        // Verify basic fields
        assertEquals(getPrivateField(original, "consultationId", String.class),
                getPrivateField(deserialized, "consultationId", String.class),
                "Consultation ID should match");
        assertEquals(getPrivateField(original, "type", ConsultationType.class),
                getPrivateField(deserialized, "type", ConsultationType.class),
                "Consultation type should match");
        assertEquals(getPrivateField(original, "consultationTime", LocalDateTime.class),
                getPrivateField(deserialized, "consultationTime", LocalDateTime.class),
                "Consultation time should match");
        assertEquals(getPrivateField(original, "consultationFee", BigDecimal.class),
                getPrivateField(deserialized, "consultationFee", BigDecimal.class),
                "Consultation fee should match");
        assertEquals(getPrivateField(original, "status", ConsultationStatus.class),
                getPrivateField(deserialized, "status", ConsultationStatus.class),
                "Consultation status should match");
        assertEquals(getPrivateField(original, "notes", String.class),
                getPrivateField(deserialized, "notes", String.class),
                "Notes should match");

        // Additional fields from the class definition
        assertEquals(getPrivateField(original, "appointmentDate", LocalDateTime.class),
                getPrivateField(deserialized, "appointmentDate", LocalDateTime.class),
                "Appointment date should match");
        assertEquals(getPrivateField(original, "medicalHistory", String.class),
                getPrivateField(deserialized, "medicalHistory", String.class),
                "Medical history should match");
        assertEquals(getPrivateField(original, "diagnosis", String.class),
                getPrivateField(deserialized, "diagnosis", String.class),
                "Diagnosis should match");
        assertEquals(getPrivateField(original, "visitReason", String.class),
                getPrivateField(deserialized, "visitReason", String.class),
                "Visit reason should match");
        assertEquals(getPrivateField(original, "followUpDate", LocalDateTime.class),
                getPrivateField(deserialized, "followUpDate", LocalDateTime.class),
                "Follow-up date should match");
        assertEquals(getPrivateField(original, "instructions", String.class),
                getPrivateField(deserialized, "instructions", String.class),
                "Instructions should match");

        // Verify complex object references
        HospitalDepartment originalDept = getPrivateField(original, "department", HospitalDepartment.class);
        HospitalDepartment deserializedDept = getPrivateField(deserialized, "department", HospitalDepartment.class);
        if (originalDept != null) {
            assertEquals(getPrivateField(originalDept, "name", String.class),
                    getPrivateField(deserializedDept, "name", String.class),
                    "Department name should match");
        }

        Patient originalPatient = getPrivateField(original, "patient", Patient.class);
        Patient deserializedPatient = getPrivateField(deserialized, "patient", Patient.class);
        if (originalPatient != null) {
            assertEquals(originalPatient.getPatientId(), deserializedPatient.getPatientId(),
                    "Patient ID should match");
        }

        Doctor originalDoctor = getPrivateField(original, "doctor", Doctor.class);
        Doctor deserializedDoctor = getPrivateField(deserialized, "doctor", Doctor.class);
        if (originalDoctor != null) {
            assertEquals(getPrivateField(originalDoctor, "staffId", String.class),
                    getPrivateField(deserializedDoctor, "staffId", String.class),
                    "Doctor ID should match");
        }

        // Verify lists with complex objects by checking their internal fields
        verifyList(original, deserialized, "diagnosticCodes", DiagnosticCode.class, "Diagnostic codes");
        verifyList(original, deserialized, "procedureCodes", ProcedureCode.class, "Procedure codes");
        verifyList(original, deserialized, "treatments", Treatment.class, "Treatments");

        verifyList(original, deserialized, "labTests", LabTest.class, "Lab tests");

        verifyPrescriptionsMap(original, deserialized);
    }

    // Assuming you need a method to verify Treatment objects as well
    private void verifyTreatment(Treatment original, Treatment deserialized, String message) throws Exception {
        assertEquals(getPrivateField(original, "treatmentId", String.class),
                getPrivateField(deserialized, "treatmentId", String.class),
                message + " (treatment ID)");
        assertEquals(getPrivateField(original, "description", String.class),
                getPrivateField(deserialized, "description", String.class),
                message + " (treatment description)");
    }

    /**
     * Helper method to verify lists of objects with deep comparison of their fields
     */
    private <T> void verifyList(Consultation original, Consultation deserialized, String fieldName, Class<T> elementType, String message) throws Exception {
        List<T> originalList = getPrivateField(original, fieldName, List.class);
        List<T> deserializedList = getPrivateField(deserialized, fieldName, List.class);

        // Add null checks for both lists
        if (originalList == null && deserializedList == null) {
            return; // Both lists are null, which is considered equal
        }

        if (originalList == null || deserializedList == null) {
            fail(message + ": one list is null and the other is not");
        }

        assertEquals(originalList.size(), deserializedList.size(), message + " size should match");

        for (int i = 0; i < originalList.size(); i++) {
            T originalItem = originalList.get(i);
            T deserializedItem = deserializedList.get(i);

            if (originalItem == null && deserializedItem == null) {
                continue;
            }

            if (originalItem == null || deserializedItem == null) {
                fail(message + ": at index " + i + ", one element is null and the other is not");
            }

            if (elementType == DiagnosticCode.class) {
                DiagnosticCode origCode = (DiagnosticCode) originalItem;
                DiagnosticCode deserCode = (DiagnosticCode) deserializedItem;
                assertEquals(getPrivateField(origCode, "fullCode", String.class),
                        getPrivateField(deserCode, "fullCode", String.class),
                        message + " code at " + i + " should match");
                assertEquals(getPrivateField(origCode, "fullDescription", String.class),
                        getPrivateField(deserCode, "fullDescription", String.class),
                        message + " description at " + i + " should match");
            }
            else if (elementType == ProcedureCode.class) {
                ProcedureCode origCode = (ProcedureCode) originalItem;
                ProcedureCode deserCode = (ProcedureCode) deserializedItem;
                assertEquals(getPrivateField(origCode, "code", String.class),
                        getPrivateField(deserCode, "code", String.class),
                        message + " code at " + i + " should match");
                assertEquals(getPrivateField(origCode, "description", String.class),
                        getPrivateField(deserCode, "description", String.class),
                        message + " description at " + i + " should match");
            }
            else if (elementType == LabTest.class) {
                LabTest origTest = (LabTest) originalItem;
                LabTest deserTest = (LabTest) deserializedItem;
                assertEquals(getPrivateField(origTest, "testId", String.class),
                        getPrivateField(deserTest, "testId", String.class),
                        message + " test ID at " + i + " should match");
                assertEquals(getPrivateField(origTest, "testType", LabTestType.class),
                        getPrivateField(deserTest, "testType", LabTestType.class),
                        message + " test type at " + i + " should match");
                assertEquals(getPrivateField(origTest, "status", String.class),
                        getPrivateField(deserTest, "status", String.class),
                        message + " status at " + i + " should match");

                String origResults = getPrivateField(origTest, "results", String.class);
                String deserResults = getPrivateField(deserTest, "results", String.class);
                assertEquals(origResults, deserResults, message + " results at " + i + " should match");
            }
            else if (elementType == Treatment.class) {
                Treatment origTreatment = (Treatment) originalItem;
                Treatment deserTreatment = (Treatment) deserializedItem;
                assertEquals(getPrivateField(origTreatment, "treatmentId", String.class),
                        getPrivateField(deserTreatment, "treatmentId", String.class),
                        message + " treatment ID at " + i + " should match");
            }
            else {
                assertEquals(originalItem, deserializedItem,
                        message + " element at " + i + " should match");
            }
        }
    }

    /**
     * Helper method to verify prescriptions map with deep comparison
     */
    private void verifyPrescriptionsMap(Consultation original, Consultation deserialized) throws Exception {
        Map<Medication, Integer> originalPrescriptions = getPrivateField(original, "prescriptions", Map.class);
        Map<Medication, Integer> deserializedPrescriptions = getPrivateField(deserialized, "prescriptions", Map.class);
        assertEquals(originalPrescriptions.size(), deserializedPrescriptions.size(), "Prescriptions map size should match");
        
        originalPrescriptions.forEach((origMedication, origQuantity) -> {
            assertTrue(deserializedPrescriptions.containsKey(origMedication), 
                      "Medication " + origMedication + " should exist in deserialized map");
            assertEquals(origQuantity, deserializedPrescriptions.get(origMedication), 
                        "Quantity for medication " + origMedication + " should match");
            
            // Verify Medication object fields
            try {
                Medication deserMedication = deserializedPrescriptions.keySet().stream()
                    .filter(med -> {
                        try {
                            return getPrivateField(med, "drugCode", String.class)
                                   .equals(getPrivateField(origMedication, "drugCode", String.class));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Matching medication not found"));
                    
                assertEquals(getPrivateField(origMedication, "drugCode", String.class),
                           getPrivateField(deserMedication, "drugCode", String.class),
                           "Medication code should match");
                assertEquals(getPrivateField(origMedication, "name", String.class),
                           getPrivateField(deserMedication, "name", String.class),
                           "Medication name should match");
                assertEquals(getPrivateField(origMedication, "pricePerUnit", BigDecimal.class),
                           getPrivateField(deserMedication, "pricePerUnit", BigDecimal.class),
                           "Medication unit price should match");
            } catch (Exception e) {
                throw new RuntimeException("Error verifying medication fields", e);
            }
        });
    }

    /**
     * Creates a test Consultation object with various properties set.
     */
    private Consultation createTestConsultation() {
        return Consultation.withRandomData();
    }
}
