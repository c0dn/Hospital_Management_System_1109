package org.bee.pages.clerk;

import org.bee.controllers.HumanController;
import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;
/**
 * Represents managing claim for the Clerk.
 * This page displays a menu of options for the user to navigate to different sections of the application.
 * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
 */
public class ManageClaimPage extends UiBase {
    /**
     * Called when the manage claim page's view is created.
     * Creates a {@link ListView} to hold the main menu options.
     *
     * @return A new {@link ListView} instance representing the main page's view.
     */
    @Override
    public View createView() {
        return new ListView(this.canvas, Color.GREEN);
    }

    /**
     * Called after the view has been created and attached to the UI.
     * Populates the view with the main menu "Manage Claim"
     * Attaches user input handlers to each menu option to navigate to the corresponding pages.
     *
     * @param parentView The parent {@link View} to which the main page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView; // Cast the parent view to a list view
        lv.setTitleHeader("Welcome to Clerk portal | Welcome Back " ); // Set the title header of the list view

        // Menu options

        lv.addItem(new TextView(this.canvas, "1. Manage Claim - Manage existing claims", Color.GREEN));

        // Attach user input handlers for navigation

        lv.attachUserInput("Manage Claim", str -> ToPage(new ManageClaimPage()));
        canvas.setRequireRedraw(true);
    }
}

