package tests;

import medical.DiagnosticCode;

/**
 * A test class for the {@link DiagnosticCode} class.
 * This class verifies the functionality of diagnostic code creation and lookup.
 */
public class DiagnosticCodeTest {
    /**
     * Main method to execute tests for {@link DiagnosticCode}.
     * It tests creating diagnostic codes, direct lookups, and handling invalid codes.
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

            // Test 2: Create another code
            System.out.println("\nTest 2 - Creating code A071:");
            DiagnosticCode code2 = DiagnosticCode.createFromCode("A071");
            System.out.println(code2);

            // Test 3: Direct description lookup
            System.out.println("\nTest 3 - Direct lookup for A150:");
            String description = DiagnosticCode.getDescriptionForCode("A150");
            System.out.println("A150: " + description);

            // Test 4: Try invalid code
            System.out.println("\nTest 4 - Testing invalid code:");
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
