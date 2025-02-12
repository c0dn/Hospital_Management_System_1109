package insurance;

import billing.Bill;
import humans.Patient;
import policy.InsurancePolicy;

import java.util.Optional;

/**
 * Represents an insurance provider that is a government entity.
 * <p>
 * The {@link GovernmentProvider} class extends {@link InsuranceProvider} and represents a provider
 * for government-issued insurance policies. It includes functionality for handling claims and checking
 * whether a patient has active coverage.
 * </p>
 */
public abstract class InsuranceProvider {

    /** The name of the insurance provider  */
    protected String providerName;

    /**
     * Processes a claim for a given patient and bill.
     *
     * @param patient The patient for whom the claim is being processed.
     * @param bill The bill associated with the claim.
     * @return {@code true} if the claim was successfully processed, otherwise {@code false}.
     */
    public abstract boolean processClaim(Patient patient, Bill bill);

    /**
     * Retrieves the insurance policy associated with a given patient.
     * @param patient The patient whose policy is being retrieved.
     * @return An {@link Optional} containing the patient's insurance policy, or an empty {@link Optional}
     * if no policy is found.
     */
    public abstract Optional<InsurancePolicy> getPatientPolicy(Patient patient);

    /**
     * Checks if the given patient has active coverage with this insurance provider.
     *
     * @param patient The patient whose coverage status is being checked.
     * @return {@code true} if the patient has active coverage, otherwise {@code false}.
     */
    public abstract boolean hasActiveCoverage(Patient patient);

    /**
     * Gets the name of the insurance provider.
     *
     * @return The name of the insurance provider.
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Sets the name of the insurance provider.
     *
     * @param providerName The name of the insurance provider to set.
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}