package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.bee.hms.medical.Medication;
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
 * Tests for serialization and deserialization of Medication objects using JSONHelper.
 * This test class verifies that Medication objects can be properly converted to JSON and back.
 */
public class MedicationSerializationTest {

    private Medication originalMedication;

    @BeforeEach
    void setUp() {

        originalMedication = Medication.getRandomMedication();
    }

    @Test
    @DisplayName("Test serializing Medication to JSON string")
    void testSerializeToJsonString() throws IOException {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalMedication);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    @DisplayName("Test deserializing Medication from JSON string")
    void testDeserializeFromJsonString() throws IOException {
        String json = JSONHelper.toJson(originalMedication);
        
        Medication deserializedMedication = JSONHelper.fromJson(json, Medication.class);
        
        assertEquals(originalMedication.getDrugCode(), deserializedMedication.getDrugCode());
        assertEquals(originalMedication.calculateCost(5), deserializedMedication.calculateCost(5));
    }

    @Test
    @DisplayName("Test serializing Medication to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
        String jsonFilePath = tempDir.resolve("medication_test.json").toString();
        
        JSONHelper.saveToJsonFile(originalMedication, jsonFilePath);
        
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        Medication fileDeserializedMedication = JSONHelper.loadFromJsonFile(jsonFilePath, Medication.class);
        
        // Verify properties match
        assertEquals(originalMedication.getDrugCode(), fileDeserializedMedication.getDrugCode());
        assertEquals(originalMedication.calculateCost(5), fileDeserializedMedication.calculateCost(5));
    }
}
