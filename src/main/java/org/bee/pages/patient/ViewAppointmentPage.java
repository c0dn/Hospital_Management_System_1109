package org.bee.pages.patient;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.BillController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.*;
import org.bee.utils.detailAdapters.AppointmentDetailsViewAdapter;
import org.bee.utils.detailAdapters.BillDetailsAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViewAppointmentPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final BillController billController = BillController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();
    private static final int ITEMS_PER_PAGE = 7;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected View createView() {
        return viewAllAppointments();
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    private View viewAllAppointments() {
        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Patient patient) {
            List<Appointment> appointments = appointmentController.getAppointmentsForPatient(patient);

            if (appointments.isEmpty()) {
                return new TextView(canvas, "No appointments found.", Color.YELLOW);
            }

            List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
            for (Appointment appointment : appointments) {
                LocalDateTime time = appointment.getAppointmentTime();
                String formattedDate = time != null ? dateFormatter.format(time) : "Not scheduled";
                String reason = appointment.getReason() != null ? appointment.getReason() : "Not specified";
                AppointmentStatus status = appointment.getAppointmentStatus();

                String displayText;
                if (time != null) {
                    String timeInfo;
                    LocalDateTime now = LocalDateTime.now();

                    if (time.isBefore(now)) {
                        timeInfo = colorText(formattedDate, Color.RED) + " (Past)";
                    } else if (time.isBefore(now.plusDays(1))) {
                        timeInfo = colorText(formattedDate, Color.YELLOW) + " (Today)";
                    } else if (time.isBefore(now.plusDays(3))) {
                        timeInfo = colorText(formattedDate, Color.GREEN) + " (Soon)";
                    } else {
                        timeInfo = formattedDate;
                    }

                    if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED) {
                        appointment.setAppointmentStatus(AppointmentStatus.PAYMENT_PENDING);
                    }
                    String statusText = formatEnum(status.toString());
                    String coloredStatus = switch (status) {
                        case COMPLETED -> colorText(formatEnum("PAYMENT_PENDING"), Color.UND_RED);
                        case ACCEPTED -> colorText(statusText, Color.CYAN);
                        case PENDING -> colorText(statusText, Color.YELLOW);
                        case DECLINED, CANCELED -> colorText(statusText, Color.RED);
                        case PAYMENT_PENDING -> colorText(statusText, Color.UND_RED);
                        case PAID -> colorText(statusText, Color.UND_GREEN);
                    };

                    displayText = String.format("%s - %s - %s", timeInfo, reason, coloredStatus);
                } else {
                    displayText = String.format("Not scheduled - %s - %s", reason, status);
                }

                menuOptions.add(new PaginatedMenuView.MenuOption(
                        appointment.getAppointmentId(),
                        displayText,
                        appointment));
            }

            PaginatedMenuView paginatedView = new PaginatedMenuView(
                    canvas,
                    "Your Appointments",
                    "Select an appointment to view details",
                    menuOptions,
                    ITEMS_PER_PAGE,
                    Color.CYAN
            );

            paginatedView.setSelectionCallback(option -> {
                try {
                    if (option != null && option.getData() != null) {
                        Appointment selectedAppointment = (Appointment) option.getData();
                        displayAppointmentDetails(selectedAppointment, canvas.getCurrentView());
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

        return new TextView(canvas, "Access denied. Only patients can view appointments.", Color.RED);
    }

    private void displayAppointmentDetails(Appointment appointment, View previousView) {
        View appointmentView = createAppointmentCompositeView(appointment);
        canvas.setCurrentView(appointmentView);
        canvas.setRequireRedraw(true);
    }

    private View createAppointmentCompositeView(Appointment appointment) {
        DetailsView<Appointment> detailsView = (DetailsView<Appointment>) createAppointmentDetailsView(appointment);

        CompositeView compositeView = new CompositeView(canvas, "", Color.CYAN);
        compositeView.addView(detailsView);

        if (humanController.getLoggedInUser() instanceof Patient) {
            MenuView actionMenu = new MenuView(canvas, "", Color.CYAN, false, true);

            MenuView.MenuSection actionSection = actionMenu.addSection("");

            if (appointment.getAppointmentStatus() == AppointmentStatus.PAYMENT_PENDING) {
                actionSection.addOption(1, "Make Payment");
                actionMenu.attachMenuOptionInput(1, "Make Payment", input -> {
                    Optional<Bill> billOpt = findBillForAppointment(appointment, BillingStatus.PAYMENT_PENDING);
                    if (billOpt.isPresent()) {
                        InvoiceDetailsPage paymentPage = new InvoiceDetailsPage(
                                billOpt.get(),
                                new BillDetailsAdapter(),
                                this::refreshCurrentView // Refresh after payment attempt
                        );
                        ToPage(paymentPage);
                    } else {
                        canvas.setSystemMessage("Could not find the associated bill for payment.", SystemMessageStatus.ERROR);
                        canvas.setRequireRedraw(true);
                    }
                });
            } else if (appointment.getAppointmentStatus() == AppointmentStatus.PAID){
                actionSection.addOption(1, "View Invoice");
                actionMenu.attachMenuOptionInput(1, "View Invoice", input -> {
                    Optional<Bill> billOpt = findBillForAppointment(appointment, BillingStatus.PAID);
                    if (billOpt.isPresent()) {
                        InvoiceDetailsPage viewPage = new InvoiceDetailsPage(
                                billOpt.get(),
                                new BillDetailsAdapter(),
                                null
                        );
                        ToPage(viewPage);
                    } else {
                        billOpt = findBillForAppointment(appointment, BillingStatus.PARTIALLY_PAID);
                        if (billOpt.isPresent()) {
                            InvoiceDetailsPage viewPage = new InvoiceDetailsPage(
                                    billOpt.get(),
                                    new BillDetailsAdapter(),
                                    null
                            );
                            ToPage(viewPage);
                        } else {
                            canvas.setSystemMessage("Could not find the associated invoice.", SystemMessageStatus.ERROR);
                            canvas.setRequireRedraw(true);
                        }
                    }
                });
            }

            compositeView.addView(actionMenu);
        }

        return compositeView;
    }

    private void refreshCurrentView() {
        View currentView = canvas.getCurrentView();
        if (currentView instanceof CompositeView compositeView) {
            // We need to find the appointment associated with this view to rebuild it.
            // This requires the DetailsView within the CompositeView to hold the data object.
            Optional<DetailsView<Appointment>> detailsViewOpt = compositeView.getChildViews().stream()
                    .filter(v -> v instanceof DetailsView)
                    .map(v -> (DetailsView<Appointment>) v)
                    .findFirst();

            if (detailsViewOpt.isPresent()) {
                Appointment currentAppointment = detailsViewOpt.get().getDataObject(); // Assumes DetailsView has a getDataObject() method
                if (currentAppointment != null) {
                    View refreshedCompositeView = createAppointmentCompositeView(currentAppointment);
                    navigateToView(refreshedCompositeView); // Use navigateToView to replace current view without backstack change
                    canvas.setSystemMessage("View refreshed.", SystemMessageStatus.INFO);
                    return; // Exit after refreshing
                }
            }
        }
        // Fallback if refresh fails
        canvas.setSystemMessage("Could not refresh view automatically.", SystemMessageStatus.WARNING);
        OnBackPressed(); // Go back to the list as a fallback
    }


    private Optional<Bill> findBillForAppointment(Appointment appointment, BillingStatus expectedBillStatus) {
        Patient patient = appointment.getPatient();
        String appointmentId = appointment.getAppointmentId();
        if (patient == null || appointmentId == null) {
            return Optional.empty();
        }

        List<Bill> patientBills = billController.getBillsForPatient(patient);

        return patientBills.stream()
                .filter(bill -> appointmentId.equals(bill.getSourceAppointmentId()))
                .filter(bill -> bill.getStatus() == expectedBillStatus)
                .findFirst();
    }

    private View createAppointmentDetailsView(Appointment appointment) {
        AppointmentDetailsViewAdapter adapter = new AppointmentDetailsViewAdapter();

        DetailsView<Appointment> detailsView = new DetailsView<>(
                canvas,
                "APPOINTMENT INFORMATION",
                appointment,
                Color.CYAN,
                adapter
        );

        return detailsView;
    }
}