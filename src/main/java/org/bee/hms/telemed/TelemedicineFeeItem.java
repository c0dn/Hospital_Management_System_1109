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

    /** Standard billing code for telemedicine fees */
    private static final String BILLING_CODE = "TELEMED-FEE";

    /** Description of the telemedicine service */
    private static final String DESCRIPTION = "Telemedicine Consultation Fee";

    /** Category for billing purposes */
    private static final String CATEGORY = "CONSULTATION";

    /** Fixed fee amount for telemedicine consultation */
    private static final BigDecimal FEE_AMOUNT = new BigDecimal("50.00");

    /** Benefit type classification for insurance */
    private static final BenefitType BENEFIT_TYPE = BenefitType.OUTPATIENT_TREATMENTS;

    /**
     * Creates a new TelemedicineFeeItem instance
     *
     */
    @JsonCreator
    public TelemedicineFeeItem() {
    }

    /**
     * Returns the full unsubsidized charges for this telemedicine consultation.
     * @return the fixed fee amount
     */
    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return FEE_AMOUNT;
    }

    /**
     * Returns the description of this billable item.
     * @return "Telemedicine Consultation Fee"
     */
    @Override
    public String getBillItemDescription() {
        return DESCRIPTION;
    }

    /**
     * Returns the category of this billable item.
     * @return "CONSULTATION"
     */
    @Override
    public String getBillItemCategory() {
        return CATEGORY;
    }

    /**
     * Returns the standardized billing code for telemedicine fees.
     * @return "TELEMED-FEE"
     */
    @Override
    public String getBillingItemCode() {
        return BILLING_CODE;
    }

    /**
     * Returns the charges for this item (same as unsubsidized charges).
     * @return the fixed fee amount
     */
    @Override
    public BigDecimal getCharges() {
        return getUnsubsidisedCharges();
    }

    /**
     * Resolves the benefit type for insurance claims.
     * @param isInpatient not used for telemedicine
     * @return {@link BenefitType#OUTPATIENT_TREATMENTS}
     */
    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        return BENEFIT_TYPE;
    }

    /**
     * Returns the benefit description for insurance claims.
     * @param isInpatient not used for telemedicine
     * @return "Telemedicine Consultation Fee"
     */
    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return DESCRIPTION;
    }

    /**
     * Returns a string representation of this telemedicine fee item.
     * @return formatted string containing code, description and amount
     */

    @Override
    public String toString() {
        return String.format("%s: %s [%s]", BILLING_CODE, DESCRIPTION, FEE_AMOUNT);
    }
}
