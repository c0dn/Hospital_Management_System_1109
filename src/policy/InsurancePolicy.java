package policy;

import humans.Patient;

import java.time.LocalDate;

/**
 * Represents an insurance policy associated with a patient.
 * It contains details such as insurance provider name, deductible and expiry date.
 */

public class InsurancePolicy {
    private final String policyId;
    private Patient policyholderName;
    private String insuranceProvider;
    private double deductible;
    private InsuranceStatus insuranceStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private double coInsuranceRate;
    private double premiumAmount;

    public InsurancePolicy(String policyId,Patient policyholderName, String insuranceProvider, double deductible,
                           InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                           double coInsuranceRate, double premiumAmount) {

        this.policyId = policyId;
        this.policyholderName = policyholderName;
        this.insuranceProvider = insuranceProvider;
        this.deductible = deductible;
        this.insuranceStatus = insuranceStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coInsuranceRate = coInsuranceRate;
        this.premiumAmount = premiumAmount;
    }

    // getters
    public String getPolicyId() { return policyId; }

    public Patient getPolicyholderName() { return policyholderName; }

    public String getInsuranceProvider() { return insuranceProvider; }

    public double getDeductible() { return deductible; }

    public InsuranceStatus getInsuranceStatus() { return insuranceStatus; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public double getCoInsuranceRate() { return coInsuranceRate; }

    public double getPremiumAmount() { return premiumAmount; }

}
