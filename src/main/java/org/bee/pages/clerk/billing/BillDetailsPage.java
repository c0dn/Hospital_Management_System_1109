package org.bee.pages.clerk.billing;

import org.bee.controllers.BillController;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.billing.PaymentMethod;
import org.bee.hms.policy.InsuranceCoverageResult;
import org.bee.pages.ObjectDetailsPage;
import org.bee.ui.*;
import org.bee.ui.details.IObjectDetailsAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;

/**
 * Page for viewing and managing bill details.
 * Provides different actions based on the current bill status.
 */
public class BillDetailsPage extends UiBase {
    private final Bill bill;
    private final IObjectDetailsAdapter<Bill> adapter;
    private final Runnable onChangeCallback;
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

    @Override
    public View createView() {
        ObjectDetailsPage<Bill> detailsPage = new ObjectDetailsPage<>(bill, adapter);
        return detailsPage.createView();
    }

    @Override
    public void OnViewCreated(View parentView) {
        BillingStatus status = bill.getStatus();
        setupActionButtons(parentView, status);
        canvas.setRequireRedraw(true);
    }

    /**
     * Sets up action buttons based on the current bill status
     */
    private void setupActionButtons(View parentView, BillingStatus status) {
        if (status.isInPreparation()) {
            parentView.attachUserInput("Submit for Processing", input -> {
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
                parentView.attachUserInput("Calculate Insurance Coverage", input -> {
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

            setupPaymentOptions(parentView);
        }
        else if (status == BillingStatus.INSURANCE_PENDING) {
            parentView.attachUserInput("Approve Insurance Claim", input -> {
                try {
                    bill.approveInsurance();
                    saveChangesAndRefresh("Insurance claim has been approved.");
                } catch (Exception e) {
                    showError("Error approving insurance", e);
                }
            });

            parentView.attachUserInput("Reject Insurance Claim", input -> {
                try {
                    bill.rejectInsurance();
                    saveChangesAndRefresh("Insurance claim has been rejected.");
                } catch (Exception e) {
                    showError("Error rejecting insurance", e);
                }
            });
        }
        else if (status == BillingStatus.INSURANCE_APPROVED) {
            setupPaymentOptions(parentView);
        }
        else if (status == BillingStatus.INSURANCE_REJECTED) {
            parentView.attachUserInput("Mark in Dispute", input -> {
                try {
                    bill.markInDispute();
                    saveChangesAndRefresh("Bill has been marked as in dispute.");
                } catch (Exception e) {
                    showError("Error marking bill in dispute", e);
                }
            });

            setupPaymentOptions(parentView);
        }
        else if (status == BillingStatus.PARTIALLY_PAID) {
            BigDecimal remainingAmount = bill.getOutstandingBalance();

            parentView.attachUserInput("Record Full Payment", input -> {
                promptForPaymentMethod(paymentMethod -> {
                    try {
                        bill.recordFullPayment(paymentMethod);
                        saveChangesAndRefresh("Full payment recorded. Bill status updated to PAID.");
                    } catch (Exception e) {
                        showError("Error recording full payment", e);
                    }
                });
            });

            parentView.attachUserInput("Record Additional Payment", input -> {
                promptForPaymentMethod(paymentMethod -> {
                    promptForPaymentAmount(remainingAmount, paymentMethod);
                });
            });

            setupRefundOption(parentView);
        }
        else if (status == BillingStatus.PAID) {
            setupRefundOption(parentView);
        }
        else if (status == BillingStatus.REFUND_PENDING) {
            parentView.attachUserInput("Complete Refund", input -> {
                try {
                    bill.completeRefund();
                    saveChangesAndRefresh("Refund has been completed. Bill status updated to REFUNDED.");
                } catch (Exception e) {
                    showError("Error completing refund", e);
                }
            });
        }
        else if (status == BillingStatus.IN_DISPUTE) {
            parentView.attachUserInput("Resolve Dispute", input -> {
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
            parentView.attachUserInput("Cancel Bill", input -> {
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
    }

    /**
     * Sets up payment options for bills that can accept payments
     */
    private void setupPaymentOptions(View parentView) {
        parentView.attachUserInput("Record Payment", input -> {
            promptForPaymentMethod(paymentMethod -> {
                promptForPaymentAmount(bill.getGrandTotal(), paymentMethod);
            });
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
     * Sets up refund option for applicable bills
     */
    private void setupRefundOption(View parentView) {
        parentView.attachUserInput("Initiate Refund", input -> {
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
     * Shows an error message
     */
    private void showError(String message, Exception e) {
        canvas.setSystemMessage(message + ": " + e.getMessage(),
                SystemMessageStatus.ERROR);
        canvas.setRequireRedraw(true);
    }

    /**
     * Saves changes and refreshes the view
     */
    private void saveChangesAndRefresh(String message) {
        billController.saveData();

        View refreshedView = createView();
        navigateToView(refreshedView);

        setupActionButtons(refreshedView, bill.getStatus());

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    /**
     * Saves changes and shows a notification without refreshing the view
     */
    private void saveChangesAndNotify(String message) {
        billController.saveData();

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount.doubleValue());
    }
}
