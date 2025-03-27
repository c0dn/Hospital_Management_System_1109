package org.bee.pages.patient;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.PaginatedView;
import org.bee.ui.views.TableView;
import org.bee.ui.views.TextView;
import org.bee.utils.ReflectionHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;

public class ViewAppointmentPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
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
            System.out.println(appointments);

            if (appointments.isEmpty()) {
                return new TextView(canvas, "No appointments found.", Color.YELLOW);
            }

            BiFunction<List<Appointment>, Integer, TableView<Appointment>> tableFactory =
                    (pageItems, pageNum) -> createAppointmentTableView(pageItems);

            return new PaginatedView<>(
                    canvas,
                    "\nTelemedicine Appointments",
                    appointments,
                    ITEMS_PER_PAGE,
                    tableFactory,
                    Color.CYAN
            );
        }
        return new TextView(canvas, "Access denied. Only patients can view appointments.", Color.RED);
    }

    private TableView<Appointment> createAppointmentTableView(List<Appointment> appointments) {
        TableView<Appointment> tableView = new TableView<>(canvas, "", Color.CYAN);

        tableView.showRowNumbers(true)
                .addColumn("Appointment ID", 15, a -> (String) ReflectionHelper.propertyAccessor("appointmentId", null).apply(a))

                .addColumn("Consult Reason", 25, a -> {
                    String reason = (String) ReflectionHelper.propertyAccessor("reason", null).apply(a);
                    return reason != null ? reason : "Not specified";
                })
                .addColumn("Date & Time", 20, a -> {
                    LocalDateTime time = (LocalDateTime) ReflectionHelper.propertyAccessor("appointmentTime", null).apply(a);
                    if (time == null) return "Not scheduled";

                    String formattedDate = dateFormatter.format(time);
                    LocalDateTime now = LocalDateTime.now();

                    // Color appointments based on timing
                    if (time.isBefore(now)) {
                        return colorText(formattedDate, Color.RED);
                    } else if (time.isBefore(now.plusDays(1))) {
                        return colorText(formattedDate, Color.YELLOW);
                    } else if (time.isBefore(now.plusDays(3))) {
                        return colorText(formattedDate, Color.GREEN);
                    }
                    return formattedDate;
                })
                .addColumn("Status", 15, a -> {
                    AppointmentStatus status = (AppointmentStatus) ReflectionHelper.propertyAccessor("appointmentStatus", null).apply(a);
                    if (status == null) return "Unknown";

                    String statusStr = formatEnum(status.toString());

                    return switch (status) {
                        case COMPLETED -> colorText(statusStr, Color.GREEN);
                        case ACCEPTED -> colorText(statusStr, Color.CYAN);
                        case PENDING -> colorText(statusStr, Color.YELLOW);
                        case DECLINED, CANCELED -> colorText(statusStr, Color.RED);
                        case PAYMENT_PENDING -> colorText(statusStr, Color.UND_RED);
                        case PAID -> colorText(statusStr, Color.UND_GREEN);
                    };
                })
                .setData(appointments);

        return tableView;

    }
}
