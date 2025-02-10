/* package billing;

import policy.InsuranceStatus;
import wards.Ward;
import humans.Patient;
import policy.InsurancePolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the calculations of the medical bills.
 * The class includes calculations for total charges, insurance coverage and total payable.
 /*

//currently missing diagnosisCharges, consultationFees, medicationCosts, procedureCharges
//submitting insurance claim should be referencing total charges (invoice) and insuranceCoverage
//approved insurance claim should be referencing total payable and insuranceCoverage

/* public class Methods {
    private static Map<String, Bill> billingRecords = new HashMap<>();

    public static void generateBill(Patient patient, Ward ward, InsurancePolicy insurancePolicy, int days,
                                    double procedureCharges, double diagnosisCharges,
                                    double consultationFees, double medicationCosts) {

        double dailyRate = ward.getDailyRate();
        double deductible = insurancePolicy.getDeductible();
        double totalCharge = (dailyRate * days) + procedureCharges + diagnosisCharges + consultationFees + medicationCosts;
        double remainingAfterDeductible = totalCharge - deductible;
        double insuranceCoverage = remainingAfterDeductible * insurancePolicy.getCoInsuranceRate();
        double totalPayable = totalCharge - insuranceCoverage;

        String policyId = insurancePolicy.getPolicyId();
        String insuranceProvider = insurancePolicy.getInsuranceProvider();
        InsuranceStatus insuranceStatus = insurancePolicy.getInsuranceStatus();

        Bill bill = new Bill(patient.getPatientId(), totalCharge, insuranceCoverage, totalPayable,
                policyId, insuranceProvider, insuranceStatus, dailyRate, days,
                procedureCharges, diagnosisCharges, consultationFees, medicationCosts,
                remainingAfterDeductible); // ward.getName(),

        System.out.println(" **Invoice for Patient: " + patient.getName() + " (ID: " + patient.getPatientId() + ")**");
        System.out.println("Ward: " + " (Daily Rate: $" + dailyRate + ")");
        System.out.println("Insurance Provider: " + insuranceProvider + " (Policy ID: " + policyId + ", Status: " + insuranceStatus + ")");
        System.out.println("Total Charges: $" + totalCharge);
        System.out.println("Deductible amount: $" + deductible);
        System.out.println("Insurance Coverage: $" + insuranceCoverage);
        System.out.println("Amount Payable by Patient: $" + totalPayable);

        billingRecords.put(patient.getPatientId(), bill);

    }
    public static void displayAllBills() {
        System.out.println("\n **All Patient Billing Records**");
        for (Bill bill : billingRecords.values()) {
            System.out.println(bill);
        }
    }

    public static Bill displayPatientBill(String patientId) {
        return billingRecords.get(patientId);
    }
} */
