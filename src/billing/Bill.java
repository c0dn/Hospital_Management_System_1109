package billing;

//test
//test2
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bill {
    private String billId;
    private String patientId;
    private LocalDateTime billDate;
    private List<BillingItem> lineItems;
    private Map<String, BigDecimal> categorizedCharges;
    private BillingStatus status;

    Bill(BillBuilder builder) {
        this.billId = builder.billId;
        this.patientId = builder.patientId;
        this.billDate = builder.billDate;
        this.lineItems = new ArrayList<>();
        this.categorizedCharges = new HashMap<>();
        this.status = BillingStatus.DRAFT;
    }


    public void addLineItem(BillableItem item, int quantity) {
        lineItems.add(new BillingItem(item, quantity));
        recalculateTotals();
    }

    public BigDecimal getTotalByCategory(String category) {
        return categorizedCharges.getOrDefault(category, BigDecimal.ZERO);
    }

    private void recalculateTotals() {
        categorizedCharges.clear();

        for (BillingItem item : lineItems) {
            String category = item.getCategory();
            BigDecimal totalPrice = item.getTotalPrice();
            categorizedCharges.put(category,
                    categorizedCharges.getOrDefault(category, BigDecimal.ZERO).add(totalPrice));
        }
    }
}