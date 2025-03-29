package org.bee.pages.patient;

import org.bee.controllers.BillController;
import org.bee.controllers.HumanController;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.humans.Patient;
import org.bee.pages.clerk.billing.ViewAllBillsPage;
import org.bee.ui.*;
import org.bee.ui.views.*;
import org.bee.utils.ReflectionHelper;
import org.bee.utils.detailAdapters.BillDetailsAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewAllInvoicePage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final BillController billController = BillController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;

    private FilterOption currentFilter = FilterOption.ALL;
    private SortOption currentSort = SortOption.DATE_DESC;

    private enum FilterOption {
        ALL("All Bills"),
//        PAYMENT_PENDING("Pending Payment"),
        OVERDUE("Overdue Bills"),
        PAID("Paid Bills"),
        PARTIALLY_PAID("Partially Paid Bills");

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
    protected View createView() {
        return selectInvoiceToView();
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    private void changeFilter(FilterOption newFilter) {
        currentFilter = newFilter;
        View refreshedView = selectInvoiceToView();
        navigateToView(refreshedView);
    }

    /**
     * Changes the current sort order and refreshes the view
     */
    private void changeSort(SortOption newSort) {
        currentSort = newSort;
        View refreshedView = selectInvoiceToView();
        navigateToView(refreshedView);
    }

    private View selectInvoiceToView() {
        Patient currentPatient = (Patient) humanController.getLoggedInUser();
        billController.loadData();
        List<Bill> bills = billController.getAllItems().stream()
                .filter(b -> {
                    Patient patient = b.getPatient();
                    return patient == null ||
                            patient.getNricFin().equals(currentPatient.getNricFin());
                })
                .toList();

        if (bills.isEmpty()) {
            return new TextView(canvas, "No bills found.", Color.YELLOW);
        }

        List<AbstractPaginatedView.MenuOption> menuOptions = new ArrayList<>();
        for (Bill b : bills) {
            String billId = ReflectionHelper.propertyAccessor("billId", "N/A").apply(b);
            LocalDateTime billDate = b.getBillDate();
            String dateString = billDate != null ? dateFormatter.format(billDate) : "Not available";
            String status = b.getStatus() != null ? formatEnum(b.getStatus().toString()) : "Unknown";
            String coloredStatus = status;
            if (status.equals(formatEnum("OVERDUE"))) {
                coloredStatus = colorText(status, Color.UND_RED);
            } else if (status.equals(formatEnum("PARTIALLY PAID"))) {
                coloredStatus = colorText(status, Color.YELLOW);
            } else if (status.equals(formatEnum("PAID"))) {
                coloredStatus = colorText(status, Color.GREEN);
            }
//            else if (status.equals(formatEnum("PAYMENT_PENDING"))) {
//                coloredStatus = colorText(status, Color.RED);
//            }
            String optionText = String.format("%s - %s [%s]",
                    billId, dateString, coloredStatus);

            menuOptions.add(new AbstractPaginatedView.MenuOption(billId, optionText, b));
        }

        String title = String.format("View Bill Details\nFilter: %s | Sort: %s",
                currentFilter.getDisplayName(),
                currentSort.getDisplayName());

        PaginatedMenuView paginatedMenuView = new PaginatedMenuView(
                canvas,
                title,
                "Select a bill to view details",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedMenuView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Bill selected = (Bill) option.getData();
                    displaySelectedBill(selected);
                } else {
                    canvas.setSystemMessage("Error: Invalid selection",
                            SystemMessageStatus.ERROR);
                    canvas.setRequireRedraw(true);
                }
            } catch (Exception e) {
                canvas.setSystemMessage("Error processing selection: " + e.getMessage(),
                        SystemMessageStatus.ERROR);
                canvas.setRequireRedraw(true);
                System.err.println("Exception in selection callback: " + e.getMessage());
            }
        });

        paginatedMenuView.attachCustomOption("Change Filter", input -> {
            promptForFilterOption();
        });

        paginatedMenuView.attachCustomOption("Change Sort Order", input -> {
            promptForSortOption();
        });

        return paginatedMenuView;
    }

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

    private List<Bill> filterBills(List<Bill> bills, FilterOption filter) {
        return switch (filter) {
//            case PAYMENT_PENDING -> bills.stream()
//                    .filter(b -> b.getStatus() == BillingStatus.PAYMENT_PENDING)
//                    .collect(Collectors.toList());
            case OVERDUE -> bills.stream()
                    .filter(b -> b.getStatus() == BillingStatus.OVERDUE)
                    .collect(Collectors.toList());
            case PAID -> bills.stream()
                    .filter(b -> b.getStatus() == BillingStatus.PAID ||
                            b.getStatus() == BillingStatus.PARTIALLY_PAID)
                    .collect(Collectors.toList());
            case PARTIALLY_PAID -> bills.stream()
                    .filter(b -> b.getStatus() == BillingStatus.PARTIALLY_PAID)
                    .collect(Collectors.toList());
            default -> new ArrayList<>(bills);
        };
    }

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

    public void displaySelectedBill(Bill bill) {
        BillDetailsAdapter adapter = new BillDetailsAdapter();
        InvoiceDetailsPage detailsPage = new InvoiceDetailsPage(bill, adapter, this::refreshView);
        ToPage(detailsPage);
    }

    private void refreshView() {
        View refreshedView = selectInvoiceToView();
        navigateToView(refreshedView);
    }

}
