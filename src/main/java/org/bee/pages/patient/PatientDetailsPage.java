package org.bee.pages.patient;

import org.bee.controllers.HumanController;
import org.bee.hms.humans.Patient;
import org.bee.pages.GenericUpdatePage;
import org.bee.pages.ObjectDetailsPage;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.ui.views.TextView;
import org.bee.utils.detailAdapters.PatientDetailsAdapter;
import org.bee.utils.formAdapters.PatientFormAdapter;

import java.util.Objects;

/**
 * A page that displays detailed information about a patient.
 * This is an example of how to use the ObjectDetailsPage with a specific adapter.
 */
public class PatientDetailsPage extends UiBase {

    private final Patient patient;
    private final PatientDetailsAdapter detailsAdapter = new PatientDetailsAdapter();

    /**
     * Creates a new PatientDetailsPage for the given patient.
     *
     * @param patient The patient to display details for
     */
    public PatientDetailsPage(Patient patient) {
        this.patient = patient;
    }

    @Override
    public View createView() {
        if (Objects.isNull(patient)) {
            return new TextView(this.canvas, "Error: No patient selected", Color.RED);
        }

        CompositeView compositeView = new CompositeView(this.canvas, "Patient Details", Color.CYAN);

        ObjectDetailsView detailsView = new ObjectDetailsView(
                this.canvas,
                "Patient Details",
                patient,
                Color.CYAN
        );

        detailsAdapter.configureView(detailsView, patient);

        compositeView.addView(detailsView);

        return compositeView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        CompositeView compositeView = (CompositeView) parentView;
        compositeView.attachUserInput("Edit Patient Details", input -> editPatient());

        canvas.setRequireRedraw(true);
    }

    /**
     * Handles the action to edit the current patient.
     * This method opens the GenericUpdatePage with a PatientFormAdapter.
     */
    private void editPatient() {
        try {
            PatientFormAdapter formAdapter = new PatientFormAdapter();

            GenericUpdatePage<Patient> updatePage = new GenericUpdatePage<>(
                    patient,
                    formAdapter,
                    () -> {
                        View refreshedView = createView();
                        navigateToView(refreshedView);
                        canvas.setSystemMessage("Patient updated successfully", SystemMessageStatus.SUCCESS);
                    }
            );

            ToPage(updatePage);
        } catch (Exception e) {
            canvas.setSystemMessage("Error opening update form: " + e.getMessage(), SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }
}