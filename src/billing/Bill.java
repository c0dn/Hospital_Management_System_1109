package billing;

/**
 * Represents an individual bill for medical services.
 * This class contains details such as patient ID, ward name and total charges.
 */

public class Bill {
    private String patientId;
    private String wardName;
    private double totalCharge;
    private double insuranceCoverage;
    private double totalPayable;

    public Bill(String patientId, String wardName, double totalCharge, double insuranceCoverage, double totalPayable) {
        this.patientId = patientId;
        this.wardName = wardName;
        this.totalCharge = totalCharge;
        this.insuranceCoverage = insuranceCoverage;
        this.totalPayable = totalPayable;
    }

    @Override
    public String toString() {
        return "Patient ID: " + patientId + ", Ward: " + wardName + ", Total Charge: $" + totalCharge +
                ", Insurance Coverage: $" + insuranceCoverage + ", Amount Payable: $" + totalPayable;
    }
}
