package org.bee.pages.clerk.insurance;

import org.bee.controllers.ClaimController;
import org.bee.controllers.ConsultationController;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.pages.GenericUpdatePage;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.AbstractPaginatedView;
import org.bee.ui.views.PaginatedMenuView;
import org.bee.ui.views.TextView;
import org.bee.utils.formAdapters.ClaimFormAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Page for updating and modifying insurance claims
 * <p>
 * Provides functionality to:
 * <ul>
 * <li>Browse and select claims for modification</li>
 * <li>Open claim update forms</li>
 * <li>Handle claim selection errors</li>
 * </ul>
 */
public class UpdateClaimPage extends UiBase {

    /** Controller for claim operations */
    private static final ClaimController claimController = ClaimController.getInstance();

    /** Number of claims to display per page, set to 7 */
    private static final int ITEMS_PER_PAGE = 7;

    /**
     * Creates the initial claim selection view
     * @return View containing paginated list of claims
     */
    @Override
    protected View createView() {
        return updateClaim();
    }

    /**
     * Handles view creation event by marking canvas for redraw
     * @param parentView The parent view container
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Creates the claim selection interface with paginated results
     * @return View displaying claims or message if none found
     */
    private View updateClaim() {
        List<InsuranceClaim> claims = claimController.getAllClaims();

        if (claims.isEmpty()) {
            return new TextView(canvas, "No claims found to update.", Color.YELLOW);
        }

        List<PaginatedMenuView.MenuOption> menuOptions = new ArrayList<>();

        for (InsuranceClaim i : claims) {
            String claimId = i.getClaimId();
            String patientName = i.getPatient() != null ? i.getPatient().getName() : "Unknown Patient";

            String optionText = String.format("%s - %s", claimId, patientName);
            menuOptions.add(new PaginatedMenuView.MenuOption(claimId, optionText, i));
        }

        PaginatedMenuView paginatedMenuView = new PaginatedMenuView(
                canvas,
                "Select Claim to Update",
                "List of Claims",
                menuOptions,
                ITEMS_PER_PAGE,
                Color.CYAN
        );

        paginatedMenuView.setSelectionCallback(option -> {
            try {
                if (option != null && option.getData() != null) {
                    InsuranceClaim selectedClaim = (InsuranceClaim) option.getData();
                    openUpdateForm(selectedClaim);
                } else {
                    canvas.setSystemMessage("Error: Invalid Selection", SystemMessageStatus.ERROR);
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
     * Opens the claim update form for the selected claim
     * @param claim The claim to be updated
     */
    private void openUpdateForm(InsuranceClaim claim) {
        try {
            ClaimFormAdapter adapter = new ClaimFormAdapter();

            GenericUpdatePage<InsuranceClaim> updatePage = new GenericUpdatePage<>(
                    claim,
                    adapter,
                    () -> {
                        View refreshedView = updateClaim();
                        navigateToView(refreshedView);
                    });
            ToPage(updatePage);
        } catch (Exception e) {
            canvas.setSystemMessage("Error opening update form: " + e.getMessage(), SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }
}
