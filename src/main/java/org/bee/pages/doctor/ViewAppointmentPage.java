package org.bee.pages.doctor;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.execeptions.ZoomApiException;
import org.bee.hms.humans.Doctor;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.TextStyle;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.ListViewOrientation;
import org.bee.ui.views.TextView;

/**
 * Manages the UI for viewing and interacting with appointments.
 *
 * This page displays a list of pending appointments and allows doctors to
 * approve, reject, or start consultations.
 */
public class ViewAppointmentPage extends UiBase {
    private ListView listView;
    private List<Appointment> appointments;
    private static final HumanController humanController = HumanController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();

    /**
     * Sets up the initial view for the appointment list.
     *
     * Creates a green ListView with a title showing the logged-in user.
     *
     * @return The configured ListView with appointments
     */
    @Override
    public View createView() {
        ListView lv = new ListView(
                this.canvas,
                Color.GREEN
        );

        lv.setTitleHeader("\n View Appointments | " + humanController.getLoginInUser());
        this.listView = lv;
        return lv;
    }

    /**
     * Populates the view with appointment data and sets up user interactions.
     *
     * Filters out completed appointments and adds a prompt for selecting appointments.
     *
     * @param parentView The parent view to populate
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView listView = (ListView) parentView;
        appointments = appointmentController.getAllAppointments()
                .stream()
                .filter(x -> x.getAppointmentStatus() != AppointmentStatus.COMPLETED)
                .toList();

        refreshUi();
//        selectAppointmentPrompt(appointments);

        listView.attachUserInput("Select Patient index", input -> {
            try {
                int selectedIndex = Integer.parseInt(input.trim());
                if (selectedIndex >= 0 && selectedIndex < appointments.size()) {
                    selectAppointmentPrompt((List<Appointment>) appointments.get(selectedIndex));
                } else {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        });

//        listView.attachUserInput("Select Patient index", str -> selectAppointmentPrompt(appointments));
//        refreshUi();
    }

    /**
     * Handles the appointment selection process.
     *
     * Allows the user to approve, reject, view patient info, or start an appointment.
     * Also handles Zoom link generation for approved appointments.
     *
     * @param appointments List of available appointments
     */
    private void selectAppointmentPrompt(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments available.");
            return;
        }
        int selectedIndex = InputHelper.getValidIndex(canvas.getTerminal(), "Select Patient index", appointments);
        Appointment selectedAppointment = appointments.get(selectedIndex);

        int selectedIndex1;
        if(selectedAppointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED){
            System.out.println("1. Approve appointment | 2. Reject appointment | 3. View Patient Info | 4. Start Appointment");
            selectedIndex1 = InputHelper.getValidIndex(canvas.getTerminal(), "Select An Option", 1, 4);
        }else{
            System.out.println("1. Approve appointment | 2. Reject appointment | 3. View Patient Info");
            selectedIndex1 = InputHelper.getValidIndex(canvas.getTerminal(), "Select An Option", 1, 3);
        }

        switch (selectedIndex1){
            case 1:
                selectedAppointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
                System.out.println("Accepted, generating zoom link...");
                try {

                    String joinUrl = appointmentController.generateZoomLink(
                            "Appointment with " + selectedAppointment.getPatient().getName(),
                            30
                    );
                    // Get the currently logged-in doctor
                    Doctor currentDoctor = (Doctor) humanController.getLoggedInUser();

                    // Assign the current doctor to the appointment if no doctor is assigned
                    if (selectedAppointment.getDoctor() == null) {
                        selectedAppointment.setDoctor(currentDoctor);
                    }

                    // Now get the assigned doctor (which should be the current doctor)
                    Doctor assignedDoctor = selectedAppointment.getDoctor();
                    if (assignedDoctor == null) {
                        System.out.println("Error: No doctor assigned to this appointment");
                        System.out.println("Please assign a doctor to this appointment first");
                        break;
                    }
                    selectedAppointment.approveAppointment(assignedDoctor, joinUrl);
                    appointmentController.saveData();
                } catch (ZoomApiException e) {
                    System.out.println("Error generating zoom link: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Error saving data: " + e.getMessage());
                }
                break;
            case 2:
                selectedAppointment.setAppointmentStatus(AppointmentStatus.DECLINED);
                appointmentController.saveData();
                break;
            case 3:
                ToPage(new PatientInfoPage(selectedAppointment.getPatient()));
                break;
            case 4:
                TeleconsultPage.setAppointment(selectedAppointment);
                ToPage(new TeleconsultPage());
                System.out.println("Starting teleconsultation for appointment: " + selectedAppointment.toString());
                break;
        }

        refreshUi();
    }

    /**
     * Updates the UI with the current list of appointments.
     *
     * Clears the existing view and adds each appointment with color-coding based on status.
     * Displays a message if no appointments are available.
     */
    private void refreshUi() {
        listView.clear();
        if (appointments.isEmpty()) {
            listView.addItem(new TextView(this.canvas, "You have no pending appointments.", Color.YELLOW));
            this.canvas.setRequireRedraw(true);
            return;}

        listView.addItem(new TextView(this.canvas, String.format("%3s | %-15s | %-20s | %-12s | %-7s | %-15s", "ID", "Patient Name", "Consult Reason", "Date", "Time", "Status"), Color.BRIGHT_WHITE));

        // loop through the appointments and display them
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            String patientName = appointment.getPatient().getName();
            String consultReason = appointment.getReason();
            String formattedTime = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String formattedDate = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            // error handling for dirty json data
            if(appointment.getAppointmentStatus() == null){
                appointment.setAppointmentStatus(AppointmentStatus.PENDING);
            }

            String status = appointment.getAppointmentStatus().toString();

            String displayText = String.format("%3d. %17s %20s %14s %9s %11s",
                    i, patientName, consultReason, formattedDate, formattedTime, status);

//            Color itemColor = getItemColor(appointment.getAppointmentStatus());

            // add row view for horizontal separation, use two text views for two different statuses.
            ListView rowView = new ListView(this.canvas, Color.WHITE, ListViewOrientation.HORIZONTAL);
            rowView.addItem(new TextView(this.canvas, displayText, Color.WHITE));
//            rowView.addItem(new TextView(this.canvas, status, itemColor, TextStyle.BOLD));
            listView.addItem(rowView);


        this.canvas.setRequireRedraw(true);
    }

    /**
     * Determines the color for an appointment based on its status.
     *
     * @param status The appointment status
     * @return The corresponding color (RED for declined, CYAN for pending, GREEN for others)
     */
//    private Color getItemColor(AppointmentStatus status) {
//        switch (status) {
//            case DECLINED:
//                return Color.RED;
//            case PENDING:
//                return Color.CYAN;
//            default:
//                return Color.GREEN;
//        }
    }
}
