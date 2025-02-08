package policy;

import humans.Patient;
import java.time.LocalDate;

/**
 * Represents a Critical Illness Insurance Policy that extends the general InsurancePolicy.
 */
public class CriticalIllnessInsurance extends InsurancePolicy {
    private double insurancePayout;

        // Constructor


        public CriticalIllnessInsurance(String policyId, String insuranceProvider, double deductible,
                                        InsuranceStatus insuranceStatus, LocalDate startDate, LocalDate endDate,
                                        double coInsuranceRate, double premiumAmount, Patient policyHolder, double insurancePayout) {
            super(policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate, coInsuranceRate, premiumAmount, policyHolder);
            this.insurancePayout = insurancePayout;
        }

        public double getInsurancePayout() {
            return insurancePayout;
        }

        //method to check if patient have
        @Override
        public void displayPolicyDetails() {
            super.displayPolicyDetails();
            System.out.format("Insurance Payout: $%.2f%n", insurancePayout);
        }
    }
