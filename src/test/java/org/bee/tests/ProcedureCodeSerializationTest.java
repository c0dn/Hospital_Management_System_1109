package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

import org.bee.hms.medical.ProcedureCode;
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
 * Tests for serialization and deserialization of ProcedureCode objects using JSONHelper.
 * This test class verifies that ProcedureCode objects can be properly converted to JSON and back.
 */
public class ProcedureCodeSerializationTest {

    private JSONHelper jsonHelper;
    private ProcedureCode originalProcedureCode;

    @BeforeEach
    void setUp() {
        // Initialize JSONHelper
        jsonHelper = JSONHelper.getInstance();
        
        // Create a test ProcedureCode object
        originalProcedureCode = ProcedureCode.getRandomCode();
    }

    @Test
    @DisplayName("Test serializing ProcedureCode to JSON string")
    void testSerializeToJsonString() {
        String json = jsonHelper.toJson(originalProcedureCode);
        
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    @DisplayName("Test deserializing ProcedureCode from JSON string")
    void testDeserializeFromJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalProcedureCode);
        
        // Deserialize from JSON string
        ProcedureCode deserializedProcedureCode = jsonHelper.fromJson(json, ProcedureCode.class);
        
        // Verify properties match
        assertEquals(originalProcedureCode.getProcedureCode(), deserializedProcedureCode.getProcedureCode());
        assertEquals(originalProcedureCode.getBillItemDescription(), deserializedProcedureCode.getBillItemDescription());
        assertEquals(originalProcedureCode.getUnsubsidisedCharges(), deserializedProcedureCode.getUnsubsidisedCharges());
    }

    @Test
    @DisplayName("Test serializing ProcedureCode to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("procedure_code_test.json").toString();
        
        // Save ProcedureCode to JSON file
        jsonHelper.saveToJsonFile(originalProcedureCode, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load ProcedureCode from JSON file
        ProcedureCode fileDeserializedProcedureCode = jsonHelper.loadFromJsonFile(jsonFilePath, ProcedureCode.class);
        
        // Verify properties match
        assertEquals(originalProcedureCode.getProcedureCode(), fileDeserializedProcedureCode.getProcedureCode());
        assertEquals(originalProcedureCode.getBillItemDescription(), fileDeserializedProcedureCode.getBillItemDescription());
        assertEquals(originalProcedureCode.getUnsubsidisedCharges(), fileDeserializedProcedureCode.getUnsubsidisedCharges());
    }

}
