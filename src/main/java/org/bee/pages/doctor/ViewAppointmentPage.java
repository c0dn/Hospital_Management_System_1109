package org.bee.pages.doctor;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.execeptions.ZoomApiException;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.Session;
import org.bee.hms.telemed.SessionStatus;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.PaginatedView;
import org.bee.ui.views.TableView;
import org.bee.ui.views.TextView;
import org.bee.utils.ReflectionHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Page for viewing and managing telemedicine appointments for doctors.
 */
public class ViewAppointmentPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
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
        Doctor currentDoctor = (Doctor) humanController.getLoggedInUser();
        List<Appointment> appointments = appointmentController.getAllAppointments().stream()
                .filter(a -> {
                    Doctor appointmentDoctor = a.getDoctor();
                    return appointmentDoctor == null ||
                            appointmentDoctor.getStaffId().equals(currentDoctor.getStaffId());
                })
                .toList();

        if (appointments.isEmpty()) {
            return new TextView(canvas, "No telemedicine appointments found.", Color.YELLOW);
        }

        BiFunction<List<Appointment>, Integer, TableView<Appointment>> tableFactory =
                (pageItems, pageNum) -> createAppointmentTableView(pageItems);

        PaginatedView<Appointment, TableView<Appointment>> paginatedView = new PaginatedView<>(
                canvas,
                "Telemedicine Appointments",
                appointments,
                ITEMS_PER_PAGE,
                tableFactory,
                Color.CYAN
        );

        paginatedView.attachUserInput("View Patient Info", input -> {
            TableView<Appointment> tableView = paginatedView.getContentView();
            if (tableView != null) {
                tableView.setSelectionCallback((rowIndex, appointment) -> {
                    ToPage(new PatientInfoPage(appointment.getPatient()));
                });
            }
        });

        paginatedView.attachUserInput("Manage Appointment", input -> {
            TableView<Appointment> tableView = paginatedView.getContentView();
            if (tableView != null) {
                tableView.setSelectionCallback((rowIndex, appointment) -> {
                    manageAppointment(appointment);
                });
            }
        });

        return paginatedView;
    }

    /**
     * Creates a TableView for displaying appointment data
     */
    private TableView<Appointment> createAppointmentTableView(List<Appointment> appointments) {
        TableView<Appointment> tableView = new TableView<>(canvas, "", Color.CYAN);

        tableView.showRowNumbers(true)
                .addColumn("Appointment ID", 15, a -> (String) ReflectionHelper.propertyAccessor("appointmentId", null).apply(a))

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

                .addColumn("Patient", 20, a -> {
                    Patient patient = (Patient) ReflectionHelper.propertyAccessor("patient", null).apply(a);
                    return patient != null ? patient.getName() : "Unknown";
                })

                .addColumn("Reason", 25, a -> {
                    String reason = (String) ReflectionHelper.propertyAccessor("reason", null).apply(a);
                    return reason != null ? reason : "Not specified";
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

                .addColumn("Actions", 15, a -> {
                    AppointmentStatus status = a.getAppointmentStatus();
                    if (status == AppointmentStatus.PENDING) {
                        return colorText("Needs Review", Color.YELLOW);
                    } else if (status == AppointmentStatus.ACCEPTED) {
                        return colorText("Start Consult", Color.GREEN);
                    } else if (status == AppointmentStatus.COMPLETED) {
                        return colorText("View Records", Color.BLUE);
                    }
                    return "";
                })

                .setData(appointments);

        return tableView;
    }

    /**
     * Manage the selected appointment
     */
    private void manageAppointment(Appointment appointment) {
        AppointmentStatus status = appointment.getAppointmentStatus();

        try {
            if (status == AppointmentStatus.PENDING) {
                canvas.setSystemMessage("Approving appointment...", SystemMessageStatus.INFO);

                Doctor currentDoctor = (Doctor) humanController.getLoggedInUser();
                if (appointment.getDoctor() == null) {
                    appointment.setDoctor(currentDoctor);
                }

                try {
                    String joinUrl = appointmentController.generateZoomLink(
                            "Appointment with " + appointment.getPatient().getName(),
                            30
                    );
                    appointment.approveAppointment(currentDoctor, joinUrl);
                    appointmentController.saveData();
                    canvas.setSystemMessage("Appointment approved successfully!", SystemMessageStatus.SUCCESS);
                } catch (ZoomApiException | IOException e) {
                    canvas.setSystemMessage("Error: " + e.getMessage(), SystemMessageStatus.ERROR);
                }
            } else if (status == AppointmentStatus.ACCEPTED) {
                TeleconsultPage.setAppointment(appointment);
                ToPage(new TeleconsultPage());
            } else if (status == AppointmentStatus.COMPLETED) {
                canvas.setSystemMessage("Viewing completed consultation records...", SystemMessageStatus.INFO);
            } else if (status == AppointmentStatus.DECLINED) {
                canvas.setSystemMessage("This appointment was declined.", SystemMessageStatus.INFO);
            }
        } catch (Exception e) {
            canvas.setSystemMessage("Error processing appointment: " + e.getMessage(), SystemMessageStatus.ERROR);
        }

        View refreshedView = viewAllAppointments();
        navigateToView(refreshedView);
    }
}