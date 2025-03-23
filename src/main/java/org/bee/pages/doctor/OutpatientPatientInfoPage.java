package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OutpatientPatientInfoPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private Patient patient;
    private Consultation consultation;

    public OutpatientPatientInfoPage() {
//        this.patient = null;
        this.consultation = null;
    }

    public OutpatientPatientInfoPage(Consultation consultation) {
        this.consultation = consultation;
    }

    @Override
    public View OnCreateView() {
        return new ListView(this.canvas, Color.GREEN);
    }

    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;

        SystemUser systemUser = humanController.getLoggedInUser();
        List<Consultation> allCases = consultationController.getAllOutpatientCases();

        if (systemUser instanceof Doctor doctor) {
            String staffId = (String) doctor.getStaffId();

            if (patient != null) {
                displayOutpatientCase(consultation, lv);
                return;
            }

            lv.setTitleHeader("List of Outpatient Cases");

            List<Consultation> cases = allCases.stream()
                    .filter(c -> c.getDoctor() != null &&
                            c.getDoctor().getStaffId().equals(staffId))
                    .collect(Collectors.toList());

            if (cases.isEmpty()) {
                System.out.println("No outpatient cases found.");
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();
                return;
            }

            int index = 0;

            // Show list of patient
            for (Consultation consultation : cases) {
                lv.addItem(new TextView(this.canvas, index + ". " + consultation.getPatient().getName(), Color.GREEN));
                index += 1;
            }

            // When selecting "Select Patient Index"
            lv.attachUserInput("Select Patient Index ", str -> {
                int selectedIndex = InputHelper.getValidIndex("Select Patient index", cases);
                consultation = cases.get(selectedIndex);

                try {
                    displayOutpatientCase(consultation, lv);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }
    }

    private void displayOutpatientCase(Consultation consultation, ListView lv) {

        lv.clear();
        lv.setTitleHeader("Patient Information");
        lv.addItem(new TextView(this.canvas, "Case ID: " + consultation.getConsultationId(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Appointment Date: " + consultation.getAppointmentDate(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Patient ID: " + consultation.getPatient().getPatientId(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Patient Name: " + consultation.getPatient().getName(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Type: " + consultation.getConsultationType(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Status: " + consultation.getStatus(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Diagnosis: " + consultation.getDiagnosis(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Doctor Name: " + consultation.getDoctor().getName(), Color.GREEN));
        //lv.addItem(new TextView(this.canvas, "Next of Kin: " + patient.get(), Color.GREEN));

        // Request UI redraw
        canvas.setRequireRedraw(true);
    }

    @Override
    public void OnBackPressed(){
        super.OnBackPressed();
    }
}
