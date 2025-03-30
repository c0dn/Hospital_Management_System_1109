package org.bee.hms.telemed;

import org.bee.hms.billing.BillableItem;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.ClaimableItem;
import org.bee.utils.JSONSerializable;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;

/**
 * Represents a specific billable item for the telemedicine consultation fee.
 * Implements BillableItem for billing and ClaimableItem for insurance claims.
 */
public class TelemedicineFeeItem implements BillableItem, ClaimableItem, JSONSerializable {

    private static final String BILLING_CODE = "TELEMED-FEE";
    private static final String DESCRIPTION = "Telemedicine Consultation Fee";
    private static final String CATEGORY = "CONSULTATION";
    private static final BigDecimal FEE_AMOUNT = new BigDecimal("50.00");
    private static final BenefitType BENEFIT_TYPE = BenefitType.OUTPATIENT_TREATMENTS;

    @JsonCreator
    public TelemedicineFeeItem() {
    }


    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return FEE_AMOUNT;
    }

    @Override
    public String getBillItemDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getBillItemCategory() {
        return CATEGORY;
    }

    @Override
    public String getBillingItemCode() {
        return BILLING_CODE;
    }


    @Override
    public BigDecimal getCharges() {
        return getUnsubsidisedCharges();
    }

    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        return BENEFIT_TYPE;
    }

    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return DESCRIPTION;
    }

    @Override
    public String toString() {
        return String.format("%s: %s [%s]", BILLING_CODE, DESCRIPTION, FEE_AMOUNT);
    }
}
