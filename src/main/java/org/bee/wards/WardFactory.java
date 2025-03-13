package org.bee.wards;

/**
 * A factory class for creating different types of hospital wards.
 * Determines the appropriate ward type based on the ward class.
 */
public class WardFactory {
    /**
     * Creates and returns a specific ward instance based on the given ward class type.
     *
     * @param name         The name of the ward.
     * @param wardClassType The classification of the ward.
     * @return An instance of a specific ward type.
     * @throws IllegalArgumentException If the ward class type is invalid.
     */
    public static Ward getWard(String name, WardClassType wardClassType) {
        if (wardClassType == null) {
            throw new IllegalArgumentException("Ward class cannot be null");
        }

        String[] parts = wardClassType.name().split("_CLASS_|_");
        String wardType = parts[0]; // First part is always the ward type
        int numberOfBeds = getNumberOfBeds(parts);

        // Create appropriate ward type
        return switch (wardType) {
            case "LABOUR" -> new LabourWard(name, wardClassType, numberOfBeds);
            case "ICU" -> new ICUWard(name, wardClassType, numberOfBeds);
            case "DAYSURGERY" -> new DaySurgeryWard(name, wardClassType, numberOfBeds);
            case "GENERAL" -> new GeneralWard(name, wardClassType, numberOfBeds);
            default -> throw new IllegalArgumentException("Invalid ward type: " + wardType);
        };
    }

    /**
     * Determines the number of beds in a ward based on its classification.
     *
     * @param parts The parts of the ward classification type name.
     * @return The number of beds in the ward.
     * @throws IllegalArgumentException If the class type is invalid.
     */
    private static int getNumberOfBeds(String[] parts) {
        String classType = parts.length > 1 ? parts[1] : null; // Second part is the class type (if it exists)

        // Determine the number of beds based on class type
        return switch (classType) {
            case "A", "SINGLE" -> 1;
            case "B1" -> 4;
            case "B2" -> 6;
            case "C" -> 8;
            case "SEATER" -> 10;
            case "COHORT" -> 5;
            case null -> 1;
            default -> throw new IllegalArgumentException("Invalid class type: " + classType);
        };
    }
}
