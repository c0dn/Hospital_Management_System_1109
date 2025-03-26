package org.bee.pages.clerk;

import org.bee.controllers.ConsultationController;
import org.bee.hms.medical.Consultation;
import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.PaginatedView;
import org.bee.ui.views.TableView;
import org.bee.ui.views.TextView;
import org.bee.utils.ReflectionHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Page for viewing all outpatient case records.
 */
public class OutpatientCasesPage extends UiBase {

    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int ITEMS_PER_PAGE = 7;

    @Override
    public View createView() {
        return viewAllOutpatientCases();
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Display a table of all outpatient cases with pagination
     */
    private View viewAllOutpatientCases() {
        List<Consultation> consultations = consultationController.getAllOutpatientCases();

        if (consultations.isEmpty()) {
            return new TextView(canvas, "No outpatient cases found.", Color.YELLOW);
        }

        BiFunction<List<Consultation>, Integer, TableView<Consultation>> tableFactory =
                (pageItems, pageNum) -> createConsultationTableView(pageItems);

        return new PaginatedView<>(
                canvas,
                "Outpatient Cases",
                consultations,
                ITEMS_PER_PAGE,
                tableFactory,
                Color.CYAN
        );
    }

    /**
     * Creates a TableView for displaying consultation data
     */
    private TableView<Consultation> createConsultationTableView(List<Consultation> consultations) {
        TableView<Consultation> tableView = new TableView<>(canvas, "", Color.CYAN);

        tableView.showRowNumbers(true)
                .addColumn("Case ID", 12, c -> ReflectionHelper.stringPropertyAccessor("consultationId", "N/A").apply(c))

                .addColumn("Date", 16, c -> {
                    LocalDateTime time = (LocalDateTime) ReflectionHelper.propertyAccessor("consultationTime", null).apply(c);
                    String formattedDate = time != null ? dateFormatter.format(time) : "Not scheduled";

                    // Recent consultations (within the last 7 days) are highlighted in green
                    if (time != null && time.isAfter(LocalDateTime.now().minusDays(7))) {
                        return colorText(formattedDate, Color.GREEN);
                    }
                    // Older consultations (over 30 days) are dim
                    else if (time != null && time.isBefore(LocalDateTime.now().minusDays(30))) {
                        return colorText(formattedDate, Color.BLUE);
                    }
                    return formattedDate;
                })

                .addColumn("Type", 12, c -> {
                    Object typeObj = ReflectionHelper.propertyAccessor("type", null).apply(c);
                    if (typeObj == null) return "Unknown";

                    String typeStr = formatEnum(typeObj.toString());

                    if (typeObj.toString().contains("EMERGENCY")) {
                        return colorText(typeStr, Color.RED);
                    } else if (typeObj.toString().contains("FOLLOW_UP")) {
                        return colorText(typeStr, Color.GREEN);
                    } else if (typeObj.toString().contains("SPECIALIZED")) {
                        return colorText(typeStr, Color.MAGENTA);
                    }
                    return typeStr;
                })

                .addColumn("Patient Name", 18, ReflectionHelper.nestedStringPropertyAccessor("patient", "name", "Unknown"))

                .addColumn("Status", 10, c -> {
                    Object statusObj = ReflectionHelper.propertyAccessor("status", null).apply(c);
                    if (statusObj == null) return "Not set";

                    String statusStr = formatEnum(statusObj.toString());

                    if (statusObj.toString().contains("COMPLETED")) {
                        return colorText(statusStr, Color.GREEN);
                    } else if (statusObj.toString().contains("IN_PROGRESS")) {
                        return colorText(statusStr, Color.YELLOW);
                    } else if (statusObj.toString().contains("CANCELLED")) {
                        return colorText(statusStr, Color.RED);
                    } else if (statusObj.toString().contains("SCHEDULED")) {
                        return colorText(statusStr, Color.CYAN);
                    }
                    return statusStr;
                })

                .addColumn("Diagnosis", 20, c -> {
                    String diagnosis = ReflectionHelper.stringPropertyAccessor("diagnosis", "No diagnosis").apply(c);

                    String lowerDiag = diagnosis.toLowerCase();
                    if (lowerDiag.contains("hypertension") ||
                            lowerDiag.contains("diabetes") ||
                            lowerDiag.contains("infection")) {
                        return colorText(diagnosis, Color.YELLOW);
                    }
                    return diagnosis;
                })

                .addColumn("Reason", 20, ReflectionHelper.stringPropertyAccessor("visitReason", "Not specified"))

                .addColumn("Doctor", 18, ReflectionHelper.nestedStringPropertyAccessor("doctor", "name", "Not assigned"))
                .addColumn("Department", 15, c -> {
                    Object dept = ReflectionHelper.propertyAccessor("department", null).apply(c);
                    return dept != null ? formatEnum(dept.toString()) : "Not assigned";
                })

                .addColumn("Follow-up", 16, c -> {
                    Object followUpObj = ReflectionHelper.propertyAccessor("followUpDate", null).apply(c);
                    if (followUpObj == null) return "None scheduled";

                    LocalDateTime followUp = (LocalDateTime) followUpObj;
                    String formatted = dateFormatter.format(followUp);

                    if (followUp.isAfter(LocalDateTime.now()) &&
                            followUp.isBefore(LocalDateTime.now().plusDays(7))) {
                        return colorText(formatted, Color.RED);
                    } else if (followUp.isAfter(LocalDateTime.now()) &&
                            followUp.isBefore(LocalDateTime.now().plusDays(14))) {
                        return colorText(formatted, Color.YELLOW);
                    } else if (followUp.isBefore(LocalDateTime.now())) {
                        return colorText(formatted, Color.MAGENTA);
                    }
                    return formatted;
                })
                .setData(consultations);

        return tableView;
    }
}