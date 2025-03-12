package claims;

import utils.CSVHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a hospital code with a unique identifier (hospital codes) and name.
 * This class loads hospital codes from a CSV file and provides
 * functionality to retrieve hospital codes by their unique identifier.
 */
public class HealthcareProvider {
    /** The unique hospital code. */
    private String code;
    /** The name of the hospital. */
    private String Name;

    /** A registry mapping hospital codes to their corresponding HospitalCode instances. */
    private static final Map<String, HealthcareProvider> HCODE_REGISTRY = new HashMap<>();

    // Static block to initialize the registry with data from a CSV file
    static {
        loadCodesFromCsv();
    }

    /**
     * Private constructor to initialize a HospitalCode instance.
     *
     * @param code The unique hospital code
     * @param Name The name of the hospital
     */
    private HealthcareProvider(String code, String Name) {
        this.code = code;
        this.Name = Name;
    }

    /**
     * Loads hospital codes from a CSV file and populates the registry.
     * The CSV file is expected to have at least two columns:
     * - Column 0: Hospital Code
     * - Column 1: Hospital Name
     */
    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("HospitalCodes.csv");

        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 2) {
                String code = record[0];
                String Name = record[1];

                HCODE_REGISTRY.put(code, new HealthcareProvider(code, Name));
            }
        }
    }

    /**
     * Retrieves a HospitalCode instance based on the provided hospital code.
     * If the code does not exist in the registry, an exception is thrown.
     *
     * @param code The unique hospital code
     * @return The corresponding HospitalCode instance
     * @throws IllegalArgumentException If the hospital code is not found
     */
    public static HealthcareProvider createFromCode(String code) {
        HealthcareProvider hospitalCode = HCODE_REGISTRY.get(code);
        if (hospitalCode == null) {
            throw new IllegalArgumentException(("Invalid hospital code: " + code));
        }
        return hospitalCode;
    }

    /**
     * Returns a string representation of the HospitalCode instance.
     *
     * @return A formatted string containing the hospital code and name
     */
    @Override
    public String toString() {
        return String.format("%s: %s", code, Name);
    }

    /**
     * Retrieves the hospital code.
     *
     * @return The hospital code
     */
    public Object getCode() { return code; }
}
