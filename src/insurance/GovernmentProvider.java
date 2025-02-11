package insurance;

import billing.Bill;
import humans.Patient;
import policy.InsurancePolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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