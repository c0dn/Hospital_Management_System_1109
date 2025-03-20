package org.bee.pages.doctor;

import org.bee.Globals;
import org.bee.controllers.HumanController;
import org.bee.telemed.Appointment;
import org.bee.telemed.AppointmentStatus;
import org.bee.telemed.UserType;
import org.bee.ui.*;
import org.bee.ui.views.ListView;
import org.bee.ui.views.ListViewOrientation;
import org.bee.ui.views.TextView;
import org.bee.util.ZoomCreator;
import org.bee.util.ZoomOAuth;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewAppointmentsPage extends UiBase {
    ListView listView;
    List<Appointment> appointments;
    HumanController controller = HumanController.getInstance();
    @Override
    public View OnCreateView() {
        ListView lv = new ListView(
                this.canvas,
                Color.GREEN
        );

        lv.setTitleHeader(" View Appointments | " + controller.getLoginInUser());
        this.listView = lv;
        return lv;
    }

    @Override
    public void OnViewCreated(View parentView) {
        HumanController controller = HumanController.getInstance();
        ListView listView = (ListView) parentView;
        appointments = Globals.appointmentController.getAppointments()
                .stream()
                .filter(x->x.getAppointmentStatus() != AppointmentStatus.COMPLETED)
                .toList();

        // filter the appointments list and only show the appointments that are completed.
        listView.attachUserInput("Select Patient index", str-> selectAppointmentPrompt(appointments));
        refreshUi();
    }

    private void selectAppointmentPrompt(List<Appointment> appointments) {
        HumanController controller = HumanController.getInstance();
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
                System.out.println("Accepted, generating zoomlink...");
                try {
                    // Get the OAuth access token
                    String accessToken = ZoomOAuth.getAccessToken();

                    // Call the method to create a Zoom meeting
                    String joinUrl = ZoomCreator.createZoomMeeting(
                            accessToken,
                            "Zoom Meeting",
                            1, // Duration in minutes
                            "UTC" // Timezone
                    );
                    selectedAppointment.approveAppointment(controller.getLoginInUser(), joinUrl);
                    Globals.appointmentController.saveAppointmentsToFile();
                }catch (IOException e){
                    System.out.println("Error generating zoom link");
                }
                break;
            case 2:
                selectedAppointment.setAppointmentStatus(AppointmentStatus.DECLINED);
                Globals.appointmentController.saveAppointmentsToFile();
                break;
            case 3:
                PatientInfoPage.patient = selectedAppointment.getPatient();
                ToPage(Globals.patientInfoPage);
                break;
            case 4:
                TeleconsultPage.setAppointment(selectedAppointment);
                ToPage(Globals.teleconsultPage);
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
