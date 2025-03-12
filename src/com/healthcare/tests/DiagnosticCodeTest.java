package tests;

import medical.DiagnosticCode;

/**
 * A test class for the {@link DiagnosticCode} class.
 * This class verifies the functionality of diagnostic code creation, lookup, and various interface methods.
 */
public class DiagnosticCodeTest {
    /**
     * Main method to execute tests for {@link DiagnosticCode}.
     * Tests creating diagnostic codes, lookups, and various interface methods.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing DiagnosticCode functionality...\n");

            // Test 1: Create a diagnostic code
            System.out.println("Test 1 - Creating code A000:");
            DiagnosticCode code1 = DiagnosticCode.createFromCode("A000");
            System.out.println(code1);

            // Test 2: Test billing interface methods
            System.out.println("\nTest 2 - Testing billing interface methods:");
            System.out.println("Billing Item Code: " + code1.getBillingItemCode());
            System.out.println("Unsubsidised Charges: " + code1.getUnsubsidisedCharges());
            System.out.println("Bill Item Description: " + code1.getBillItemDescription());
            System.out.println("Bill Item Category: " + code1.getBillItemCategory());

            // Test 3: Test claimable interface methods
            System.out.println("\nTest 3 - Testing claimable interface methods:");
            System.out.println("Charges: " + code1.getCharges());
            System.out.println("Diagnosis Code: " + code1.getDiagnosisCode());
            System.out.println("Benefit Description (Inpatient): " + code1.getBenefitDescription(true));
            System.out.println("Benefit Description (Outpatient): " + code1.getBenefitDescription(false));

            // Test 4: Test benefit type resolution
            System.out.println("\nTest 4 - Testing benefit type resolution:");
            // Test maternity code (O-series)
            DiagnosticCode maternityCode = DiagnosticCode.createFromCode("O0001");
            System.out.println("Maternity code benefit type: " + maternityCode.resolveBenefitType(false));
            
            // Test cancer code (C-series)
            DiagnosticCode cancerCode = DiagnosticCode.createFromCode("C000");
            System.out.println("Cancer code benefit type: " + cancerCode.resolveBenefitType(false));
            
            // Test dental code (K-series)
            DiagnosticCode dentalCode = DiagnosticCode.createFromCode("K000");
            System.out.println("Dental code benefit type: " + dentalCode.resolveBenefitType(false));

            // Test 5: Direct description lookup
            System.out.println("\nTest 5 - Direct lookup for A150:");
            String description = DiagnosticCode.getDescriptionForCode("A150");
            System.out.println("A150 description: " + description);

            // Test 6: Test random code generation
            System.out.println("\nTest 6 - Testing random code generation:");
            DiagnosticCode randomCode1 = DiagnosticCode.getRandomCode();
            DiagnosticCode randomCode2 = DiagnosticCode.getRandomCode();
            System.out.println("Random Code 1: " + randomCode1);
            System.out.println("Random Code 2: " + randomCode2);

            // Test 7: Try invalid code
            System.out.println("\nTest 7 - Testing invalid code:");
            try {
                DiagnosticCode invalid = DiagnosticCode.createFromCode("XYZ");
                System.out.println("Error: Invalid code test failed!");
            } catch (IllegalArgumentException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

            // Test 8: Test inpatient vs outpatient benefit type resolution
            System.out.println("\nTest 8 - Testing inpatient vs outpatient benefit resolution:");
            DiagnosticCode generalCode = DiagnosticCode.createFromCode("A000");
            System.out.println("Inpatient benefit type: " + generalCode.resolveBenefitType(true));
            System.out.println("Outpatient benefit type: " + generalCode.resolveBenefitType(false));

            System.out.println("\nAll tests completed successfully!");

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
