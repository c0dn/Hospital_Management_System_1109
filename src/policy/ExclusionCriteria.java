package policy;

import java.util.Set;

/**
 * Represents the exclusion criteria for an insurance policy.
 * <p>
 *     This class defines the conditions under which certain diagnoses, procedures, benefits, or accident types
 *     are excluded from coverage. It helps to determine if a specific claim item is excluded based on these criteria.
 * </p>
 */
public class ExclusionCriteria {
    private Set<String> excludedDiagnosis;
    private Set<String> excludedProcedures;
    private Set<BenefitType> excludedBenefits;
    private Set<AccidentType> excludedAccidentTypes;

    /**
     * Constructor for ExclusionCriteria.
     *
     * @param excludedDiagnosis A set of diagnosis codes that are excluded.
     * @param excludedProcedures A set of procedure codes that are excluded.
     * @param excludedBenefits A set of benefit types that are excluded.
     * @param excludedAccidentTypes A set of accident types that are excluded.
     */
    public ExclusionCriteria(Set<String> excludedDiagnosis, Set<String> excludedProcedures,
                             Set<BenefitType> excludedBenefits, Set<AccidentType> excludedAccidentTypes) {
        this.excludedDiagnosis = excludedDiagnosis;
        this.excludedProcedures = excludedProcedures;
        this.excludedBenefits = excludedBenefits;
        this.excludedAccidentTypes = excludedAccidentTypes;
    }

    /**
     * Determines whether the given claim item applies the exclusion criteria.
     * <p>
     *     This method checks if the diagnosis, procedure, or benefit type of the given claim item is excluded
     *     from coverage based on the exclusion criteria.
     * </p>
     *
     * @param item The claimable item (e.g., diagnosis, procedure, benefit type).
     * @param isInpatient Indicates whether the claim item is for an inpatient treatment.
     * @return {@code true} if the item is excluded based on the exclusion criteria, {@code false} otherwise.
     */
    public boolean applies(ClaimableItem item, boolean isInpatient) {
        return excludedDiagnosis.contains(item.getDiagnosisCode()) ||
                excludedProcedures.contains(item.getProcedureCode()) ||
                excludedBenefits.contains(item.resolveBenefitType(isInpatient));
    }

    /**
     * Checks if the given accident type is excluded from coverage.
     *
     * @param type The accident type to check.
     * @return {@code true} if the accident type is excluded, {@code false} otherwise.
     */
    public boolean isExcludedAccident(AccidentType type) {
        return excludedAccidentTypes.contains(type);
    }
}