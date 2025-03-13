package org.bee.hms.tests;

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
import org.bee.hms.utils.DataGenerator;

/**
 * A test class for the {@link BillBuilder} class.
 * This class verifies the functionality of bill creation using different types of visits and consultations.
 */
public class BillBuilderTest {
    /**
     * Main method to execute tests for {@link BillBuilder}.
     * It tests creating bills using different types of medical services.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing BillBuilder functionality...\n");
            DataGenerator gen = DataGenerator.getInstance();

            // Test 1: Create a bill from a regular consultation
            System.out.println("Test 1 - Creating bill from regular consultation:");
            Consultation consultation = Consultation.withRandomData();
            Bill consultationBill = new BillBuilder<Visit>()
                    .withPatientId(gen.generatePatientId())
                    .withConsultation(consultation)
                    .build();
            
            verifyBill(consultationBill, "Consultation Bill");

            // Test 2: Create a bill from a regular visit
            System.out.println("\nTest 2 - Creating bill from regular visit:");
            Visit visit = Visit.withRandomData();
            visit.updateStatus(VisitStatus.DISCHARGED); // Finalize visit before billing
            
            Bill visitBill = new BillBuilder<Visit>()
                    .withPatientId(gen.generatePatientId())
                    .withVisit(visit)
                    .build();
            
            verifyBill(visitBill, "Visit Bill");

            // Test 3: Create a bill from an emergency visit
            System.out.println("\nTest 3 - Creating bill from emergency visit:");
            EmergencyVisit emergencyVisit = EmergencyVisit.withRandomData();
            emergencyVisit.updateStatus(VisitStatus.DISCHARGED); // Finalize emergency visit before billing
            
            Bill emergencyBill = new BillBuilder<EmergencyVisit>()
                    .withPatientId(gen.generatePatientId())
                    .withVisit(emergencyVisit)
                    .build();
            
            verifyBill(emergencyBill, "Emergency Visit Bill");

            // Test 4: Create a bill with insurance policy
            System.out.println("\nTest 4 - Creating bill with insurance policy:");
            PrivateProvider provider = new PrivateProvider();
            Patient patient = Patient.builder().withRandomData(gen.generatePatientId()).build();
            Optional<InsurancePolicy> policy = provider.getPatientPolicy(patient);
            Bill insuredBill = new BillBuilder<>()
                    .withPatientId("P1004")
                    .withConsultation(Consultation.withRandomData())
                    .withInsurancePolicy(policy.orElseThrow())
                    .build();
            
            verifyBill(insuredBill, "Insured Bill");

            // Test 5: Create a bill with multiple consultations
            System.out.println("\nTest 5 - Creating bill with multiple consultations:");
            BillBuilder<Visit> multiConsultBuilder = new BillBuilder<>()
                    .withPatientId(gen.generatePatientId());
            
            // Add multiple consultations
            for (int i = 0; i < 3; i++) {
                multiConsultBuilder.withConsultation(Consultation.withRandomData());
            }
            
            Bill multiConsultBill = multiConsultBuilder.build();
            verifyBill(multiConsultBill, "Multiple Consultations Bill");

            System.out.println("\nAll tests passed successfully!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to verify the contents and validity of a generated bill.
     *
     * @param bill The bill to verify
     * @param billType Description of the bill type for logging
     */
    private static void verifyBill(Bill bill, String billType) {
        System.out.println("\nVerifying " + billType + ":");
        
        // Verify patient information
        Patient patient = bill.getPatient();
        if (patient == null) {
            throw new AssertionError("Patient should not be null");
        }
        System.out.println("Patient ID: " + patient.getPatientId());

        // Verify bill total
        BigDecimal totalAmount = bill.getTotalAmount();
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssertionError("Bill total should be greater than zero");
        }
        System.out.println("Total Amount: $" + totalAmount);

        // Print categorized charges
        System.out.println("Charges by category:");
        // Common categories to check
        String[] categories = {
            "CONSULTATION",
            "MEDICATION",
            "PROCEDURE",
            "WARD_STAY",
            "EMERGENCY"
        };
        
        for (String category : categories) {
            BigDecimal categoryAmount = bill.getTotalByCategory(category);
            if (categoryAmount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("- " + category + ": $" + categoryAmount);
            }
        }
    }
}
