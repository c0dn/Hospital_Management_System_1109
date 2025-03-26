package org.bee.pages.clerk;

import org.bee.controllers.AppointmentController;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.*;
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

/**
 * Page for viewing and managing telemedicine appointments.
 */
public class TelemedicineAppointmentPage extends UiBase {

    private static final AppointmentController appointmentController = AppointmentController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;

    @Override
    public View createView() {
        return viewAllAppointments();
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Display a table of all telemedicine appointments with pagination
     */
    private View viewAllAppointments() {
        List<Appointment> appointments = appointmentController.getAllAppointments();

        if (appointments.isEmpty()) {
            return new TextView(canvas, "No telemedicine appointments found.", Color.YELLOW);
        }

        BiFunction<List<Appointment>, Integer, TableView<Appointment>> tableFactory =
                (pageItems, pageNum) -> createAppointmentTableView(pageItems);

        return new PaginatedView<>(
                canvas,
                "Telemedicine Appointments",
                appointments,
                ITEMS_PER_PAGE,
                tableFactory,
                Color.CYAN
        );
    }

    /**
     * Creates a TableView for displaying appointment data
     */
    private TableView<Appointment> createAppointmentTableView(List<Appointment> appointments) {
        TableView<Appointment> tableView = new TableView<>(canvas, "", Color.CYAN);

        tableView.showRowNumbers(true)
                // Appointment ID - using session ID as a proxy since Appointment doesn't have a direct ID field
                .addColumn("Appointment ID", 15, a -> (String) ReflectionHelper.propertyAccessor("appointmentId", null).apply(a))

                // Appointment date and time - color coded based on when it's scheduled
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

                // Patient information
                .addColumn("Patient", 20, a -> {
                    Patient patient = (Patient) ReflectionHelper.propertyAccessor("patient", null).apply(a);
                    return patient != null ? patient.getName() : "Unknown";
                })

                // Reason for appointment
                .addColumn("Reason", 25, a -> {
                    String reason = (String) ReflectionHelper.propertyAccessor("reason", null).apply(a);
                    return reason != null ? reason : "Not specified";
                })

                // Doctor assigned
                .addColumn("Doctor", 20, a -> {
                    Doctor doctor = (Doctor) ReflectionHelper.propertyAccessor("doctor", null).apply(a);
                    if (doctor == null) {
                        return colorText("Not assigned", Color.YELLOW);
                    }
                    return doctor.getName();
                })

                // Status with color coding
                .addColumn("Status", 15, a -> {
                    AppointmentStatus status = (AppointmentStatus) ReflectionHelper.propertyAccessor("appointmentStatus", null).apply(a);
                    if (status == null) return "Unknown";

                    String statusStr = formatEnum(status.toString());

                    return switch (status) {
                        case COMPLETED -> colorText(statusStr, Color.GREEN);
                        case ACCEPTED -> colorText(statusStr, Color.CYAN);
                        case PENDING -> colorText(statusStr, Color.YELLOW);
                        case DECLINED, CANCELED -> colorText(statusStr, Color.RED);
                    };
                })

                // Session information (if available)
                .addColumn("Session", 15, a -> {
                    Session session = (Session) ReflectionHelper.propertyAccessor("session", null).apply(a);
                    if (session == null) {
                        return "No session";
                    }

                    SessionStatus sessionStatus = session.getSessionStatus();
                    String statusText = sessionStatus != null ? formatEnum(sessionStatus.toString()) : "Unknown";

                    if (sessionStatus == SessionStatus.ONGOING) {
                        return colorText(statusText, Color.GREEN);
                    } else if (sessionStatus == SessionStatus.COMPLETED) {
                        return colorText(statusText, Color.BLUE);
                    }

                    return statusText;
                })

                // Doctor notes (if available)
                .addColumn("Notes", 25, a -> {
                    String notes = (String) ReflectionHelper.propertyAccessor("doctorNotes", null).apply(a);
                    if (notes == null || notes.isEmpty()) {
                        return "";
                    }

                    // Truncate notes if too long
                    if (notes.length() > 25) {
                        return notes.substring(0, 22) + "...";
                    }
                    return notes;
                })

                // Has medical certificate indicator
                .addColumn("MC", 5, a -> {
                    MedicalCertificate mc = (MedicalCertificate) ReflectionHelper.propertyAccessor("mc", null).apply(a);
                    return mc != null ? colorText("Yes", Color.GREEN) : "No";
                })

                .setData(appointments);

        return tableView;
    }
}