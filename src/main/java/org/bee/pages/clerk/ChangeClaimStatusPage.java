package org.bee.pages.clerk;

import org.bee.hms.claims.ClaimStatus;
import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

public class ChangeClaimStatusPage extends UiBase {

    public View createView() {
        return new ListView(this.canvas, Color.GREEN);
    }
    /**
     * Called when the main page's view is created.
     * Creates a {@link ListView} to hold the main menu options.
     * Sets the title header to "Main".
     *
     * @return A new {@link ListView} instance representing the main page's view.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;
        lv.setTitleHeader("Change Claim Status");

        // Add your implementation here
        // For example:
        // 1. Display current claim status
        // 2. Provide options to change the status
        // 3. Handle user input for status change

        // Example implementation:
        lv.addItem(new TextView(this.canvas, "Current Claim Status: " + getCurrentClaimStatus(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Select new status:", Color.GREEN));

        for (ClaimStatus status : ClaimStatus.values()) {
            lv.addItem(new TextView(this.canvas, status.toString(), Color.GREEN));
        }

        lv.attachUserInput("Select Status", str -> changeClaimStatus(str, lv));

        canvas.setRequireRedraw(true);
    }

    private String getCurrentClaimStatus() {
        // Implement logic to get current claim status
        return "DRAFT"; // Placeholder
    }

    private void changeClaimStatus(String newStatus, ListView lv) {
        // Implement logic to change claim status
        // For example:
        // 1. Validate the new status
        // 2. Update the claim status
        // 3. Display confirmation message

        lv.addItem(new TextView(this.canvas, "Claim status updated to: " + newStatus, Color.GREEN));
        canvas.setRequireRedraw(true);
    }
}
