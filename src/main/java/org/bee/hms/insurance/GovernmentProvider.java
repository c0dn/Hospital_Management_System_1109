package org.bee.hms.insurance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.AccidentType;
import org.bee.hms.policy.BaseCoverage;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.CompositeCoverage;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.CoverageLimit;
import org.bee.hms.policy.ExclusionCriteria;
import org.bee.hms.policy.HeldInsurancePolicy;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.hms.wards.WardClassType;
import org.bee.utils.DataGenerator;

/**
 * Represents an insurance provider that is a government entity.
 * <p>
 * The {@link GovernmentProvider} class extends {@link InsuranceProvider} and represents a provider
 * for government-issued insurance policies. It includes functionality for handling claims and checking
 * whether a patient has active coverage.
 * </p>
 */
public class GovernmentProvider extends InsuranceProvider {

    /** The coverage database that stores different types of insurance coverage. */
    private static final Map<String, BaseCoverage> coverageDatabase = new HashMap<>();

    // Static initializer block to populate the coverage database
    static {

        Set<String> excludedDiagnosis = Set.of(
                "Z41\\.1",     // Exact match
                "Z00.*",       // Any code starting with Z00
                "E66\\..*"     // Any E66 code with any suffix
        );

        Set<String> excludedProcedures = Set.of(
                "0B[HJQ]",     // Match 0BH, 0BJ, or 0BQ
                "3E0.*"        // Any procedure starting with 3E0
        );

        // MediShield Life
        CoverageLimit mediShieldLimit = new CoverageLimit.Builder()
                .withAnnualLimit(new BigDecimal("150000"))
                .withLifetimeLimit(new BigDecimal("2000000"))
                .addBenefitLimit(BenefitType.HOSPITALIZATION, new BigDecimal("1200"))
                .addBenefitLimit(BenefitType.SURGERY, new BigDecimal("4500"))
                .addBenefitLimit(BenefitType.ONCOLOGY_TREATMENTS, new BigDecimal("3000"))
                .addBenefitLimit(BenefitType.ACCIDENT, new BigDecimal("150000"))
                .addWardLimit(WardClassType.GENERAL_CLASS_B2, new BigDecimal("150000"))
                .addWardLimit(WardClassType.GENERAL_CLASS_C, new BigDecimal("150000"))
                .build();

        BaseCoverage mediShieldCoverage = new BaseCoverage.Builder()
                .withCoinsurance(BigDecimal.valueOf(0.10))
                .withDeductible(BigDecimal.valueOf(1500))
                .withDeathBenefitAmount(BigDecimal.valueOf(100000))
                .withLimits(mediShieldLimit)
                .withCoveredBenefits(Set.of(
                        BenefitType.HOSPITALIZATION,
                        BenefitType.SURGERY,
                        BenefitType.OUTPATIENT_TREATMENTS,
                        BenefitType.ONCOLOGY_TREATMENTS,
                        BenefitType.DIAGNOSTIC_IMAGING
                ))
                .withExclusions(new ExclusionCriteria(
                        excludedDiagnosis,
                        excludedProcedures,
                        Set.of(BenefitType.DENTAL, BenefitType.MATERNITY),
                        Set.of(AccidentType.TEMPORARY_DISABILITY)
                ))
                .build();

        // CareShield Life
        CoverageLimit careShieldLimit = new CoverageLimit.Builder()
                .withAnnualLimit(new BigDecimal("50000"))
                .addAccidentLimit(AccidentType.PERMANENT_DISABILITY, new BigDecimal("600"))
                .addBenefitLimit(BenefitType.CRITICAL_ILLNESS, new BigDecimal("120000"))
                .build();

        BaseCoverage careShieldCoverage = new BaseCoverage.Builder()
                .withCoinsurance(BigDecimal.valueOf(0.05))
                .withDeductible(BigDecimal.ZERO)
                .withDeathBenefitAmount(BigDecimal.valueOf(50000))
                .withLimits(careShieldLimit)
                .withCoveredBenefits(Set.of(
                        BenefitType.CRITICAL_ILLNESS,
                        BenefitType.PREVENTIVE_CARE
                ))
                // Pre-existing Conditions
                .withExclusions(new ExclusionCriteria(
                        Set.of(
                                "^E1[0-4]\\..*",          // Diabetes mellitus (E10-E14)
                                "^I1[0-5]\\..*",          // Hypertensive diseases (I10-I15)
                                "^C3[0-4]\\..*",          // Respiratory/intrathoracic cancers
                                "^F2[0-9]\\..*",          // Schizophrenia/schizotypal/delusional
                                "^M1[5-9]\\..*",          // Osteoarthritis family
                                "^J4[0-5]\\..*",          // Chronic lower respiratory diseases
                                "^K7[0-4]\\..*",          // Liver diseases
                                "^N18\\..*",              // All chronic kidney disease stages
                                "^B20\\..*"               // HIV with any subcodes
                        ),
                        Set.of(),
                        Set.of(BenefitType.MINOR_SURGERY),
                        Set.of(AccidentType.TEMPORARY_DISABILITY)
                ))
                .build();

        // ElderShield Supplement
        CoverageLimit elderShieldLimit = new CoverageLimit.Builder()
                .withAnnualLimit(new BigDecimal("75000"))
                .addAccidentLimit(AccidentType.PERMANENT_DISABILITY, new BigDecimal("400"))
                .build();

        BaseCoverage elderShieldCoverage = new BaseCoverage.Builder()
                .withCoinsurance(BigDecimal.valueOf(0.15))
                .withDeductible(BigDecimal.valueOf(500))
                .withLimits(elderShieldLimit)
                .withCoveredBenefits(Set.of(
                        BenefitType.PREVENTIVE_CARE,
                        BenefitType.CHRONIC_CONDITIONS
                ))
                // Occupational Exclusions (Z codes)
                .withExclusions(new ExclusionCriteria(
                        Set.of(
                                "^Z5[6-7]\\..*"         // All occupational exposure codes (Z56-Z57)
                        ),
                        Set.of(),
                        Set.of(BenefitType.ACUTE_CONDITIONS),
                        Set.of(AccidentType.TEMPORARY_DISABILITY)
                ))
                .build();

        coverageDatabase.put("mediShieldCoverage", mediShieldCoverage);
        coverageDatabase.put("careShieldCoverage", careShieldCoverage);
        coverageDatabase.put("elderShieldCoverage", elderShieldCoverage);
    }
    
