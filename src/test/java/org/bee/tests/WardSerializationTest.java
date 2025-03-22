package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;

import org.bee.hms.wards.Bed;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;
import org.bee.utils.JSONHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for serialization and deserialization of Ward objects using JSONHelper.
 * This test class verifies that Ward objects can be properly converted to JSON and back.
 */
public class WardSerializationTest {
    
    private JSONHelper jsonHelper;
    private Ward generalWard;
    private Ward icuWard;
    private Ward labourWard;
    private Ward daySurgeryWard;

    @BeforeEach
    void setUp() {
        jsonHelper = JSONHelper.getInstance();

        // Create test ward objects
        generalWard = WardFactory.getWard("General Ward A", WardClassType.GENERAL_CLASS_A);
        icuWard = WardFactory.getWard("ICU Ward 1", WardClassType.ICU);
        labourWard = WardFactory.getWard("Labour Ward 1", WardClassType.LABOUR_CLASS_A);
        daySurgeryWard = WardFactory.getWard("Day Surgery Ward 1", WardClassType.DAYSURGERY_CLASS_SINGLE);
    }

    /**
     * Helper method to access private fields using reflection
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
    @DisplayName("Test General Ward serialization and deserialization")
    void testGeneralWardSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("general_ward_test.json").toString();
        testWardSerialization(generalWard, jsonFilePath, "general");
    }

    @Test
    @DisplayName("Test ICU Ward serialization and deserialization")
    void testICUWardSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("icu_ward_test.json").toString();
        testWardSerialization(icuWard, jsonFilePath, "icu");
    }

    @Test
    @DisplayName("Test Labour Ward serialization and deserialization")
    void testLabourWardSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("labour_ward_test.json").toString();
        testWardSerialization(labourWard, jsonFilePath, "labour");
    }

    @Test
    @DisplayName("Test Day Surgery Ward serialization and deserialization")
    void testDaySurgeryWardSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("day_surgery_ward_test.json").toString();
        testWardSerialization(daySurgeryWard, jsonFilePath, "daySurgery");
    }

    /**
     * Helper method to test ward serialization and deserialization
     */
    private void testWardSerialization(Ward ward, String jsonFilePath, String expectedType) throws Exception {
        // Serialize to JSON string first
        String json = jsonHelper.toJson(ward);
        assertNotNull(json, "JSON string should not be null");
        assertTrue(json.contains(expectedType), "JSON should contain the correct ward type");

        // Save to JSON file
        jsonHelper.saveToJsonFile(ward, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Ward deserializedWard = jsonHelper.loadFromJsonFile(jsonFilePath, Ward.class);

        // Verify fields
        assertEquals(ward.getWardName(), deserializedWard.getWardName(), "Ward name should match");
        assertEquals(ward.getDailyRate(), deserializedWard.getDailyRate(), 0.001, "Daily rate should match");
        
        // Verify beds
        Map<Integer, Bed> originalBeds = ward.getBeds();
        Map<Integer, Bed> deserializedBeds = deserializedWard.getBeds();
        
        assertEquals(originalBeds.size(), deserializedBeds.size(), "Number of beds should match");
        
        // Verify each bed
        for (Map.Entry<Integer, Bed> entry : originalBeds.entrySet()) {
            int bedNumber = entry.getKey();
            Bed originalBed = entry.getValue();
            Bed deserializedBed = deserializedBeds.get(bedNumber);
            
            assertNotNull(deserializedBed, "Deserialized bed should not be null");
            assertEquals(originalBed.toString(), deserializedBed.toString(), 
                "Bed toString() should match for bed " + bedNumber);
        }
    }

    @Test
    @DisplayName("Test direct JSON string serialization and deserialization")
    void testJsonStringSerialization() {
        // Test with each ward type
        Ward[] wards = {generalWard, icuWard, labourWard, daySurgeryWard};
        String[] wardTypes = {"general", "icu", "labour", "daySurgery"};

        for (int i = 0; i < wards.length; i++) {
            Ward ward = wards[i];
            String expectedType = wardTypes[i];

            // Serialize to JSON string
            String json = jsonHelper.toJson(ward);
            
            // Basic JSON string checks
            assertNotNull(json, "JSON string should not be null");
            assertTrue(json.contains(expectedType), 
                "JSON should contain the correct ward type: " + expectedType);
            assertTrue(json.contains(ward.getWardName()), 
                "JSON should contain the ward name: " + ward.getWardName());

            // Deserialize back to object
            Ward deserializedWard = jsonHelper.fromJson(json, Ward.class);
            
            // Verify key properties
            assertEquals(ward.getWardName(), deserializedWard.getWardName(), 
                "Ward name should match for " + expectedType + " ward");
            assertEquals(ward.getDailyRate(), deserializedWard.getDailyRate(), 0.001, 
                "Daily rate should match for " + expectedType + " ward");
            assertEquals(ward.getBeds().size(), deserializedWard.getBeds().size(), 
                "Number of beds should match for " + expectedType + " ward");
        }
    }
}
