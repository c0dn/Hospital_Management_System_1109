package policy;

import java.util.Objects;

public class BaseInsurancePolicy {
    private final Coverage coverage;

    public BaseInsurancePolicy(Coverage coverage) {
        this.coverage = Objects.requireNonNull(coverage);
    }

    public Coverage getCoverage() {
        return coverage;
    }
}