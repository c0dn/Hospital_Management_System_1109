package org.bee.hms.medical;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bee.utils.CSVHelper;
import org.bee.utils.JSONSerializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a medication with associated details loaded from a CSV file.
 * <p>
 *     This class maintains details about medications, including:
 * </p>
 * <ul>
 *     <li>Drug code, name, category</li>
 *     <li>Standard dosage and unit form</li>
 *     <li>Price per unit and manufacturer details</li>
 * </ul>
 */
public class Medication implements JSONSerializable {
    /** The unique drug code identifying this medication. */
    @JsonProperty("code")
    protected String drugCode;
    /** The name of the medication. */
    @JsonIgnore
    protected String name;
    /** The category to which this medication belongs (eg. Antibiotics, Painkillers). */
    @JsonIgnore
    protected String category;
    /** The standard dosage for this medication. */
    @JsonIgnore
    protected String standardDosage;
    /** The unit form of the medication (eg. tablet, capsule, injection). */
    @JsonIgnore
    protected String unitForm;
    /** The price per unit of the medication. */
    @JsonIgnore
    protected BigDecimal pricePerUnit;
    /** A description of the unit of measurement (eg. per table, per bottle). */
    @JsonIgnore
    protected String unitDescription;
    /** The manufacturer of the medication. */
    @JsonIgnore
    protected String manufacturer;

    /**
     * A registry storing all available medications, mapped by drug code.
     */
    private static final Map<String, Medication> DRUG_REGISTRY = new HashMap<>();

    static {
        loadDrugsFromCsv();
    }

    /**
     * Constructs a new Medication instance.
     *
     * @param drugCode The unique identifier for the drug.
     * @param name The name of the medication.
     * @param category The category of the medication.
     * @param standardDosage The standard dosage of the medication.
     * @param unitForm The unit form of the medication.
     * @param pricePerUnit The price per unit of the medication.
     * @param unitDescription A description of the unit.
     * @param manufacturer The manufacturer of the medication.
     */
    private Medication(String drugCode, String name, String category,
                      String standardDosage, String unitForm, BigDecimal pricePerUnit,
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
        String databaseDir = System.getProperty("database.dir", "database");
        List<String[]> records = CSVHelper.readCSV(databaseDir + "/drugs.csv");

        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 8) {
                String drugCode = record[0];
                String name = record[1];
                String category = record[2];
                String standardDosage = record[3];
                String unitForm = record[4];
                BigDecimal pricePerUnit = new BigDecimal(record[5]).setScale(2, RoundingMode.HALF_UP);
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
     * Creates a {@link Medication} from the given code and sets its cost.
     * This is used for deserialization.
     */
    @JsonCreator
    public static Medication createFromCodeAndCost(
            @JsonProperty("code") String code) {
        return createFromCode(code);
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
                .filter(med -> med.category.equalsIgnoreCase(category))
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
                .map(med -> med.category)
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
    public BigDecimal calculateCost(int quantity) {
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Prints the drug's information in a formatted manner.
     */
    public void printDrugInformation() {
        System.out.format("Drug Code: %s%n", drugCode);
        System.out.format("%s - %s (%s)%n", manufacturer, name, category);
        System.out.format("Dosage: %s%n", standardDosage);
        System.out.format("Price: $%s / %s%n",
                pricePerUnit.setScale(2, RoundingMode.HALF_UP), unitForm);
    }

    /**
     * Returns a string representation of the medication.
     *
     * @return A formatted string with the medication details.
     */
    @JsonIgnore
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

    /**
     * Gets a random medication from the registry
     * @return A randomly selected Medication
     */
    public static Medication getRandomMedication() {
        String[] codes = DRUG_REGISTRY.keySet().toArray(new String[0]);
        int randomIndex = (int) (Math.random() * codes.length);
        return createFromCode(codes[randomIndex]);
    }

    /**
     * Gets the unique drug code for this medication.
     * 
     * @return The drug code
     */
    @JsonIgnore
    public String getDrugCode() {
        return drugCode;
    }


}
