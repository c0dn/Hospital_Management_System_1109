package org.bee.tests;

import java.math.BigDecimal;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link BillBuilder}.
 * Verifies the functionality of building bills with visits and consultations.
 */
public class BillBuilderTest {
    
    @Test
    void testVisitBillCreation() {
        Patient patient = Patient.builder()
                .withRandomData("P1001")
                .build();
        
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);

        Bill bill = new BillBuilder()
                .withPatientId(patient.getPatientId())
                .withVisit(visit)
                .build();

        verifyBill(bill);
    }

    @Test
    void testConsultationBillCreation() {
        Patient patient = Patient.builder()
                .withRandomData("P1002")
                .build();
        
        Consultation consultation = Consultation.withRandomData();

        Bill bill = new BillBuilder()
                .withPatientId(patient.getPatientId())
                .withConsultation(consultation)
                .build();

        verifyBill(bill);
    }

    @Test
    void testCombinedBillCreation() {
        Patient patient = Patient.builder()
                .withRandomData("P1003")
                .build();
        
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);
        
        Consultation consultation = Consultation.withRandomData();

        Bill bill = new BillBuilder()
                .withPatientId(patient.getPatientId())
                .withVisit(visit)
                .withConsultation(consultation)
                .build();

        verifyBill(bill);
    }

    @Test
    void testNullValidation() {
        Patient patient = Patient.builder()
                .withRandomData("P1004")
                .build();

        BillBuilder builder = new BillBuilder().withPatientId(patient.getPatientId());

        // Test null visit
        assertThrows(NullPointerException.class, () -> builder.withVisit(null),
                "Should throw NullPointerException for null visit");

        // Test null consultation
        assertThrows(NullPointerException.class, () -> builder.withConsultation(null),
                "Should throw NullPointerException for null consultation");
    }

    private void verifyBill(Bill bill) {
        assertNotNull(bill, "Bill should not be null");
        assertNotNull(bill.getPatient(), "Patient should not be null");
        assertTrue(bill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0,
                "Bill should have positive total amount");
    }
}
