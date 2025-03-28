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
import java.util.List;
import java.util.function.BiFunction;

/**
 * Page for viewing outpatient cases for doctors.
 */
public class OutpatientPatientInfoPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;

    private final Consultation selectedConsultation;

    /**
     * Default constructor - shows list of outpatient cases
     */
    public OutpatientPatientInfoPage() {
        this.selectedConsultation = null;
    }

    /**
     * Constructor with pre-selected consultation
     */
    public OutpatientPatientInfoPage(Consultation consultation) {
        this.selectedConsultation = consultation;
    }

    @Override
    public View createView() {
        if (selectedConsultation != null) {
            return displayConsultationDetails(selectedConsultation, canvas.getCurrentView());
        } else {
            return viewAllOutpatientCases();
        }
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Display a table of all outpatient cases with pagination
     */
    private View viewAllOutpatientCases() {
        // Get cases for the current doctor
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

        // Add action for viewing details
        paginatedView.attachUserInput("View Case Details", input -> {
            TableView<Consultation> tableView = paginatedView.getContentView();
            if (tableView != null) {
                tableView.setSelectionCallback((rowIndex, consultation) -> {
                    displayConsultationDetails(consultation, canvas.getCurrentView());
                });
            }
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

    private View displayConsultationDetails(Consultation consultation, View previousView) {
        ConsultationDetailsViewAdapter adapter = new ConsultationDetailsViewAdapter();

        DetailsView<Consultation> detailsView = new DetailsView<>(
                canvas,
                "OUTPATIENT CASE DETAILS",
                consultation,
                Color.CYAN,
                adapter
        );

        if (previousView != null) {
            detailsView.setPreviousView(previousView);
        }

        if (humanController.getLoggedInUser() instanceof Doctor) {
            detailsView.addAction("Update Case", "u", () -> {
                ToPage(new UpdateOutpatientCase(consultation));
            });
        }

        return detailsView;
    }
}