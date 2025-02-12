package tests;

import medical.DiagnosticCode;
import policy.BenefitType;

/**
 * A test class for the {@link DiagnosticCode} class.
 * This class verifies the functionality of diagnostic code creation, lookup, and critical illness classification.
 */
public class DiagnosticCodeTest {
    /**
     * Main method to execute tests for {@link DiagnosticCode}.
     * Tests creating diagnostic codes, lookups, critical illness classifications, and handling invalid codes.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing DiagnosticCode functionality...\n");

            // Test 1: Create a diagnostic code (known to be NONE)
            System.out.println("Test 1 - Creating code A000 (should be NONE):");
            DiagnosticCode code1 = DiagnosticCode.createFromCode("A000");
            System.out.println(code1);
            System.out.println("Critical Illness Classification: " + code1.resolveBenefitType(true));

            // Test 2: Create code with BACTERIAL_MENINGITIS classification
            System.out.println("\nTest 2 - Creating code A0101 (should be BACTERIAL_MENINGITIS):");
            DiagnosticCode code2 = DiagnosticCode.createFromCode("A0101");
            System.out.println(code2);
            System.out.println("Critical Illness Classification: " + code2.resolveBenefitType(false));

            // Test 3: Create code with MAJOR_CANCERS classification
            System.out.println("\nTest 3 - Creating code A1781 (should be MAJOR_CANCERS):");
            DiagnosticCode code3 = DiagnosticCode.createFromCode("A1781");
            System.out.println(code3);

            // Test 4: Direct description lookup
            System.out.println("\nTest 4 - Direct lookup for A150:");
            String description = DiagnosticCode.getDescriptionForCode("A150");
            System.out.println("A150: " + description);

            // Test 5: Verify critical illness classification type
            System.out.println("\nTest 5 - Verifying classification type:");
            DiagnosticCode meningitisCode = DiagnosticCode.createFromCode("A0101");
            if (meningitisCode.resolveBenefitType(true) == BenefitType.CRITICAL_ILLNESS) {
                System.out.println("Classification verification passed!");
            } else {
                System.out.println("Classification verification failed!");
            }

            // Test 6: Try invalid code
            System.out.println("\nTest 6 - Testing invalid code:");
            try {
                DiagnosticCode invalid = DiagnosticCode.createFromCode("XYZ");
            } catch (IllegalArgumentException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}