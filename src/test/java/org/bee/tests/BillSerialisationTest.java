package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.billing.BillingItemLine;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.DiagnosticCode;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.print.Doc;

/**
 * Tests for serialization and deserialization of Bill objects using JSONHelper.
 * This test class verifies that Bill objects can be properly converted to JSON and back,
 * maintaining all their properties and relationships.
 */
public class BillSerialisationTest {
    
    private Bill testBill;
    private Patient testPatient;
    private Doctor testDoctor;

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

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
            .withRandomBaseData()
            .patientId(DataGenerator.generatePatientId())
            .build();

        testDoctor = Doctor.builder()
                .withRandomBaseData()
                .build();

        BillableItem testItem = DiagnosticCode.getRandomCode();

        Consultation consultation = Consultation.withRandomData(testPatient, testDoctor);

        testBill = new BillBuilder()
            .withPatient(testPatient)
                .withConsultation(consultation)
            .build();
        testBill.addLineItem(testItem, 2);
    }

    @Test
    @DisplayName("Test Bill serialization and deserialization")
    void testBillSerialization(@TempDir Path tempDir) throws Exception {
        String jsonFilePath = tempDir.resolve("bill_test.json").toString();
        
        // Serialize to JSON string first
        String json = JSONHelper.toJson(testBill);
        assertNotNull(json, "JSON string should not be null");
        assertTrue(json.contains(testBill.getPatient().getPatientId()), "JSON should contain patient ID");
        assertTrue(json.contains("diagnostic"), "JSON should contain billable item type");

        // Save to JSON file
        JSONHelper.saveToJsonFile(testBill, jsonFilePath);

        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists(), "JSON file should exist");
        assertTrue(jsonFile.length() > 0, "JSON file should not be empty");

        // Load from JSON file
        Bill deserializedBill = JSONHelper.loadFromJsonFile(jsonFilePath, Bill.class);

        // Verify fields
        assertEquals(getPrivateField(testBill, "billId", String.class), 
                    getPrivateField(deserializedBill, "billId", String.class), 
                    "Bill ID should match");
        assertEquals(testBill.getPatient().getPatientId(), 
                    deserializedBill.getPatient().getPatientId(), 
                    "Patient ID should match");
        assertEquals(testBill.getStatus(), deserializedBill.getStatus(), 
                    "Billing status should match");
        
        // Verify line items
        List<BillingItemLine> originalItems = getPrivateField(testBill, "lineItems", List.class);
        List<BillingItemLine> deserializedItems = getPrivateField(deserializedBill, "lineItems", List.class);
        
        assertEquals(originalItems.size(), deserializedItems.size(), 
                    "Number of line items should match");

        // Verify line item content
        BillingItemLine originalLine = originalItems.getFirst();
        BillingItemLine deserializedLine = deserializedItems.getFirst();
        assertEquals(originalLine.getBillEntry(), deserializedLine.getBillEntry(),
                    "Bill entry details should match");

        // Verify categorized charges
        Map<String, BigDecimal> originalCharges = getPrivateField(testBill, "categorizedCharges", Map.class);
        Map<String, BigDecimal> deserializedCharges = getPrivateField(deserializedBill, "categorizedCharges", Map.class);
        
        assertEquals(originalCharges.size(), deserializedCharges.size(), 
                    "Number of categorized charges should match");
        
        for (Map.Entry<String, BigDecimal> entry : originalCharges.entrySet()) {
            String category = entry.getKey();
            assertTrue(deserializedCharges.containsKey(category), 
                    "Deserialized charges should contain category: " + category);
            assertEquals(0, entry.getValue().compareTo(deserializedCharges.get(category)), 
                    "Charge amount should match for category: " + category);
        }

        assertEquals(testBill.getTotalAmount(), deserializedBill.getTotalAmount(), 
                    "Total amount should match");
    }

    @Test
    @DisplayName("Test direct JSON string serialization and deserialization")
    void testJsonStringSerialization() throws Exception {
        String json = JSONHelper.toJson(testBill);
        
        // Basic JSON string checks
        assertNotNull(json, "JSON string should not be null");
        assertTrue(json.contains("billId"), "JSON should contain billId field");
        assertTrue(json.contains("patient"), "JSON should contain patient field");
        assertTrue(json.contains("lineItems"), "JSON should contain lineItems field");
        assertTrue(json.contains("diagnostic"), "JSON should contain billable item type");
        
        // Deserialize back to object
        Bill deserializedBill = JSONHelper.fromJson(json, Bill.class);
        
        // Verify key properties
        assertEquals(getPrivateField(testBill, "billId", String.class), 
                    getPrivateField(deserializedBill, "billId", String.class), 
                    "Bill ID should match");
        assertEquals(testBill.getPatient().getPatientId(), 
                    deserializedBill.getPatient().getPatientId(), 
                    "Patient ID should match");
        assertEquals(testBill.getTotalAmount(), deserializedBill.getTotalAmount(), 
                    "Total amount should match");
        assertEquals(testBill.getStatus(), deserializedBill.getStatus(), 
                    "Status should match");
    }
}
