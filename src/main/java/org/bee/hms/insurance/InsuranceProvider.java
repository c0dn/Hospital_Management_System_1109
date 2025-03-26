package org.bee.hms.insurance;

import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.InsurancePolicy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GovernmentProvider.class, name = "government"),
    @JsonSubTypes.Type(value = PrivateProvider.class, name = "private")
})
public abstract class InsuranceProvider {

    /** The name of the insurance provider. */
    protected String providerName;

    /**
     * Processes an insurance claim for a given patient.
     * This method determines whether the insurance claim filed by the specified patient
     * is valid and processes it accordingly.
     *
     * @param patient The patient associated with the insurance claim.
     * @param claim The insurance claim to be processed.
     * @return {@code true} if the claim is successfully processed, {@code false} otherwise.
     */
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

    /**
     * Submits an insurance claim for a given patient.
     * This method will send patients' claim information to the provider system.
     *
     * @param patient The patient associated with the insurance claim.
     * @param claim The insurance claim to be processed.
     * @return {@code true} if the claim is successfully submmited, {@code false} otherwise.
     */
    public abstract boolean submitClaim(Patient patient, InsuranceClaim claim);

}
