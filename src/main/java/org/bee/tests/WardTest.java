package org.bee.tests;

import org.bee.humans.Patient;
import org.bee.wards.Bed;
import org.bee.wards.Ward;
import org.bee.wards.WardClassType;
import org.bee.wards.WardFactory;

/**
 * A test class for the Ward system.
 * This class verifies the functionality of ward creation, bed management, and patient assignment.
 */
public class WardTest {
    /**
     * Main method to execute tests for Ward system.
     * It tests creating different types of wards, bed allocation, and patient assignment.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Ward System functionality...\n");

            // Test 1: Create a General Ward
            System.out.println("Test 1 - Creating General Ward Class C:");
            Ward generalWard = WardFactory.getWard("General Ward", WardClassType.GENERAL_CLASS_C);
            System.out.println("Ward Name: " + generalWard.getWardName());
            System.out.println("Daily Rate: $" + generalWard.getDailyRate());
            System.out.println("Number of Beds: " + generalWard.getBeds().size());

            // Test 2: Create an ICU Ward
            System.out.println("\nTest 2 - Creating ICU Ward:");
            Ward icuWard = WardFactory.getWard("ICU", WardClassType.ICU);
            System.out.println("Ward Name: " + icuWard.getWardName());
            System.out.println("Daily Rate: $" + icuWard.getDailyRate());
            System.out.println("Number of Beds: " + icuWard.getBeds().size());

            // Test 3: Create a Day Surgery Ward
            System.out.println("\nTest 3 - Creating Day Surgery Seater Ward:");
            Ward daySurgeryWard = WardFactory.getWard("Day Surgery", WardClassType.DAYSURGERY_CLASS_SEATER);
            System.out.println("Ward Name: " + daySurgeryWard.getWardName());
            System.out.println("Daily Rate: $" + daySurgeryWard.getDailyRate());
            System.out.println("Number of Beds: " + daySurgeryWard.getBeds().size());

            // Test 4: Assign a patient to a bed
            System.out.println("\nTest 4 - Assigning patient to bed:");
            Bed bed = generalWard.getBeds().get(1);
            Patient patient = Patient.builder().withRandomData("P1002").build();
            bed.assignPatient(patient);
            System.out.println(bed);

            // Test 5: Try invalid ward class
            System.out.println("\nTest 5 - Testing invalid ward class:");
            try {
                Ward invalidWard = WardFactory.getWard("Test Ward", null);
            } catch (IllegalArgumentException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
