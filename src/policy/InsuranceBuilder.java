package policy;

import humans.Patient;
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
    private Patient policyHolder;
    private double payout;

    // Constructor
    public InsuranceBuilder() {}

    public InsuranceBuilder policyId(String policyId) {
        this.policyId = policyId;
        return this;
    }

    public InsuranceBuilder provider(String provider) {
        this.provider = provider;
        return this;
    }

    public InsuranceBuilder deductible(double deductible) {
        this.deductible = deductible;
        return this;
    }

    public InsuranceBuilder status(InsuranceStatus status) {
        this.status = status;
        return this;
    }

    public InsuranceBuilder startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public InsuranceBuilder endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public InsuranceBuilder coInsuranceRate(double coInsuranceRate) {
        this.coInsuranceRate = coInsuranceRate;
        return this;
    }

    public InsuranceBuilder premium(double premium) {
        this.premium = premium;
        return this;
    }

    public InsuranceBuilder policyHolder(Patient policyHolder) {
        this.policyHolder = policyHolder;
        return this;
    }

    public InsuranceBuilder payout(double payout) {
        this.payout = payout;
        return this;
    }

    public InsuranceBuilder withRandomData(Patient policyHolder) {
        this.policyId = generatePolicyId();
        this.provider = getRandomInsuranceProvider();
        this.deductible = generateDeductible();
        this.status = generateInsuranceStatus();
        LocalDate[] dates = generatePolicyDates();
        this.startDate = dates[0];
        this.endDate = dates[1];
        this.coInsuranceRate = generateCoInsuranceRate();
        this.premium = generatePremiumAmount();
        this.payout = generateInsurancePayout();
        this.policyHolder = policyHolder;
        return this;
    }

    private static String generatePolicyId() {
        return String.format("POL%06d", random.nextInt(1000000));
    }

    private static String getRandomInsuranceProvider() {
        String[] providers = {
                "Great Eastern", "Prudential", "AIA Singapore", "NTUC Income", "AXA Insurance"
        };
        return getRandomElement(providers);
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

    private static InsuranceStatus generateInsuranceStatus() {
        return getRandomEnum(InsuranceStatus.class);
    }

    private static LocalDate[] generatePolicyDates() {
        LocalDate startDate = LocalDate.now().minusDays(random.nextInt(365));
        LocalDate endDate = startDate.plusYears(1 + random.nextInt(4)); // 1 to 5 years duration
        return new LocalDate[]{startDate, endDate};
    }

    private static <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }

    private static <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[random.nextInt(values.length)];
    }

    public InsurancePolicy build() {
        validateFields();

        return new InsurancePolicy(
                policyId, provider, deductible, status, startDate, endDate,
                coInsuranceRate, premium, policyHolder, payout
        );
    }

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
