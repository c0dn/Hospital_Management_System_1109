package policy;

import java.time.LocalDate;

/**
 * A generic superclass for building different types of insurance policies.
 * @param <T> The type of the subclass builder to enable method chaining.
 */
public abstract class InsuranceBuilder<T extends InsuranceBuilder<T>> {

    /** Unique identifier for the insurance policy. */
    protected String policyId;
    /** Name of the insurance provider issuing the policy. */
    protected String insuranceProvider;
    /** The deductible amount the policyholder must pay before insurance coverage begins. */
    protected double deductible;
    /** The current status of the insurance policy (e.g., active, expired, pending). */
    protected InsuranceStatus insuranceStatus;
    /** The start date of the insurance policy. */
    protected LocalDate startDate;
    /** The end date of the insurance policy. */
    protected LocalDate endDate;
    /** The percentage of costs shared by the insured person after the deductible is met. */
    protected double coInsuranceRate;
    /** This is the amount the policyholder pays for the insurance policy. */
    protected double premiumAmount;
    /** The amount of money the insurance policy will pay out. */
    protected double insurancePayout;
    /** The name of the insurance policy. */
    protected String insuranceName;
    /** A description of the insurance policy. */
    protected String insuranceDescription;

    // Self-reference for method chaining
    /**
     * Returns a self-reference to the builder for method chaining.
     *
     * @return A reference to the builder subclass.
     */
    protected abstract T self();

    /**
     * Sets the policy ID.
     *
     * @param policyId The unique policy ID.
     * @return The builder instance.
     */
    public T policyId(String policyId) {
        this.policyId = policyId;
        return self();
    }

    /**
     * Sets the insurance provider name.
     *
     * @param provider The name of the insurance provider.
     * @return The builder instance.
     */
    public T provider(String provider) {
        this.insuranceProvider = provider;
        return self();
    }

    /**
     * Sets the deductible amount.
     *
     * @param deductible The deductible amount.
     * @return The builder instance.
     */
    public T deductible(double deductible) {
        this.deductible = deductible;
        return self();
    }

    /**
     * Sets the insurance status.
     *
     * @param status The insurance policy status.
     * @return The builder instance.
     */
    public T status(InsuranceStatus status) {
        this.insuranceStatus = status;
        return self();
    }

    /**
     * Sets the start date of the insurance policy.
     *
     * @param startDate The start date.
     * @return The builder instance.
     */
    public T startDate(LocalDate startDate) {
        this.startDate = startDate;
        return self();
    }

    /**
     * Sets the end date of the insurance policy.
     *
     * @param endDate The end date.
     * @return The builder instance.
     */
    public T endDate(LocalDate endDate) {
        this.endDate = endDate;
        return self();
    }

    /**
     * Sets the co-insurance rate.
     *
     * @param coInsuranceRate The co-insurance rate as a percentage.
     * @return The builder instance.
     */
    public T coInsuranceRate(double coInsuranceRate) {
        this.coInsuranceRate = coInsuranceRate;
        return self();
    }

    /**
     * Sets the premium amount.
     *
     * @param premium The premium amount.
     * @return The builder instance.
     */
    public T premium(double premium) {
        this.premiumAmount = premium;
        return self();
    }

    /**
     * Sets the insurance payout amount.
     *
     * @param payout The payout amount.
     * @return The builder instance.
     */
    public T payout(double payout) {
        this.insurancePayout = payout;
        return self();
    }

    /**
     * Sets the insurance name.
     *
     * @param insuranceName The name of the insurance.
     * @return The builder instance.
     */
    public T insuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
        return self();
    }

    /**
     * Sets the insurance description.
     *
     * @param insuranceDescription The insurance policy description.
     * @return The builder instance.
     */
    public T insuranceDescription(String insuranceDescription) {
        this.insuranceDescription = insuranceDescription;
        return self();
    }

    /**
     * Validates required fields before building the insurance policy.
     *
     * @throws IllegalStateException If any required field is missing or invalid.
     */
    protected void validateFields() {
        if (insuranceName == null || insuranceName.trim().isEmpty()) {
            throw new IllegalStateException("Insurance name is required");
        }
        if (policyId == null || policyId.trim().isEmpty()) {
            throw new IllegalStateException("Policy ID is required");
        }
        if (insuranceProvider == null || insuranceProvider.trim().isEmpty()) {
            throw new IllegalStateException("Insurance provider is required");
        }
        if (insuranceDescription == null || insuranceDescription.trim().isEmpty()) {
            throw new IllegalStateException("Insurance description is required");
        }
        if (insuranceStatus == null) {
            throw new IllegalStateException("Insurance status is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalStateException("Valid start and end dates are required");
        }
    }

    /**
     * Builds and returns an instance of the insurance policy.
     *
     * @return An instance of InsurancePolicy.
     */
    public abstract InsurancePolicy build();
}
