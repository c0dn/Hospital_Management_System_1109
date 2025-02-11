package medical;

import billing.BillableItem;
import utils.CSVHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a diagnostic code based on the ICD-10 classification.
 * This class allows retrieving descriptions for diagnostic codes from a predefined CSV file.
 *
 * <br><br> It maintains a static registry of codes and descriptions, which are loaded at runtime from a CSV file using {@link CSVHelper}
 */
public class DiagnosticCode implements BillableItem {
    private String code;
    private String description;
    private BigDecimal cost;
    private static final Map<String, String> CODE_REGISTRY = new HashMap<>();

    // Static initializer to load codes from CSV
    static {
        loadCodesFromCsv();
    }


    private DiagnosticCode(String code, String description, BigDecimal cost) {
        this.code = code;
        this.description = description;
        this.cost = cost;
    }

    /**
     * Loads diagnostic codes and descriptions from a CSV file into the registry.
     *
     * <br><br> The CSV file should have at least 2 columns:
     * <br>- Column 1: Diagnostic code
     * <br>- Column 2: Description
     *
     * <br><br> This method removes any quotes from the descriptions before storing them.
     */
    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("icd-10-codes.csv");

        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 2) {
                // Remove quotes from description if present
                String description = record[1].replaceAll("\"", "");
                CODE_REGISTRY.put(record[0], description);
            }
        }
    }

    /**
     * Creates a new {@new DiagnosticCode} instance from and existing code.
     *
     * @param code The diagnostic code.
     * @return A new DiagnosticCode instance corresponding to the given code.
     * @throws IllegalArgumentException if the code is not found in the registry.
     */
    public static DiagnosticCode createFromCode(String code) {
        String description = CODE_REGISTRY.get(code);
        if (description == null) {
            throw new IllegalArgumentException("Invalid diagnostic code: " + code);
        }
        return new DiagnosticCode(code, description, generateRandomPrice());
    }

    /**
     * Generate a random price for a diagnostic code.
     *
     * <br><br> The price is randomly generated between 100 and 300, then rounded to 2 decimal places.
     *
     * @return A randomly generated BigDecimal price.
     */
    private static BigDecimal generateRandomPrice() {
        Random random = new Random();
        double randomValue = 100 + (random.nextDouble() * 200);
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
    }

    
    public String getBillingItemCode() {
        return String.format("DIAG-%s", code);
    }

    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return cost;
    }

    /**
     * Get the description of the diagnostic code.
     *
     * @return The description associated with this diagnostic code.
     */
    public String getBillItemDescription() {
        return description;
    }

    @Override
    public String getBillItemCategory() {
        return "DIAGNOSIS";
    }

    /**
     * Retrieves the description for a given diagnostic code form the registry.
     *
     * @param code The diagnostic code.
     * @return The description of the code, or {@code null} if the code is not found.
     */

    public static String getDescriptionForCode(String code) {
        return CODE_REGISTRY.get(code);
    }

    /**
     * Returns a string representation of the diagnostic code.
     *
     * @return A formatted string in the form of "code: description".
     */
    @Override
    public String toString() {
        return String.format("%s: %s", code, description);
    }
}