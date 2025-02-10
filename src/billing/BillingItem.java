package billing;

import java.math.BigDecimal;

public class BillingItem {

    private BillableItem item;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public BillingItem(BillableItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = item.getPrice();
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getBillEntry() {
        return String.format("%s %s - %s %s%n%s", item.getCode(), item.getDescription(), quantity, unitPrice, item.getCategory());
    }

    public String getCategory() {
        return item.getCategory();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

}
