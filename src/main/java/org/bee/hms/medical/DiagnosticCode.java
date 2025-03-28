package org.bee.hms.medical;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.ClaimableItem;
import org.bee.utils.CSVHelper;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONSerializable;

/**
 * Represents a diagnostic code, typically used for medical diagnosis.
 * <p>
 * This class provides functionality to map a code to its relevant description, category,
 * and cost. It also determines the type of benefit (hospitalization, maternity, dental, etc.)
 * based on the category code.
 * </p>
 */
public class DiagnosticCode implements BillableItem, ClaimableItem, JSONSerializable {
    /** The category code of the diagnosis (e.g., ICD-10 category) */
    @JsonIgnore
    private final String categoryCode;

    /** The diagnosis code (e.g., ICD-10 code) */
    @JsonIgnore
    private final String diagnosisCode;

    /** The full code (e.g., ICD-10 CM code) */
    @JsonProperty("code")
    private String fullCode;

    /** The abbreviated description of the diagnosis */
    @JsonIgnore
    private final String abbreviatedDescription;

    /** The full description of the diagnosis */
    @JsonIgnore
    private final String fullDescription;

    /** The category title of the diagnosis */
    @JsonIgnore
    private final String categoryTitle;

    /** The cost of the diagnostic code, used for billing purposes */
    @JsonProperty("cost")
    private BigDecimal cost;

