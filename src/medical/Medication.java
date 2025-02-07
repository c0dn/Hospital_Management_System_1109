package medical;

import utils.CSVHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a medication with associated details loaded from a CSV file.
 * This class maintains a static registry of medications using {@link CSVHelper}
 */
public class Medication {
    private String drugCode;
    private String name;
    private String category;
    private String standardDosage;
    private String unitForm;
    private double pricePerUnit;
    private String unitDescription;
    private String manufacturer;

    private static final Map<String, Medication> DRUG_REGISTRY = new HashMap<>();

    // Static initializer to load medications from CSV
    static {
        loadDrugsFromCsv();
    }

    /**
     * Constructs a Medication instance.
     */
    public Medication(String drugCode, String name, String category,
                      String standardDosage, String unitForm, double pricePerUnit,
                      String unitDescription, String manufacturer) {
        this.drugCode = drugCode;
        this.name = name;
        this.category = category;
        this.standardDosage = standardDosage;
        this.unitForm = unitForm;
        this.pricePerUnit = pricePerUnit;
        this.unitDescription = unitDescription;
        this.manufacturer = manufacturer;
    }

    /**
     * Loads medications and their details from a CSV file into the registry.
     */
    private static void loadDrugsFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("drugs.csv");

        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 8) {
                String drugCode = record[0];
                String name = record[1];
                String category = record[2];
                String standardDosage = record[3];
                String unitForm = record[4];
                double pricePerUnit = Double.parseDouble(record[5]);
                String unitDescription = record[6];
                String manufacturer = record[7];

                DRUG_REGISTRY.put(drugCode, new Medication(
                        drugCode, name, category, standardDosage, unitForm,
                        pricePerUnit, unitDescription, manufacturer));
            }
        }
    }

    /**
     * Creates a new Medication instance from an existing drug code.
     *
     * @param drugCode The drug code to look up
     * @return A new Medication instance
     * @throws IllegalArgumentException if the drug code is not found
     */
    public static Medication createFromCode(String drugCode) {
        Medication medication = DRUG_REGISTRY.get(drugCode);
        if (medication == null) {
            throw new IllegalArgumentException("Invalid drug code: " + drugCode);
        }
        return medication;
    }


    /**
     * Retrieves a list of medications for a specific category.
     *
     * @param category  The category to filter by
     * @param limit     Maximum number of medications to return (0 or negative for no limit)
     * @param randomize Whether to randomize the results
     * @return List of medications in the specified category
     */
    public static List<Medication> getMedicationsByCategory(String category, int limit, boolean randomize) {
        List<Medication> medications = DRUG_REGISTRY.values().stream()
                .filter(med -> med.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        if (randomize) {
            Collections.shuffle(medications);
        }

        if (limit > 0 && limit < medications.size()) {
            return medications.subList(0, limit);
        }

        return medications;
    }

    /**
     * Gets all available medication categories.
     *
     * @return List of unique medication categories
     */
    public static List<String> getAllCategories() {
        return DRUG_REGISTRY.values().stream()
                .map(Medication::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    /**
     * Calculates the total cost for a specific quantity of medication.
     *
     * @param quantity The number of units needed
     * @return The total cost
     */
    public double calculateCost(int quantity) {
        return pricePerUnit * quantity;
    }


    // Getters
    public String getDrugCode() {
        return drugCode;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getBillingUnit() {
        return unitForm;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Returns a string representation of the medication.
     */
    @Override
    public String toString() {
        return String.format("""
                        %s: %s %s (%s)
                        Category: %s
                        Manufacturer: %s
                        Price: $%.2f (%s)""",
                drugCode, name, standardDosage, unitForm,
                category, manufacturer,
                pricePerUnit, unitDescription);
    }

}