package claims;

import utils.CSVHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalCode {
    private String code;
    private String Name;

    private static final Map<String, HospitalCode> HCODE_REGISTRY = new HashMap<>();

    static {
        loadCodesFromCsv();
    }

    private HospitalCode(String code, String Name) {
        this.code = code;
        this.Name = Name;
    }

    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("HospitalCodes.csv");

        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 2) {
                String code = record[0];
                String Name = record[1];

                HCODE_REGISTRY.put(code, new HospitalCode(code, Name));
            }
        }
    }

    public static HospitalCode createFromCode(String code) {
        HospitalCode hospitalCode = HCODE_REGISTRY.get(code);
        if (hospitalCode == null) {
            throw new IllegalArgumentException(("Invalid hospital code: " + code));
        }
        return hospitalCode;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", code, Name);
    }

    public Object getCode() { return code; }
}