    /** A registry to store diagnostic codes loaded from a CSV file */
    private static final Map<String, DiagnosticCode> CODE_REGISTRY = new HashMap<>();

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
        this.cost = new BigDecimal(DataGenerator.generateRandomInt(100, 500));
    }



    /**
     * Loads diagnostic codes from a CSV file and stores them in the code registry.
     */
    private static void loadCodesFromCsv() {
        String databaseDir = System.getProperty("database.dir", "database");
        List<String[]> records = CSVHelper.readCSV(databaseDir + "/icd-10-cm.csv");

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

    /**
     * Creates a {@link DiagnosticCode} from the given code and sets its cost.
     * This is used for deserialization.
     */
    @JsonCreator
    public static DiagnosticCode createFromCodeAndCost(
            @JsonProperty("code") String code,
            @JsonProperty("cost") BigDecimal cost) {
        DiagnosticCode diagnosticCode = createFromCode(code);
        diagnosticCode.cost = cost;
        return diagnosticCode;
    }



    /**
     * Returns the billing item code for the diagnostic code.
     * <p>
     * This method generates a billing item code using the diagnostic code's full code.
     * </p>
     *
     * @return A string representing the billing item code.
     */
    @Override
    public String getBillingItemCode() {
        return String.format("DIAG-%s", fullCode);
    }

    /**
     * Returns the unsubsidized charges for the diagnostic code.
     * <p>
     * The unsubsidized charges refer to the cost of the diagnostic code, used for billing.
     * </p>
     *
     * @return The unsubsidized charges (cost).
     */
    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return cost;
    }

    /**
     * Returns the abbreviated description of the diagnostic code.
     * <p>
     * This description provides a brief overview of the diagnosis.
     * </p>
     *
     * @return The abbreviated description of the diagnostic code.
     */
    @Override
    public String getBillItemDescription() {
        return abbreviatedDescription;
    }

    /**
     * Returns the category of the diagnostic code for billing purposes.
     * <p>
     * The category for diagnostic codes is always "DIAGNOSIS".
     * </p>
     *
     * @return The category of the diagnostic code ("DIAGNOSIS").
     */
    @Override
    public String getBillItemCategory() {
        return "DIAGNOSIS";
    }

    /**
     * Returns the full description of the diagnostic code.
     * <p>
     * This method retrieves the full description of the diagnostic code from the registry.
     * </p>
     *
     * @param code The diagnostic code to retrieve the description for.
     * @return The full description of the diagnostic code.
     */
    public static String getDescriptionForCode(String code) {
        DiagnosticCode diagnosticCode = CODE_REGISTRY.get(code);
        return diagnosticCode != null ? diagnosticCode.fullDescription : null;
    }

    /**
     * Returns a string representation of the diagnostic code.
     * <p>
     * This method returns a string that includes the full code, abbreviated description, and cost.
     * </p>
     *
     * @return A string representation of the diagnostic code.
     */
    @Override
    public String toString() {
        return String.format("%s: %s [%s]", fullCode, abbreviatedDescription, cost);
    }

    /**
     * Returns the charges for the diagnostic code.
     * <p>
     * @return The charges for the diagnostic code.
     */
    @Override
    public BigDecimal getCharges() {
        return cost;
    }

    /**
     * Resolves the benefit type based on the category code.
     * <p>
     * This method determines the type of benefit (e.g., hospitalization, maternity, dental) based on
     * the category code. If no match is found, it falls back to a default benefit type based on inpatient status.
     * </p>
     *
     * @param isInpatient A boolean indicating if the diagnosis is related to inpatient treatment.
     * @return The resolved {@link BenefitType}.
     */

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
                new BenefitMapping("^Z5[1-3].*", BenefitType.PREVENTIVE_CARE), // Health screenings
                new BenefitMapping("^Z[0-9]{2}.*", isInpatient ? BenefitType.HOSPITALIZATION : BenefitType.OUTPATIENT_TREATMENTS)

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


    /**
     * Returns the benefit description for the diagnostic code.
     * <p>
     * This method provides a detailed description of the benefit for the given diagnostic code.
     * </p>
     *
     * @param isInpatient A boolean indicating if the diagnosis is related to inpatient treatment.
     * @return The benefit description for the diagnostic code.
     */
    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return fullDescription;
    }

    /**
     * Returns the full diagnostic code.
     * <p>
     * This method returns the full code for the diagnosis, such as the ICD-10 code.
     * </p>
     *
     * @return The full diagnostic code.
     */
    @Override
    public String getDiagnosisCode() {
        return this.fullCode;
    }

    /**
     * Returns the diagnostic code with abbreviated description
     * @return Formatted string combining the full code and abbreviated description
     */
    public String getDCode() { return this.fullCode + ": " + abbreviatedDescription; }

    /**
     * Gets a random diagnostic code from the registry
     * @return A randomly selected DiagnosticCode
     */
    public static DiagnosticCode getRandomCode() {
        Set<String> codes = CODE_REGISTRY.keySet();
        return createFromCode(DataGenerator.getRandomElement(codes));
    }
    
    /**
     * Gets a random diagnostic code that matches the specified benefit type
     * 
     * @param benefitType The benefit type to match
     * @return A randomly selected DiagnosticCode that matches the specified benefit type
     * @throws IllegalArgumentException if no diagnostic codes match the specified benefit type
     */
    public static DiagnosticCode getRandomCodeForBenefitType(BenefitType benefitType, boolean isInPatient) {
        // Create a list to store matching codes
        List<String> matchingCodes = new java.util.ArrayList<>();
        
        // Iterate through all codes in the registry
        for (Map.Entry<String, DiagnosticCode> entry : CODE_REGISTRY.entrySet()) {
            DiagnosticCode code = entry.getValue();
            
            // Check if this code matches the specified benefit type
            // We'll check for both inpatient and outpatient scenarios
            if (code.resolveBenefitType(isInPatient) == benefitType) {
                matchingCodes.add(entry.getKey());
            }
        }
        
        if (matchingCodes.isEmpty()) {
            throw new IllegalArgumentException("No diagnostic codes found for benefit type: " + benefitType);
        }
        
        return createFromCode(DataGenerator.getRandomElement(matchingCodes));
    }
}

/**
 * A record that represents a mapping between a regular expression pattern and a {@link BenefitType}.
 * <p>
 * This record is used to associate a diagnostic code's category with a specific type of benefit (e.g., maternity,
 * critical illness, accident, etc.). The mapping is based on matching the category code to a regular expression pattern.
 * </p>
 */
record BenefitMapping(String pattern, BenefitType benefitType) {
    /**
     * Checks if the provided diagnostic code matches the pattern defined for this benefit mapping.
     * <p>
     * This method evaluates whether the given diagnostic code (e.g., category code) matches the regular expression pattern
     * defined for this benefit mapping.
     * </p>
     *
     * @param code The diagnostic code (category code) to match against the pattern.
     * @return {@code true} if the code matches the pattern, {@code false} otherwise.
     */
    public boolean matches(String code) {
        return Pattern.matches(pattern, code);
    }

}
