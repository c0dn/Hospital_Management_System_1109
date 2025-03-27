package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.*;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A UI class that extends UiBase to display outpatient patient information.
 * This page allows doctors to view a list of outpatient cases assigned to them
 * and select a specific case to view detailed patient and consultation information.
 */
public class OutpatientPatientInfoPage extends UiBase {

    /**
     * Singleton instance of the HumanController to manage user-related data.
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Singleton instance of the ConsultationController to manage consultation-related data.
     */
    private static final ConsultationController consultationController = ConsultationController.getInstance();

    /**
     * The patient associated with the current consultation.
     */
    private Patient patient;
    /**
     * The consultation being displayed or managed .
     */
    private Consultation consultation;

    private static final int ITEMS_PER_PAGE = 7;

    /**
    * Constructor for creating an instance of OutpatientPatientInfoPage
    */
    public OutpatientPatientInfoPage() {
//        this.patient = null;
        this.consultation = null;
    }

    /**
     * Constructor for creating an instance of OutpatientPatientInfoPage with a specific consultation.
     *
     * @param consultation The Consultation object to be displayed or managed.
     */
    public OutpatientPatientInfoPage(Consultation consultation) {
        this.consultation = consultation;
    }

    /**
     * Creates and returns the view for the outpatient patient information page.
     *
     * @return A ListView object initialized with a green background color.
     */
    @Override
    public View createView() {
        return displayAllOutpatientCase();
    }

    /**
     * Called when the view is created. Displays a list of outpatient cases for the logged-in doctor
     * or detailed information about a specific consultation if already selected.
     *
     * @param parentView The parent view, expected to be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
//        ListView lv = (ListView) parentView;
//
//        SystemUser systemUser = humanController.getLoggedInUser();
//        List<Consultation> allCases = consultationController.getAllOutpatientCases();
//
//        if (systemUser instanceof Doctor doctor) {
//            String staffId = doctor.getStaffId();
//
//            if (patient != null) {
//                displayOutpatientCase(consultation, lv);
//                return;
//            }
//
//            lv.setTitleHeader("List of Outpatient Cases");
//
//            List<Consultation> cases = allCases.stream()
//                    .filter(c -> c.getDoctor() != null &&
//                            c.getDoctor().getStaffId().equals(staffId))
//                    .collect(Collectors.toList());
//
//            if (cases.isEmpty()) {
//                System.out.println("No outpatient cases found.");
//                System.out.println("\nPress Enter to continue...");
//                new Scanner(System.in).nextLine();
//                return;
//            }
//
//            int index = 0;
//
//            // Show list of patient
//            for (Consultation consultation : cases) {
//                lv.addItem(new TextView(this.canvas, index + ". " + consultation.getPatient().getName(), Color.GREEN));
//                index += 1;
//            }
//
//            // When selecting "Select Patient Index"
//            lv.attachUserInput("Select Patient Index ", str -> {
//                int selectedIndex = InputHelper.getValidIndex(canvas.getTerminal(), "Select Patient index", cases);
//                consultation = cases.get(selectedIndex);
//
//                try {
//                    displayOutpatientCase(consultation, lv);
//                } catch (Exception e) {
//                    throw e;
//                }
//            });
//        }
    }
//
    private View displayAllOutpatientCase() {
        List<Consultation> consultations = consultationController.getAllOutpatientCases();

        if (consultations.isEmpty()) {
            return new TextView(canvas, "No outpatient cases found.", Color.YELLOW);
        }

        List<AbstractPaginatedView.MenuOption> menuOptions = new ArrayList<>();
        for (Consultation c : consultations) {
            String patientName = c.getPatient().getName();

            String optionText = String.format("%s", patientName);
            menuOptions.add(new AbstractPaginatedView.MenuOption(patientName, optionText, c));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "\nSelect Outpatient Case to View",
                "List of Outpatients",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Consultation selectedPatient = (Consultation) option.getData();
                    displayOutpatientCase(selectedPatient, new ListView(canvas, Color.ESCAPE));

                } else {
                    canvas.setSystemMessage("Error: Invalid selection");
                    canvas.setRequireRedraw(true);
                }
            } catch (Exception e) {
                canvas.setSystemMessage("Error processing selection: " + e.getMessage());
                canvas.setRequireRedraw(true);
                System.err.println("Exception in selection callback: " + e.getMessage());
            }
        });

        return paginatedView;
    }

    /**
     * Displays detailed information about a specific outpatient case in the provided ListView.
     *
     * @param consultation The Consultation object containing details about the case.
     * @param lv           The ListView where the details will be displayed.
     */
    private void displayOutpatientCase(Consultation consultation, View previousView) {

        DetailsView<Consultation> detailsView = new DetailsView<>(canvas, "", consultation, Color.ESCAPE);
        detailsView.setPreviousView(previousView);

        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Case ID: ", consultation.getConsultationId());
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Appointment Date: ", String.valueOf(consultation.getAppointmentDate()));
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Patient ID: ", consultation.getPatient().getNricFin());
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Patient Name: ", consultation.getPatient().getName());
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Type: ", String.valueOf(consultation.getConsultationType()));
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Status: ", String.valueOf(consultation.getStatus()));
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Diagnosis: ", consultation.getDiagnosis());
        detailsView.addDetail("\u001b[1mOUTPATIENT INFORMATION\n", "Doctor Name: ", consultation.getDoctor().getName());

//        lv.clear();
//        lv.setTitleHeader("Patient Information");
//        lv.addItem(new TextView(this.canvas, "Case ID: " + consultation.getConsultationId(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Appointment Date: " + consultation.getAppointmentDate(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Patient ID: " + consultation.getPatient().getPatientId(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Patient Name: " + consultation.getPatient().getName(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Type: " + consultation.getConsultationType(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Status: " + consultation.getStatus(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Diagnosis: " + consultation.getDiagnosis(), Color.ESCAPE));
//        lv.addItem(new TextView(this.canvas, "Doctor Name: " + consultation.getDoctor().getName(), Color.ESCAPE));
        //lv.addItem(new TextView(this.canvas, "Next of Kin: " + patient.get(), Color.GREEN));

        canvas.setCurrentView(detailsView);
        // Request UI redraw
        canvas.setRequireRedraw(true);
    }
    /**
     * Handles the back button press by calling the superclass implementation.
     */
    @Override
    public void OnBackPressed(){
        super.OnBackPressed();
    }
}
