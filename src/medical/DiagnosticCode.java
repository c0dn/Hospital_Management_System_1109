package medical;

import billing.BillableItem;
import policy.BenefitType;
import policy.ClaimableItem;
import utils.CSVHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DiagnosticCode implements BillableItem, ClaimableItem {
    private String categoryCode;
    private String diagnosisCode;
    private String fullCode;
    private String abbreviatedDescription;
    private String fullDescription;
    private String categoryTitle;
    private BigDecimal cost; // We'll keep this for billing purposes

    private static final Map<String, DiagnosticCode> CODE_REGISTRY = new HashMap<>();
    private static final BigDecimal DEFAULT_COST = new BigDecimal("100.00"); // Default cost if needed

    static {
        loadCodesFromCsv();
    }

    private DiagnosticCode(String categoryCode, String diagnosisCode, String fullCode,
                           String abbreviatedDescription, String fullDescription,
                           String categoryTitle) {
        this.categoryCode = categoryCode;
        this.diagnosisCode = diagnosisCode;
        this.fullCode = fullCode;
        this.abbreviatedDescription = abbreviatedDescription;
        this.fullDescription = fullDescription;
        this.categoryTitle = categoryTitle;
        this.cost = DEFAULT_COST; // Setting default cost
    }

    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("icd-10-cm.csv");

        for (int i = 1; i < records.size(); i++) { // Skip header row
            String[] record = records.get(i);
            if (record.length >= 6) {
                String categoryCode = record[0];
                String diagnosisCode = record[1];
                String fullCode = record[2];
                String abbreviatedDesc = record[3].replaceAll("\"", "");
                String fullDesc = record[4].replaceAll("\"", "");
                String categoryTitle = record[5].replaceAll("\"", "");

                DiagnosticCode diagnosticCode = new DiagnosticCode(
                        categoryCode,
                        diagnosisCode,
                        fullCode,
                        abbreviatedDesc,
                        fullDesc,
                        categoryTitle
                );

                CODE_REGISTRY.put(fullCode, diagnosticCode);
            }
        }
    }

    public static DiagnosticCode createFromCode(String code) {
        DiagnosticCode diagnosticCode = CODE_REGISTRY.get(code);
        if (diagnosticCode == null) {
            throw new IllegalArgumentException("Invalid diagnostic code: " + code);
        }
        return new DiagnosticCode(
                diagnosticCode.categoryCode,
                diagnosticCode.diagnosisCode,
                diagnosticCode.fullCode,
                diagnosticCode.abbreviatedDescription,
                diagnosticCode.fullDescription,
                diagnosticCode.categoryTitle
        );
    }

    @Override
    public String getBillingItemCode() {
        return String.format("DIAG-%s", fullCode);
    }

    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return cost;
    }

    @Override
    public String getBillItemDescription() {
        return abbreviatedDescription;
    }

    @Override
    public String getBillItemCategory() {
        return "DIAGNOSIS";
    }

    public static String getDescriptionForCode(String code) {
        DiagnosticCode diagnosticCode = CODE_REGISTRY.get(code);
        return diagnosticCode != null ? diagnosticCode.fullDescription : null;
    }

    @Override
    public String toString() {
        return String.format("%s: %s [%s]", fullCode, abbreviatedDescription, cost);
    }

    @Override
    public BigDecimal getCharges() {
        return cost;
    }


    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        if (categoryCode == null || categoryCode.isEmpty()) {
            return defaultFallback(isInpatient);
        }

        // Ordered list of benefit mappings (higher priority first)
        List<BenefitMapping> benefitMappings = List.of(
                new BenefitMapping("^O.*", BenefitType.MATERNITY),  // Pregnancy/childbirth
                new BenefitMapping("^C\\d{2}.*", BenefitType.CRITICAL_ILLNESS),  // Neoplasms
                new BenefitMapping("^I(2[0-5]|3|4[0-1]).*", BenefitType.CRITICAL_ILLNESS),  // Heart diseases
                new BenefitMapping("^(G30|E10|E11).*", BenefitType.CRITICAL_ILLNESS),  // Neuro/Diabetes
                new BenefitMapping("^[ST].*", BenefitType.ACCIDENT),  // Injury/poisoning
                new BenefitMapping("^K0[0-5].*", BenefitType.DENTAL),  // Dental disorders
                new BenefitMapping("^Z74.*", BenefitType.PREVENTIVE_CARE),  // Need for assistance
                new BenefitMapping("^(E66|I10|J45|N18).*", BenefitType.CHRONIC_CONDITIONS), // Chronic
                new BenefitMapping("^(J06|N30|R05).*", BenefitType.ACUTE_CONDITIONS),  // Acute
                new BenefitMapping("^Z5[1-3].*", BenefitType.PREVENTIVE_CARE) // Health screenings
        );

        for (BenefitMapping mapping : benefitMappings) {
            if (mapping.matches(categoryCode)) {
                return mapping.benefitType();
            }
        }

        return isInpatient ? BenefitType.HOSPITALIZATION
                : BenefitType.OUTPATIENT_TREATMENTS;
    }

    private BenefitType defaultFallback(boolean isInpatient) {
        return isInpatient ? BenefitType.HOSPITALIZATION
                : BenefitType.OUTPATIENT_TREATMENTS;
    }


    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return fullDescription;
    }

    @Override
    public String getDiagnosisCode() {
        return this.fullCode;
    }
}

record BenefitMapping(String pattern, BenefitType benefitType) {
    public boolean matches(String code) {
        return Pattern.matches(pattern, code);
    }
}