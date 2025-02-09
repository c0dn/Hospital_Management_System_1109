package policy;

import java.time.LocalDate;

/**
 * A generic superclass for building different types of insurance policies.
 * @param <T> The type of the subclass builder to enable method chaining.
 */
public abstract class InsuranceBuilder<T extends InsuranceBuilder<T>> {

    protected String policyId;
    protected String insuranceProvider;
    protected double deductible;
    protected InsuranceStatus insuranceStatus;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected double coInsuranceRate;
    protected double premiumAmount;
    protected double insurancePayout;
    protected String insuranceName;
    protected String insuranceDescription;

    // Self-reference for method chaining
    protected abstract T self();

    public T policyId(String policyId) {
        this.policyId = policyId;
        return self();
    }

    public T provider(String provider) {
        this.insuranceProvider = provider;
        return self();
    }

    public T deductible(double deductible) {
        this.deductible = deductible;
        return self();
    }

    public T status(InsuranceStatus status) {
        this.insuranceStatus = status;
        return self();
    }

    public T startDate(LocalDate startDate) {
        this.startDate = startDate;
        return self();
    }

    public T endDate(LocalDate endDate) {
        this.endDate = endDate;
        return self();
    }

    public T coInsuranceRate(double coInsuranceRate) {
        this.coInsuranceRate = coInsuranceRate;
        return self();
    }

    public T premium(double premium) {
        this.premiumAmount = premium;
        return self();
    }

    public T payout(double payout) {
        this.insurancePayout = payout;
        return self();
    }

    public T insuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
        return self();
    }

    public T insuranceDescription(String insuranceDescription) {
        this.insuranceDescription = insuranceDescription;
        return self();
    }

    protected void validateFields() {
        if (policyId == null || policyId.trim().isEmpty()) {
            throw new IllegalStateException("Policy ID is required");
        }
        if (insuranceProvider == null || insuranceProvider.trim().isEmpty()) {
            throw new IllegalStateException("Insurance provider is required");
        }
        if (deductible <= 0) {
            throw new IllegalStateException("Deductible must be greater than 0");
        }
        if (insuranceStatus == null) {
            throw new IllegalStateException("Insurance status is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalStateException("Valid start and end dates are required");
        }
        if (coInsuranceRate < 0 || coInsuranceRate > 1) {
            throw new IllegalStateException("Co-insurance rate must be between 0 and 1");
        }
        if (premiumAmount <= 0) {
            throw new IllegalStateException("Premium amount must be greater than 0");
        }
        if (insurancePayout <= 0) {
            throw new IllegalStateException("Insurance payout must be greater than 0");
        }
    }

    public abstract InsurancePolicy build();
}
