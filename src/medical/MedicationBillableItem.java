package medical;

import billing.BillableItem;
import policy.BenefitType;
import policy.ClaimableItem;

import java.math.BigDecimal;

public class MedicationBillableItem implements BillableItem, ClaimableItem {
    private final Medication medication;
    private final int quantity;

    public MedicationBillableItem(Medication medication, int quantity, boolean isInpatient) {
        this.medication = medication;
        this.quantity = quantity;
    }

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


    @Override
    public BigDecimal getCharges() {
        return getUnsubsidisedCharges();
    }

    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        return isInpatient ? BenefitType.HOSPITALIZATION : BenefitType.OUTPATIENT_TREATMENTS;
    }

    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return String.format("Medication: %s (%s) x %d %s",
                medication.name,
                medication.category,
                quantity,
                medication.unitDescription
        );
    }
}
