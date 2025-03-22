package org.bee.hms.billing;

import java.math.BigDecimal;

import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.MedicationBillableItem;
import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.medical.WardStay;
import org.bee.utils.JSONReadable;
import org.bee.utils.JSONWritable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MedicationBillableItem.class, name = "medication"),
    @JsonSubTypes.Type(value = ProcedureCode.class, name = "procedure"),
    @JsonSubTypes.Type(value = DiagnosticCode.class, name = "diagnostic"),
    @JsonSubTypes.Type(value = WardStay.class, name = "wardStay")
})
public interface BillableItem extends JSONWritable, JSONReadable {
    BigDecimal getUnsubsidisedCharges();
    String getBillItemDescription();
    String getBillItemCategory();
    String getBillingItemCode();
}
