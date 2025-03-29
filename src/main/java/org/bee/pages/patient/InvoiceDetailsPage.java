package org.bee.pages.patient;

import org.bee.controllers.BillController;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.billing.PaymentMethod;
import org.bee.pages.ObjectDetailsPage;
import org.bee.ui.InputHelper;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.utils.detailAdapters.BillDetailsAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Consumer;

public class InvoiceDetailsPage extends UiBase {

    private final Bill bill;
    private final IObjectDetailsAdapter<Bill> adapter;
    private final Runnable onChangeCallback;
    private final static BillController billController = BillController.getInstance();

    public InvoiceDetailsPage(Bill bill, IObjectDetailsAdapter<Bill> adapter, Runnable onChangeCallback) {
        this.bill = bill;
        this.adapter = adapter;
        this.onChangeCallback = onChangeCallback;
    }

    @Override
    protected View createView() {
        ObjectDetailsPage<Bill> detailsPage = new ObjectDetailsPage<>(bill, adapter);
        return detailsPage.createView();
    }

    @Override
    public void OnViewCreated(View parentView) {
        BillingStatus status = bill.getStatus();
        setUpActionButtons(parentView, status);
        canvas.setRequireRedraw(true);
    }

    private void setupPaymentOptions(View parentView) {
        parentView.attachUserInput("Record Payment", input -> {
            promptForPaymentMethod(paymentMethod -> {
                promptForPaymentAmount(bill.getOutstandingBalance(), paymentMethod);
            });
        });
    }

    private void promptForPaymentMethod(Consumer<PaymentMethod> callback) {
        String[] paymentMethods = {"CASH", "CREDIT_CARD", "PAYNOW"};

        try {
            int methodIndex = InputHelper.getValidIndex(canvas.getTerminal(),
                    "Select payment method:", 1, paymentMethods.length);

            String selectedMethod = paymentMethods[methodIndex - 1];
            PaymentMethod paymentMethod = PaymentMethod.valueOf(selectedMethod);

            callback.accept(paymentMethod);
        } catch (Exception e) {
            showError("Error selecting payment method", e);
        }
    }

    /**
     * Prompts the user to enter a payment amount
     */
    private void promptForPaymentAmount(BigDecimal maxAmount, PaymentMethod paymentMethod) {
        try {
            maxAmount = BigDecimal.valueOf(maxAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());
            double amount = InputHelper.getValidDouble(canvas.getTerminal(),
                    "Enter payment amount (up to $" + formatCurrency(maxAmount) + "):",
                    0.01, maxAmount.doubleValue());

            BigDecimal paymentAmount = new BigDecimal(amount);

            if (paymentAmount.compareTo(bill.getOutstandingBalance()) >= 0) {
                bill.recordFullPayment(paymentMethod);
                saveChangesAndRefresh("Full payment of $" + formatCurrency(paymentAmount) + " recorded. Bill status updated to PAID.");
            } else {
                bill.recordPartialPayment(paymentAmount, paymentMethod);
                saveChangesAndRefresh("Partial payment of $" + formatCurrency(paymentAmount) + " recorded. " +
                        "Outstanding balance: $" + formatCurrency(bill.getOutstandingBalance()));
            }
        } catch (Exception e) {
            showError("Error recording payment", e);
        }
    }

    private void saveChangesAndRefresh(String message) {
        billController.saveData();

        View refreshedView = createView();
        navigateToView(refreshedView);

        setUpActionButtons(refreshedView, bill.getStatus());

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    private void setUpActionButtons(View parentView, BillingStatus status) {
        if ((status == BillingStatus.OVERDUE) || (status == BillingStatus.PARTIALLY_PAID) || (status == BillingStatus.PAYMENT_PENDING)){
            setupPaymentOptions(parentView);
        }
    }

    private void saveChangesAndNotify(String message) {
        billController.saveData();

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    private void showError(String message, Exception e) {
        canvas.setSystemMessage(message + ": " + e.getMessage(),
                SystemMessageStatus.ERROR);
        canvas.setRequireRedraw(true);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount.doubleValue());
    }

}