    /**
     * Constructs a new {@code GovernmentProvider} instance.
     * <p>
     * The coverage database is initialized statically with predefined government insurance plans
     * such as MediShield Life, CareShield Life, and ElderShield Supplement.
     * </p>
     */
    public GovernmentProvider() {
        // Coverage database is now initialized in the static block
    }


    /**
     * Processes an insurance claim for a given patient.
     * Updates the claim status and may fully approve or partially approve
     *
     * @param patient The patient for whom the claim is being processed.
     * @param claim The insurance claim to be reviewed and updated.
     * @return {@code true} indicating that the claim has been successfully processed.
     */
    @Override
    public boolean processClaim(Patient patient, InsuranceClaim claim) {
        // In the real world, this method will be sending information to the provider for claim processing
        claim.startReview();

        BigDecimal claimAmount = claim.getClaimAmount();

        int approvalType = DataGenerator.generateRandomInt(1, 10);

        if (approvalType <= 7) {
            // 70% chance of full approval
            claim.approveClaim(claimAmount);
        } else {
            // 20% chance of partial approval
            // Approve between 50% and 90% of the claimed amount
            int percentage = DataGenerator.generateRandomInt(50, 90);
            BigDecimal approvedAmount = claimAmount.multiply(
                    BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );
            claim.processPartialApproval(approvedAmount, "We wish to please our shareholders");
        }

        return true;
    }

    /**
     * Submits an insurance claim for a given patient.
     * This method will send patients' claim information to the provider system.
     *
     * @param patient The patient associated with the insurance claim.
     * @param claim   The insurance claim to be processed.
     */
    @Override
    public boolean submitClaim(Patient patient, InsuranceClaim claim) {
        // In the real world, this will be the step where we submit a claim and it's details to the provider's system.
        claim.submitForProcessing();
        return true;
    }

    /**
     * Retrieves the insurance policy for a given patient.
     * <p>
     * This method checks if the patient is eligible for government coverage (based on residency
     * or citizenship) and then creates a policy using predefined coverage plans like MediShield Life
     * and CareShield Life if applicable.
     * </p>
     *
     * @param patient The patient whose insurance policy is to be retrieved.
     * @return An {@code Optional} containing the insurance policy if the patient is eligible,
     *         or an empty {@code Optional} if the patient is not eligible.
     */
    @Override
    public Optional<InsurancePolicy> getPatientPolicy(Patient patient) {
        // In the real world, the actual policy will be retrieved from gov services
        if (!patient.isPermanentResident() && !patient.isSingaporean()) {
            return Optional.empty();
        }

        Coverage mediShield = coverageDatabase.get("mediShieldCoverage");

        Coverage careShield = null;
        if (patient.getDOB().getYear() >= 1980) {
            careShield = coverageDatabase.get("careShieldCoverage");
        }

        Coverage finalCoverage = careShield != null
                ? new CompositeCoverage(mediShield, careShield)
                : mediShield;

        HeldInsurancePolicy policy = new HeldInsurancePolicy.Builder(String.format("GOVT-%010d-%s",
                DataGenerator.generateRandomInt(1_000_000_000),
                patient.getPatientId()), patient, finalCoverage, this, "Government base policy")
                .withExpirationDate(LocalDateTime.now().plusYears(1))
                .build();

        return Optional.of(policy);
    }

    /**
     * Checks if the patient has active coverage under the government insurance plan.
     * <p>
     * This method returns {@code false} as active coverage verification is not implemented yet.
     * </p>
     *
     * @param patient The patient whose coverage status is to be checked.
     * @return {@code false} indicating that active coverage status is not yet implemented.
     */
    @Override
    public boolean hasActiveCoverage(Patient patient) {
        return true;
    }

    /**
     * Gets the name of this insurance provider
     * <p>
     * this method returns the fixed string "Gov Provider" representing government health services
     * </p>
     * @return The constant provider name "Gov Provider"
     */

    @Override
    public String getProviderName() {
        return "Gov Provider";
    }
}
