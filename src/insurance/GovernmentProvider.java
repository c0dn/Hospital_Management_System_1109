package insurance;

import billing.Bill;
import humans.Patient;
import policy.InsurancePolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents an insurance provider that is a government entity.
 * <p>
 * The {@link GovernmentProvider} class extends {@link InsuranceProvider} and represents a provider
 * for government-issued insurance policies. It includes functionality for handling claims and checking
 * whether a patient has active coverage
 * </p>
 */
public class GovernmentProvider extends InsuranceProvider {

    private final Map<String, InsurancePolicy> policyDatabase;

    public GovernmentProvider() {
        this.policyDatabase = new HashMap<>();
    }

    @Override
    public boolean processClaim(Patient patient, Bill bill) {
        return false;
    }

    @Override
    public Optional<InsurancePolicy> getPatientPolicy(Patient patient) {
        return Optional.empty();
    }

    @Override
    public boolean hasActiveCoverage(Patient patient) {
        return false;
    }

}