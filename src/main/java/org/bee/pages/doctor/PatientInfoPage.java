package org.bee.pages.doctor;

import org.bee.controllers.HumanController;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.Sex;
import org.bee.hms.medical.Consultation;
import org.bee.ui.*;
import org.bee.ui.views.*;
import org.bee.utils.detailAdapters.PatientDetailsViewAdapter;

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
    private View patientListView;
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
        if (patient == null) {
            patientListView = createPatientListView();
        } else {
            patientListView = createPatientDetailsView(patient);
        }

        return patientListView;
    }

    /**
     * Called when the view is created. Populates the view with data.
     *
     * @param parentView The parent view created in createView
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Creates a view showing all patients in a paginated list
     */
    private View createPatientListView() {
        List<Patient> patients = humanController.getAllPatients();

        if (patients.isEmpty()) {
            return new TextView(canvas, "No patients found.", Color.YELLOW);
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
        for (Patient p : patients) {
            String patientName = p.getName();
            String patientId = p.getPatientId();
            String nricFin = p.getNricFin();

            String optionText = String.format("%s (%s) - ID: %s",
                    patientName, nricFin, patientId);

            menuOptions.add(new PaginatedMenuView.MenuOption(patientId, optionText, p));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "\nPatient Records",
                "Select a patient to view details",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Patient selectedPatient = (Patient) option.getData();
                    displaySelectedPatient(selectedPatient, canvas.getCurrentView());
                } else {
                    canvas.setSystemMessage("Error: Invalid selection",
                            SystemMessageStatus.ERROR);
                    canvas.setRequireRedraw(true);
                }
            } catch (Exception e) {
                canvas.setSystemMessage("Error processing selection: " + e.getMessage(),
                        SystemMessageStatus.ERROR);
                canvas.setRequireRedraw(true);
                System.err.println("Exception in selection callback: " + e.getMessage());
            }
        });

        return paginatedView;
    }

    /**
     * Creates a view showing details for a specific patient
     */
    private View createPatientDetailsView(Patient patient) {
        PatientDetailsViewAdapter adapter = new PatientDetailsViewAdapter();

        DetailsView<Patient> detailsView = new DetailsView<>(
                canvas,
                "PATIENT INFORMATION",
                patient,
                Color.CYAN,
                adapter
        );

        return detailsView;
    }


    /**
     * Displays detailed information for the selected patient
     */
    public void displaySelectedPatient(Patient patient) {
        displaySelectedPatient(patient, canvas.getCurrentView());
    }

    /**
     * Displays detailed information for the selected patient
     * with support for returning to the previous view
     */
    public void displaySelectedPatient(Patient patient, View previousView) {
        PatientDetailsViewAdapter adapter = new PatientDetailsViewAdapter();

        DetailsView<Patient> detailsView = new DetailsView<>(
                canvas,
                "PATIENT INFORMATION",
                patient,
                Color.CYAN,
                adapter
        );

        detailsView.setPreviousView(previousView);

        if (humanController.getLoggedInUser() instanceof Doctor) {
            detailsView.addAction("Schedule Appointment", "s", () -> {
                canvas.setSystemMessage("Feature coming soon", SystemMessageStatus.INFO);
                canvas.setRequireRedraw(true);
            });
        }

        canvas.setCurrentView(detailsView);
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