package policy;

import java.time.LocalDate;

public abstract class InsurancePolicy {
    private String policyId;
    private String insuranceProvider;
    private double deductible;
    private InsuranceStatus insuranceStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private double coInsuranceRate;

    public InsurancePolicy(String policyId, String insuranceProvider, double deductible,
                           InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                           double coInsuranceRate) {

        this.policyId = policyId;
        this.insuranceProvider = insuranceProvider;
        this.deductible = deductible;
        this.insuranceStatus = insuranceStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coInsuranceRate = coInsuranceRate;
    }

    // getters
    public String getPolicyId() { return policyId; }

    public String getInsuranceProvider() { return insuranceProvider; }

    public double getDeductible() { return deductible; }

    public InsuranceStatus getInsuranceStatus() { return insuranceStatus; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public double getCoInsuranceRate() { return coInsuranceRate; }
}
