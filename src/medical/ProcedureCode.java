package medical;

import utils.CSVHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a medical procedure code with associated description, category, and price.
 * This class maintains a static registry of codes loaded from a CSV file using {@link CSVHelper}
 */
public class ProcedureCode {
    /** The unique procedure code identifier. */
    private String code;
    /** A description of the procedure. */
    private String description;
    /** The category to which this procedure belongs. */
    private String category;
    /** The cost of the procedure. */
    private BigDecimal price;

    /**
     * A registry storing all available procedure codes, mapped by code identifier.
     */
    private static final Map<String, ProcedureCode> CODE_REGISTRY = new HashMap<>();

    // Static initializer to load codes from CSV

    /**
     * Static initializer that loads procedure codes from a CSV file when the class is first loaaded.
     */
    static {
        loadCodesFromCsv();
    }

    /**
     * Constructs a new {@code ProcedureCode} instance.
     *
     * @param code The unique identifier for the procedure.
     * @param description A brief description of the procedure.
     * @param category The category of the procedure.
     * @param price The cost of the procedure.
     */
    private ProcedureCode(String code, String description, String category, BigDecimal price) {
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
                BigDecimal price = new BigDecimal(record[3]).setScale(2, RoundingMode.HALF_UP);

                CODE_REGISTRY.put(code, new ProcedureCode(code, description, category, price));
            }
        }
    }

    /**
     * Creates a new ProcedureCode instance from an existing code.
     *
     * @param code The procedure code.
     * @return A new ProcedureCode instance.
     * @throws IllegalArgumentException if the code is not found in the registry.
     */
    public static ProcedureCode createFromCode(String code) {
        ProcedureCode procedureCode = CODE_REGISTRY.get(code);
        if (procedureCode == null) {
            throw new IllegalArgumentException("Invalid procedure code: " + code);
        }
        return procedureCode;
    }

    /**
     * Retrieves the price of the procedure.
     *
     * @return The cost of the procedure as a {@link BigDecimal}.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Returns a string representation of the procedure code.
     *
     * @return A formatted string containing code, description, category, and price.
     */
    @Override
    public String toString() {
        return String.format("%s: %s (Category: %s, Price: $%s)",
                code, description, category, price.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Retrieves the procedure code identifier.
     *
     * @return The procedure code as a {@code String}.
     */
    public Object getCode() {
        return code;
    }
}
