package medical;

import billing.BillableItem;
import policy.CriticalIllnessType;
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
 * <br><br> It maintains a static registry of codes, descriptions, and critical illness classifications loaded from a CSV file using {@link CSVHelper}.
 */
public class DiagnosticCode implements BillableItem {
    private String code;
    private String description;
    private BigDecimal cost;
    private CriticalIllnessType criticalIllnessClassification;

    private static final Map<String, DiagnosticCode> CODE_REGISTRY = new HashMap<>();

    static {
        loadCodesFromCsv();
    }

    private DiagnosticCode(String code, String description, CriticalIllnessType criticalIllnessClassification, BigDecimal cost) {
        this.code = code;
        this.description = description;
        this.criticalIllnessClassification = criticalIllnessClassification;
        this.cost = cost;
    }

    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("classified_icd_codes.csv");

        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 3) {
                String code = record[0];
                String description = record[1].replaceAll("\"", "");
                CriticalIllnessType illnessType = "NONE".equals(record[2]) ? null : CriticalIllnessType.valueOf(record[2]);
                CODE_REGISTRY.put(code, new DiagnosticCode(code, description, illnessType, generateRandomPrice()));
            }
        }
    }

    public static DiagnosticCode createFromCode(String code) {
        DiagnosticCode diagnosticCode = CODE_REGISTRY.get(code);
        if (diagnosticCode == null) {
            throw new IllegalArgumentException("Invalid diagnostic code: " + code);
        }
        return new DiagnosticCode(
                diagnosticCode.code,
                diagnosticCode.description,
                diagnosticCode.criticalIllnessClassification,
                generateRandomPrice()
        );
    }

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

    public String getBillItemDescription() {
        return description;
    }

    @Override
    public String getBillItemCategory() {
        return "DIAGNOSIS";
    }

    public static String getDescriptionForCode(String code) {
        DiagnosticCode diagnosticCode = CODE_REGISTRY.get(code);
        return diagnosticCode != null ? diagnosticCode.description : null;
    }

    public CriticalIllnessType getCriticalIllnessClassification() {
        return criticalIllnessClassification;
    }

    @Override
    public String toString() {
        return String.format("%s: %s [%s]", code, description, criticalIllnessClassification != null ? criticalIllnessClassification : "NONE");
    }
}