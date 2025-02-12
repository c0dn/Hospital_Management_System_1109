package policy;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExclusionCriteria {
    private final List<Pattern> excludedDiagnosisPatterns;
    private final List<Pattern> excludedProcedurePatterns;
    private final Set<BenefitType> excludedBenefits;
    private final Set<AccidentType> excludedAccidentTypes;

    public ExclusionCriteria(Set<String> excludedDiagnosis, Set<String> excludedProcedures,
                             Set<BenefitType> excludedBenefits, Set<AccidentType> excludedAccidentTypes) {
        this.excludedDiagnosisPatterns = compilePatterns(excludedDiagnosis);
        this.excludedProcedurePatterns = compilePatterns(excludedProcedures);
        this.excludedBenefits = excludedBenefits;
        this.excludedAccidentTypes = excludedAccidentTypes;
    }

    private List<Pattern> compilePatterns(Set<String> patterns) {
        return patterns.stream()
                .map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
    }

    public boolean applies(ClaimableItem item, boolean isInpatient) {
        return matchesAnyPattern(item.getDiagnosisCode(), excludedDiagnosisPatterns) ||
                matchesAnyPattern(item.getProcedureCode(), excludedProcedurePatterns) ||
                excludedBenefits.contains(item.resolveBenefitType(isInpatient));
    }

    private boolean matchesAnyPattern(String code, List<Pattern> patterns) {
        return patterns.stream().anyMatch(p -> p.matcher(code).find());
    }

    public boolean isExcludedAccident(AccidentType type) {
        return excludedAccidentTypes.contains(type);
    }
}