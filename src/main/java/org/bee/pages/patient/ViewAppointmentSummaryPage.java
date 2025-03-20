package org.bee.pages.patient;

import org.bee.controllers.HumanController;
import org.bee.hms.telemed.Appointment;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewAppointmentSummaryPage extends UiBase {
    private static Appointment appointment;
    private ListView listView;


    private static final HumanController humanController = HumanController.getInstance();

    public static void setAppointment(Appointment appointment) {
        ViewAppointmentSummaryPage.appointment = appointment;
    }

    @Override
    public View OnCreateView() {
        listView = new ListView(
                this.canvas,
                Color.CYAN
        );
        listView.setTitleHeader("Appointment Summary for " + humanController.getLoginInUser());
        return listView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;
        refreshUi();
    }

    private void refreshUi () {
        listView.clear();
        listView.addItem(new TextView(this.canvas, "Attending Doctor Name: " + appointment.getDoctor().getName(), Color.BLUE, TextStyle.BOLD));
        listView.addItem(new TextView(this.canvas, "Patient Name: " + appointment.getPatient().getName(), Color.GREEN, TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Patient Contact No: " + appointment.getContact().getPersonalPhone(), Color.GREEN, TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Appointment Time: " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")), Color.GREEN, TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Reason: " + appointment.getReason() + "\n", Color.GREEN, TextStyle.ITALIC));
        String MCString = "Medical Certificate not yet given.";

        // separate the editable items with different colours
        if (appointment.getMc() != null) {
            MCString = "Medical Certificate: from: " +
                    appointment.getMc().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")) + " to: " +
                    appointment.getMc().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM"));
        }
        listView.addItem(new TextView(this.canvas,
                MCString,
                Color.CYAN,
                TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Doctor Notes/Follow up: " + appointment.getDoctorNotes(), Color.CYAN, TextStyle.ITALIC));

        // Add prescription details

//        Prescription prescription = appointment.getBilling().getPrescription();
//        if (prescription != null) {
//            List<Medicine> medicines = prescription.getMedicines();
//            if (medicines != null && !medicines.isEmpty()) {
//                listView.addItem(new TextView(this.canvas, "Prescription:", Color.BLUE, TextStyle.BOLD));
//                for (Medicine medicine : medicines) {
//                    listView.addItem(new TextView(this.canvas, "  - " + medicine.getMedName() + " x " + medicine.getMedQuantity() + " | Dosage: " + medicine.getMedDosage(), Color.GREEN));
//                }
//            }
//        }
        canvas.setRequireRedraw(true);
    }
}

