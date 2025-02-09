package wardsAmelia;

public class WardFactory {
    public static Ward getWard(String name, WardClassType wardClassType) {
        if (wardClassType == null) {
            throw new IllegalArgumentException("Ward class cannot be null");
        }

        String[] parts = wardClassType.name().split("_CLASS_|_");
        String wardType = parts[0]; // First part is always the ward type
        String classType = parts.length > 1 ? parts[1] : ""; // Second part is the class type (if it exists)

        // Determine number of beds based on class type
        int numberOfBeds = switch (classType) {
            case "A" -> 1;
            case "B1" -> 4;
            case "B2" -> 6;
            case "C" -> 8;
            case "SEATER" -> 10;
            case "COHORT" -> 5;
            case "SINGLE" -> 1;
            case "" -> 1;
            default -> throw new IllegalArgumentException("Invalid class type: " + classType);
        };

        // Create appropriate ward type
        return switch (wardType) {
            case "LABOUR" -> new LabourWard(name, wardClassType, numberOfBeds);
            case "ICU" -> new ICUWard(name, wardClassType, numberOfBeds);
            case "DAYSURGERY" -> new DaySurgeryWard(name, wardClassType, numberOfBeds);
            case "GENERAL" -> new GeneralWard(name, wardClassType, numberOfBeds);
            default -> throw new IllegalArgumentException("Invalid ward type: " + wardType);
        };
    }
}