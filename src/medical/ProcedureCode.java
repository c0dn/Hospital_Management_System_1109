package medical;

import utils.CSVHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a medical procedure code with associated description, category, and price.
 * This class maintains a static registry of codes loaded from a CSV file using {@link CSVHelper}
 */
public class ProcedureCode {
    private String code;
    private String description;
    private String category;
    private double price;

    private static final Map<String, ProcedureCode> CODE_REGISTRY = new HashMap<>();

    // Static initializer to load codes from CSV
    static {
        loadCodesFromCsv();
    }

    /**
     * Constructs a ProcedureCode instance.
     * @param code The procedure code
     * @param description The description of the procedure
     * @param category The category of the procedure
     * @param price The price of the procedure
     */
    public ProcedureCode(String code, String description, String category, double price) {
        this.code = code;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    /**
     * Loads procedure codes and their details from a CSV file into the registry.
     */
    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("procedure_code.csv");

        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 4) {
                String code = record[0];
                String description = record[1].replaceAll("\"", "");
                String category = record[2];
                double price = Double.parseDouble(record[3]);

                CODE_REGISTRY.put(code, new ProcedureCode(code, description, category, price));
            }
        }
    }

    /**
     * Creates a new ProcedureCode instance from an existing code.
     *
     * @param code The procedure code
     * @return A new ProcedureCode instance
     * @throws IllegalArgumentException if the code is not found in the registry
     */
    public static ProcedureCode createFromCode(String code) {
        ProcedureCode procedureCode = CODE_REGISTRY.get(code);
        if (procedureCode == null) {
            throw new IllegalArgumentException("Invalid procedure code: " + code);
        }
        return procedureCode;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Returns a string representation of the procedure code.
     *
     * @return A formatted string containing code, description, category, and price
     */
    @Override
    public String toString() {
        return String.format("%s: %s (Category: %s, Price: $%.2f)",
                code, description, category, price);
    }

    public Object getCode() {
        return code;
    }
}
