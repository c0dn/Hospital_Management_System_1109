package org.bee.pages.clerk.insurance;

import org.bee.controllers.ClaimController;
import org.bee.controllers.HumanController;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.AbstractPaginatedView;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;
import org.bee.utils.detailAdapters.ClaimDetailsViewAdaptor;

import javax.sound.midi.SysexMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * A paginated view displaying all insurance claims in the system for selection.
 * <p>This page provides:</p>
 * <ul>
 *   <li>A paginated menu of all claims with key details</li>
 *   <li>Ability to select a claim to view its details</li>
 *   <li>Automatic refresh capability</li>
 * </ul>
 * Claims are displayed with ID, patient name, masked NRIC, submission date and status.
 */
public class ViewAllClaimsPage extends UiBase {

    /** Controller for human/patient related operations */
    private static final HumanController humanController = HumanController.getInstance();

    /** Controller for claim related operations */
    private static final ClaimController claimController = ClaimController.getInstance();

    /** Formatter for claim submission dates */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Number of claims to display per page, set to 7 */
    private static final int ITEMS_PER_PAGE = 7;

    /**
     * Creates the main view for displaying all claims.
     * @return the root view component
     */
    @Override
    protected View createView() {
        return selectClaimToView();
    }

    /**
     * Handles post-creation view initialization.
     * @param parentView the parent view container
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Creates and configures the claim selection menu view.
     * @return the configured paginated menu view
     */
    private View selectClaimToView() {
        claimController.loadData();
        List<InsuranceClaim> allClaims = claimController.getAllClaims();

        if (allClaims.isEmpty()) {
            return new TextView(canvas, "No claims found in the system.", Color.YELLOW);
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();

        for (InsuranceClaim claim : allClaims) {
            String claimId = claim.getClaimId();
            String patientName = claim.getPatient() != null ? claim.getPatient().getName() : "Unknown Patient";
            LocalDateTime claimSubmissionDate = claim.getSubmissionDate();
            String patientNRIC = claim.getPatient() != null ? humanController.maskNRIC(claim.getPatient().getNricFin()) : "Unknown Patient";
            String formattedDate = claimSubmissionDate != null ? dateFormatter.format(claimSubmissionDate) : "Unknown Date";
            String status = String.valueOf(claim.getClaimStatus());

            String optionText = String.format("%s - %s, %s - %s (%s)", claimId, patientName, patientNRIC, formattedDate, status);
            menuOptions.add(new PaginatedMenuView.MenuOption(claimId, optionText, claim));
        }

        PaginatedMenuView paginatedMenuView = new PaginatedMenuView(
                canvas,
                "Select Claim to view details",
                "List of Claims",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedMenuView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    InsuranceClaim selectedClaim = (InsuranceClaim) option.getData();
                    openClaimDetails(selectedClaim);
                } else {
                    canvas.setSystemMessage("Error: Invalid selection", SystemMessageStatus.ERROR);
                    canvas.setRequireRedraw(true);
                }
            } catch (Exception e) {
                canvas.setSystemMessage("Error processing selection: " + e.getMessage(), SystemMessageStatus.ERROR);
                canvas.setRequireRedraw(true);
            }
        });

        return paginatedMenuView;
    }

    /**
     * Opens the details view for a selected claim.
     * @param claim the claim to display details for
     */
    private void openClaimDetails(InsuranceClaim claim) {
        ClaimDetailsViewAdaptor adaptor = new ClaimDetailsViewAdaptor();
        ClaimDetailsPage detailsPage = new ClaimDetailsPage(claim, adaptor, this::refreshView);
        ToPage(detailsPage);
    }

    /**
     * Refreshes the claims list view.
     */
    private void refreshView() {
        View refreshedView = selectClaimToView();
        OnViewCreated(refreshedView);
        canvas.setCurrentView(refreshedView);
    }

}
