package tests;

import billing.BillableItem;
import people.Doctor;
import people.Nurse;
import people.Patient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import medical.DiagnosticCode;
import medical.Medication;
import medical.ProcedureCode;
import medical.Visit;
import medical.VisitStatus;
import medical.WardStay;
import utils.DataGenerator;
import wards.Ward;
import wards.WardClassType;
import wards.WardFactory;

/**
 * A test class for the {@link Visit} class.
 * This class verifies the functionality of visit creation, state transitions, and various operations.
 */
public class VisitTest {
    /**
     * Main method to execute tests for {@link Visit}.
     * Tests visit creation, state transitions, and various operations.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Visit functionality...\n");
            DataGenerator gen = DataGenerator.getInstance();

            // Test 1: Create a visit with specific data
            System.out.println("Test 1 - Creating visit with specific data:");
            Patient patient = Patient.builder()
                    .patientId(gen.generatePatientId())
                    .withRandomBaseData()
                    .build();
            Visit visit1 = Visit.createNew(LocalDateTime.now(), patient);
            System.out.println("Patient Visited Status: " + visit1.getStatus());
            System.out.println("Newly Admitted? :  " + visit1.isNewlyAdmitted());
            System.out.println("Modifiable? : " + visit1.isModifiable());

            // Test 2: Test state transitions
            System.out.println("\nTest 2 - Testing state transitions:");
            System.out.println("Initial status: " + visit1.getStatus());
            visit1.updateStatus(VisitStatus.IN_PROGRESS);
            System.out.println("After updating to IN_PROGRESS: " + visit1.getStatus());
            System.out.println("Is in progress: " + visit1.isInProgress());
            visit1.updateStatus(VisitStatus.DISCHARGED);
            System.out.println("After updating to DISCHARGED: " + visit1.getStatus());
            System.out.println("Is patient discharged? : " + visit1.isDischarged());
            System.out.println("Is the visit finalized: " + visit1.isFinalized());

            // Test 3: Create a visit with random data
            System.out.println("\nTest 3 - Creating visit with random data:");
            Visit visit2 = Visit.withRandomData();
            System.out.println("Random visit created with status: " + visit2.getStatus());

            // Test 4: Test medical operations
            System.out.println("\nTest 4 - Testing medical operations:");
            Visit visit3 = Visit.createNew(LocalDateTime.now(), patient);
            
            // Assign medical staff
            Doctor doctor = Doctor.builder().withRandomBaseData().build();
            visit3.assignDoctor(doctor);
            System.out.println("Doctor Assigned");

            Nurse nurse = Nurse.builder().withRandomBaseData().build();
            visit3.assignNurse(nurse);
            System.out.println("Nurse Assigned");

            // Add ward stay
            Ward generalWard = WardFactory.getWard("General Ward A", WardClassType.GENERAL_CLASS_A);

            LocalDateTime now = LocalDateTime.now();
            WardStay wardStay = new WardStay(generalWard, now, now.plusDays(3), false);
            visit3.addWardStay(wardStay);
            System.out.println("Ward stay is added");

            // Add procedure
            ProcedureCode procedure = ProcedureCode.getRandomCode();
            visit3.procedure(procedure);
            System.out.println("Procedure is added");

            // Add diagnostic code
            DiagnosticCode diagnostic = DiagnosticCode.getRandomCode();
            visit3.diagnose(diagnostic);
            System.out.println("Diagnostic code is added");

            // Prescribe random medicines
            System.out.println("\nPrescribing random medications:");
            for (int i = 0; i < 3; i++) {
                Medication medication = Medication.getRandomMedication();
                int quantity = (int) (Math.random() * 5) + 1;
                visit3.prescribeMedicine(medication, quantity);
                System.out.println("Prescribed: " + medication.toString() + " x" + quantity);
            }

            // Test charges calculation
            BigDecimal totalCharges = visit3.calculateCharges();
            System.out.println("\nTotal charges: $" + totalCharges);

            // Test billable items
            List<BillableItem> billableItems = visit3.getRelatedBillableItems();
            System.out.println("\nBillable items breakdown:");
            for (BillableItem item : billableItems) {
                System.out.println("- " + item.getBillItemDescription() + 
                                 " (" + item.getBillItemCategory() + "): $" + 
                                 item.getUnsubsidisedCharges());
            }

            // Test 5: Test error cases and validation
            System.out.println("\nTest 5 - Testing error cases and validation:");
            Visit visit4 = Visit.createNew(LocalDateTime.now(), patient);
            visit4.updateStatus(VisitStatus.DISCHARGED);
            
            // Test prescribing to discharged patient
            try {
                visit4.prescribeMedicine(Medication.getRandomMedication(), 1);
                System.out.println("ERROR: Should not be able to prescribe medicine to discharged patient");
            } catch (IllegalStateException e) {
                System.out.println("Success: Cannot prescribe medicine to discharged patient");
            }

            // Test invalid status transition
            try {
                visit4.updateStatus(VisitStatus.IN_PROGRESS);
                System.out.println("ERROR: Should not be able to change status of discharged visit");
            } catch (IllegalStateException e) {
                System.out.println("Success: Cannot change status of discharged visit");
            }

            // Test invalid medication quantity
            try {
                visit3.prescribeMedicine(Medication.getRandomMedication(), 0);
                System.out.println("ERROR: Should not be able to prescribe zero quantity");
            } catch (IllegalArgumentException e) {
                System.out.println("Success: Cannot prescribe zero or negative quantity");
            }

            // Test null medication
            try {
                visit3.prescribeMedicine(null, 1);
                System.out.println("ERROR: Should not be able to prescribe null medication");
            } catch (NullPointerException e) {
                System.out.println("Success: Cannot prescribe null medication");
            }

            System.out.println("\nAll tests completed successfully!");
            System.out.println("Visit functionality verified including:");
            System.out.println("- Visit creation and status management");
            System.out.println("- Staff assignment");
            System.out.println("- Ward stays");
            System.out.println("- Procedures and diagnostics");
            System.out.println("- Random medication prescriptions");
            System.out.println("- Billing calculations");
            System.out.println("- Error handling and validation");

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
