package tests;

import claims.HospitalCode;

public class HospitalCodeTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing HospitalCode functionality...\n");

            // Test 1: Create a hospital code
            System.out.println("Test 1 - Creating code WZ:");
            HospitalCode code = HospitalCode.createFromCode("WZ");
            System.out.println(code);

            // Test 2: Create another hospital code
            System.out.println("\nTest 2 - Creating code 0M:");
            HospitalCode code2 = HospitalCode.createFromCode("0M");
            System.out.println(code2);

            // Test 3: Try invalid code
            System.out.println("\nTest 4 - Testing invalid code:");
            try {
                HospitalCode invalid = HospitalCode.createFromCode("]]]");
            } catch (IllegalArgumentException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

            // Test 4: Create multiple instances of same code
            System.out.println("\nTest 5 - Creating multiple instances of same code:");
            HospitalCode proc3 = HospitalCode.createFromCode("TZ");
            System.out.println("First instance: " + code);
            System.out.println("Second instance: " + code2);
            System.out.println("Are they the same code? " + code.getCode().equals(code2.getCode()));

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
