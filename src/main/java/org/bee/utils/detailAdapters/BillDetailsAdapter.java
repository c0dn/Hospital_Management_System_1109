package org.bee.utils.detailAdapters;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingItemLine;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.utils.ReflectionHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Adapter for displaying Bill details.
 * This adapter configures an ObjectDetailsView to show bill information
 * organized into relevant sections.
 */
public class BillDetailsAdapter implements IObjectDetailsAdapter<Bill> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public ObjectDetailsView configureView(ObjectDetailsView view, Bill bill) {
        view.setSectionWidth(80);

        // Bill basic information section
        ObjectDetailsView.Section basicSection = view.addSection("Bill Information");
        basicSection.addField(new ObjectDetailsView.Field<>("Bill ID", b ->
                ReflectionHelper.propertyAccessor("billId", "Not available").apply(b)));

        basicSection.addField(new ObjectDetailsView.Field<>("Date", b -> {
            LocalDateTime date = (LocalDateTime) ReflectionHelper.propertyAccessor("billDate", null).apply(b);
            return date != null ? DATE_FORMATTER.format(date) : "Not available";
        }));

        basicSection.addField(new ObjectDetailsView.Field<Bill>("Status", b ->
                Optional.ofNullable(b.getStatus()).map(Object::toString).orElse("Not available")));

        basicSection.addField(new ObjectDetailsView.Field<Bill>("Payment Method", b ->
                Optional.ofNullable(b.getPaymentMethod()).map(Object::toString).orElse("Not specified")));

        // Patient information section
        ObjectDetailsView.Section patientSection = view.addSection("Patient Information");
        patientSection.addField(new ObjectDetailsView.Field<Bill>("Patient", b -> {
            Patient patient = b.getPatient();
            return patient != null ? patient.getName() + " (ID: " + patient.getPatientId() + ")" : "Not available";
        }));

        patientSection.addField(new ObjectDetailsView.Field<Bill>("Residential Status", b -> {
            Patient patient = b.getPatient();
            if (patient == null) return "Not available";

            Object residentialStatus = ReflectionHelper.propertyAccessor("residentialStatus", null).apply(patient);
            return residentialStatus != null ? residentialStatus.toString() : "Not available";
        }));

        // Insurance section
        ObjectDetailsView.Section insuranceSection = view.addSection("Insurance Information");
        insuranceSection.addField(new ObjectDetailsView.Field<Bill>("Insurance Provider", b -> {
            InsurancePolicy policy = b.getInsurancePolicy();
            return policy != null ? policy.getInsuranceProvider().getProviderName() : "No insurance";
        }));

        insuranceSection.addField(new ObjectDetailsView.Field<Bill>("Policy Number", b -> {
            InsurancePolicy policy = b.getInsurancePolicy();
            return policy != null ? policy.getPolicyNumber() : "No policy";
        }));

        insuranceSection.addField(new ObjectDetailsView.Field<Bill>("Policy Active", b -> {
            InsurancePolicy policy = b.getInsurancePolicy();
            return policy != null ? (policy.isActive() ? "Yes" : "No") : "No policy";
        }));

        // Bill amounts section
        ObjectDetailsView.Section amountsSection = view.addSection("Bill Amounts");
        amountsSection.addField(new ObjectDetailsView.Field<Bill>("Total Amount", b ->
                formatCurrency(b.getTotalAmount())));

        amountsSection.addField(new ObjectDetailsView.Field<Bill>("Discount", b -> {
            Optional<Double> discountPercentage = b.getDiscountPercentage();
            return discountPercentage.map(aDouble -> String.format("%s (%.0f%%)",
                    formatCurrency(b.getDiscountAmount()),
                    aDouble * 100)).orElseGet(() -> formatCurrency(b.getDiscountAmount()));
        }));

        amountsSection.addField(new ObjectDetailsView.Field<Bill>("Discounted Total", b ->
                formatCurrency(b.getDiscountedTotal())));

        amountsSection.addField(new ObjectDetailsView.Field<Bill>("Tax", b ->
                formatCurrency(b.getTaxAmount())));

        amountsSection.addField(new ObjectDetailsView.Field<Bill>("Grand Total", b ->
                formatCurrency(b.getGrandTotal())));

        ObjectDetailsView.Section itemsSection = view.addSection("Line Items");

        @SuppressWarnings("unchecked")
        List<BillingItemLine> lineItems = (List<BillingItemLine>) ReflectionHelper.propertyAccessor("lineItems", null).apply(bill);

        if (lineItems != null && !lineItems.isEmpty()) {
            for (int i = 0; i < lineItems.size(); i++) {
                final int itemIndex = i;
                itemsSection.addField(new ObjectDetailsView.Field<>("Item " + (i + 1), b -> {
                    @SuppressWarnings("unchecked")
                    List<BillingItemLine> items = (List<BillingItemLine>) ReflectionHelper.propertyAccessor("lineItems", null).apply(b);
                    if (items != null && items.size() > itemIndex) {
                        BillingItemLine item = items.get(itemIndex);
                        return String.format("%s - %s: %s",
                                item.getItem().getBillingItemCode(),
                                item.getItem().getBillItemDescription(),
                                formatCurrency(item.getTotalPrice()));
                    }
                    return "Item information not available";
                }));
            }
        } else {
            itemsSection.addField(new ObjectDetailsView.Field<>("Items", b -> "No line items available"));
        }

        return view;
    }

    /**
     * Format a BigDecimal as a currency string
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount.doubleValue());
    }

    @Override
    public String getObjectTypeName() {
        return "Bill";
    }
}