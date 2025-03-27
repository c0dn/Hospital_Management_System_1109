package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.pages.GenericUpdatePage;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.AbstractPaginatedView;
import org.bee.ui.views.ListView;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;
import org.bee.utils.formAdapters.ConsultationFormAdapter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A UI class that extends UiBase to handle updating outpatient case information.
 * This class allows doctors to view and update their assigned outpatient cases.
 */
public class UpdateOutpatientCase extends UiBase {
    private static final int ITEMS_PER_PAGE = 7;

    /**
     * Singleton instance of the HumanController to manage user-related data.
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Singleton instance of the ConsultationController to manage consultation-related data
    */
    private static final ConsultationController consultationController = ConsultationController.getInstance();

    /**
     * The patient associated with the current consultation.
     */
    private Patient patient;

    /**
     * The consultation updated.
     */
    private Consultation consultation;


    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Creates and returns the view for the update outpatient case page.
     *
     * @return A ListView object initialized with a green background color.
     */
    @Override
    public View createView() {
        return updateOutpatientRecord();
    }

    /**
     * Called when the view is created. Displays a list of outpatient cases for the logged-in doctor
     * or initiates the update process for a specific consultation if already selected.
     *
     * @param parentView The parent view, a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {

        canvas.setRequireRedraw(true);
//        ListView lv = (ListView) parentView;

//        SystemUser systemUser = humanController.getLoggedInUser();
//        List<Consultation> allCases = consultationController.getAllOutpatientCases();
//
//        if (systemUser instanceof Doctor doctor) {
//            String staffId = doctor.getStaffId();
//
//            if (consultation != null) {
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
//                    updateOutpatientCase();
//                } catch (Exception e) {
//                    throw e;
//                }
//            });
//        }
    }

    private View updateOutpatientRecord() {
        List<Consultation> consultations = consultationController.getAllOutpatientCases();

        if (consultations.isEmpty()) {
            return new TextView(canvas, "No outpatient cases found to update.", Color.YELLOW);
        }

        List<AbstractPaginatedView.MenuOption> menuOptions = new ArrayList<>();
        for (Consultation c : consultations) {
            String patientName = c.getPatient() != null ? c.getPatient().getName() : "Unknown Patient";
            String consultId = c.getConsultationId();
            String diagnosis = c.getDiagnosis() != null ? c.getDiagnosis() : "No diagnosis";

            String optionText = String.format("%s - %s (%s)", consultId, patientName, diagnosis);
            menuOptions.add(new PaginatedMenuView.MenuOption(consultId, optionText, c));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "Select Consultation to Update",
                "Available Consultations",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        // Set the callback with proper error handling
        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Consultation selectedConsultation = (Consultation) option.getData();
                    openUpdateForm(selectedConsultation);
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

    private void openUpdateForm(Consultation consultation) {
        try {
            ConsultationFormAdapter adapter = new ConsultationFormAdapter();

            GenericUpdatePage<Consultation> updatePage = new GenericUpdatePage<>(
                    consultation,
                    adapter,
                    () -> {
                        View refreshedView = updateOutpatientRecord();
                        navigateToView(refreshedView);
                    }
            );

            ToPage(updatePage);
        } catch (Exception e) {
            canvas.setSystemMessage("Error opening update form: " + e.getMessage());
            canvas.setRequireRedraw(true);
            System.err.println("Exception in openUpdateForm: " + e.getMessage());
        }
    }

    /**
     * Displays detailed information about a specific outpatient case .
     *
     * @param consultation The Consultation details about the case.
     * @param lv           consultation details will be displayed.
     */
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

    /**
     * Allows the logged-in doctor to select a consultation from their assigned outpatient cases.
     *
     * @return The selected Consultation object, or null if no cases are found or selected.
     */
    private Consultation selectConsultation() {
        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Doctor doctor) {
            String staffId = doctor.getStaffId();
            List<Consultation> consultations = consultationController.getAllOutpatientCases()
                    .stream()
                    .filter(c -> c.getDoctor() != null &&
                            c.getDoctor().getStaffId().equals(staffId))
                    .toList();

            if (consultations.isEmpty()) {
                System.out.println("No outpatient cases found..");
                return null;
            }

            System.out.println("Select a consultation to update:");
            for (int i = 0; i < consultations.size(); i++) {
                Consultation c = consultations.get(i);
                System.out.printf("%d. %s - %s\n", i + 1, c.getConsultationId(), c.getPatient().getName());
            }

            int choice = InputHelper.getValidIndex(canvas.getTerminal(), "Enter your choice", 1, consultations.size());
            return consultations.get(choice - 1);
        }
        return null;
    }

    /**
     * Initiates the process of updating an outpatient case.
     * Prompts the user to select specific fields to update and validates input before applying changes.
     */
    private void updateOutpatientCase() {
        consultation = selectConsultation();

        if (consultation == null) {
            System.out.println("No consultation selected. Returning to main menu.");
            return;
        }

        System.out.println("\nCurrent Particulars:");
        consultation.displayConsultation();

        ConsultationFormAdapter adapter = new ConsultationFormAdapter();

        GenericUpdatePage<Consultation> updatePage = new GenericUpdatePage<>(
                consultation,
                adapter,
                () -> System.out.println("Consultation information updated successfully!")
        );

        ToPage(updatePage);
    }

    /**
     * This method is called when the user presses the back button.
     * It simply calls the superclass implementation.
     */
    @Override
    public void OnBackPressed(){
        super.OnBackPressed();
    }
}