package org.bee.pages.doctor;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.execeptions.ZoomApiException;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.Session;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.*;
import org.bee.utils.ReflectionHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Page for viewing and managing telemedicine appointments for doctors.
 */
public class ViewAppointmentPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;
    private View appointmentListView;

    @Override
    public View createView() {
        appointmentListView = createAppointmentListView();
        return appointmentListView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Creates a list view showing all appointments
     */
    private View createAppointmentListView() {
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

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
        for (Appointment a : appointments) {
            String patientName = a.getPatient() != null ? a.getPatient().getName() : "Unknown";
            String appointmentId = ReflectionHelper.propertyAccessor("appointmentId", "N/A").apply(a);
            String shortAppointmentId = appointmentId.length() > 8
                    ? appointmentId.substring(0, 8)
                    : appointmentId;
            LocalDateTime appointmentTime = a.getAppointmentTime();
            String timeString = appointmentTime != null ? dateFormatter.format(appointmentTime) : "Not scheduled";
            String reason = a.getReason() != null ? a.getReason() : "Not specified";
            String status;
            if (a.getAppointmentStatus() == AppointmentStatus.PAYMENT_PENDING || a.getAppointmentStatus() == AppointmentStatus.PAID) {
                status = formatEnum("COMPLETED"); // Display as PAYMENT_PENDING
            } else {
                status = a.getAppointmentStatus() != null ? formatEnum(a.getAppointmentStatus().toString()) : "Unknown";
            }
            String coloredStatus = status;
            if (status.equals(formatEnum("COMPLETED"))) {
                coloredStatus = colorText(status, Color.GREEN);
            } else if (status.equals(formatEnum("ACCEPTED"))) {
                coloredStatus = colorText(status, Color.CYAN);
            } else if (status.equals(formatEnum("PENDING"))) {
                coloredStatus = colorText(status, Color.YELLOW);
            } else if ((status.equals(formatEnum("DECLINED")) || status.equals(formatEnum("CANCELED")))) {
                coloredStatus = colorText(status, Color.RED);
            }
            
            String patientNRIC = a.getPatient() != null ? humanController.maskNRIC(a.getPatient().getNricFin()) : "Unknown";

            String optionText = String.format("%s - %s, %s (%s) - %s - %s",
                    shortAppointmentId, patientName, patientNRIC, timeString, reason, coloredStatus);

            menuOptions.add(new PaginatedMenuView.MenuOption(shortAppointmentId, optionText, a));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "\nTelemedicine Appointments",
                "Select an appointment to view details",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Appointment selected = (Appointment) option.getData();
                    displaySelectedAppointment(selected, canvas.getCurrentView());
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

        return paginatedView;
    }

    /**
     * Creates a detail view for a specific appointment
     */
    private View createAppointmentDetailsView(Appointment appointment) {
        CompositeView compositeView = new CompositeView(canvas, "", Color.CYAN);

        DetailsView<Appointment> detailsView = new DetailsView<>(
                canvas,
                "TELEMEDICINE APPOINTMENT DETAILS",
                appointment,
                Color.CYAN
        );

        detailsView.addDetail("Basic Information", "Appointment ID",
                (String) ReflectionHelper.propertyAccessor("appointmentId", "N/A").apply(appointment));

        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        detailsView.addDetail("Basic Information", "Date & Time",
                appointmentTime != null ? dateFormatter.format(appointmentTime) : "Not scheduled");

        Patient patient = appointment.getPatient();
        detailsView.addDetail("Patient Information", "Name",
                patient != null ? patient.getName() : "Unknown");
        detailsView.addDetail("Patient Information", "Patient ID",
                patient != null ? patient.getPatientId() : "Unknown");

        detailsView.addDetail("Appointment Details", "Reason",
                appointment.getReason() != null ? appointment.getReason() : "Not specified");
        detailsView.addDetail("Appointment Details", "Status",
                appointment.getAppointmentStatus() != null ?
                        formatEnum(appointment.getAppointmentStatus().toString()) : "Unknown");

        Session session = (Session) ReflectionHelper.propertyAccessor("session", null).apply(appointment);
        if (session != null) {
            detailsView.addDetail("Session Information", "Status",
                    session.getSessionStatus() != null ?
                            formatEnum(session.getSessionStatus().toString()) : "Unknown");

            String joinUrl = (String) ReflectionHelper.propertyAccessor("joinUrl", null).apply(session);
            if (joinUrl != null && !joinUrl.isEmpty()) {
                detailsView.addDetail("Session Information", "Join URL", joinUrl);
            }
        }

        compositeView.addView(detailsView);

        if (humanController.getLoggedInUser() instanceof Doctor) {
            MenuView actionMenu = new MenuView(canvas, "", Color.CYAN, false, true);

            AppointmentStatus status = appointment.getAppointmentStatus();

            if (status == AppointmentStatus.PENDING) {
                actionMenu.attachLetterOption('a', "Approve Appointment", input -> {
                    approveAppointment(appointment);
                });

                actionMenu.attachLetterOption('d', "Decline Appointment", input -> {
                    declineAppointment(appointment);
                });
            } else if (status == AppointmentStatus.ACCEPTED) {
                actionMenu.attachLetterOption('s', "Start Consultation", input -> {
                    startConsultation(appointment);
                });
            } else if (status == AppointmentStatus.COMPLETED) {
                actionMenu.attachLetterOption('v', "View Records", input -> {
                    viewRecords(appointment);
                });
            }

            actionMenu.attachLetterOption('p', "View Patient Details", input -> {
                ToPage(new PatientInfoPage(appointment.getPatient()));
            });

            compositeView.addView(actionMenu);
        }

        return compositeView;
    }

    /**
     * Approve the appointment
     */
    private void approveAppointment(Appointment appointment) {
        try {
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

                displaySelectedAppointment(appointment, null);
            } catch (ZoomApiException | IOException e) {
                canvas.setSystemMessage("Error: " + e.getMessage(), SystemMessageStatus.ERROR);
            }
        } catch (Exception e) {
            canvas.setSystemMessage("Error approving appointment: " + e.getMessage(), SystemMessageStatus.ERROR);
        }
    }

    /**
     * Decline the appointment
     */
    private void declineAppointment(Appointment appointment) {
        try {
            Doctor currentDoctor = (Doctor) humanController.getLoggedInUser();
            appointment.setAppointmentStatus(AppointmentStatus.DECLINED);
            appointmentController.saveData();
            canvas.setSystemMessage("Appointment declined.", SystemMessageStatus.SUCCESS);

            appointmentListView = createAppointmentListView();
            navigateToView(appointmentListView);
        } catch (Exception e) {
            canvas.setSystemMessage("Error declining appointment: " + e.getMessage(), SystemMessageStatus.ERROR);
        }
    }

    /**
     * Start the consultation
     */
    private void startConsultation(Appointment appointment) {
        try {
            TeleconsultPage.setAppointment(appointment);
            ToPage(new TeleconsultPage());
        } catch (Exception e) {
            canvas.setSystemMessage("Error starting consultation: " + e.getMessage(), SystemMessageStatus.ERROR);
        }
    }

    /**
     * View consultation records
     */
    private void viewRecords(Appointment appointment) {
        canvas.setSystemMessage("Viewing completed consultation records...", SystemMessageStatus.INFO);
    }

    /**
     * Displays the selected appointment
     */
    public void displaySelectedAppointment(Appointment appointment) {
        displaySelectedAppointment(appointment, canvas.getCurrentView());
    }

    /**
     * Displays detailed information for the selected appointment
     * with support for returning to the previous view
     */
    public void displaySelectedAppointment(Appointment appointment, View previousView) {
        View appointmentView = createAppointmentDetailsView(appointment);
        canvas.setCurrentView(appointmentView);
        canvas.setRequireRedraw(true);
    }

    /**
     * Handles the back button press by calling the superclass implementation.
     */
    @Override
    public void OnBackPressed() {
        super.OnBackPressed();
    }
}