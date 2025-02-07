package billing;


import wards.Ward;
import humans.Patient;
import policy.InsurancePolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the calculations of the medical bills.
 * The class includes calculations for total charges, insurance coverage and total payable.
 */

//currently missing diagnosisCharges, consultationFees, medicationCosts, procedureCharges
//submitting insurance claim should be referencing total charges (invoice) and insuranceCoverage
//approved insurance claim should be referencing total payable and insuranceCoverage


public class Billing {
    private static Map<String, Bill> billingRecords = new HashMap<>();

    public static void generateBill(Patient patient, int days, double procedureCharges,
                                    double diagnosisCharges, double consultationFees, double medicationCosts) {

        double dailyRate = ward.getDailyRate();
        double totalCharge = (dailyRate * days) + procedureCharges + diagnosisCharges + consultationFees + medicationCosts;
        double insuranceCoverage = totalCharge * insurancePolicy.getcoInsuranceRate();
        double totalPayable = totalCharge - insuranceCoverage;

        String policyId = insurancePolicy.getPolicyId();
        double deductible = insurancePolicy.getDeductible();
        String insuranceProvider = insurancePolicy.getInsuranceProvider();
        String insuranceStatus = insurancePolicy.getInsuranceStatus();

        System.out.println("\n🧾 **Invoice for Patient: " + patient.getName() + " (ID: " + patient.getPatientId() + ")**");
        System.out.println("Ward: " + ward.getName() + " (Daily Rate: $" + dailyRate + ")");
        System.out.println("Total Charge: $" + totalCharge);
        System.out.println("Insurance Covers: $" + insuranceCoverage);
        System.out.println("Amount Payable by Patient: $" + totalPayable);
    }

    public static void displayAllBills() {
        System.out.println("\n📜 **All Patient Billing Records**");
        for (Bill bill : billingRecords.values()) {
            System.out.println(bill);
        }
    }

    public static Bill getBill(String patientId) {
        return billingRecords.get(patientId);
    }
}












