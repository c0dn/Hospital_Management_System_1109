package policy;

import java.time.LocalDate;
import java.util.Random;

public class InsuranceBuilder {

    private static final Random random = new Random();

    private String policyId;
    private String provider;
    private double deductible;
    private InsuranceStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private double coInsuranceRate;
    private double premium;
    private double payout;
    private String insuranceName;  // Insurance Name (Policy Name)
    private String insuranceDescription;  // Insurance Description

    public InsuranceBuilder() {}

    /**
     * Returns the current instance of the builder class for chaining.
     */

    public InsuranceBuilder self() {
        return this;
    }

    public InsuranceBuilder policyId(String policyId) {
        this.policyId = policyId;
        return self();
    }

    public InsuranceBuilder provider(String provider) {
        this.provider = provider;
        return self();
    }

    public InsuranceBuilder deductible(double deductible) {
        this.deductible = deductible;
        return self();
    }

    public InsuranceBuilder status(InsuranceStatus status) {
        this.status = status;
        return self();
    }

    public InsuranceBuilder startDate(LocalDate startDate) {
        this.startDate = startDate;
        return self();
    }

    public InsuranceBuilder endDate(LocalDate endDate) {
        this.endDate = endDate;
        return self();
    }

    public InsuranceBuilder coInsuranceRate(double coInsuranceRate) {
        this.coInsuranceRate = coInsuranceRate;
        return self();
    }

    public InsuranceBuilder premium(double premium) {
        this.premium = premium;
        return self();
    }

    public InsuranceBuilder payout(double payout) {
        this.payout = payout;
        return self();
    }

    public InsuranceBuilder insuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
        return self();
    }

    public InsuranceBuilder insuranceDescription(String insuranceDescription) {
        this.insuranceDescription = insuranceDescription;
        return self();
    }


    // Helper methods to generate random data
    private static String generatePolicyId() {
        return String.format("POL%06d", random.nextInt(1000000));
    }


    private static double generateDeductible() {
        return random.nextDouble() * (5000 - 500) + 500;
    }

    private static double generateCoInsuranceRate() {
        return random.nextDouble() * (0.3 - 0.1) + 0.1;
    }

    private static double generatePremiumAmount() {
        return random.nextDouble() * (1000 - 100) + 100;
    }

    private static double generateInsurancePayout() {
        return random.nextDouble() * (50000 - 5000) + 5000;
    }

    private static LocalDate[] generatePolicyDates() {
        LocalDate startDate = LocalDate.now().minusDays(random.nextInt(365));
        LocalDate endDate = startDate.plusYears(1 + random.nextInt(4)); // 1 to 5 years duration
        return new LocalDate[]{startDate, endDate};
    }

    // Finalize the InsurancePolicy build
    public InsurancePolicy build() {
        validateFields();

        return new InsurancePolicy(
                policyId, provider, deductible, status, startDate, endDate,
                coInsuranceRate, premium, payout, insuranceName, insuranceDescription
        );
    }

    // Validate required fields before building the policy
    private void validateFields() {
        if (policyId == null || policyId.trim().isEmpty()) {
            throw new IllegalStateException("Policy ID is required");
        }
        if (provider == null || provider.trim().isEmpty()) {
            throw new IllegalStateException("Insurance provider is required");
        }
        if (deductible <= 0) {
            throw new IllegalStateException("Deductible must be greater than 0");
        }
        if (status == null) {
            throw new IllegalStateException("Insurance status is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalStateException("Valid start and end dates are required");
        }
        if (coInsuranceRate < 0 || coInsuranceRate > 1) {
            throw new IllegalStateException("Co-insurance rate must be between 0 and 1");
        }
        if (premium <= 0) {
            throw new IllegalStateException("Premium amount must be greater than 0");
        }
        if (payout <= 0) {
            throw new IllegalStateException("Insurance payout must be greater than 0");
        }
    }
}

