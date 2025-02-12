package insurance;

import billing.Bill;
import claims.InsuranceClaim;
import humans.Patient;
import policy.InsurancePolicy;

import java.util.Optional;

/**
 * Represents the abstract class for an insurance provider.
 * <p>
 * This class serves as the base for different types of insurance providers. It provides
 * abstract methods for processing claims, retrieving patient insurance policies, and checking
 * whether a patient has active coverage.
 * </p>
 */
public abstract class InsuranceProvider {

    /** The name of the insurance provider. */
    protected String providerName;

    /**
     * Processes a claim for a patient.
     * <p>
     * Define how an insurance provider processes claims for medical bills incurred by a patient.
     * </p>
     *
     * @param patient The patient who is making the claim.
     * @param bill The bill for which the claim is being made.
     * @return A boolean value indicating whether the claim was successfully processed.
     */
    public abstract boolean processClaim(Patient patient, Bill bill);
    public abstract boolean processClaim(Patient patient, InsuranceClaim claim);

    /**
     * Retrieves the insurance policy for a given patient.
     * <p>
     * Define how an insurance provider retrieves or generates a policy for a given patient.
     * </p>
     *
     * @param patient The patient whose insurance policy is to be retrieved.
     * @return An {@code Optional} containing the patient's insurance policy if it exists,
     *         or an empty {@code Optional} if the patient is not covered.
     */
    public abstract Optional<InsurancePolicy> getPatientPolicy(Patient patient);

    /**
     * Checks if the patient has active coverage under the insurance provider.
     * <p>
     * Method to determine whether the given patient
     * has an active insurance coverage with the provider.
     * </p>
     *
     * @param patient The patient whose coverage status is to be checked.
     * @return A boolean value indicating whether the patient has active coverage.
     */
    public abstract boolean hasActiveCoverage(Patient patient);

    /**
     * Gets the name of the insurance provider.
     *
     * @return The name of the provider.
     */
    public String getProviderName() {
        return providerName;
    }

}
