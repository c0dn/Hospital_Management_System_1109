package policy;

import insurance.InsuranceProvider;

import java.util.Objects;

/**
 * Represents a basic insurance policy that contains coverage details and the associated insurance provider.
 * <p>
 * This class provides methods to access the coverage and the insurance provider for the policy.
 * </p>
 */
public class BaseInsurancePolicy {

    /** The coverage associated with this insurance policy. */
    private final Coverage coverage;

    /** The insurance provider associated with this policy. */
    protected final InsuranceProvider provider;

    /**
     * Constructs a new {@link BaseInsurancePolicy} with the specified coverage and provider.
     * <p>
     * The coverage must not be {@code null}. If the coverage is {@code null}, an {@link IllegalArgumentException}
     * will be thrown. The provider can be {@code null}, but it's generally preferred to provide a valid provider.
     * </p>
     *
     * @param coverage The coverage details for the insurance policy.
     * @param provider The insurance provider for this policy.
     * @throws NullPointerException if the coverage is {@code null}.
     */
    public BaseInsurancePolicy(Coverage coverage, InsuranceProvider provider) {
        this.coverage = Objects.requireNonNull(coverage, "Coverage must not be null");
        this.provider = provider;
    }

    /**
     * Gets the coverage associated with this insurance policy.
     *
     * @return The coverage for this policy.
     */
    public Coverage getCoverage() {
        return coverage;
    }
}