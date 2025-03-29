package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.humans.Doctor;
import org.bee.hms.medical.Consultation;
import org.bee.pages.GenericUpdatePage;
import org.bee.ui.*;
import org.bee.ui.views.*;
import org.bee.utils.detailAdapters.ConsultationDetailsViewAdapter;
import org.bee.utils.formAdapters.ConsultationFormAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays and manages outpatient consultation information for doctors
 * <p>
 * This page operates in two modes:
 * <ul>
 * <li><b>List View</b>: Shows paginated list of all outpatient cases </li>
 * <li><b>Detail View</b>: Displays complete case details when a consultation is selected</li>
 * </ul>
 * <p>Key features:
 * <ul>
 * <li>Displays consultations with patient info, timing and diagnosis</li>
 * <li>Provides case update functionality for doctors</li>
 * <li>Supports paginated browsing (7 items per page)</li>
 * <li>Handles consultation selection and display</li>
 * </ul>
 */
public class ConsultationInfoPage extends UiBase {

    /** Controller for human-related operations (e.g., doctor/patient data) */
    private static final HumanController humanController = HumanController.getInstance();

    /** Controller for consultation  operations */
    private static final ConsultationController consultationController = ConsultationController.getInstance();

    /** Display consultation timestamps (format: yyyy-MM-dd HH:mm) */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Number of consultations to display per page in list view, set to 7*/
    private static final int ITEMS_PER_PAGE = 7;

    /** Currently selected consultation  */
    private final Consultation selectedConsultation;

    /** Reference to the current view being displayed */
    private View consultationListView;
    /**
     * Default constructor - shows list of outpatient cases
     */
    public ConsultationInfoPage() {
        this.selectedConsultation = null;
    }

    /**
     * Creates a ConsultationInfoPage with a specific consultation pre-selected for detail view.
     * @param consultation The consultation to display in detail view
     */
    public ConsultationInfoPage(Consultation consultation) {
        this.selectedConsultation = consultation;
    }

    /**
     * Creates the appropriate consultation view
     * @return The constructed View object for display
     */
    @Override
    public View createView() {
        if (selectedConsultation != null) {
            consultationListView = createConsultationDetailsView(selectedConsultation);
        } else {
            consultationListView = createConsultationListView();
        }
        return consultationListView;
    }

    /**
     * Handles post-view creation initialization
     * <p>
     * Marks the canvas for redraw to ensure proper rendering
     *
     * @param parentView The parent view container
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Creates a list view showing all outpatient consultations
     */
    private View createConsultationListView() {
        Doctor currentDoctor = (Doctor) humanController.getLoggedInUser();
        List<Consultation> consultations = consultationController.getConsultationsByDoctorId(
                currentDoctor.getStaffId());

        if (consultations.isEmpty()) {
            return new TextView(canvas, "No outpatient cases found.", Color.YELLOW);
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();
        for (Consultation c : consultations) {
            String patientName = c.getPatient() != null ? c.getPatient().getName() : "Unknown";
            String consultId = c.getConsultationId();
            LocalDateTime consultTime = c.getConsultationTime();
            String timeString = consultTime != null ? dateFormatter.format(consultTime) : "Not scheduled";
            String diagnosis = c.getDiagnosis() != null ? c.getDiagnosis() : "No diagnosis";

            String optionText = String.format("%s - %s (%s) - %s",
                    consultId, patientName, timeString, diagnosis);

            menuOptions.add(new PaginatedMenuView.MenuOption(consultId, optionText, c));
        }

        PaginatedMenuView paginatedView = new PaginatedMenuView(
                canvas,
                "\nOutpatient Cases",
                "Select a case to view details",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    Consultation selected = (Consultation) option.getData();
                    displaySelectedConsultation(selected);
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
     * Creates a view showing consultation details
     */
    private View createConsultationDetailsView(Consultation consultation) {
        ConsultationDetailsViewAdapter adapter = new ConsultationDetailsViewAdapter();
        CompositeView compositeView = new CompositeView(canvas, "", Color.CYAN);

        DetailsView<Consultation> detailsView = new DetailsView<>(
                canvas,
                "OUTPATIENT CASE DETAILS",
                consultation,
                Color.CYAN,
                adapter
        );

        compositeView.addView(detailsView);

        if (humanController.getLoggedInUser() instanceof Doctor) {
            MenuView actionMenu = new MenuView(canvas, "", Color.CYAN, false, true);

            actionMenu.attachLetterOption('u', "Update Case", input -> {
                openUpdateForm(consultation);
            });

            actionMenu.attachLetterOption('s', "Schedule Appointment", input -> {
                canvas.setSystemMessage("Feature coming soon!", SystemMessageStatus.INFO);
                canvas.setRequireRedraw(true);
            });

            compositeView.addView(actionMenu);
        }

        return compositeView;
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
                        displaySelectedConsultation(consultation);
                    }
            );

            ToPage(updatePage);
        } catch (Exception e) {
            canvas.setSystemMessage("Error opening update form: " + e.getMessage(), SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }

    /**
     * Displays the detailed view for a selected consultation
     *
     * @param consultation The consultation to display, cannot be null
     */
    public void displaySelectedConsultation(Consultation consultation) {
        View consultationView = createConsultationDetailsView(consultation);
        canvas.setCurrentView(consultationView);
        canvas.setRequireRedraw(true);
    }

    /**
     * Handles back button press events
    */
    @Override
    public void OnBackPressed() {
        super.OnBackPressed();
    }
}