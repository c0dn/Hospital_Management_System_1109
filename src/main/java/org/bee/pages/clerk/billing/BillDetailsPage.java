package org.bee.pages.clerk.billing;

import org.bee.controllers.BillController;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.billing.PaymentMethod;
import org.bee.hms.policy.InsuranceCoverageResult;
import org.bee.ui.*;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.MenuView;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.ui.views.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Page for viewing and managing bill details.
 * Provides different actions based on the current bill status.
 */
public class BillDetailsPage extends UiBase {
    /** Bill being displayed and managed */
    private final Bill bill;

    /** Adapter for customizing bill details display */
    private final IObjectDetailsAdapter<Bill> adapter;

    /** Callback to execute when bill changes */
    private final Runnable onChangeCallback;

    /** Controller for bill operations */
    private final static BillController billController = BillController.getInstance();

    /**
     * Creates a new BillDetailsPage.
     *
     * @param bill The bill to display and manage
     * @param adapter The adapter for configuring bill display
     * @param onChangeCallback A callback to execute when the bill is changed
     */
    public BillDetailsPage(Bill bill, IObjectDetailsAdapter<Bill> adapter, Runnable onChangeCallback) {
        this.bill = bill;
        this.adapter = adapter;
        this.onChangeCallback = onChangeCallback;
    }

    /**
     * Creates the bill details view
     * @return CompositeView containing bill details and action menu, or error view if no bill is selected
     */
    @Override
    public View createView() {
        if (Objects.isNull(bill)) {
            return new TextView(this.canvas, "Error: No bill selected", Color.RED);
        }

        CompositeView compositeView = new CompositeView(this.canvas, "Bill Details", Color.CYAN);

        ObjectDetailsView detailsView = new ObjectDetailsView(
                this.canvas,
                "Bill Details",
                bill,
                Color.CYAN
        );

        adapter.configureView(detailsView, bill);

        MenuView menuView = new MenuView(this.canvas, "", Color.CYAN, false, true);
        BillingStatus status = bill.getStatus();
        setupActionButtons(menuView, status);

        compositeView.addView(detailsView);
        compositeView.addView(menuView);

        return compositeView;
    }

    /**
     * Triggers UI refresh after view creation.
     * @param parentView The parent view container
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Sets up action buttons based on the current bill status
     */
    private void setupActionButtons(MenuView menuView, BillingStatus status) {
        menuView.clearUserInputs();

        MenuView.MenuSection actionSection = menuView.addSection("Available Actions");
        int optionIndex = 1;

        if (status.isInPreparation()) {
            actionSection.addOption(optionIndex, "Submit for Processing");
            menuView.attachMenuOptionInput(optionIndex++, "Submit for Processing", input -> {
                try {
                    bill.submitForProcessing();
                    saveChangesAndNotify("Bill has been submitted for processing successfully!");
                } catch (Exception e) {
                    showError("Error submitting bill", e);
                }
            });
        }
        else if (status == BillingStatus.SUBMITTED) {
            if (bill.getInsurancePolicy() != null && bill.getInsurancePolicy().isActive()) {
                actionSection.addOption(optionIndex, "Calculate Insurance Coverage");
                menuView.attachMenuOptionInput(optionIndex++, "Calculate Insurance Coverage", input -> {
                    try {
                        InsuranceCoverageResult result = bill.calculateInsuranceCoverage();

                        if (result.isApproved()) {
                            saveChangesAndNotify("Insurance coverage calculated successfully. Status updated to INSURANCE_PENDING.");
                        } else {
                            String reason = result.getDenialReason().orElse("Unknown reason");
                            canvas.setSystemMessage("Insurance coverage denied: " + reason,
                                    SystemMessageStatus.WARNING);
                            canvas.setRequireRedraw(true);
                        }
                    } catch (Exception e) {
                        showError("Error calculating insurance coverage", e);
                    }
                });
            }

            setupPaymentOptions(actionSection, menuView, optionIndex++);
        }
        else if (status == BillingStatus.INSURANCE_PENDING) {
            actionSection.addOption(optionIndex, "Approve Insurance Claim");
            menuView.attachMenuOptionInput(optionIndex++, "Approve Insurance Claim", input -> {
                try {
                    bill.approveInsurance();
                    saveChangesAndRefresh("Insurance claim has been approved.");
                } catch (Exception e) {
                    showError("Error approving insurance", e);
                }
            });

            actionSection.addOption(optionIndex, "Reject Insurance Claim");
            menuView.attachMenuOptionInput(optionIndex++, "Reject Insurance Claim", input -> {
                try {
                    bill.rejectInsurance();
                    saveChangesAndRefresh("Insurance claim has been rejected.");
                } catch (Exception e) {
                    showError("Error rejecting insurance", e);
                }
            });
        }
        else if (status == BillingStatus.INSURANCE_APPROVED) {
            setupPaymentOptions(actionSection, menuView, optionIndex++);
        }
        else if (status == BillingStatus.INSURANCE_REJECTED) {
            actionSection.addOption(optionIndex, "Mark in Dispute");
            menuView.attachMenuOptionInput(optionIndex++, "Mark in Dispute", input -> {
                try {
                    bill.markInDispute();
                    saveChangesAndRefresh("Bill has been marked as in dispute.");
                } catch (Exception e) {
                    showError("Error marking bill in dispute", e);
                }
            });

            setupPaymentOptions(actionSection, menuView, optionIndex++);
        }
        else if (status == BillingStatus.PARTIALLY_PAID) {
            BigDecimal remainingAmount = bill.getOutstandingBalance();

            actionSection.addOption(optionIndex, "Record Full Payment");
            menuView.attachMenuOptionInput(optionIndex++, "Record Full Payment", input -> {
                promptForPaymentMethod(paymentMethod -> {
                    try {
                        bill.recordFullPayment(paymentMethod);
                        saveChangesAndRefresh("Full payment recorded. Bill status updated to PAID.");
                    } catch (Exception e) {
                        showError("Error recording full payment", e);
                    }
                });
            });

            actionSection.addOption(optionIndex, "Record Additional Payment");
            menuView.attachMenuOptionInput(optionIndex++, "Record Additional Payment", input -> {
                promptForPaymentMethod(paymentMethod -> {
                    promptForPaymentAmount(remainingAmount, paymentMethod);
                });
            });

            setupRefundOption(actionSection, menuView, optionIndex++);
        }
        else if (status == BillingStatus.PAID) {
            setupRefundOption(actionSection, menuView, optionIndex++);
        }
        else if (status == BillingStatus.REFUND_PENDING) {
            actionSection.addOption(optionIndex, "Complete Refund");
            menuView.attachMenuOptionInput(optionIndex++, "Complete Refund", input -> {
                try {
                    bill.completeRefund();
                    saveChangesAndRefresh("Refund has been completed. Bill status updated to REFUNDED.");
                } catch (Exception e) {
                    showError("Error completing refund", e);
                }
            });
        }
        else if (status == BillingStatus.IN_DISPUTE) {
            actionSection.addOption(optionIndex, "Resolve Dispute");
            menuView.attachMenuOptionInput(optionIndex++, "Resolve Dispute", input -> {
                boolean resolved = InputHelper.getYesNoInput(canvas.getTerminal(),
                        "Was the dispute resolved in favor of the patient? (y/n)");

                try {
                    if (resolved) {
                        if (bill.getSettledAmount().compareTo(BigDecimal.ZERO) > 0) {
                            bill.initiateRefund();
                            saveChangesAndRefresh("Dispute resolved in favor of patient. Refund initiated.");
                        } else {
                            bill.cancelBill();
                            saveChangesAndRefresh("Dispute resolved in favor of patient. Bill cancelled.");
                        }
                    } else {
                        bill.setStatus(BillingStatus.SUBMITTED);
                        saveChangesAndRefresh("Dispute resolved in favor of provider. Bill status reset to SUBMITTED.");
                    }
                } catch (Exception e) {
                    showError("Error resolving dispute", e);
                }
            });
        }

        // Any non-finalized bill can be cancelled
        if (!status.isFinalized()) {
            actionSection.addOption(optionIndex, "Cancel Bill");
            menuView.attachMenuOptionInput(optionIndex++, "Cancel Bill", input -> {
                boolean confirm = InputHelper.getYesNoInput(canvas.getTerminal(),
                        "Are you sure you want to cancel this bill? This action cannot be undone. (y/n)");

                if (confirm) {
                    try {
                        bill.cancelBill();
                        saveChangesAndRefresh("Bill has been cancelled successfully.");
                    } catch (Exception e) {
                        showError("Error cancelling bill", e);
                    }
                } else {
                    canvas.setSystemMessage("Bill cancellation aborted.", SystemMessageStatus.INFO);
                    canvas.setRequireRedraw(true);
                }
            });
        }

        // Set the max range for numeric options
        menuView.setNumericOptionMaxRange(optionIndex - 1);
    }

