package wardsAmelia;

public class WardFactory {
    public static Ward getWard(String name, WardClassType wardClassType) {
        if (wardClassType == null) {
            throw new IllegalArgumentException("Ward class cannot be null");
        }

        String wardType = wardClassType.name().split("_")[0];
        String classType = wardClassType.name().split("_CLASS_")[1];

        // Determine number of beds based on class type
        int numberOfBeds = switch (classType) {
            case "A" -> 1;
            case "B1" -> 4;
            case "B2" -> 6;
            case "C" -> 8;
            default -> throw new IllegalArgumentException("Invalid class type: " + classType);
        };

        // Create appropriate ward type
        return switch (wardType) {
            case "LABOUR" -> new LabourWard(name, wardClassType, numberOfBeds);
            case "ICU" -> new ICUWard(name, wardClassType, numberOfBeds);
            case "DAY" -> new DaySurgeryWard(name, wardClassType, numberOfBeds);
            case "GENERAL" -> new GeneralWard(name, wardClassType, numberOfBeds);
            default -> throw new IllegalArgumentException("Invalid ward type: " + wardType);
        };
    }
}