package medical;

import billing.BillableItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Consultation {
    private String consultationId;
    private ConsultationType type;
    private String doctorId;
    private LocalDateTime consultationTime;
    private BigDecimal consultationFee;
    private List<DiagnosticCode> diagnosticCodes;
    private List<ProcedureCode> procedureCodes;
    private Map<Medication, Integer> prescriptions;
    private String notes;


    /**
     * Returns all related charges as separate BillableItems
     * This includes diagnostics, procedures, and medications with their quantities
     */
    public List<BillableItem> getRelatedBillableItems() {
        List<BillableItem> items = new ArrayList<>();

        // Add diagnostics
        if (diagnosticCodes != null) {
            items.addAll(diagnosticCodes);
        }

        // Add procedures
        if (procedureCodes != null) {
            items.addAll(procedureCodes);
        }

        // Add medications with their quantities as MedicationBillableItem
        if (prescriptions != null) {
            prescriptions.forEach((medication, quantity) ->
                    items.add(new MedicationBillableItem(medication, quantity)));
        }

        return items;
    }


    public BigDecimal calculateCharges() {
        BigDecimal total = consultationFee;

        if (diagnosticCodes != null) {
            total = total.add(diagnosticCodes.stream()
                    .map(DiagnosticCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (procedureCodes != null) {
            total = total.add(procedureCodes.stream()
                    .map(ProcedureCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (prescriptions != null) {
            total = total.add(prescriptions.entrySet().stream()
                    .map(entry -> entry.getKey().calculateCost(entry.getValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return total;
    }

    public String getCategory() {
        return switch (type) {
            case EMERGENCY -> "EMERGENCY_CONSULTATION";
            case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
            case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
            case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
        };
    }

}



record MedicationBillableItem(Medication medication, int quantity) implements BillableItem {

    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return medication.calculateCost(quantity);
    }

    @Override
    public String getBillItemDescription() {
        return medication.name + " x " + quantity;
    }

    @Override
    public String getBillItemCategory() {
        return "MEDICATION";
    }

    @Override
    public String getBillingItemCode() {
        return "MED-" + medication.drugCode;
    }
}
