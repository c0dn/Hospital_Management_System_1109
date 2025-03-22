package org.bee.tests;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import org.bee.hms.billing.BillableItem;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.ConsultationType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class ConsultationTest {
    
    @Test
    void testRandomConsultations() {
        // Create multiple random consultations and verify their data
        for (int i = 0; i < 3; i++) {
            Consultation consultation = Consultation.withRandomData();
            
            assertNotNull(consultation.getCategory(), "Consultation category should not be null");
            
            // Test billable items
            List<BillableItem> items = consultation.getRelatedBillableItems();
            assertNotNull(items, "Billable items list should not be null");
            assertFalse(items.isEmpty(), "Billable items list should not be empty");
            
            // Test total charges
            BigDecimal totalCharges = consultation.calculateCharges();
            assertNotNull(totalCharges, "Total charges should not be null");
            assertTrue(totalCharges.compareTo(BigDecimal.ZERO) > 0, 
                    "Total charges should be positive");
        }
    }

    @ParameterizedTest
    @EnumSource(ConsultationType.class)
    void testConsultationCategories(ConsultationType type) throws Exception {
        Consultation consultation = Consultation.withRandomData();
        
        // Use reflection to set the consultation type
        setPrivateField(consultation, "type", type);
        
        String expectedCategory = switch (type) {
            case EMERGENCY -> "EMERGENCY_CONSULTATION";
            case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
            case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
            case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
            case NEW_CONSULTATION -> "NEW_CONSULTATION";
            case ROUTINE_CHECKUP -> "ROUTINE_CHECKUP_CONSULTATION";
        };

        
        assertEquals(expectedCategory, consultation.getCategory(),
                "Category should match expected value for " + type);
    }

    @Test
    void testBillableItemsComposition() throws Exception {
        Consultation consultation = Consultation.withRandomData();
        List<BillableItem> billableItems = consultation.getRelatedBillableItems();
        
        assertNotNull(billableItems, "Billable items should not be null");
        assertFalse(billableItems.isEmpty(), "Billable items should not be empty");

        // Calculate total of individual items
        BigDecimal itemsTotal = billableItems.stream()
                .map(BillableItem::getUnsubsidisedCharges)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get consultation fee using reflection
        BigDecimal consultationFee = getPrivateField(consultation, "consultationFee", BigDecimal.class);
        assertNotNull(consultationFee, "Consultation fee should not be null");
        assertTrue(consultationFee.compareTo(BigDecimal.ZERO) > 0, 
                "Consultation fee should be positive");
        
        // Verify total calculation
        BigDecimal expectedTotal = itemsTotal.add(consultationFee);
        BigDecimal actualTotal = consultation.calculateCharges();
        
        assertEquals(expectedTotal, actualTotal, 
                "Total charges should equal sum of items plus consultation fee");
    }

    @Test
    void testConsultationChargesBreakdown() {
        Consultation consultation = Consultation.withRandomData();
        List<BillableItem> items = consultation.getRelatedBillableItems();
        
        // Verify each billable item
        for (BillableItem item : items) {
            assertNotNull(item, "Billable item should not be null");
            assertTrue(item.getUnsubsidisedCharges().compareTo(BigDecimal.ZERO) > 0,
                    "Item charges should be positive");
        }
        
        // Verify total charges include all items
        BigDecimal total = consultation.calculateCharges();
        assertNotNull(total, "Total charges should not be null");
        assertTrue(total.compareTo(BigDecimal.ZERO) > 0,
                "Total charges should be positive");
    }

    /**
     * Utility method to access private fields using reflection.
     * 
     * @param <T> The type of the field
     * @param object The object containing the field
     * @param fieldName The name of the field to access
     * @param fieldType The class of the field type
     * @return The value of the field
     * @throws Exception If reflection fails
     */
    private <T> T getPrivateField(Object object, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> currentClass = object.getClass();
        Field field = null;
        
        // Search through the class hierarchy
        while (currentClass != null) {
            try {
                field = currentClass.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        
        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        }
        
        field.setAccessible(true);
        return fieldType.cast(field.get(object));
    }

    /**
     * Utility method to set private fields using reflection.
     * 
     * @param object The object containing the field
     * @param fieldName The name of the field to set
     * @param value The value to set
     * @throws Exception If reflection fails
     */
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Class<?> currentClass = object.getClass();
        Field field = null;
        
        // Search through the class hierarchy
        while (currentClass != null) {
            try {
                field = currentClass.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        
        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        }
        
        field.setAccessible(true);
        field.set(object, value);
    }
}
