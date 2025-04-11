package org.bee.pages.clerk.insurance;

import org.bee.controllers.ClaimController;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.pages.GenericUpdatePage;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.MenuView;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.ui.views.TextView;
import org.bee.utils.formAdapters.ClaimFormAdapter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A UI page that displays detailed information about an insurance claim
 * This page shows claim details using an {@link ObjectDetailsView}
 * along with a menu of context-sensitive actions based on the claim's current status.
 *
 */
public class ClaimDetailsPage extends UiBase {

    /** The insurance claim being displayed and managed */
    private final InsuranceClaim claim;

    /** Adapter that configures how claim details are rendered */
    private final IObjectDetailsAdapter<InsuranceClaim> adapter;

    /** Callback to trigger when claim data changes */
    private final Runnable onChangeCallback;

    /** Shared controller for claim operations */
    private final static ClaimController claimController = ClaimController.getInstance();

    /**
     * Creates a new ClaimDetailsPage to display and manage an insurance claim.
     *
     * @param claim the insurance claim to display
     * @param adapter the adapter that controls how claim details are rendered
     * @param onChangeCallback callback to execute when claim data changes
     */
    public ClaimDetailsPage(InsuranceClaim claim, IObjectDetailsAdapter<InsuranceClaim> adapter, Runnable onChangeCallback) {
        this.claim = claim;
        this.adapter = adapter;
        this.onChangeCallback = onChangeCallback;
    }

    /**
     * Creates the main view hierarchy for displaying claim details and action buttons.
     * @return the root CompositeView containing all UI elements
     */
    @Override
    protected View createView() {
        if (Objects.isNull(claim)) {
            return new TextView(this.canvas, "Error: No claim selected", Color.RED);
        }

        CompositeView compositeView = new CompositeView(this.canvas, "Claim Details", Color.CYAN);

        ObjectDetailsView detailsView = new ObjectDetailsView(
                this.canvas,
                "Claim Details",
                claim,
                Color.CYAN
        );

        adapter.configureView(detailsView, claim);

        MenuView menuView = new MenuView(this.canvas, "", Color.CYAN, false, true);
        ClaimStatus status = claim.getClaimStatus();
        setUpActionButtons(menuView, status);

        compositeView.addView(detailsView);
        compositeView.addView(menuView);

        return compositeView;
    }

    /**
     * Handles post-creation setup after the view hierarchy is built.
     * @param parentView the Parent view of the UiBase class, provided in OnCreateView
     */
    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    /**
     * Configures action buttons in the menu based on the claim's current status.
     *
     * @param menuView The menu view to populate with actions
     * @param status The current claim status determining available actions
     */
    private void setUpActionButtons(MenuView menuView, ClaimStatus status) {
        menuView.clearUserInputs();

        MenuView.MenuSection actionSection = menuView.addSection("Available Actions");
        int optionIndex = 1;

        if (status == ClaimStatus.DRAFT) {
            actionSection.addOption(optionIndex, "Submit for Processing");
            menuView.attachMenuOptionInput(optionIndex, "Submit for Processing", input -> {
                try {
                    claim.submitForProcessing();
                    saveChangesAndNotify("Claim has been submitted for processing successfully!");
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
            actionSection.addOption(optionIndex++, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
            actionSection.addOption(optionIndex++, "Update Claim Details");
            menuView.attachMenuOptionInput(optionIndex, "Update Claim Details", input -> {
                try {
                    openUpdateForm(claim);
                } catch (Exception e) {
                    showError("Error updating claim details", e);
                }
            });
        } else if (status == ClaimStatus.SUBMITTED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
            actionSection.addOption(optionIndex++, "Update Claim Details");
            menuView.attachMenuOptionInput(optionIndex, "Update Claim Details", input -> {
                try {
                    openUpdateForm(claim);
                } catch (Exception e) {
                    showError("Error updating claim details", e);
                }
            });
        } else if (status == ClaimStatus.IN_REVIEW) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
        } else if (status == ClaimStatus.PENDING_INFORMATION) {
            actionSection.addOption(optionIndex, "Update Claim Details");
            menuView.attachMenuOptionInput(optionIndex, "Update Claim Details", input -> {
                try {
                    openUpdateForm(claim);
                } catch (Exception e) {
                    showError("Error updating claim details", e);
                }
            });
        } else if (status == ClaimStatus.APPROVED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
        } else if (status == ClaimStatus.PARTIALLY_APPROVED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
            actionSection.addOption(optionIndex++, "Update Claim Details");
            menuView.attachMenuOptionInput(optionIndex, "Update Claim Details", input -> {
                try {
                    openUpdateForm(claim);
                } catch (Exception e) {
                    showError("Error updating claim details", e);
                }
            });
        } else if (status == ClaimStatus.DENIED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
            actionSection.addOption(optionIndex++, "Update Claim Details");
            menuView.attachMenuOptionInput(optionIndex, "Update Claim Details", input -> {
                try {
                    openUpdateForm(claim);
                } catch (Exception e) {
                    showError("Error updating claim details", e);
                }
            });
        } else if (status == ClaimStatus.APPEALED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
            actionSection.addOption(optionIndex++, "Update Claim Details");
            menuView.attachMenuOptionInput(optionIndex, "Update Claim Details", input -> {
                try {
                    openUpdateForm(claim);
                } catch (Exception e) {
                    showError("Error updating claim details", e);
                }
            });
        } else if (status == ClaimStatus.CANCELLED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
        } else if (status == ClaimStatus.EXPIRED) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
        } else if (status == ClaimStatus.PAID) {
            actionSection.addOption(optionIndex, "Submit New Claim");
            menuView.attachMenuOptionInput(optionIndex, "Submit New Claim", input -> {
                try {
                    ToPage(new NewInsuranceClaimPage());
                } catch (Exception e) {
                    showError("Error submitting claim", e);
                }
            });
        }
    }

    /**
     * Displays an error message
     *
     * @param message The main error message to display
     * @param e The exception that caused the error
     */
    private void showError(String message, Exception e) {
        // ... method implementation ...
    }

    /**
     * Saves claim changes, navigates back, and refreshes the view with success message.
     *
     * @param message The success message to display
     */
    private void saveChangesAndRefresh(String message) {
        // ... method implementation ...
    }


    private void saveChangesAndNotify(String message) {
        // ... method implementation ...
    }

    /**
     * Formats a monetary amount
     *
     * @param amount The amount to format
     * @return Formatted string with 2 decimal places
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount.doubleValue());
    }

    /**
     * Opens an edit form for modifying the specified insurance claim.
     * @param claim the insurance claim to be edited
     */
    private void openUpdateForm(InsuranceClaim claim) {
        try {
            ClaimFormAdapter adapter = new ClaimFormAdapter();

            GenericUpdatePage<InsuranceClaim> updatePage = new GenericUpdatePage<>(
                    claim,
                    adapter,
                    () -> {
                        navigateToView(canvas.getCurrentView());
                    });
            ToPage(updatePage);
        } catch (Exception e) {
            canvas.setSystemMessage("Error opening update form: " + e.getMessage(), SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }

}
