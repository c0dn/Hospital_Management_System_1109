package medical;

import utils.CSVHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosticCode {
    private String code;
    private String description;
    private static final Map<String, String> CODE_REGISTRY = new HashMap<>();

    // Static initializer to load codes from CSV
    static {
        loadCodesFromCsv();
    }

    public DiagnosticCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

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

    public static DiagnosticCode createFromCode(String code) {
        String description = CODE_REGISTRY.get(code);
        if (description == null) {
            throw new IllegalArgumentException("Invalid diagnostic code: " + code);
        }
        return new DiagnosticCode(code, description);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionForCode(String code) {
        return CODE_REGISTRY.get(code);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", code, description);
    }
}