package medical;

import billing.BillableItem;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import policy.BenefitType;
import policy.ClaimableItem;
import utils.CSVHelper;

/**
 * Represents a diagnostic code, typically used for medical diagnosis.
 * <p>
 * This class provides functionality to map a code to its relevant description, category,
 * and cost. It also determines the type of benefit (hospitalization, maternity, dental, etc.)
 * based on the category code.
 * </p>
 */
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

    /**
     * Private constructor to initialize a {@link DiagnosticCode}.
     *
     * @param categoryCode          Category code of the diagnosis.
     * @param diagnosisCode         The diagnosis code.
     * @param fullCode              The full code (ICD-10 CM code).
     * @param abbreviatedDescription Abbreviated description of the diagnosis.
     * @param fullDescription       Full description of the diagnosis.
     * @param categoryTitle         The category title for the diagnosis.
     */
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

    /**
     * Creates a {@link DiagnosticCode} from the given code.
     * If the code does not exist, an exception will be thrown.
     *
     * @param code The diagnostic code to create from.
     * @return The corresponding {@link DiagnosticCode} object.
     * @throws IllegalArgumentException if the code is invalid.
     */
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

    /**
     * Gets a random diagnostic code from the registry
     * @return A randomly selected DiagnosticCode
     */
    public static DiagnosticCode getRandomCode() {
        String[] codes = CODE_REGISTRY.keySet().toArray(new String[0]);
        int randomIndex = (int) (Math.random() * codes.length);
        return createFromCode(codes[randomIndex]);
    }
}

record BenefitMapping(String pattern, BenefitType benefitType) {
    public boolean matches(String code) {
        return Pattern.matches(pattern, code);
    }
}
