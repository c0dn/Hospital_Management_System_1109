package policy;

import java.time.LocalDate;
import utils.DataGenerator;

/**
 * A generic superclass for building different types of insurance policies.
 * @param <T> The type of the subclass builder to enable method chaining.
 */
public abstract class InsuranceBuilder<T extends InsuranceBuilder<T>> {
    protected static final DataGenerator dataGenerator = DataGenerator.getInstance();

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

    InsuranceBuilder() {}

    /**
     * Returns the current instance of the builder class.
     * Exists so chaining is possible
     *
     * @return The current instance of type T.
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

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

    public T withRandomBaseData() {
        this.insuranceProvider = dataGenerator.getRandomInsuranceCompany();
        this.deductible = dataGenerator.generateDeductible();
        this.insuranceStatus = dataGenerator.getRandomEnum(InsuranceStatus.class);
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusYears(1);
        this.coInsuranceRate = dataGenerator.generateCoInsuranceRate();
        this.premiumAmount = dataGenerator.generatePremium();
        this.insurancePayout = 100000 + dataGenerator.generatePremium() * 100; // Higher payout based on premium
        return self();
    }

    public abstract InsurancePolicy build();
}
