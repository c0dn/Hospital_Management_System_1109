package org.bee.pages.clerk;

import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

public class ClaimStatusPage extends UiBase {

    /**
     * Called when the main page's view is created.
     * Creates a {@link ListView} to hold the main menu options.
     * Sets the title header to "Main".
     *
     * @return A new {@link ListView} instance representing the main page's view.
     */
    @Override
    public View OnCreateView() {
        ListView lv = new ListView(this.canvas, Color.GREEN);
        lv.setTitleHeader("Main");
        return lv;
    }
    /**
     * Called after the view has been created and attached to the UI.
     * Populates the view with the main menu options, such as "New Claim", "Manage Claim", "Claim Status", "Change Claim Status".
     * Attaches user input handlers to each menu option to navigate to the corresponding pages.
     *
     * @param parentView The parent {@link View} to which the main page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView; // Cast the parent view to a list view
        lv.setTitleHeader("Welcome to Clerk portal | Welcome Back " ); // Set the title header of the list view

        // Menu options
        lv.addItem(new TextView(this.canvas, "1. New Claim - Submit new claim", Color.GREEN));

        lv.addItem(new TextView(this.canvas, "3. Claim Status - Check existing claim status", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "4. Change Claim Status - Update existing claim status", Color.GREEN));

        // Attach user input handlers for navigation
        lv.attachUserInput("New Claim", str -> ToPage(new NewClaimPage()));
        lv.attachUserInput("Manage Claim", str -> ToPage(new ManageClaimPage()));
        lv.attachUserInput("Claim Status", str -> ToPage(new ClaimStatusPage()));
        lv.attachUserInput("Change Claim Status", str -> ToPage(new ChangeClaimStatusPage()));

        canvas.setRequireRedraw(true);
    }
}
