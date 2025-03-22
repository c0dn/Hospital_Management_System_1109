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
    void testDeserializeFromJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalConsultation);

        // Deserialize from JSON string
        Consultation deserializedConsultation = jsonHelper.fromJson(json, Consultation.class);

        // Verify key properties match between original and deserialized objects
        assertEquals(originalConsultation.getConsultationId(), deserializedConsultation.getConsultationId());
        assertEquals(originalConsultation.getType(), deserializedConsultation.getType());
        assertEquals(originalConsultation.getDoctorId(), deserializedConsultation.getDoctorId());
        assertEquals(originalConsultation.getConsultationTime(), deserializedConsultation.getConsultationTime());
        assertEquals(originalConsultation.getConsultationFee(), deserializedConsultation.getConsultationFee());

        // Verify nested objects
        assertEquals(originalConsultation.getDiagnosticCodes().size(),
                deserializedConsultation.getDiagnosticCodes().size());
        assertEquals(originalConsultation.getProcedureCodes().size(),
                deserializedConsultation.getProcedureCodes().size());

        // Verify prescriptions map
        assertEquals(originalConsultation.getPrescriptions().size(),
                deserializedConsultation.getPrescriptions().size());
    }

    @Test
    @DisplayName("Test serializing Consultation to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
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

            // Verify key properties match between original and file-deserialized objects
            assertEquals(originalConsultation.getConsultationId(), fileDeserializedConsultation.getConsultationId());
            assertEquals(originalConsultation.getType(), fileDeserializedConsultation.getType());
            assertEquals(originalConsultation.getDoctorId(), fileDeserializedConsultation.getDoctorId());
            assertEquals(originalConsultation.getPrescriptions().size(),
                    fileDeserializedConsultation.getPrescriptions().size());
    }

    /**
     * Creates a test Consultation object with various properties set.
     */
    private Consultation createTestConsultation() {

        return Consultation.withRandomData();
    }

}
