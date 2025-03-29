package org.bee.pages.clerk.billing;

import org.bee.controllers.BillController;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.ui.*;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;
import org.bee.utils.detailAdapters.BillDetailsAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page for viewing all bills in the system.
 * This page displays a paginated table of bills with their details.
 */
public class ViewAllBillsPage extends UiBase {

    private static final BillController billController = BillController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;

    private FilterOption currentFilter = FilterOption.ALL;
    private SortOption currentSort = SortOption.DATE_DESC;

    /**
     * Filter options for bills
     */
    private enum FilterOption {
        ALL("All Bills"),
        PENDING("Pending Bills"),
        INSURANCE("Insurance-Related"),
        PAID("Paid Bills"),
        REQUIRES_ACTION("Requires Action");

        private final String displayName;

        FilterOption(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Sort options for bills
     */
    private enum SortOption {
        DATE_DESC("Date (Newest First)"),
        DATE_ASC("Date (Oldest First)"),
        AMOUNT_DESC("Amount (Highest First)"),
        AMOUNT_ASC("Amount (Lowest First)");

        private final String displayName;

        SortOption(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public View createView() {
        return selectBillToView();
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Changes the current filter and refreshes the view
     */
    private void changeFilter(FilterOption newFilter) {
        currentFilter = newFilter;
        View refreshedView = selectBillToView();
        navigateToView(refreshedView);
    }

    /**
     * Changes the current sort order and refreshes the view
     */
    private void changeSort(SortOption newSort) {
        currentSort = newSort;
        View refreshedView = selectBillToView();
        navigateToView(refreshedView);
    }

    /**
     * Display a selection menu of bills with current filter and sort options
     */
    private View selectBillToView() {
        billController.loadData();
        List<Bill> allBills = billController.getAllItems();

        if (allBills.isEmpty()) {
            return new TextView(canvas, "No bills found in the system.", Color.YELLOW);
        }

        List<Bill> filteredBills = filterBills(allBills, currentFilter);

        if (filteredBills.isEmpty()) {
            return new TextView(canvas,
                    "No bills found matching filter: " + currentFilter.getDisplayName(),
                    Color.YELLOW);
        }

        List<Bill> sortedBills = sortBills(filteredBills, currentSort);

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
        for (Bill bill : sortedBills) {
            String patientName = bill.getPatient() != null ? bill.getPatient().getName() : "Unknown Patient";
            LocalDateTime billDate = bill.getBillDate();
            String formattedDate = billDate != null ? dateFormatter.format(billDate) : "Unknown Date";
            BigDecimal billAmount = bill.getGrandTotal();
            String status = bill.getStatus() != null ? bill.getStatus().getDisplayName() : "Unknown Status";

            String paymentInfo = "";
            if (bill.getStatus() == BillingStatus.PARTIALLY_PAID) {
                paymentInfo = String.format(" (Paid: $%s, Due: $%s)",
                        formatCurrency(bill.getSettledAmount()), formatCurrency(bill.getOutstandingBalance()));
            }

            String coloredStatus = status;
            if (bill.getStatus() != null) {
                if (bill.getStatus().isFinalized()) {
                    coloredStatus = colorText(status, Color.GREEN);
                } else if (bill.getStatus().requiresAction()) {
                    coloredStatus = colorText(status, Color.RED);
                } else if (bill.getStatus().isInsuranceRelated()) {
                    coloredStatus = colorText(status, Color.CYAN);
                }
            }

            String optionText = String.format("Bill #%s - %s - %s - $%s - %s%s",
                    bill.getBillId(), patientName, formattedDate, formatCurrency(billAmount), coloredStatus, paymentInfo);

            menuOptions.add(new PaginatedMenuView.MenuOption(bill.getBillId(), optionText, bill));
        }

        String title = String.format("View Bill Details\nFilter: %s | Sort: %s",
                currentFilter.getDisplayName(),
                currentSort.getDisplayName());

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                title,
                "Available Bills",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        // Set selection callback
        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Bill selectedBill = (Bill) option.getData();
                    openBillDetails(selectedBill);
                } else {
                    canvas.setSystemMessage("Error: Invalid selection", SystemMessageStatus.ERROR);
                    canvas.setRequireRedraw(true);
                }
            } catch (Exception e) {
                canvas.setSystemMessage("Error processing selection: " + e.getMessage(), SystemMessageStatus.ERROR);
                canvas.setRequireRedraw(true);
            }
        });

        // Add filter and sort options after the pagination navigation options
        paginatedView.attachCustomOption("Change Filter", input -> {
            promptForFilterOption();
        });

        paginatedView.attachCustomOption("Change Sort Order", input -> {
            promptForSortOption();
        });

        return paginatedView;
    }

