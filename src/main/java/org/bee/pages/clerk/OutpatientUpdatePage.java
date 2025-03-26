package org.bee.pages.clerk;

import org.bee.controllers.ConsultationController;
import org.bee.hms.medical.Consultation;
import org.bee.pages.GenericUpdatePage;
import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;
import org.bee.utils.formAdapters.ConsultationFormAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Page for selecting and updating outpatient case details.
 */
public class OutpatientUpdatePage extends UiBase {

    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private static final int ITEMS_PER_PAGE = 7;

    @Override
    public View createView() {
        return updateOutpatientCase();
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Display a selection menu for updating outpatient case information
     */
    private View updateOutpatientCase() {
        List<Consultation> consultations = consultationController.getAllOutpatientCases();

        if (consultations.isEmpty()) {
            return new TextView(canvas, "No outpatient cases found to update.", Color.YELLOW);
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
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

    /**
     * Opens the update form for a specific consultation
     */
    private void openUpdateForm(Consultation consultation) {
        try {
            ConsultationFormAdapter adapter = new ConsultationFormAdapter();

            GenericUpdatePage<Consultation> updatePage = new GenericUpdatePage<>(
                    consultation,
                    adapter,
                    () -> {
                        View refreshedView = updateOutpatientCase();
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
}