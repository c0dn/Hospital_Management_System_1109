package tests;

import medical.ProcedureCode;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A test class for the {@link ProcedureCode} class.
 * This class verifies the functionality of procedure code creation, cost calculations, and handling invalid codes.
 */
public class ProcedureCodeTest {
    /**
     * Main method to execute tests for {@link ProcedureCode}.
     * It tests creating procedure codes, calculating total costs,
     * and handling invalid or duplicate codes.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing ProcedureCode functionality...\n");

            // Test 1: Create a procedure code
            System.out.println("Test 1 - Creating code 0011M:");
            ProcedureCode proc1 = ProcedureCode.createFromCode("0011M");
            System.out.println(proc1);

            // Test 2: Create another procedure code
            System.out.println("\nTest 2 - Creating code 0019M:");
            ProcedureCode proc2 = ProcedureCode.createFromCode("0019M");
            System.out.println(proc2);

            // Test 3: Calculate total cost
            System.out.println("\nTest 3 - Calculating total cost:");
            BigDecimal totalCost = proc1.getPrice().add(proc2.getPrice());
            System.out.printf("Total cost for both procedures: $%s%n",
                    totalCost.setScale(2, RoundingMode.HALF_UP));


            // Test 4: Try invalid code
            System.out.println("\nTest 4 - Testing invalid code:");
            try {
                ProcedureCode invalid = ProcedureCode.createFromCode("XYZ");
            } catch (IllegalArgumentException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

            // Test 5: Create multiple instances of same code
            System.out.println("\nTest 5 - Creating multiple instances of same code:");
            ProcedureCode proc3 = ProcedureCode.createFromCode("0011M");
            System.out.println("First instance: " + proc1);
            System.out.println("Second instance: " + proc3);
            System.out.println("Are they the same code? " + proc1.getCode().equals(proc3.getCode()));

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}