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

/**
 * A UI class that extends UiBase to display a summary of an appointment.
 * This class creates a view with details of a specific appointment, including
 * doctor and patient information, appointment time, reason, medical certificate,
 * and doctor's notes.
 */
public class ViewAppointmentSummaryPage extends UiBase {
    /**
     * The appointment to be displayed in the summary.
     */
    private static Appointment appointment;

    /**
     * The ListView used to display the appointment details.
     */
    private ListView listView;

    /**
     * The HumanController instance used to get the logged-in user information.
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Sets the appointment to be displayed in the summary.
     *
     * @param appointment The Appointment object to be displayed.
     */
    public static void setAppointment(Appointment appointment) {
        ViewAppointmentSummaryPage.appointment = appointment;
    }

    /**
     * Creates and returns the view for the appointment summary.
     *
     * @return A ListView object containing the appointment summary.
     */
    @Override
    public View createView() {
        listView = new ListView(
                this.canvas,
                Color.CYAN
        );
        listView.setTitleHeader("Appointment Summary for " + humanController.getLoginInUser());
        return listView;
    }

    /**
     * Called when the view is created. Refreshes the UI to display appointment details.
     *
     * @param parentView The parent view, expected to be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;
        refreshUi();
    }

    /**
     * Refreshes the UI by clearing the existing items and adding new items
     * with the appointment details. This includes doctor and patient information,
     * appointment time, reason, medical certificate details, and doctor's notes.
     */
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

