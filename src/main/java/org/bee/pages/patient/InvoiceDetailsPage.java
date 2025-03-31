package org.bee.pages.patient;

import org.bee.controllers.BillController;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.billing.PaymentMethod;
import org.bee.ui.*;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.MenuView;
import org.bee.ui.views.ObjectDetailsView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Consumer;
/**

 A page that displays detailed information about a patient's invoice.
 This class extends UiBase and implements functionality for viewing and managing bill details.
 */

public class InvoiceDetailsPage extends UiBase {

    private final Bill bill;
    private final IObjectDetailsAdapter<Bill> adapter;
    private final Runnable onChangeCallback;
    private final static BillController billController = BillController.getInstance();

    /**
     * Constructs an {@code InvoiceDetailsPage} with the given bill, adapter, and change callback.
     *
     * @param bill             The bill associated with this invoice details page.
     * @param adapter          The adapter used to manage bill details.
     * @param onChangeCallback A callback function that is triggered when changes occur.
     */
    public InvoiceDetailsPage(Bill bill, IObjectDetailsAdapter<Bill> adapter, Runnable onChangeCallback) {
        this.bill = bill;
        this.adapter = adapter;
        this.onChangeCallback = onChangeCallback;
    }

    @Override
    protected View createView() {
        if (Objects.isNull(bill)) {
            return getBlankListView("No Bill", "Bill data missing you donkey");
        }

        CompositeView compositeView = new CompositeView(this.canvas, adapter.getObjectTypeName() + " Details", Color.CYAN); //

        ObjectDetailsView detailsView = new ObjectDetailsView(
                this.canvas,
                "",
                bill,
                Color.CYAN
        );
        adapter.configureView(detailsView, bill);
        MenuView menuView = new MenuView(this.canvas, "", Color.CYAN, false, true);
        setUpActionButtons(menuView, bill.getStatus());

        compositeView.addView(detailsView);
        compositeView.addView(menuView);

        return compositeView;
    }

    @Override
    public void OnViewCreated(View parentView) {
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
            // Ensure both amounts have the same scale for accurate comparison
            maxAmount = maxAmount.setScale(2, RoundingMode.HALF_UP);
            double amount = InputHelper.getValidDouble(canvas.getTerminal(),
                    "Enter payment amount (up to $" + formatCurrency(maxAmount) + "):",
                    0.01, maxAmount.doubleValue());

            BigDecimal paymentAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
            BigDecimal outstandingBalance = bill.getOutstandingBalance().setScale(2, RoundingMode.HALF_UP);

            if (paymentAmount.compareTo(outstandingBalance) == 0) {
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

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    private void setUpActionButtons(MenuView menuView, BillingStatus status) {

        MenuView.MenuSection actionSection = menuView.addSection("Available Actions");
        int optionIndex = 1;

        if ((status == BillingStatus.OVERDUE) || (status == BillingStatus.PARTIALLY_PAID) || (status == BillingStatus.PAYMENT_PENDING)){
            actionSection.addOption(optionIndex, "Record Payment");
            menuView.attachMenuOptionInput(optionIndex, "Record Payment", input -> {
                promptForPaymentMethod(paymentMethod -> {
                    promptForPaymentAmount(bill.getOutstandingBalance(), paymentMethod);
                });
            });
            optionIndex++;
        }
        menuView.setNumericOptionMaxRange(optionIndex - 1);
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
