package billing;

import java.math.BigDecimal;
import java.util.List;

public interface BillableItem {
    BigDecimal getUnsubsidisedCharges();

    String getBillItemDescription();

    String getBillItemCategory();

    String getBillingItemCode();

}