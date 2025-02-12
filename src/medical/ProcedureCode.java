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
 * This class implements {@link BillableItem} and {@link ClaimableItem} interfaces to handle the
 * billing and claims of the procedure.
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

    /**
     * Constructor for ProcedureCode
     *
     * @param code The unique procedure code.
     * @param description The description of the procedure.
     */
    private ProcedureCode(String code, String description) {
        this.code = code;
        this.description = description;
        this.price = DEFAULT_PRICE;
    }

    /**
     * Loads the procedure codes and their descriptions from a CSV file.
     * The codes are stored in a registry for quick lookup.
     */
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

    /**
     * Creates a ProcedureCode from the provided procedure code string.
     *
     * @param code The procedure code to retrieve from the registry.
     * @return A {@link ProcedureCode} corresponding to the provided code.
     * @throws IllegalArgumentException If the procedure code is invalid.
     */
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

    /**
     * Returns the billing item code for the procedure, prefixed with "PROC-".
     *
     * @return A string representing the billing item code.
     */
    @Override
    public String getBillingItemCode() {
        return String.format("PROC-%s", code);
    }

    /**
     * Returns the unsubsidised charges for the procedure, which is the price of the procedure.
     *
     * @return The unsubsidised charges for the procedure.
     */
    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return price;
    }

    /**
     * Returns the description of the procedure.
     *
     * @return The procedure description.
     */
    @Override
    public String getBillItemDescription() {
        return description;
    }

    /**
     * Returns the category of the bill item, which is "PROCEDURE".
     *
     * @return The category of the bill item.
     */
    @Override
    public String getBillItemCategory() {
        return "PROCEDURE";
    }

    /**
     * Returns a string representation of the procedure code, description, and price.
     *
     * @return A string representing the procedure code, description, and price.
     */
    @Override
    public String toString() {
        return String.format("%s: %s [%s]", code, description, price);
    }

    /**
     * Returns the charges for the procedure.
     *
     * @return The charges for the procedure.
     */
    @Override
    public BigDecimal getCharges() {
        return price;
    }

    /**
     * Resolves the appropriate benefit type based on the procedure code and whether the patient is an inpatient.
     * The benefit type is determined based on the procedure code's section and body system.
     *
     * @param isInpatient A boolean value indicating whether the patient is an inpatient.
     * @return A {@link BenefitType} representing the type of benefit for the procedure.
     */
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

    /**
     * Fallback method for determining the benefit type in case of an invalid or unsupported procedure code.
     *
     * @param isInpatient A boolean value indicating whether the patient is an inpatient.
     * @return The fallback {@link BenefitType}, either HOSPITALIZATION or OUTPATIENT_TREATMENTS.
     */
    private BenefitType defaultFallback(boolean isInpatient) {
        return isInpatient ? BenefitType.HOSPITALIZATION
                : BenefitType.OUTPATIENT_TREATMENTS;
    }

    /**
     * Returns a description of the benefit for the procedure, indicating whether it is inpatient or outpatient,
     * and providing additional details like the body system if applicable.
     *
     * @param isInpatient A boolean value indicating whether the patient is an inpatient.
     * @return A string representing a description of the procedure benefit.
     */
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

    /**
     * Returns the body system related to the procedure based on the second character of the procedure code.
     *
     * @param secondChar The second character in the procedure code.
     * @return The body system description, or null if not applicable.
     */
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

    /**
     * Returns the section of the procedure code, which represents the category of the procedure.
     *
     * @return A string representing the procedure section.
     */
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

    /**
     * Returns the procedure code.
     *
     * @return The procedure code.
     */
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
