package org.bee.pages.clerk.insurance;

import org.bee.controllers.BillController;
import org.bee.controllers.ClaimController;
import org.bee.hms.billing.Bill;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.policy.InsuranceCoverageResult;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.pages.clerk.billing.BillDetailsPage;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.MenuView;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;
import org.bee.utils.detailAdapters.BillDetailsAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Page for submitting new insurance claims.
 * This page allows clerks to select a finalized bill and submit it as an insurance claim.
 */
public class NewInsuranceClaimPage extends UiBase {

    /** Manages bill operations and data */
    private static final BillController billController = BillController.getInstance();

    /** Handles insurance claim processing */
    private static final ClaimController claimController = ClaimController.getInstance();

    /** Number of items per page in lists,set to 7 */
    private static final int ITEMS_PER_PAGE = 7;

    /** Formats dates as yyyy-MM-dd HH:mm */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Creates the bill selection view for claim processing.
     * @return View showing bills available for claiming
     */
    @Override
    public View createView() {
        return selectBillForClaim();
    }

    /**
     * Refreshes the UI after view creation.
     * @param parentView The parent view container
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Display a selection menu of bills eligible for insurance claims
     */
    private View selectBillForClaim() {
        List<Bill> eligibleBills = getEligibleBills();

        if (eligibleBills.isEmpty()) {

            return getBlankListView("No Eligible Bills",
                    "No eligible bills found for insurance claims.\nPlease ensure bills are finalized and have valid insurance policies attached.");
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
        for (Bill bill : eligibleBills) {
            String patientName = bill.getPatient() != null ? bill.getPatient().getName() : "Unknown Patient";
            LocalDateTime billDate = bill.getBillDate();
            String formattedDate = billDate != null ? dateFormatter.format(billDate) : "Unknown Date";
            String billAmount = bill.getGrandTotal().toString();
            String insuranceProvider = bill.getInsurancePolicy() != null ?
                    bill.getInsurancePolicy().getInsuranceProvider().getProviderName() : "No Insurance";

            String optionText = String.format("Bill #%s - %s - %s - $%s - %s",
                    bill.getBillId(), patientName, formattedDate, billAmount, insuranceProvider);

            menuOptions.add(new PaginatedMenuView.MenuOption(bill.getBillId(), optionText, bill));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "Select Bill for Insurance Claim",
                "Eligible Bills",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Bill selectedBill = (Bill) option.getData();
                    displayBillDetails(selectedBill);
                } else {
                    canvas.setSystemMessage("Error: Invalid selection", SystemMessageStatus.ERROR);
                    canvas.setRequireRedraw(true);
                }
            } catch (Exception e) {
                canvas.setSystemMessage("Error processing selection: " + e.getMessage(), SystemMessageStatus.ERROR);
                canvas.setRequireRedraw(true);
            }
        });

        return paginatedView;
    }


    /**
     * Get bills that are eligible for insurance claims
     * Eligible bills must be finalized and have an associated insurance policy
     */
    private List<Bill> getEligibleBills() {
        return billController.getAllItems().stream()
                .filter(bill -> bill.getStatus().isSubmitted())
                .filter(bill -> bill.getInsurancePolicy() != null &&
                        bill.getInsurancePolicy().isActive())
                .collect(Collectors.toList());
    }

    /**
     * Display detailed information about the selected bill
     */
    private void displayBillDetails(Bill selectedBill) {
        IObjectDetailsAdapter<Bill> adapter = new BillDetailsAdapter();

        BillDetailsPage detailsPage = new BillDetailsPage(
                selectedBill,
                adapter,
                () -> processClaimForBill(selectedBill)
        );

        ToPage(detailsPage);
    }

    /**
     * Process insurance claim for the selected bill
     */
    private void processClaimForBill(Bill selectedBill) {
        try {
            InsurancePolicy policy = selectedBill.getInsurancePolicy();

            if (policy == null) {
                showResultMessage("No insurance policy associated with this bill.", false);
                return;
            }

            InsuranceProvider provider = policy.getInsuranceProvider();

            if (provider == null) {
                showResultMessage("No insurance provider found for the policy.", false);
                return;
            }

            // Calculate insurance coverage
            InsuranceCoverageResult coverageResult = selectedBill.calculateInsuranceCoverage();

            if (!coverageResult.isApproved()) {
                String reason = coverageResult.getDenialReason().orElse("Unknown reason");
                showResultMessage("Insurance claim denied: " + reason, false);
                return;
            }

            Optional<InsuranceClaim> claimOpt = coverageResult.claim();

            if (claimOpt.isEmpty()) {
                showResultMessage("Failed to create insurance claim.", false);
                return;
            }

            InsuranceClaim claim = claimOpt.get();

            claimController.addClaim(claim);

            boolean submitted = provider.submitClaim(selectedBill.getPatient(), claim);

            if (submitted) {
                showResultMessage("Insurance claim successfully submitted to " +
                        provider.getProviderName() + " for processing.", true);
            } else {
                showResultMessage("Failed to submit claim to insurance provider.", false);
            }

        } catch (Exception e) {
            showResultMessage("Error processing claim: " + e.getMessage(), false);
        }
    }

    /**
     * Display a result message after claim processing
     */
    private void showResultMessage(String message, boolean success) {
        TextView resultView = new TextView(
                canvas,
                message,
                success ? Color.GREEN : Color.RED
        );
        navigateToView(resultView);

        resultView.attachUserInput("Return to Bill Selection", input -> {
            View refreshedView = selectBillForClaim();
            navigateToView(refreshedView);
        });
    }
}
