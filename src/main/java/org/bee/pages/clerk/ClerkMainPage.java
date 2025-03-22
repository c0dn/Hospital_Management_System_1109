package org.bee.pages.clerk;

import org.bee.controllers.AppointmentController;

import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;
/**
 * Represents the main page for the Clerk.
 * This page displays a menu of options for the user to navigate to different sections of the application.
 * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
 */
public class ClerkMainPage extends UiBase {
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
        //lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
        //lv.addItem(new TextView(this.canvas, "Telemedicine Services", Color.GREEN));

        lv.addItem(new TextView(this.canvas, "1. View All Telemedicine cases- To view all telemed cases ", Color.GREEN));
        //lv.addItem(new TextView(this.canvas, "2. View Billing For Telemedicine Service - To view billing for teleconsultation ", Color.GREEN));

        //lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
        //lv.addItem(new TextView(this.canvas, "Outpatient Management Services", Color.GREEN));

        //lv.addItem(new TextView(this.canvas, "3. View All Outpatient Cases - To view all outpatient cases", Color.GREEN));
        //lv.addItem(new TextView(this.canvas, "4. Update Fields For Outpatient cases - To update outpatient cases", Color.GREEN));
        //lv.addItem(new TextView(this.canvas, "5. View Billing For Telemedicine Service - To view billing outpatient ", Color.GREEN));

        lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
        lv.addItem(new TextView(this.canvas, "Insurance Claim Management", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "6. New Claim - Submit new claim", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "7. Manage Claim - Manage existing claims", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "8. Claim Status - Check existing claim status", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "9. Change Claim Status - Update existing claim status", Color.GREEN));

        // Attach user input handlers for navigation
        lv.attachUserInput("View All Telemedicine cases", str -> viewAllAppointments());
        lv.attachUserInput("New Claim", str -> ToPage(new NewClaimPage()));
        lv.attachUserInput("Manage Claim", str -> ToPage(new ManageClaimPage()));
        lv.attachUserInput("Claim Status", str -> ToPage(new ClaimStatusPage()));
        lv.attachUserInput("Change Claim Status", str -> ToPage(new ChangeClaimStatusPage()));

        canvas.setRequireRedraw(true);
    }

    private AppointmentController appointmentController = AppointmentController.getInstance();

    private void viewAllAppointments() {
        appointmentController.viewAllAppointments();
    }
}