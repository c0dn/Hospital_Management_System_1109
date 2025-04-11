package org.bee.tests;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.bee.hms.billing.BillableItem;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.Medication;
import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.medical.WardStay;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;
import org.bee.utils.DataGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link Visit} class.
 * This class verifies the functionality of visit creation, state transitions, and various operations.
 */
public class VisitTest {
    private Patient testPatient;
    private Visit testVisit;
    private Doctor testDoctor;
    private Nurse testNurse;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .patientId(DataGenerator.generatePatientId())
                .withRandomBaseData()
                .build();
        testVisit = Visit.createNew(LocalDateTime.now(), testPatient);
        testDoctor = Doctor.builder().withRandomBaseData().build();
        testNurse = Nurse.builder().withRandomBaseData().build();
    }

    @Test
    void testVisitCreation() throws Exception {
        assertNotNull(testVisit, "Visit should not be null");
        assertEquals(VisitStatus.ADMITTED, testVisit.getStatus(), 
                "Initial status should be ADMITTED");
        assertTrue(testVisit.isNewlyAdmitted(), "Visit should be marked as newly admitted");
        assertTrue(testVisit.isModifiable(), "Visit should be modifiable");
        Patient visitPatient = getPrivateField(testVisit, "patient", Patient.class);
        assertEquals(testPatient.getPatientId(), visitPatient.getPatientId(), "Patient should match");
    }

    @Test
    void testStateTransitions() {
        Visit visit = Visit.withRandomData();
        assertEquals(VisitStatus.ADMITTED, visit.getStatus(), "Initial status should be ADMITTED");
        
        visit.updateStatus(VisitStatus.IN_PROGRESS);
        assertEquals(VisitStatus.IN_PROGRESS, visit.getStatus(), 
                "Status should be IN_PROGRESS");
        assertTrue(visit.isInProgress(), "Visit should be in progress");
        
        visit.updateStatus(VisitStatus.DISCHARGED);
        assertEquals(VisitStatus.DISCHARGED, visit.getStatus(), 
                "Status should be DISCHARGED");
        assertTrue(visit.isDischarged(), "Visit should be discharged");
        assertTrue(visit.isFinalized(), "Visit should be finalized");
    }

    @Test
    void testMedicalOperations() {
        // Test staff assignment
        testVisit.assignDoctor(testDoctor);
        testVisit.assignNurse(testNurse);
        
        // Test ward stay
        Ward generalWard = WardFactory.getWard("General Ward A", WardClassType.GENERAL_CLASS_A);
        LocalDateTime now = LocalDateTime.now();
        WardStay wardStay = new WardStay(generalWard, now, now.plusDays(3));
        testVisit.addWardStay(wardStay);
        
        // Test procedure and diagnosis
        ProcedureCode procedure = ProcedureCode.getRandomCode();
        testVisit.procedure(procedure);
        
        DiagnosticCode diagnostic = DiagnosticCode.getRandomCode();
        testVisit.diagnose(diagnostic);
        
        // Verify all additions were successful
        List<BillableItem> items = testVisit.getRelatedBillableItems();
        assertNotNull(items, "Billable items should not be null");
        assertFalse(items.isEmpty(), "Billable items should not be empty");
    }

    @Test
    void testMedicationPrescription() {
        // Test multiple prescriptions
        for (int i = 0; i < 3; i++) {
            Medication medication = Medication.getRandomMedication();
            int quantity = i + 1;
            testVisit.prescribeMedicine(medication, quantity);
        }
        
        BigDecimal totalCharges = testVisit.calculateCharges();
        assertTrue(totalCharges.compareTo(BigDecimal.ZERO) > 0, 
                "Total charges should be positive");
        
        List<BillableItem> billableItems = testVisit.getRelatedBillableItems();
        assertFalse(billableItems.isEmpty(), "Should have billable items");
        
        // Verify each billable item
        for (BillableItem item : billableItems) {
            assertNotNull(item.getBillItemDescription(), "Item description should not be null");
            assertNotNull(item.getBillItemCategory(), "Item category should not be null");
            assertTrue(item.getUnsubsidisedCharges().compareTo(BigDecimal.ZERO) > 0,
                    "Item charges should be positive");
        }
    }

    @Test
    void testErrorCases() {
        Visit dischargedVisit = Visit.createNew(LocalDateTime.now(), testPatient);
        dischargedVisit.updateStatus(VisitStatus.DISCHARGED);
        
        // Test prescribing to discharged patient
        assertThrows(IllegalStateException.class, () -> 
                dischargedVisit.prescribeMedicine(Medication.getRandomMedication(), 1),
                "Should not be able to prescribe medicine to discharged patient");
        
        // Test invalid status transition
        assertThrows(IllegalStateException.class, () -> 
                dischargedVisit.updateStatus(VisitStatus.IN_PROGRESS),
                "Should not be able to change status of discharged visit");
        
        // Test invalid medication quantity
        assertThrows(IllegalArgumentException.class, () -> 
                testVisit.prescribeMedicine(Medication.getRandomMedication(), 0),
                "Should not be able to prescribe zero quantity");
        
        // Test null medication
        assertThrows(IllegalArgumentException.class, () -> 
                testVisit.prescribeMedicine(null, 1),
                "Should not be able to prescribe null medication");
    }

    @Test
    void testChargesCalculation() {
        // Add various billable items
        testVisit.procedure(ProcedureCode.getRandomCode());
        testVisit.diagnose(DiagnosticCode.getRandomCode());
        testVisit.prescribeMedicine(Medication.getRandomMedication(), 2);
        
        BigDecimal totalCharges = testVisit.calculateCharges();
        assertTrue(totalCharges.compareTo(BigDecimal.ZERO) > 0,
                "Total charges should be positive");
        
        List<BillableItem> items = testVisit.getRelatedBillableItems();
        BigDecimal sumOfItems = items.stream()
                .map(BillableItem::getUnsubsidisedCharges)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        assertEquals(totalCharges, sumOfItems,
                "Total charges should equal sum of individual items");
    }

    @Test
    void testWardStayManagement() {
        Ward generalWard = WardFactory.getWard("General Ward A", WardClassType.GENERAL_CLASS_A);
        LocalDateTime now = LocalDateTime.now();
        
        // Add multiple ward stays
        WardStay stay1 = new WardStay(generalWard, now, now.plusDays(2));
        WardStay stay2 = new WardStay(generalWard, now.plusDays(2), now.plusDays(4));
        
        testVisit.addWardStay(stay1);
        testVisit.addWardStay(stay2);
        
        List<BillableItem> items = testVisit.getRelatedBillableItems();
        long wardStayCount = items.stream()
                .filter(item -> item.getBillItemCategory().equals("WARD"))
                .count();
        
        assertEquals(2, wardStayCount, "Should have two ward stay billable items");
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
}
