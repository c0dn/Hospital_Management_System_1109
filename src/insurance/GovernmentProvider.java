package insurance;

import billing.Bill;
import humans.Patient;
import policy.*;
import utils.DataGenerator;
import wards.WardClassType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private final Map<String, BaseCoverage> coverageDatabase;

    /**
     * Constructs a new {@code GovernmentProvider} instance.
     * <p>
     * Initializes the coverage database with predefined government insurance plans
     * such as MediShield Life, CareShield Life, and ElderShield Supplement.
     * </p>
     */
    public GovernmentProvider() {
        this.coverageDatabase = new HashMap<>();
    }

    /**
     * Processes a claim for a patient.
     * <p>
     * This method processes the insurance claim for the given patient and bill.
     * However, in this implementation, it returns false since processing is not yet implemented.
     * </p>
     *
     * @param patient The patient who is making the claim.
     * @param bill The bill for which the claim is being made.
     * @return {@code false} indicating that the claim processing is not implemented.
     */
    @Override
    public boolean processClaim(Patient patient, Bill bill) {
        return false;
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
        // Checks patient eligibility based on residency or citizenship status.
        if (!patient.isPermanentResident() && !patient.isSingaporean()) {
            return Optional.empty();
        }

        // Retrieves the appropriate government coverage based on the patient's information
        Coverage mediShield = coverageDatabase.get("mediShieldCoverage");

        Coverage careShield = null;
        if (patient.getDOB().getYear() >= 1980) {
            careShield = coverageDatabase.get("careShieldCoverage");
        }

        // Creates a composite coverage if both MediShield and CareShield are applicable
        Coverage finalCoverage = careShield != null
                ? new CompositeCoverage(mediShield, careShield)
                : mediShield;

        // Constructs a new insurance policy for the patient
        HeldInsurancePolicy policy = new HeldInsurancePolicy.Builder(String.format("GOVT-%010d-%s",
                DataGenerator.getInstance().generateRandomInt(1_000_000_000),
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
        return false;
    }
}