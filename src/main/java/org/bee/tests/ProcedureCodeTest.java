package org.bee.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.policy.BenefitType;

/**
 * A test class for the {@link ProcedureCode} class.
 * This class verifies the functionality of procedure code creation, cost calculations, 
 * benefit type resolution, and various code classifications.
 */
public class ProcedureCodeTest {
    /**
     * Main method to execute tests for {@link ProcedureCode}.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing ProcedureCode functionality...\n");

            // Test 1: Create a procedure code
            System.out.println("Test 1 - Creating code 0016070:");
            ProcedureCode proc1 = ProcedureCode.createFromCode("0016070");
            System.out.println(proc1);

            // Test 2: Create another procedure code
            System.out.println("\nTest 2 - Creating code 0016071:");
            ProcedureCode proc2 = ProcedureCode.createFromCode("0016071");
            System.out.println(proc2);

            // Test 3: Calculate total cost
            System.out.println("\nTest 3 - Calculating total cost:");
            BigDecimal totalCost = proc1.getCharges().add(proc2.getCharges());
            System.out.printf("Total cost for both procedures: $%s%n",
                    totalCost.setScale(2, RoundingMode.HALF_UP));

            // Test 4: Try invalid code
            System.out.println("\nTest 4 - Testing invalid code:");
            try {
                ProcedureCode invalid = ProcedureCode.createFromCode("XYZ");
                System.out.println("Error: Should have thrown exception for invalid code");
            } catch (IllegalArgumentException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

            // Test 5: Test benefit type resolution for different codes
            System.out.println("\nTest 5 - Testing benefit type resolution:");
            testBenefitTypeResolution("1016070", true, "Maternity");  // Maternity
            testBenefitTypeResolution("3E033VZ", false, "Medication Administration");  // Medication admin
            testBenefitTypeResolution("5A1955Z", true, "Diagnostic Imaging");  // Imaging
            testBenefitTypeResolution("6A0Z0ZZ", false, "Oncology Treatments");  // Oncology
            testBenefitTypeResolution("0D11074", true, "Major Surgery");  // Major surgery (cardiovascular)
            
            // Test 6: Test benefit descriptions
            System.out.println("\nTest 6 - Testing benefit descriptions:");
            ProcedureCode surgicalProc = ProcedureCode.createFromCode("0D11074");
            System.out.println("Inpatient description: " + surgicalProc.getBenefitDescription(true));
            System.out.println("Outpatient description: " + surgicalProc.getBenefitDescription(false));

            // Test 7: Test procedure sections
            System.out.println("\nTest 7 - Testing procedure sections:");
            testProcedureSection("0016070", "Medical and Surgical");
            testProcedureSection("1016070", "Obstetrics");
            testProcedureSection("5A1955Z", "Imaging");
            testProcedureSection("7A03X0Z", "Radiation Oncology");

            // Test 8: Test random code generation
            System.out.println("\nTest 8 - Testing random code generation:");
            ProcedureCode randomCode1 = ProcedureCode.getRandomCode();
            ProcedureCode randomCode2 = ProcedureCode.getRandomCode();
            System.out.println("Random code 1: " + randomCode1);
            System.out.println("Random code 2: " + randomCode2);

            // Test 9: Test billing item methods
            System.out.println("\nTest 9 - Testing billing item methods:");
            ProcedureCode billingProc = ProcedureCode.createFromCode("0016070");
            System.out.println("Billing code: " + billingProc.getBillingItemCode());
            System.out.println("Category: " + billingProc.getBillItemCategory());
            System.out.println("Unsubsidised charges: " + billingProc.getUnsubsidisedCharges());

            System.out.println("\nAll tests completed successfully!");

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to test benefit type resolution
     */
    private static void testBenefitTypeResolution(String code, boolean isInpatient, String expectedType) {
        try {
            ProcedureCode proc = ProcedureCode.createFromCode(code);
            BenefitType benefit = proc.resolveBenefitType(isInpatient);
            System.out.printf("Code %s (%s): %s%n", 
                code, 
                isInpatient ? "inpatient" : "outpatient", 
                benefit);
        } catch (IllegalArgumentException e) {
            System.out.printf("Code %s: %s%n", code, e.getMessage());
        }
    }

    /**
     * Helper method to test procedure section identification
     */
    private static void testProcedureSection(String code, String expectedSection) {
        try {
            ProcedureCode proc = ProcedureCode.createFromCode(code);
            String section = proc.getProcedureSection();
            System.out.printf("Code %s: %s%n", code, section);
        } catch (IllegalArgumentException e) {
            System.out.printf("Code %s: %s%n", code, e.getMessage());
        }
    }
}
