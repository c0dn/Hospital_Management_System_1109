package billing;

import java.math.BigDecimal;

public interface BillableItem {
    BigDecimal getPrice();
    String getDescription();
    String getCategory();
    String getCode();
}