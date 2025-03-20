package org.bee.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.humans.Patient;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.EmergencyVisit;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;

public class BillBuilderTest {
    private DataGenerator gen;
    
    @BeforeEach
    void setUp() {
        gen = DataGenerator.getInstance();
    }
    
    @Test
    void testConsultationBill() {
        // Create a bill from a regular consultation
        Consultation consultation = Consultation.withRandomData();
        Bill consultationBill = new BillBuilder<Visit>()
                .withPatientId(gen.generatePatientId())
                .withConsultation(consultation)
                .build();
        
        assertNotNull(consultationBill, "Bill should not be null");
        assertNotNull(consultationBill.getPatient(), "Patient should not be null");
        assertTrue(consultationBill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, 
                "Bill total should be greater than zero");

        // Check consultation-specific charges
        BigDecimal consultationCharges = consultationBill.getTotalByCategory("CONSULTATION");
        assertTrue(consultationCharges.compareTo(BigDecimal.ZERO) > 0, 
                "Consultation charges should be present");
    }
    
    @Test
    void testRegularVisitBill() {
        // Create a bill from a regular visit
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED); // Finalize visit before billing
        
        Bill visitBill = new BillBuilder<Visit>()
                .withPatientId(gen.generatePatientId())
                .withVisit(visit)
                .build();
        
        assertNotNull(visitBill, "Bill should not be null");
        assertNotNull(visitBill.getPatient(), "Patient should not be null");
        assertTrue(visitBill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, 
                "Bill total should be greater than zero");

        // Verify categories have appropriate charges
        String[] expectedCategories = {"CONSULTATION", "MEDICATION", "PROCEDURE"};
        for (String category : expectedCategories) {
            BigDecimal categoryAmount = visitBill.getTotalByCategory(category);
            assertNotNull(categoryAmount, "Category amount should not be null for " + category);
        }
    }
    
    @Test
    void testEmergencyVisitBill() {
        // Create a bill from an emergency visit
        EmergencyVisit emergencyVisit = EmergencyVisit.withRandomData();
        emergencyVisit.updateStatus(VisitStatus.DISCHARGED);
        
        Bill emergencyBill = new BillBuilder<EmergencyVisit>()
                .withPatientId(gen.generatePatientId())
                .withVisit(emergencyVisit)
                .build();
        
        assertNotNull(emergencyBill, "Bill should not be null");
        assertNotNull(emergencyBill.getPatient(), "Patient should not be null");
        assertTrue(emergencyBill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, 
                "Bill total should be greater than zero");
        
        // Check emergency-specific charges
        BigDecimal emergencyCharges = emergencyBill.getTotalByCategory("EMERGENCY");
        assertTrue(emergencyCharges.compareTo(BigDecimal.ZERO) > 0, 
                "Emergency charges should be present");
    }
    
    @Test
    void testInsuredBill() {
        // Create a bill with insurance policy
        PrivateProvider provider = new PrivateProvider();
        Patient patient = Patient.builder().withRandomData(gen.generatePatientId()).build();
        Optional<InsurancePolicy> policy = provider.getPatientPolicy(patient);
        
        assertTrue(policy.isPresent(), "Insurance policy should be present");
        
        Bill insuredBill = new BillBuilder<>()
                .withPatientId("P1004")
                .withConsultation(Consultation.withRandomData())
                .withInsurancePolicy(policy.get())
                .build();
        
        assertNotNull(insuredBill, "Insured bill should not be null");
        assertNotNull(insuredBill.getPatient(), "Patient should not be null");
        assertTrue(insuredBill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, 
                "Bill total should be greater than zero");
        
        // Access the insurance policy field using reflection
        try {
            InsurancePolicy attachedPolicy = getPrivateField(insuredBill, "insurancePolicy", InsurancePolicy.class);
            assertNotNull(attachedPolicy, "Insurance policy should be attached to bill");
        } catch (Exception e) {
            fail("Failed to access private field: " + e.getMessage());
        }
    }
    
    @Test
    void testMultipleConsultationsBill() {
        // Create a bill with multiple consultations
        BillBuilder<Visit> multiConsultBuilder = new BillBuilder<>()
                .withPatientId(gen.generatePatientId());
        
        // Add multiple consultations
        for (int i = 0; i < 3; i++) {
            multiConsultBuilder.withConsultation(Consultation.withRandomData());
        }
        
        Bill multiConsultBill = multiConsultBuilder.build();
        
        assertNotNull(multiConsultBill, "Multiple consultations bill should not be null");
        assertNotNull(multiConsultBill.getPatient(), "Patient should not be null");
        assertTrue(multiConsultBill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, 
                "Bill total should be greater than zero");
        
        // Check for consultation charges
        BigDecimal consultationCharges = multiConsultBill.getTotalByCategory("CONSULTATION");
        assertTrue(consultationCharges.compareTo(BigDecimal.ZERO) > 0, 
                "Consultation charges should be present");

        // The total should reflect multiple consultations
        BigDecimal totalAmount = multiConsultBill.getTotalAmount();
        BigDecimal singleConsultAmount = new BillBuilder<Visit>()
                .withPatientId(gen.generatePatientId())
                .withConsultation(Consultation.withRandomData())
                .build()
                .getTotalAmount();
                
        assertTrue(totalAmount.compareTo(singleConsultAmount) > 0,
                "Multiple consultations should result in higher total");
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
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return fieldType.cast(field.get(object));
    }
}
