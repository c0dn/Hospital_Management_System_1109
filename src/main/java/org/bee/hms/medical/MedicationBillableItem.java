package org.bee.hms.medical;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.ClaimableItem;
/**
 * Represents a billable item for medication in an insurance claim.
 * <p>
 * This class implements both {@link BillableItem} and {@link ClaimableItem} interfaces,
 * representing a medication  that can be billed as part of an insurance claim.
 * It calculates the charges and provides a description for
 * the billed item, including details about the medication, its quantity, and its category.
 * </p>
 */
public class MedicationBillableItem implements BillableItem, ClaimableItem {
    private final Medication medication;
    private final int quantity;

    /**
     * Constructor for creating a MedicationBillableItem.
     *
     * @param medication The {@link Medication} being billed.
     * @param quantity   The quantity of the medication.
     */
    public MedicationBillableItem(Medication medication, int quantity) {
        this.medication = medication;
        this.quantity = quantity;
    }

    @JsonCreator
    public static MedicationBillableItem create(
            @JsonProperty("medication") Medication medication,
            @JsonProperty("quantity") int quantity) {
        return new MedicationBillableItem(medication, quantity);
    }

    /**
     * Retrieves the unsubsidised charges for the medication.
     *
     * @return A {@link BigDecimal} representing the unsubsidised cost of the medication.
     */
    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return medication.calculateCost(quantity);
    }

    /**
     * Retrieves the description of the billable item.
     *
     * @return A string describing the medication and its quantity.
     */
    @Override
    public String getBillItemDescription() {
        return medication.name + " x " + quantity;
    }

    /**
     * Retrieves the category for the billable item.
     *
     * @return A string representing the category of the billable item ("MEDICATION").
     */
    @Override
    public String getBillItemCategory() {
        return "MEDICATION";
    }

    /**
     * Retrieves the billing item code for the medication.
     *
     * @return A string representing the billing code for the medication.
     */
    @Override
    public String getBillingItemCode() {
        return "MED-" + medication.drugCode;
    }

    /**
     * Retrieves the charges for the medication.
     *
     * @return A {@link BigDecimal} representing the charges for the medication.
     */
    @Override
    public BigDecimal getCharges() {
        return getUnsubsidisedCharges();
    }

    /**
     * Resolves the benefit type for the medication depending on whether the patient is inpatient or outpatient.
     *
     * @param isInpatient Flag indicating whether the patient is inpatient or outpatient.
     * @return The corresponding {@link BenefitType} for the medication.
     */
    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        return isInpatient ? BenefitType.HOSPITALIZATION : BenefitType.OUTPATIENT_TREATMENTS;
    }


    @Override
    public Medication getMedication() {
        return medication;
    }

    /**
     * Retrieves a description of the benefit for the medication.
     *
     * @param isInpatient Flag indicating whether the patient is inpatient or outpatient.
     * @return A string describing the medication and its category.
     */
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
