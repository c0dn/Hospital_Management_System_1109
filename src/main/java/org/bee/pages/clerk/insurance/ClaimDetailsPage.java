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

public class ClaimDetailsPage extends UiBase {

    private final InsuranceClaim claim;
    private final IObjectDetailsAdapter<InsuranceClaim> adapter;
    private final Runnable onChangeCallback;
    private final static ClaimController claimController = ClaimController.getInstance();

    public ClaimDetailsPage(InsuranceClaim claim, IObjectDetailsAdapter<InsuranceClaim> adapter, Runnable onChangeCallback) {
        this.claim = claim;
        this.adapter = adapter;
        this.onChangeCallback = onChangeCallback;
    }

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

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

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

    private void showError(String message, Exception e) {
        canvas.setSystemMessage(message + ": " + e.getMessage(),
                SystemMessageStatus.ERROR);
        canvas.setRequireRedraw(true);
    }

    private void saveChangesAndRefresh(String message) {
        claimController.saveData();

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);

        if (onChangeCallback != null) {
            onChangeCallback.run();
        } else {
            OnBackPressed();
        }
    }

    private void saveChangesAndNotify(String message) {
        claimController.saveData();

        OnBackPressed();
        View refreshedView = createView();
        navigateToView(refreshedView);

        canvas.setSystemMessage(message, SystemMessageStatus.SUCCESS);
        canvas.setRequireRedraw(true);

    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount.doubleValue());
    }

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
