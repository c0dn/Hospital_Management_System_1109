package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.ui.*;
import org.bee.ui.views.*;
import org.bee.utils.detailAdapters.ConsultationDetailsViewAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Page for viewing outpatient cases for doctors.
 */
public class ConsultationInfoPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;

    private final Consultation selectedConsultation;

    /**
     * Default constructor - shows list of outpatient cases
     */
    public ConsultationInfoPage() {
        this.selectedConsultation = null;
    }

    /**
     * Constructor with pre-selected consultation
     */
    public ConsultationInfoPage(Consultation consultation) {
        this.selectedConsultation = consultation;
    }

    @Override
    public View createView() {
        if (selectedConsultation != null) {
            return createConsultationCompositeView(selectedConsultation, canvas.getCurrentView());
        } else {
            return viewAllOutpatientCases();
        }
    }

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
     * Display a table of all outpatient cases with pagination
     */
    private View viewAllOutpatientCases() {
        Doctor currentDoctor = (Doctor) humanController.getLoggedInUser();
        List<Consultation> consultations = consultationController.getConsultationsByDoctorId(
                currentDoctor.getStaffId());

        if (consultations.isEmpty()) {
            return new TextView(canvas, "No outpatient cases found.", Color.YELLOW);
        }

        BiFunction<List<Consultation>, Integer, TableView<Consultation>> tableFactory =
                (pageItems, pageNum) -> createConsultationTableView(pageItems);

        PaginatedView<Consultation, TableView<Consultation>> paginatedView = new PaginatedView<>(
                canvas,
                "Outpatient Cases",
                consultations,
                ITEMS_PER_PAGE,
                tableFactory,
                Color.CYAN
        );

        paginatedView.attachUserInput("View Case Details", input -> {
            TableView<Consultation> tableView = paginatedView.getContentView();
            if (tableView != null) {
                tableView.setSelectionCallback((rowIndex, consultation) -> {
                    View detailsView = createConsultationCompositeView(consultation, canvas.getCurrentView());
                    canvas.setCurrentView(detailsView);
                    canvas.setRequireRedraw(true);
                });
            }
        });

        paginatedView.attachUserInput("Select Patient", input -> {
            View patientListView = createPatientListView();
            canvas.setCurrentView(patientListView);
            canvas.setRequireRedraw(true);
        });

        return paginatedView;
    }

    /**
     * Creates a TableView for displaying consultation data
     */
    private TableView<Consultation> createConsultationTableView(List<Consultation> consultations) {
        TableView<Consultation> tableView = new TableView<>(canvas, "", Color.CYAN);

        tableView.showRowNumbers(true)
                .addColumn("Case ID", 12, c -> c.getConsultationId())
                .addColumn("Date", 16, c -> {
                    LocalDateTime time = c.getConsultationTime();
                    String formattedDate = time != null ? dateFormatter.format(time) : "Not scheduled";

                    if (time != null && time.isAfter(LocalDateTime.now().minusDays(7))) {
                        return colorText(formattedDate, Color.GREEN);
                    }
                    else if (time != null && time.isBefore(LocalDateTime.now().minusDays(30))) {
                        return colorText(formattedDate, Color.BLUE);
                    }
                    return formattedDate;
                })
                .addColumn("Patient Name", 18, c -> {
                    Patient patient = c.getPatient();
                    return patient != null ? patient.getName() : "Unknown";
                })
                .addColumn("Status", 10, c -> {
                    if (c.getStatus() == null) return "Not set";

                    String statusStr = formatEnum(c.getStatus().toString());

                    return switch (c.getStatus()) {
                        case COMPLETED -> colorText(statusStr, Color.GREEN);
                        case IN_PROGRESS -> colorText(statusStr, Color.YELLOW);
                        case CANCELLED -> colorText(statusStr, Color.RED);
                        case SCHEDULED -> colorText(statusStr, Color.CYAN);
                    };
                })
                .addColumn("Diagnosis", 20, c -> {
                    String diagnosis = c.getDiagnosis();
                    if (diagnosis == null || diagnosis.isEmpty()) {
                        return "No diagnosis";
                    }

                    String lowerDiag = diagnosis.toLowerCase();
                    if (lowerDiag.contains("hypertension") ||
                            lowerDiag.contains("diabetes") ||
                            lowerDiag.contains("infection")) {
                        return colorText(diagnosis, Color.YELLOW);
                    }
                    return diagnosis;
                })
                .setData(consultations);

        return tableView;
    }

    /**
     * Creates a composite view for displaying consultation details with action buttons
     */
    private View createConsultationCompositeView(Consultation consultation, View previousView) {
        CompositeView compositeView = new CompositeView(canvas, "", Color.CYAN);

        ConsultationDetailsViewAdapter adapter = new ConsultationDetailsViewAdapter();
        DetailsView<Consultation> detailsView = new DetailsView<>(
                canvas,
                "OUTPATIENT CASE DETAILS",
                consultation,
                Color.CYAN,
                adapter
        );

        compositeView.addView(detailsView);

        if (humanController.getLoggedInUser() instanceof Doctor) {
            MenuView actionMenu = new MenuView(canvas, "", Color.CYAN, true, false);
            MenuView.MenuSection actionSection = actionMenu.addSection("Available Actions");
            actionSection.addOption(1, "Update Case (u)");

            actionMenu.attachLetterOption('u', "Update Case", input -> {
                ToPage(new UpdateOutpatientCase(consultation));
            });

            compositeView.addView(actionMenu);
        }

        return compositeView;
    }

    /**
     * Displays details about a selected patient
     */
    private void displaySelectedPatient(Patient patient, View previousView) {
        // Here you can implement what happens when a patient is selected
        // For example, you could show their medical history or add a new consultation

        canvas.setSystemMessage("Selected patient: " + patient.getName() + ". Functionality coming soon.",
                SystemMessageStatus.INFO);
        canvas.setRequireRedraw(true);
    }
}