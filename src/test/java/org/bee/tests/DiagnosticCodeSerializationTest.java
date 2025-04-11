package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

import org.bee.hms.medical.DiagnosticCode;
import org.bee.utils.JSONHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for serialization and deserialization of DiagnosticCode objects using JSONHelper.
 * This test class verifies that DiagnosticCode objects can be properly converted to JSON and back.
 */
public class DiagnosticCodeSerializationTest {
    private DiagnosticCode originalDiagnosticCode;

    @BeforeEach
    void setUp() {
        originalDiagnosticCode = DiagnosticCode.getRandomCode();
    }

    @Test
    @DisplayName("Test serializing DiagnosticCode to JSON string")
    void testSerializeToJsonString() {
        String json = JSONHelper.toJson(originalDiagnosticCode);
        
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    @DisplayName("Test deserializing DiagnosticCode from JSON string")
    void testDeserializeFromJsonString() {
        String json = JSONHelper.toJson(originalDiagnosticCode);
        
        DiagnosticCode deserializedDiagnosticCode = JSONHelper.fromJson(json, DiagnosticCode.class);
        
        // Verify properties match using available getters
        assertEquals(originalDiagnosticCode.getDiagnosisCode(), deserializedDiagnosticCode.getDiagnosisCode());
        assertEquals(originalDiagnosticCode.getBillItemDescription(), deserializedDiagnosticCode.getBillItemDescription());
        assertEquals(originalDiagnosticCode.getUnsubsidisedCharges(), deserializedDiagnosticCode.getUnsubsidisedCharges());
    }

    @Test
    @DisplayName("Test serializing DiagnosticCode to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
        String jsonFilePath = tempDir.resolve("diagnostic_code_test.json").toString();

        JSONHelper.saveToJsonFile(originalDiagnosticCode, jsonFilePath);
        
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        DiagnosticCode fileDeserializedDiagnosticCode = JSONHelper.loadFromJsonFile(jsonFilePath, DiagnosticCode.class);
        
        assertEquals(originalDiagnosticCode.getDiagnosisCode(), fileDeserializedDiagnosticCode.getDiagnosisCode());
        assertEquals(originalDiagnosticCode.getBillItemDescription(), fileDeserializedDiagnosticCode.getBillItemDescription());
        assertEquals(originalDiagnosticCode.getUnsubsidisedCharges(), fileDeserializedDiagnosticCode.getUnsubsidisedCharges());
    }
}
