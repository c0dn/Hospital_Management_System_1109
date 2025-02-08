package billing;


import policy.InsurancePolicy;
import policy.InsuranceStatus;

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
    private InsurancePolicy insurancePolicy;
    private BigDecimal insuranceCoverage;
    private BigDecimal patientResponsibility;


    Bill(BillBuilder builder) {
        this.billId = builder.billId;
        this.patientId = builder.patientId;
        this.billDate = builder.billDate;
        this.lineItems = new ArrayList<>();
        this.categorizedCharges = new HashMap<>();
        this.status = BillingStatus.DRAFT;
    }


    public void addLineItem(BillableItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("BillableItem cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        lineItems.add(new BillingItem(item, quantity));
        recalculateTotals();
    }

    public void calculateInsuranceCoverage() {
        if (insurancePolicy == null ||
                insurancePolicy.getInsuranceStatus() != InsuranceStatus.ACTIVE) {
            patientResponsibility = getTotalAmount();
            insuranceCoverage = BigDecimal.ZERO;
            return;
        }

        BigDecimal totalAmount = getTotalAmount();
        BigDecimal deductible = BigDecimal.valueOf(insurancePolicy.getDeductible());

        // Apply deductible
        BigDecimal afterDeductible = totalAmount.subtract(deductible);
        if (afterDeductible.compareTo(BigDecimal.ZERO) <= 0) {
            insuranceCoverage = BigDecimal.ZERO;
            patientResponsibility = totalAmount;
            return;
        }

        // Calculate insurance portion
        BigDecimal coInsuranceRate = BigDecimal.valueOf(insurancePolicy.getCoInsuranceRate());
        insuranceCoverage = afterDeductible.multiply(coInsuranceRate);
        patientResponsibility = totalAmount.subtract(insuranceCoverage);

        // Update status
        status = BillingStatus.INSURANCE_PENDING;
    }


    public BigDecimal getTotalAmount() {
        return categorizedCharges.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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