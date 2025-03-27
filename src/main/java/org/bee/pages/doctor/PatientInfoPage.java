package org.bee.pages.doctor;

import org.bee.controllers.HumanController;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.Sex;
import org.bee.hms.medical.Consultation;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.AbstractPaginatedView;
import org.bee.ui.views.ListView;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dual-purpose page can ask user to select multiple patients, or display a single patient
 */
public class PatientInfoPage extends UiBase {
    private static final HumanController humanController = HumanController.getInstance();
    private Patient patient;
    private static final int ITEMS_PER_PAGE = 7;

    /**
     * Default constructor for selecting a patient from a list
     */
    public PatientInfoPage() {
        this.patient = null;
    }

    /**
     * Constructor to display information for a specific patient
     *
     * @param patient The patient whose information will be displayed
     */
    public PatientInfoPage(Patient patient) {
        this.patient = patient;
    }

    /**
     * Creates and returns the view for the patient information page.
     *
     * @return A ListView object initialized with a green background color.
     */
    @Override
    public View createView() {
//        return new ListView(this.canvas, Color.GREEN);
        return displayAllPatient();
    }

    /**
     * Called when the view is created. Displays either a specific patient's information
     * or a list of all patients, depending on whether a patient has been pre-selected.
     *
     * @param parentView The parent view, expected to be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
//
//        ListView lv = (ListView) parentView;
//
//        if(patient != null){
//            displayAllPatient();
//            return;
//        }
//        lv.setTitleHeader("List of Patients");
//        List<Patient> patients = humanController.getAllPatients();
//        int index = 0;
//
//        // Show list of patient
//        for (Patient patient : patients) {
//            lv.addItem(new TextView(this.canvas, index + ". " + patient.getName(), Color.GREEN));
////            System.out.println(index + ". " + patient.getName());
//            index += 1;
//        }
//
//        // When selecting "Select Patient Index"
//        lv.attachUserInput1("Select Patient Index ", str -> {
//            int selectedIndex = InputHelper.getValidIndex(canvas.getTerminal(), "Select Patient index", patients);
//            patient = patients.get(selectedIndex);
//
//            try {
//                displayAllPatient();
//            }catch (Exception e){
//                throw e;
//            }
//        });
        canvas.setRequireRedraw(true);
    }

    private View displayAllPatient() {
        List<Patient> patients = humanController.getAllPatients();

        if (patients.isEmpty()) {
            return new TextView(canvas, "No patient cases found.", Color.YELLOW);
        }

        List<AbstractPaginatedView.MenuOption> menuOptions = new ArrayList<>();
        for (Patient p : patients) {
            String patientName = p.getName();

            String optionText = String.format("%s", patientName);
            menuOptions.add(new AbstractPaginatedView.MenuOption(patientName, optionText, p));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "\nSelect Patient Record to View",
                "List of Patients",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        // Set the callback with proper error handling
        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Patient selectedPatient = (Patient) option.getData();
                    displaySelectedPatient(selectedPatient, new ListView(canvas, Color.ESCAPE));

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

    public void displaySelectedPatient(Patient patient, ListView lv) {
        lv.clear();
        lv.setTitleHeader("\nPatient Information");
        lv.addItem(new TextView(this.canvas, "Name: " + patient.getName(), Color.ESCAPE));
        lv.addItem(new TextView(this.canvas, "ID: " + patient.getNricFin(), Color.ESCAPE));
        lv.addItem(new TextView(this.canvas, "Age: " + patient.getAge(), Color.ESCAPE));
        lv.addItem(new TextView(this.canvas, "Gender: " + patient.getSex(), Color.ESCAPE));
        lv.addItem(new TextView(this.canvas, "Address: " + patient.getAddress(), Color.ESCAPE));
        lv.addItem(new TextView(this.canvas, "Patient ID: " + patient.getPatientId(), Color.ESCAPE));
        lv.addItem(new TextView(this.canvas, "Date of Birth: " + patient.getDOB(), Color.ESCAPE));
        //lv.addItem(new TextView(this.canvas, "Next of Kin: " + patient.get(), Color.GREEN));

        // Request UI redraw
        canvas.setCurrentView(lv);
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