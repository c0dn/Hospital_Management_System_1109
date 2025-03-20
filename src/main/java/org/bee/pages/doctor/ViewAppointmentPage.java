package org.bee.pages.doctor;

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

public class ViewAppointmentPage extends UiBase {
    private ListView listView;
    private List<Appointment> appointments;
    private static final HumanController humanController = HumanController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();

    @Override
    public View OnCreateView() {
        ListView lv = new ListView(
                this.canvas,
                Color.GREEN
        );

        lv.setTitleHeader(" View Appointments | " + humanController.getLoginInUser());
        this.listView = lv;
        return lv;
    }

    @Override
    public void OnViewCreated(View parentView) {
        ListView listView = (ListView) parentView;
        appointments = appointmentController.getAllAppointments()
                .stream()
                .filter(x->x.getAppointmentStatus() != AppointmentStatus.COMPLETED)
                .toList();

        listView.attachUserInput("Select Patient index", str -> selectAppointmentPrompt(appointments));
        refreshUi();
    }

    private void selectAppointmentPrompt(List<Appointment> appointments) {
        int selectedIndex = InputHelper.getValidIndex("Select Patient index", appointments);
        Appointment selectedAppointment = appointments.get(selectedIndex);

        int selectedIndex1;
        if(selectedAppointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED){
            System.out.println("1. Approve appointment | 2. Reject appointment | 3. View Patient Info | 4. Start Appointment");
            selectedIndex1 = InputHelper.getValidIndex("Select An Option", 1, 4);
        }else{
            System.out.println("1. Approve appointment | 2. Reject appointment | 3. View Patient Info");
            selectedIndex1 = InputHelper.getValidIndex("Select An Option", 1, 3);
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
                    Doctor assignedDoctor = selectedAppointment.getDoctor();
                    if (assignedDoctor == null) {
                        System.out.println("Error: No doctor assigned to this appointment");
                        System.out.println("Please assign a doctor to this appointment first");
                        break;
                    }
                    selectedAppointment.approveAppointment(assignedDoctor, joinUrl);
                    appointmentController.saveAppointments();
                } catch (ZoomApiException e) {
                    System.out.println("Error generating zoom link: " + e.getMessage());
                }
                break;
            case 2:
                selectedAppointment.setAppointmentStatus(AppointmentStatus.DECLINED);
                appointmentController.saveAppointments();
                break;
            case 3:
                ToPage(new PatientInfoPage(selectedAppointment.getPatient()));
                break;
            case 4:
                // Comment out for now as TeleconsultPage is not available
                // TeleconsultPage.setAppointment(selectedAppointment);
                // ToPage(Globals.teleconsultPage);
                System.out.println("Starting teleconsultation for appointment: " + selectedAppointment.toString());
                break;
        }

        refreshUi();
    }

    private void refreshUi() {
        listView.clear();
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

            String displayText = String.format("%d. %s | Consult Reason: %s | Date: %s | Time: %s |",
                    i, patientName, consultReason, formattedDate, formattedTime);

            Color itemColor = getItemColor(appointment.getAppointmentStatus());

            // add row view for horizontal separation, use two text views for two different statuses.
            ListView rowView = new ListView(this.canvas, itemColor, ListViewOrientation.HORIZONTAL);
            rowView.addItem(new TextView(this.canvas, displayText, itemColor));
            rowView.addItem(new TextView(this.canvas, status, itemColor, TextStyle.BOLD));
            listView.addItem(rowView);
        }
        this.canvas.setRequireRedraw(true);
    }

    private Color getItemColor(AppointmentStatus status) {
        switch (status) {
            case DECLINED:
                return Color.RED;
            case PENDING:
                return Color.CYAN;
            default:
                return Color.GREEN;
        }
    }
}
