package org.bee.tests;

import org.bee.hms.medical.*;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Helper method to access private fields using reflection
     */
    private <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
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

        // Verify key properties match between original and deserialized objects using reflection
        assertEquals(
                getPrivateField(originalConsultation, "consultationId", String.class),
                getPrivateField(deserializedConsultation, "consultationId", String.class)
        );
        assertEquals(
                getPrivateField(originalConsultation, "type", ConsultationType.class),
                getPrivateField(deserializedConsultation, "type", ConsultationType.class)
        );
        assertEquals(
                getPrivateField(originalConsultation, "doctorId", String.class),
                getPrivateField(deserializedConsultation, "doctorId", String.class)
        );
        assertEquals(
                getPrivateField(originalConsultation, "consultationTime", LocalDateTime.class),
                getPrivateField(deserializedConsultation, "consultationTime", LocalDateTime.class)
        );
        assertEquals(
                getPrivateField(originalConsultation, "consultationFee", BigDecimal.class),
                getPrivateField(deserializedConsultation, "consultationFee", BigDecimal.class)
        );

        // Verify nested objects
        List<DiagnosticCode> originalDiagnosticCodes = getPrivateField(originalConsultation, "diagnosticCodes", List.class);
        List<DiagnosticCode> deserializedDiagnosticCodes = getPrivateField(deserializedConsultation, "diagnosticCodes", List.class);
        assertEquals(originalDiagnosticCodes.size(), deserializedDiagnosticCodes.size());

        List<ProcedureCode> originalProcedureCodes = getPrivateField(originalConsultation, "procedureCodes", List.class);
        List<ProcedureCode> deserializedProcedureCodes = getPrivateField(deserializedConsultation, "procedureCodes", List.class);
        assertEquals(originalProcedureCodes.size(), deserializedProcedureCodes.size());

        // Verify prescriptions map
        Map<Medication, Integer> originalPrescriptions = getPrivateField(originalConsultation, "prescriptions", Map.class);
        Map<Medication, Integer> deserializedPrescriptions = getPrivateField(deserializedConsultation, "prescriptions", Map.class);
        assertEquals(originalPrescriptions.size(), deserializedPrescriptions.size());
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

        // Verify key properties match between original and file-deserialized objects using reflection
        assertEquals(
                getPrivateField(originalConsultation, "consultationId", String.class),
                getPrivateField(fileDeserializedConsultation, "consultationId", String.class)
        );
        assertEquals(
                getPrivateField(originalConsultation, "type", ConsultationType.class),
                getPrivateField(fileDeserializedConsultation, "type", ConsultationType.class)
        );
        assertEquals(
                getPrivateField(originalConsultation, "doctorId", String.class),
                getPrivateField(fileDeserializedConsultation, "doctorId", String.class)
        );

        Map<Medication, Integer> originalPrescriptions = getPrivateField(originalConsultation, "prescriptions", Map.class);
        Map<Medication, Integer> deserializedPrescriptions = getPrivateField(fileDeserializedConsultation, "prescriptions", Map.class);
        assertEquals(originalPrescriptions.size(), deserializedPrescriptions.size());
    }

    /**
     * Creates a test Consultation object with various properties set.
     */
    private Consultation createTestConsultation() {
        return Consultation.withRandomData();
    }
}