    /**
     * Prompt user to select a filter option
     */
    private void promptForFilterOption() {
        String[] options = new String[FilterOption.values().length];
        for (int i = 0; i < FilterOption.values().length; i++) {
            options[i] = FilterOption.values()[i].getDisplayName();
        }

        int selected = InputHelper.getValidIndex(canvas.getTerminal(),
                "Select filter option:", 1, options.length);

        changeFilter(FilterOption.values()[selected - 1]);
    }

    /**
     * Prompt user to select a sort option
     */
    private void promptForSortOption() {
        String[] options = new String[SortOption.values().length];
        for (int i = 0; i < SortOption.values().length; i++) {
            options[i] = SortOption.values()[i].getDisplayName();
        }

        int selected = InputHelper.getValidIndex(canvas.getTerminal(),
                "Select sort option:", 1, options.length);

        changeSort(SortOption.values()[selected - 1]);
    }

    /**
     * Filter bills based on the selected filter option
     */
    private List<Bill> filterBills(List<Bill> bills, FilterOption filter) {
        return switch (filter) {
            case PENDING -> bills.stream()
                    .filter(b -> b.getStatus() == BillingStatus.PENDING ||
                            b.getStatus() == BillingStatus.SUBMITTED ||
                            b.getStatus() == BillingStatus.DRAFT)
                    .collect(Collectors.toList());
            case INSURANCE -> bills.stream()
                    .filter(b -> b.getStatus().isInsuranceRelated())
                    .collect(Collectors.toList());
            case PAID -> bills.stream()
                    .filter(b -> b.getStatus() == BillingStatus.PAID ||
                            b.getStatus() == BillingStatus.PARTIALLY_PAID)
                    .collect(Collectors.toList());
            case REQUIRES_ACTION -> bills.stream()
                    .filter(b -> b.getStatus().requiresAction() ||
                            b.getStatus() == BillingStatus.INSURANCE_PENDING ||
                            b.getStatus() == BillingStatus.REFUND_PENDING)
                    .collect(Collectors.toList());
            default -> new ArrayList<>(bills);
        };
    }

    /**
     * Sort bills based on the selected sort option
     */
    private List<Bill> sortBills(List<Bill> bills, SortOption sort) {
        List<Bill> sortedBills = new ArrayList<>(bills);

        switch (sort) {
            case DATE_DESC:
                sortedBills.sort((b1, b2) -> {
                    if (b1.getBillDate() == null) return 1;
                    if (b2.getBillDate() == null) return -1;
                    return b2.getBillDate().compareTo(b1.getBillDate());
                });
                break;
            case DATE_ASC:
                sortedBills.sort((b1, b2) -> {
                    if (b1.getBillDate() == null) return 1;
                    if (b2.getBillDate() == null) return -1;
                    return b1.getBillDate().compareTo(b2.getBillDate());
                });
                break;
            case AMOUNT_DESC:
                sortedBills.sort((b1, b2) -> b2.getGrandTotal().compareTo(b1.getGrandTotal()));
                break;
            case AMOUNT_ASC:
                sortedBills.sort(Comparator.comparing(Bill::getGrandTotal));
                break;
        }

        return sortedBills;
    }

    /**
     * Open the bill details page for a specific bill
     */
    private void openBillDetails(Bill bill) {
        BillDetailsAdapter adapter = new BillDetailsAdapter();
        BillDetailsPage detailsPage = new BillDetailsPage(bill, adapter, this::refreshView);
        ToPage(detailsPage);
    }

    /**
     * Refresh the current view after returning from bill details
     */
    private void refreshView() {
        View refreshedView = selectBillToView();
        OnViewCreated(refreshedView);
        canvas.setCurrentView(refreshedView);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        BigDecimal rounded = amount.setScale(2, RoundingMode.HALF_UP);
        return String.format("$%.2f", rounded);
    }

}