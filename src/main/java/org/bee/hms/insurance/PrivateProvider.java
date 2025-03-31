package org.bee.hms.insurance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.AccidentType;
import org.bee.hms.policy.BaseCoverage;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.CoverageLimit;
import org.bee.hms.policy.ExclusionCriteria;
import org.bee.hms.policy.HeldInsurancePolicy;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;
import org.bee.hms.wards.WardClassType;

/**
 * Represents a private insurance provider.
 * <p>
 * The {@link PrivateProvider} class extends {@link InsuranceProvider} and represents an insurance
 * provider offering private insurance policies. It includes functionality for processing claims,
 * retrieving a patient's policy, and checking if a patient has active coverage.
 * </p>
 */
public class PrivateProvider extends InsuranceProvider {



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
            claim.processPartialApproval(approvedAmount, "Some expenses not covered under policy terms");
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
                .withAnnualLimit(new BigDecimal(DataGenerator.generateRandomInt(100_000, 1_000_000)))
                .withLifetimeLimit(new BigDecimal(DataGenerator.generateRandomInt(1_000_000, 10_000_000)))
                .addBenefitLimit(BenefitType.HOSPITALIZATION, new BigDecimal(DataGenerator.generateRandomInt(2_000, 10_000)))
                .addBenefitLimit(BenefitType.SURGERY, new BigDecimal(DataGenerator.generateRandomInt(10_000, 100_000)))
                .addBenefitLimit(BenefitType.DIAGNOSTIC_IMAGING, new BigDecimal(DataGenerator.generateRandomInt(1_000, 5_000)))
                .addBenefitLimit(BenefitType.ONCOLOGY_TREATMENTS, new BigDecimal(DataGenerator.generateRandomInt(5_000, 20_000)))
                .addWardLimit(WardClassType.GENERAL_CLASS_A, new BigDecimal(DataGenerator.generateRandomInt(150_000, 300_000)))
                .addWardLimit(WardClassType.GENERAL_CLASS_B1, new BigDecimal(DataGenerator.generateRandomInt(100_000, 200_000)))
                .addWardLimit(WardClassType.GENERAL_CLASS_C, new BigDecimal(DataGenerator.generateRandomInt(100_000, 200_000)))
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
                .withCoinsurance(BigDecimal.valueOf(DataGenerator.generateRandomInt(10, 30) / 100.0)) // 10-30%
                .withDeductible(BigDecimal.valueOf(DataGenerator.generateRandomInt(1_000, 5_000)))
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
                .plusYears(DataGenerator.generateRandomInt(1, 10))
                .plusMonths(DataGenerator.generateRandomInt(0, 11))
                .plusDays(DataGenerator.generateRandomInt(0, 30));

        return Optional.of(new HeldInsurancePolicy.Builder(
                String.format("PRIV-%06d", DataGenerator.generateRandomInt(100_000, 999_999)),
                patient,
                coverage,
                this,
                DataGenerator.getRandomInsuranceName())
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
        return true;
    }

    /**
     * Gets the name of this private insurance provider
     *
     * @return Constant string "Private Provider"
     */
    @Override
    public String getProviderName() {
        return "Private Provider";
    }
}