    /**
     * Sets up payment options for bills that can accept payments
     */
    private void setupPaymentOptions(MenuView.MenuSection actionSection, MenuView menuView, int optionIndex) {
        actionSection.addOption(optionIndex, "Record Payment");
        menuView.attachMenuOptionInput(optionIndex, "Record Payment", input -> {
            promptForPaymentMethod(paymentMethod -> {
                promptForPaymentAmount(bill.getGrandTotal(), paymentMethod);
            });
        });
    }

    /**
     * Sets up refund option for applicable bills
     */
    private void setupRefundOption(MenuView.MenuSection actionSection, MenuView menuView, int optionIndex) {
        actionSection.addOption(optionIndex, "Initiate Refund");
        menuView.attachMenuOptionInput(optionIndex, "Initiate Refund", input -> {
            boolean confirm = InputHelper.getYesNoInput(canvas.getTerminal(),
                    "Are you sure you want to initiate a refund for this bill? (y/n)");

            if (confirm) {
                try {
                    bill.initiateRefund();
                    saveChangesAndRefresh("Refund has been initiated. Bill status updated to REFUND_PENDING.");
                } catch (Exception e) {
                    showError("Error initiating refund", e);
                }
            } else {
                canvas.setSystemMessage("Refund initiation aborted.", SystemMessageStatus.INFO);
                canvas.setRequireRedraw(true);
            }
        });
    }

    /**
     * Prompts the user to select a payment method
     */
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

    /**
     * Shows an error message
     */
    private void showError(String message, Exception e) {
        canvas.setSystemMessage(message + ": " + e.getMessage(),
                SystemMessageStatus.ERROR);
        canvas.setRequireRedraw(true);
    }

    /**
     * Saves changes and returns to the parent page (bill list).
     * This should be used for major status changes where we want to
     * go back to the bill list after completion.
     */
    private void saveChangesAndRefresh(String message) {
        billController.saveData();

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        } else {
            OnBackPressed();
        }
    }

    /**
     * Saves changes and shows a notification without navigating away
     * This should be used for intermediate status updates where we want to
     * remain on the same page after the operation
     */
    private void saveChangesAndNotify(String message) {
        billController.saveData();

        OnBackPressed();
        View refreshedView = createView();
        navigateToView(refreshedView);

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount.doubleValue());
    }
}