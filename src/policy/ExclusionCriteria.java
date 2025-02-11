package policy;

import java.util.Set;

public class ExclusionCriteria {
    private Set<String> excludedDiagnosis;
    private Set<String> excludedProcedures;
    private Set<BenefitType> excludedBenefits;
    private Set<AccidentType> excludedAccidentTypes;

    public ExclusionCriteria(Set<String> excludedDiagnosis, Set<String> excludedProcedures,
                             Set<BenefitType> excludedBenefits, Set<AccidentType> excludedAccidentTypes) {
        this.excludedDiagnosis = excludedDiagnosis;
        this.excludedProcedures = excludedProcedures;
        this.excludedBenefits = excludedBenefits;
        this.excludedAccidentTypes = excludedAccidentTypes;
    }

    public boolean applies(ClaimableItem item, boolean isInpatient) {
        return excludedDiagnosis.contains(item.getDiagnosisCode()) ||
                excludedProcedures.contains(item.getProcedureCode()) ||
                excludedBenefits.contains(item.resolveBenefitType(isInpatient));
    }

    public boolean isExcludedAccident(AccidentType type) {
        return excludedAccidentTypes.contains(type);
    }
}