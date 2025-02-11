package insurance;

import billing.Bill;
import humans.Patient;
import policy.InsurancePolicy;

import java.util.Optional;

/**
 * Represents the abstract class for an insurance provider.
 * Provides methods to process claims and retrieve patient insurance details.
 */
public abstract class InsuranceProvider {

    protected String providerName;

    public abstract boolean processClaim(Patient patient, Bill bill);


    public abstract Optional<InsurancePolicy> getPatientPolicy(Patient patient);


    public abstract boolean hasActiveCoverage(Patient patient);

}