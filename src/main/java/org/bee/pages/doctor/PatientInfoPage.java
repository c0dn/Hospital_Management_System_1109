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
 * Dual purpose page, can ask user to select multiple patients, or display a single patient
 */
public class PatientInfoPage extends UiBase {
    public static HumanController humanController = HumanController.getInstance();
    public static Patient patient;
    @Override
    public View OnCreateView() {
        return new ListView(this.canvas, Color.GREEN);
    }

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
            lv.addItem(new TextView(this.canvas, index + ". " + patient.getName(), Color.GREEN));
            index += 1;
        }

        // When selecting "Select Patient Index"
        lv.attachUserInput("Select Patient Index", str -> {
            int selectedIndex = InputHelper.getValidIndex("Select Patient index", patients);
            patient = patients.get(selectedIndex);

            try {
                displayPatient(patient, lv);
            }catch (Exception e){
                e.printStackTrace();
                throw e;
            }
        });
    }

    private void displayPatient(Patient patient, ListView lv) {
        lv.clear();
        lv.setTitleHeader("Patient Information");
        lv.addItem(new TextView(this.canvas, "Name: " + patient.getName(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "ID: " + patient.getNricFin(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Age: " + patient.getAge(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Gender: " + patient.getSex(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Address: " + patient.getAddress(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Patient ID: " + patient.getPatientId(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Date of Birth: " + patient.getDOB(), Color.GREEN));
        //lv.addItem(new TextView(this.canvas, "Next of Kin: " + patient.get(), Color.GREEN));

        // Request UI redraw
        canvas.setRequireRedraw(true);
    }

    @Override
    public void OnBackPressed(){
        super.OnBackPressed();
        PatientInfoPage.patient = null;
    }

}