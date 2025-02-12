package insurance;

import billing.Bill;
import humans.Patient;
import policy.*;
import utils.DataGenerator;
import wards.WardClassType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a private insurance provider.
 * <p>
 * The {@link PrivateProvider} class extends {@link InsuranceProvider} and represents an insurance
 * provider offering private insurance policies. It includes functionality for processing claims,
 * retrieving a patient's policy, and checking if a patient has active coverage.
 * </p>
 */
public class PrivateProvider extends InsuranceProvider {

    /** Instance of the DataGenerator used to generate random data. */
    private final DataGenerator dataGen = DataGenerator.getInstance();

    /**
     * Processes a claim for a patient.
     * <p>
     * Define how claims for medical
     * bills incurred by a patient are processed. In the {@code PrivateProvider} class, this
     * implementation currently returns {@code false}.
     * </p>
     *
     * @param patient The patient who is making the claim.
     * @param bill The bill for which the claim is being made.
     * @return A boolean value indicating whether the claim was successfully processed.
     */
    @Override
    public boolean processClaim(Patient patient, Bill bill) {
        return false;
    }

    /**
     * Retrieves the insurance policy for a given patient.
     * <p>
     * This method generates a random insurance policy for the patient, using random values
     * for various coverage limits, deductibles, exclusions, and covered benefits. The generated
     * policy is returned wrapped in an {@code Optional}.
     * </p>
     *
     * @param patient The patient whose insurance policy is to be retrieved.
     * @return An {@code Optional} containing the patient's generated insurance policy if it exists.
     */
    @Override
    public Optional<InsurancePolicy> getPatientPolicy(Patient patient) {
        // In the real world it should retrieve actual policy

        Set<BenefitType> randomBenefits = new HashSet<>(Arrays.asList(
                BenefitType.HOSPITALIZATION,
                BenefitType.SURGERY,
                BenefitType.DIAGNOSTIC_IMAGING,
                BenefitType.OUTPATIENT_TREATMENTS,
                BenefitType.ONCOLOGY_TREATMENTS,
                BenefitType.CHRONIC_CONDITIONS
        ));

        CoverageLimit coverageLimit = new CoverageLimit.Builder()
                .withAnnualLimit(BigDecimal.valueOf(dataGen.generateRandomInt(100_000, 1_000_000)))
                .withLifetimeLimit(BigDecimal.valueOf(dataGen.generateRandomInt(1_000_000, 10_000_000)))
                .addBenefitLimit(BenefitType.HOSPITALIZATION, dataGen.generateRandomInt(2_000, 10_000))
                .addBenefitLimit(BenefitType.SURGERY, dataGen.generateRandomInt(10_000, 100_000))
                .addBenefitLimit(BenefitType.DIAGNOSTIC_IMAGING, dataGen.generateRandomInt(1_000, 5_000))
                .addBenefitLimit(BenefitType.ONCOLOGY_TREATMENTS, dataGen.generateRandomInt(5_000, 20_000))
                .addWardLimit(WardClassType.GENERAL_CLASS_A, dataGen.generateRandomInt(150_000, 300_000))
                .addWardLimit(WardClassType.GENERAL_CLASS_B1, dataGen.generateRandomInt(100_000, 200_000))
                .addWardLimit(WardClassType.GENERAL_CLASS_C, dataGen.generateRandomInt(100_000, 200_000))
                .build();

        Set<String> diagnosisCodes = new HashSet<>(Arrays.asList(
                "Z41\\.1", "Z34\\.*", "Z51\\.*", "Z52\\.*"
        ));
        Set<String> procedureCodes = new HashSet<>(Arrays.asList(
                "0BH.*", "0BJ.*", "0DJ.*", "0FJ.*"
        ));

        Set<BenefitType> excludedBenefits = new HashSet<>(Arrays.asList(
                BenefitType.MATERNITY,
                BenefitType.DENTAL,
                BenefitType.ACCIDENT
        ));
        Set<AccidentType> excludedAccidents = new HashSet<>(Arrays.asList(
                AccidentType.TEMPORARY_DISABILITY,
                AccidentType.PERMANENT_DISABILITY
        ));

        BaseCoverage coverage = new BaseCoverage.Builder()
                .withCoinsurance(BigDecimal.valueOf(dataGen.generateRandomInt(10, 30) / 100.0)) // 10-30%
                .withDeductible(BigDecimal.valueOf(dataGen.generateRandomInt(1_000, 5_000)))
                .withLimits(coverageLimit)
                .withCoveredBenefits(randomBenefits)
                .withExclusions(new ExclusionCriteria(
                        diagnosisCodes,
                        procedureCodes,
                        excludedBenefits,
                        excludedAccidents
                ))
                .build();

        LocalDateTime expirationDate = LocalDateTime.now()
                .plusYears(dataGen.generateRandomInt(1, 10))
                .plusMonths(dataGen.generateRandomInt(0, 11))
                .plusDays(dataGen.generateRandomInt(0, 30));

        return Optional.of(new HeldInsurancePolicy.Builder(
                String.format("P%06d", dataGen.generateRandomInt(100_000, 999_999)),
                patient,
                coverage,
                this,
                dataGen.getRandomInsuranceName())
                .withExpirationDate(expirationDate)
                .build());
    }

    /**
     * Checks if the patient has active coverage under the private insurance provider.
     * <p>
     * Check if a given patient has active coverage with the private provider.
     * In the {@code PrivateProvider} class, this implementation currently
     * returns {@code false}.
     * </p>
     *
     * @param patient The patient whose coverage status is to be checked.
     * @return A boolean value indicating whether the patient has active coverage.
     */
    @Override
    public boolean hasActiveCoverage(Patient patient) {
        return false;
    }
}
