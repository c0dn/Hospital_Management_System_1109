package insurance;

import claims.ClaimStatus;
import claims.InsuranceClaim;
import humans.Patient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import policy.*;
import utils.DataGenerator;
import wards.WardClassType;

/**
 * Represents an insurance provider that is a government entity.
 * <p>
 * The {@link GovernmentProvider} class extends {@link InsuranceProvider} and represents a provider
 * for government-issued insurance policies. It includes functionality for handling claims and checking
 * whether a patient has active coverage
 * </p>
 */
public class GovernmentProvider extends InsuranceProvider {

    private final Map<String, BaseCoverage> coverageDatabase;

    public GovernmentProvider() {
        this.coverageDatabase = new HashMap<>();

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
                .withAnnualLimit(BigDecimal.valueOf(150000))
                .withLifetimeLimit(BigDecimal.valueOf(2000000))
                .addBenefitLimit(BenefitType.HOSPITALIZATION, 1200)
                .addBenefitLimit(BenefitType.SURGERY, 4500)
                .addBenefitLimit(BenefitType.ONCOLOGY_TREATMENTS, 3000)
                .addBenefitLimit(BenefitType.ACCIDENT, 150000)
                .addWardLimit(WardClassType.GENERAL_CLASS_B2, 150000)
                .addWardLimit(WardClassType.GENERAL_CLASS_C, 150000)
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
                .withAnnualLimit(BigDecimal.valueOf(50000))
                .addAccidentLimit(AccidentType.PERMANENT_DISABILITY, 600)
                .addBenefitLimit(BenefitType.CRITICAL_ILLNESS, 120000)
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
                .withAnnualLimit(BigDecimal.valueOf(75000))
                .addAccidentLimit(AccidentType.PERMANENT_DISABILITY, 400)
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


    @Override
    public boolean processClaim(Patient patient, InsuranceClaim claim) {
        // Always approve fully for testing purposes
        claim.updateStatus(ClaimStatus.IN_REVIEW);
        claim.updateStatus(ClaimStatus.APPROVED);
        return true;
    }

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
                DataGenerator.getInstance().generateRandomInt(1_000_000_000),
                patient.getPatientId()), patient, finalCoverage, this, "Government base policy")
                .withExpirationDate(LocalDateTime.now().plusYears(1))
                .build();


        return Optional.of(policy);
    }

    @Override
    public boolean hasActiveCoverage(Patient patient) {
        return true;
    }


    @Override
    public String getProviderName() {
        return "Gov Provider";
    }

}
