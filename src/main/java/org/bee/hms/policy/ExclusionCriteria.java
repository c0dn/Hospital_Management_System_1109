package org.bee.hms.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.ProcedureCode;
import org.bee.utils.JSONSerializable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents the criteria used to exclude certain diagnoses, procedures, benefits, and accident types
 * from coverage under an insurance policy.
 * <p>
 * This class helps determine whether a claim item (such as a diagnosis or procedure) is excluded
 * from coverage based on patterns (for diagnoses and procedures) and specific benefit or accident types.
 * </p>
 */
public class ExclusionCriteria implements JSONSerializable {

    private final List<Pattern> excludedDiagnosisPatterns;
    private final List<Pattern> excludedProcedurePatterns;
    private final Set<BenefitType> excludedBenefits;
    private final Set<AccidentType> excludedAccidentTypes;

    /**
     * Creates an ExclusionCriteria object with specified exclusion patterns and types.
     *
     * @param excludedDiagnosis     A set of diagnosis codes (as regular expressions) to be excluded from coverage.
     * @param excludedProcedures    A set of procedure codes (as regular expressions) to be excluded from coverage.
     * @param excludedBenefits      A set of benefit types that are excluded from coverage.
     * @param excludedAccidentTypes A set of accident types that are excluded from coverage.
     */
    public ExclusionCriteria(Set<String> excludedDiagnosis, Set<String> excludedProcedures,
                             Set<BenefitType> excludedBenefits, Set<AccidentType> excludedAccidentTypes) {
        this.excludedDiagnosisPatterns = compilePatterns(excludedDiagnosis);
        this.excludedProcedurePatterns = compilePatterns(excludedProcedures);
        this.excludedBenefits = excludedBenefits != null ? new HashSet<>(excludedBenefits) : new HashSet<>();
        this.excludedAccidentTypes = excludedAccidentTypes != null ? new HashSet<>(excludedAccidentTypes) : new HashSet<>();
    }


    /**
     * JSON Creator method for deserializing ExclusionCriteria
     */
    @JsonCreator
    public static ExclusionCriteria create(
            @JsonProperty("excludedDiagnosisPatterns") Set<String> excludedDiagnosis,
            @JsonProperty("excludedProcedurePatterns") Set<String> excludedProcedures,
            @JsonProperty("excludedBenefits") Set<BenefitType> excludedBenefits,
            @JsonProperty("excludedAccidentTypes") Set<AccidentType> excludedAccidentTypes
    ) {
        return new ExclusionCriteria(
                excludedDiagnosis == null ? Collections.emptySet() : excludedDiagnosis,
                excludedProcedures == null ? Collections.emptySet() : excludedProcedures,
                excludedBenefits == null ? Collections.emptySet() : excludedBenefits,
                excludedAccidentTypes == null ? Collections.emptySet() : excludedAccidentTypes
        );
    }

    /**
     * Compiles a set of regular expression patterns from a set of strings.
     *
     * @param patterns A set of strings to compile into regular expression patterns.
     * @return A list of compiled regular expression patterns.
     */
    private static List<Pattern> compilePatterns(Set<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return Collections.emptyList();
        }

        return patterns.stream()
                .filter(Objects::nonNull)
                .map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
    }

    /**
     * Determines if a claim item applies exclusion criteria, meaning the item is excluded from coverage.
     *
     * @param item        The claimable item (such as a procedure or diagnosis) to check.
     * @param isInpatient Flag indicating whether the item is for an inpatient or outpatient.
     * @return true if the item matches any exclusion criteria; false otherwise.
     */
    public boolean applies(ClaimableItem item, boolean isInpatient) {
        if (item instanceof DiagnosticCode diagnosticCode) {
            return matchesAnyPattern(diagnosticCode.getDiagnosisCode(), excludedDiagnosisPatterns)
                    || excludedBenefits.contains(diagnosticCode.resolveBenefitType(isInpatient));
        } else if (item instanceof ProcedureCode procedureCode) {
            return matchesAnyPattern(procedureCode.getProcedureCode(), excludedProcedurePatterns)
                    || excludedBenefits.contains(procedureCode.resolveBenefitType(isInpatient));
        }
        return excludedBenefits.contains(item.resolveBenefitType(isInpatient));
    }

    /**
     * Checks if a given code (diagnosis or procedure) matches any of the exclusion patterns.
     *
     * @param code     The code to check.
     * @param patterns A list of exclusion patterns to match against.
     * @return true if any of the patterns match the code; false otherwise.
     */
    private boolean matchesAnyPattern(String code, List<Pattern> patterns) {
        return patterns.stream().anyMatch(p -> p.matcher(code).find());
    }

    /**
     * Determines if a specific accident type is excluded from coverage.
     *
     * @param type The type of accident to check.
     * @return true if the accident type is excluded; false otherwise.
     */
    public boolean isExcludedAccident(AccidentType type) {
        return excludedAccidentTypes.contains(type);
    }
}
