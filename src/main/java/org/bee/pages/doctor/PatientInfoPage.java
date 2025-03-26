package org.bee.pages.doctor;

import org.bee.controllers.HumanController;
import org.bee.hms.humans.Patient;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

import java.util.List;

/**
 * Dual-purpose page can ask user to select multiple patients, or display a single patient
 */
public class PatientInfoPage extends UiBase {
    private static final HumanController humanController = HumanController.getInstance();
    private Patient patient;


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
        return new ListView(this.canvas, Color.GREEN);
    }

    /**
     * Called when the view is created. Displays either a specific patient's information
     * or a list of all patients, depending on whether a patient has been pre-selected.
     *
     * @param parentView The parent view, expected to be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {

        ListView lv = (ListView) parentView;

        if(patient != null){
            displayPatient(patient, lv);
            return;
        }
        lv.setTitleHeader("List of Patients");
        List<Patient> patients = humanController.getAllPatients();
        int index = 0;

        // Show list of patient
        for (Patient patient : patients) {
//            lv.addItem(new TextView(this.canvas, index + ". " + patient.getName(), Color.GREEN));
            System.out.println(index + ". " + patient.getName());
            index += 1;
        }

        // When selecting "Select Patient Index"
//        lv.attachUserInput("Select Patient Index ", str -> {
            int selectedIndex = InputHelper.getValidIndex(canvas.getTerminal(), "Select Patient index", patients);
            patient = patients.get(selectedIndex);

            try {
                displayPatient(patient, lv);
            }catch (Exception e){
                throw e;
            }
//        });
    }

    /**
     * Displays detailed information about a specific patient .
     *
     * @param patient The Patient details to be displayed.
     * @param lv patient's information will be shown.
     */
    private void displayPatient(Patient patient, ListView lv) {
        lv.clear();
        lv.setTitleHeader("Patient Information");
        lv.addItem(new TextView(this.canvas, "Name: " + patient.getName(), Color.WHITE));
        lv.addItem(new TextView(this.canvas, "ID: " + patient.getNricFin(), Color.WHITE));
        lv.addItem(new TextView(this.canvas, "Age: " + patient.getAge(), Color.WHITE));
        lv.addItem(new TextView(this.canvas, "Gender: " + patient.getSex(), Color.WHITE));
        lv.addItem(new TextView(this.canvas, "Address: " + patient.getAddress(), Color.WHITE));
        lv.addItem(new TextView(this.canvas, "Patient ID: " + patient.getPatientId(), Color.WHITE));
        lv.addItem(new TextView(this.canvas, "Date of Birth: " + patient.getDOB(), Color.WHITE));
        //lv.addItem(new TextView(this.canvas, "Next of Kin: " + patient.get(), Color.GREEN));

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