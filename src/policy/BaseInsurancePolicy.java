package policy;

import insurance.InsuranceProvider;

import java.util.Objects;

public class BaseInsurancePolicy {
    private final Coverage coverage;
    protected final InsuranceProvider provider;

    public BaseInsurancePolicy(Coverage coverage, InsuranceProvider provider) {
        this.coverage = Objects.requireNonNull(coverage);
        this.provider = provider;
    }

    public Coverage getCoverage() {
        return coverage;
    }
}