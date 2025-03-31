package org.bee.pages.clerk;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.pages.GenericUpdatePage;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.*;
import org.bee.utils.ReflectionHelper;
import org.bee.utils.detailAdapters.ConsultationDetailsViewAdapter;
import org.bee.utils.detailAdapters.PatientDetailsViewAdapter;
import org.bee.utils.formAdapters.ConsultationFormAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Page for viewing all outpatient case records.
 */
public class OutpatientCasesPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;
    private Consultation consultation;
    private View consultationListView;

    @Override
    public View createView() {
        if (consultation == null) {
            consultationListView = createConsultationListView();
        } else {
            consultationListView = createConsultationCompositeView(consultation);
        }


        return consultationListView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    private View createConsultationListView() {
        List<Consultation> consultations = consultationController.getAllOutpatientCases();

        if (consultations.isEmpty()) {
            return getBlankListView("No Outpatient Cases",
                    "No Outpatient Cases found.\nPlease check filtering options or add new patient records.");
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
        for (Consultation c : consultations) {
            String caseId = c.getConsultationId();
            String patientName = c.getPatient().getName();
            String patientNRIC = humanController.maskNRIC(c.getPatient().getNricFin());
            String date = dateFormatter.format(c.getConsultationTime());

            String optionText = String.format("%s - %s, %s (%s)", caseId, patientName, patientNRIC, date);

            menuOptions.add(new PaginatedMenuView.MenuOption(caseId, optionText, c));
        }

        PaginatedMenuView paginatedMenuView = new PaginatedMenuView(
                canvas,
                "\nOutpatient Cases",
                "Select a case to view details",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedMenuView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Consultation selectedConsultation = (Consultation) option.getData();
                    displaySelectedConsultation(selectedConsultation, canvas.getCurrentView());
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

        return paginatedMenuView;
    }

    private View createConsultationDetailsView(Consultation consultation) {
        ConsultationDetailsViewAdapter adapter = new ConsultationDetailsViewAdapter();

        DetailsView<Consultation> detailsView = new DetailsView<>(
                canvas,
                "OUTPATIENT INFORMATION",
                consultation,
                Color.CYAN,
                adapter
        );

        return detailsView;
    }

    private View createConsultationCompositeView(Consultation consultation) {
        DetailsView<Consultation> detailsView = (DetailsView<Consultation>) createConsultationDetailsView(consultation);

        CompositeView compositeView = new CompositeView(canvas, "", Color.CYAN);
        compositeView.addView(detailsView);


        if (humanController.getLoggedInUser() instanceof Clerk) {
            MenuView actionMenu = new MenuView(canvas, "", Color.CYAN, false, true);

            MenuView.MenuSection actionSection = actionMenu.addSection("");
            actionSection.addOption(1, "Update Outpatient Case");

            actionMenu.attachMenuOptionInput(1, "Update Outpatient Case", input -> {
                openUpdateForm(consultation);
                canvas.setRequireRedraw(true);
            });


            compositeView.addView(actionMenu);
        }

        return compositeView;
    }

    /**
     * Displays the selected consultation using the current view.
     *
     * @param consultation The consultation to display.
     */
    public void displaySelectedConsultation(Consultation consultation) {
        displaySelectedConsultation(consultation, canvas.getCurrentView());
    }

    /**
     * Displays the selected consultation in a new view while keeping track of the previous view.
     *
     * @param consultation  The consultation to display.
     * @param previousView  The previous view before displaying the consultation.
     */
    public void displaySelectedConsultation(Consultation consultation, View previousView) {
        View patientView = createConsultationCompositeView(consultation);
        canvas.setCurrentView(patientView);
        canvas.setRequireRedraw(true);
    }

    private void openUpdateForm(Consultation consultation) {
        try {
            ConsultationFormAdapter adapter = new ConsultationFormAdapter();

            GenericUpdatePage<Consultation> updatePage = new GenericUpdatePage<>(
                    consultation,
                    adapter,
                    () -> {
                        navigateToView(canvas.getCurrentView());
                    }
            );

            ToPage(updatePage);
        } catch (Exception e) {
            canvas.setSystemMessage("Error opening update form: " + e.getMessage(), SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }

    /**
     * Handles the back button press by calling the superclass implementation.
     */
    @Override
    public void OnBackPressed(){
        super.OnBackPressed();
    }

}