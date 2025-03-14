package org.bee.tests;

import java.math.BigDecimal;
import java.util.List;

import org.bee.hms.billing.BillableItem;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.ConsultationType;

/**
 * A test class for the {@link Consultation} class.
 * This class verifies the functionality of consultation creation and its methods.
 */
public class ConsultationTest {
    /**
     * Main method to execute tests for {@link Consultation}.
     * It tests creating consultations and verifies their functionality.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Consultation functionality...\n");

            // Test 1: Create multiple random consultations and verify their data
            System.out.println("Test 1 - Creating multiple random consultations:");
            for (int i = 0; i < 3; i++) {
                Consultation consultation = Consultation.withRandomData();
                System.out.println("\nConsultation " + (i + 1) + ":");
                System.out.println("Category: " + consultation.getCategory());
                
                // Test billable items
                List<BillableItem> items = consultation.getRelatedBillableItems();
                System.out.println("Number of billable items: " + items.size());
                
                // Test total charges
                BigDecimal totalCharges = consultation.calculateCharges();
                System.out.println("Total charges: $" + totalCharges);
                
                // Verify that total charges are positive
                if (totalCharges.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new AssertionError("Total charges should be positive");
                }
            }

            // Test 2: Verify consultation categories
            System.out.println("\nTest 2 - Verifying consultation categories:");
            for (ConsultationType type : ConsultationType.values()) {
                Consultation consultation = Consultation.withRandomData();
                String expectedCategory = switch (type) {
                    case EMERGENCY -> "EMERGENCY_CONSULTATION";
                    case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
                    case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
                    case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
                };
                
                // Force the consultation type
                java.lang.reflect.Field typeField = Consultation.class.getDeclaredField("type");
                typeField.setAccessible(true);
                typeField.set(consultation, type);
                
                String actualCategory = consultation.getCategory();
                if (!actualCategory.equals(expectedCategory)) {
                    throw new AssertionError(
                        "Category mismatch for " + type + 
                        ". Expected: " + expectedCategory + 
                        ", Got: " + actualCategory
                    );
                }
                System.out.println("Verified category for " + type + ": " + actualCategory);
            }

            // Test 3: Verify billable items composition
            System.out.println("\nTest 3 - Verifying billable items composition:");
            Consultation consultation = Consultation.withRandomData();
            List<BillableItem> billableItems = consultation.getRelatedBillableItems();
            
            System.out.println("Billable items breakdown:");
            billableItems.forEach(item -> 
                System.out.println("- " + item.getClass().getSimpleName() + 
                    ": $" + item.getUnsubsidisedCharges())
            );

            // Verify total matches sum of individual items plus consultation fee
            BigDecimal itemsTotal = billableItems.stream()
                .map(BillableItem::getUnsubsidisedCharges)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            java.lang.reflect.Field feeField = Consultation.class.getDeclaredField("consultationFee");
            feeField.setAccessible(true);
            BigDecimal consultationFee = (BigDecimal) feeField.get(consultation);
            
            BigDecimal expectedTotal = itemsTotal.add(consultationFee);
            BigDecimal actualTotal = consultation.calculateCharges();
            
            if (!expectedTotal.equals(actualTotal)) {
                throw new AssertionError(
                    "Total charges mismatch. Expected: $" + expectedTotal + 
                    ", Got: $" + actualTotal
                );
            }
            System.out.println("Verified total charges calculation is correct: $" + actualTotal);

            System.out.println("\nAll tests passed successfully!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
