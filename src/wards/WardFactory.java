package wards;

public class WardFactory {
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