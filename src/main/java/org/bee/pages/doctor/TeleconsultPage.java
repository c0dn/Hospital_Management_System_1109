package org.bee.pages.doctor;

import org.bee.controllers.AppointmentController;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.ui.*;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.ui.views.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Page for conducting a teleconsultation session.
 * Allows doctors to add notes, medical certificates, and complete the consultation.
 */
public class TeleconsultPage extends UiBase {
    private static Appointment appointment;
    private ObjectDetailsView detailsView;

    public static void setAppointment(Appointment appointment) {
        TeleconsultPage.appointment = appointment;
    }

    @Override
    protected View createView() {
        if (appointment == null) {
            return new TextView(canvas, "No appointment selected. Please select an appointment first.", Color.RED);
        }

        CompositeView compositeView = new CompositeView(canvas, "Teleconsultation Session", Color.CYAN);

        detailsView = new ObjectDetailsView(canvas, "", appointment, Color.CYAN);
        configureDetailsView();

        compositeView.addView(detailsView);
        return compositeView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        if (appointment == null) {
            canvas.setSystemMessage("No appointment selected", SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
            return;
        }

        CompositeView compositeView = (CompositeView) parentView;

        compositeView.attachUserInput("Update Doctor Notes", input -> updateDoctorNotes());
        compositeView.attachUserInput("Add Medical Certificate", input -> addMedicalCertificate());

        if (appointment.getMc() != null) {
            compositeView.attachUserInput("Remove Medical Certificate", input -> removeMedicalCertificate());
        }

        compositeView.attachUserInput("Finish Consultation", input -> finishConsultation());

        canvas.setRequireRedraw(true);
    }

    /**
     * Configure the details view with appointment information
     */
    private void configureDetailsView() {
        detailsView = new ObjectDetailsView(canvas, "", appointment, Color.CYAN);

        ObjectDetailsView.Section patientSection = detailsView.addSection("Patient Information");
        patientSection.addField(new ObjectDetailsView.Field<Appointment>("Patient Name", a ->
                a.getPatient() != null ? a.getPatient().getName() : "Unknown"));
        patientSection.addField(new ObjectDetailsView.Field<Appointment>("Appointment Time", a ->
                a.getAppointmentTime() != null ?
                        a.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) :
                        "Not scheduled"));
        patientSection.addField(new ObjectDetailsView.Field<Appointment>("Reason", a ->
                a.getReason() != null ? a.getReason() : "Not specified"));

        ObjectDetailsView.Section zoomSection = detailsView.addSection("Session Information");
        zoomSection.addField(new ObjectDetailsView.Field<Appointment>("Zoom Link", a ->
                a.getSession() != null && a.getSession().getZoomLink() != null ?
                        a.getSession().getZoomLink() : "No link available"));

        ObjectDetailsView.Section notesSection = detailsView.addSection("Medical Information");
        notesSection.addField(new ObjectDetailsView.Field<Appointment>("Doctor Notes", a ->
                a.getDoctorNotes() != null ? a.getDoctorNotes() : "No notes yet"));

        ObjectDetailsView.Section mcSection = detailsView.addSection("Medical Certificate");
        if (appointment.getMc() != null) {
            mcSection.addField(new ObjectDetailsView.Field<Appointment>("Start Date", a ->
                    a.getMc() != null ? a.getMc().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Not set"));
            mcSection.addField(new ObjectDetailsView.Field<Appointment>("End Date", a ->
                    a.getMc() != null ? a.getMc().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Not set"));
            mcSection.addField(new ObjectDetailsView.Field<Appointment>("Remarks", a ->
                    a.getMc() != null && a.getMc().getRemarks() != null ? a.getMc().getRemarks() : "No remarks"));
        } else {
            mcSection.addField(new ObjectDetailsView.Field<>("Status", a -> "No medical certificate issued"));
        }
    }

    /**
     * Update doctor notes for the appointment
     */
    private void updateDoctorNotes() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Doctor Notes:");
        String newNotes = scanner.nextLine();
        appointment.setDoctorNotes(newNotes);

        // Refresh the view
        configureDetailsView();
        View refreshedView = createView();
        navigateToView(refreshedView);
        OnViewCreated(refreshedView);

        canvas.setSystemMessage("Doctor notes updated successfully", SystemMessageStatus.SUCCESS);
    }

    /**
     * Add a medical certificate to the appointment
     */
    private void addMedicalCertificate() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Medical Certificate Remarks:");
        String remarks = scanner.nextLine();

        LocalDate today = LocalDate.now();
        LocalDate endDay = today.plusDays(12 - 1);
        MedicalCertificate mc = new MedicalCertificate(today.atStartOfDay(), endDay.atTime(23, 59), remarks);
        appointment.setMedicalCertificate(mc);

        // Refresh the view
        configureDetailsView();
        View refreshedView = createView();
        navigateToView(refreshedView);
        OnViewCreated(refreshedView);

        canvas.setSystemMessage("Medical certificate added successfully", SystemMessageStatus.SUCCESS);
    }

    /**
     * Remove the medical certificate from the appointment
     */
    private void removeMedicalCertificate() {
        if (appointment.getMc() == null) {
            canvas.setSystemMessage("No medical certificate to remove", SystemMessageStatus.WARNING);
            return;
        }

        appointment.setMedicalCertificate(null);

        // Refresh the view
        configureDetailsView();
        View refreshedView = createView();
        navigateToView(refreshedView);
        OnViewCreated(refreshedView);

        canvas.setSystemMessage("Medical certificate removed", SystemMessageStatus.SUCCESS);
    }

    /**
     * Finish the consultation and save the appointment data
     */
    private void finishConsultation() {
        try {
            appointment.finishAppointment(appointment.getDoctorNotes());
            AppointmentController.getInstance().updateAppointment(appointment, appointment);

            canvas.setSystemMessage("Consultation completed successfully", SystemMessageStatus.SUCCESS);
            this.OnBackPressed();
        } catch (Exception e) {
            canvas.setSystemMessage("Error completing consultation: " + e.getMessage(), SystemMessageStatus.ERROR);
        }
    }
}