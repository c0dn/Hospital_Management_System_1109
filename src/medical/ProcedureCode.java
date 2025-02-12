package medical;

import billing.BillableItem;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import policy.BenefitType;
import policy.ClaimableItem;
import utils.CSVHelper;

/**
 * Represents a medical procedure code with associated description and price.
 * Procedure codes can be loaded from a CSV file and used for billing purposes.
 */
public class ProcedureCode implements BillableItem, ClaimableItem {
    private String code;
    private String description;
    private BigDecimal price;
    private static final Map<String, ProcedureCode> CODE_REGISTRY = new HashMap<>();
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("1000.00");

    static {
        loadCodesFromCsv();
    }

    private ProcedureCode(String code, String description) {
        this.code = code;
        this.description = description;
        this.price = DEFAULT_PRICE;
    }

    private static void loadCodesFromCsv() {
        CSVHelper csvHelper = CSVHelper.getInstance();
        List<String[]> records = csvHelper.readCSV("icd-10-pcs.csv");

        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 2) {
                String code = record[0];
                String description = record[1].replaceAll("\"", "");
                CODE_REGISTRY.put(code, new ProcedureCode(code, description));
            }
        }
    }

    public static ProcedureCode createFromCode(String code) {
        ProcedureCode procedureCode = CODE_REGISTRY.get(code);
        if (procedureCode == null) {
            throw new IllegalArgumentException("Invalid procedure code: " + code);
        }
        return new ProcedureCode(
                procedureCode.code,
                procedureCode.description
        );
    }

    @Override
    public String getBillingItemCode() {
        return String.format("PROC-%s", code);
    }

    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return price;
    }

    @Override
    public String getBillItemDescription() {
        return description;
    }

    @Override
    public String getBillItemCategory() {
        return "PROCEDURE";
    }

    @Override
    public String toString() {
        return String.format("%s: %s [%s]", code, description, price);
    }

    @Override
    public BigDecimal getCharges() {
        return price;
    }

    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        if (code == null || code.length() < 2) return defaultFallback(isInpatient);

        char section = code.charAt(0);
        char bodySystem = code.charAt(1);
        String fullCode = code.substring(0, 3);

        // Handle special procedure categories
        if (section == '1') return BenefitType.MATERNITY;
        if (section == '3' && fullCode.equals("3E0")) return BenefitType.MEDICATION_ADMIN; // Chemo infusions
        if (section == '5') return BenefitType.DIAGNOSTIC_IMAGING;
        if (section == '6' || section == '7') return BenefitType.ONCOLOGY_TREATMENTS;

        // Enhanced surgical categorization
        if (section == '0') {
            return switch(bodySystem) {
                case 'D' -> BenefitType.MAJOR_SURGERY;       // Cardiovascular
                case 'F' -> BenefitType.MAJOR_SURGERY;       // Neurological
                case 'G' -> BenefitType.MINOR_SURGERY;       // Skin/Muscle
                case 'H' -> BenefitType.MINOR_SURGERY;       // Orthopedic
                default -> isInpatient ? BenefitType.HOSPITALIZATION
                        : BenefitType.MINOR_SURGERY;
            };
        }

        return defaultFallback(isInpatient);
    }

    private BenefitType defaultFallback(boolean isInpatient) {
        return isInpatient ? BenefitType.HOSPITALIZATION
                : BenefitType.OUTPATIENT_TREATMENTS;
    }



    @Override
    public String getBenefitDescription(boolean isInpatient) {
        StringBuilder description = new StringBuilder();
        description.append(isInpatient ? "Inpatient" : "Outpatient")
                .append(" Surgical Procedure: ")
                .append(this.description);

        // Only get body system if it's a medical/surgical procedure (starts with '0')
        if (code != null && code.length() >= 2 && code.charAt(0) == '0') {
            String bodySystem = getBodySystem(code.charAt(1));
            if (bodySystem != null) {
                description.append(" (").append(bodySystem).append(")");
            }
        }

        return description.toString();
    }

    private String getBodySystem(char secondChar) {
        return switch (secondChar) {
            case '0' -> "Central Nervous System";
            case '1' -> "Peripheral Nervous System";
            case '2' -> "Heart and Great Vessels";
            case '3' -> "Upper Arteries";
            case '4' -> "Lower Arteries";
            case '5' -> "Upper Veins";
            case '6' -> "Lower Veins";
            case '7' -> "Lymphatic and Hematic Systems";
            case '8' -> "Eye";
            case '9' -> "Ear, Nose, Sinus";
            case 'B' -> "Respiratory System";
            case 'C' -> "Mouth and Throat";
            case 'D' -> "Gastrointestinal System";
            case 'F' -> "Hepatobiliary System and Pancreas";
            case 'G' -> "Endocrine System";
            case 'H' -> "Skin and Breast";
            case 'J' -> "Musculoskeletal System";
            case 'K' -> "Urinary System";
            case 'L' -> "Female Reproductive System";
            case 'M' -> "Male Reproductive System";
            case 'N' -> "Obstetrics";
            case 'P' -> "Bones and Joints";
            case 'Q' -> "Upper Extremities";
            case 'R' -> "Lower Extremities";
            default -> null;
        };
    }



    public String getProcedureSection() {
        char firstDigit = code.charAt(0);
        return switch (firstDigit) {
            case '0' -> "Medical and Surgical";
            case '1' -> "Obstetrics";
            case '2' -> "Placement";
            case '3' -> "Administration";
            case '4' -> "Measurement and Monitoring";
            case '5' -> "Imaging";
            case '6' -> "Nuclear Medicine";
            case '7' -> "Radiation Oncology";
            case '8' -> "Other Procedures";
            case '9' -> "Chiropractic";
            default -> null;
        };
    }


    @Override
    public String getProcedureCode() {
        return code;
    }

    /**
     * Gets a random procedure code from the registry
     * @return A randomly selected ProcedureCode
     */
    public static ProcedureCode getRandomCode() {
        String[] codes = CODE_REGISTRY.keySet().toArray(new String[0]);
        int randomIndex = (int) (Math.random() * codes.length);
        return createFromCode(codes[randomIndex]);
    }
}
