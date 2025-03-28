package org.bee.pages.doctor;

import org.bee.controllers.HumanController;
import org.bee.ui.*;
import org.bee.ui.views.ListView;
import org.bee.ui.views.MenuView;

/**
 * Represents the main page for the doctors.
 * This page displays a menu of options for the doctor to navigate to different sections of the application.
 * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
 */
public class DoctorMainPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Called when the main page's view is created.
     * Creates a {@link ListView} to hold the main menu options.
     * Sets the title header to "Main".
     *
     * @return A new {@link ListView} instance representing the main page's view.
     */
    @Override
    public View createView() {
        return new MenuView(this.canvas, humanController.getUserGreeting(), Color.GREEN, true, false);
    }

    /**
     * Called after the view has been created and attached to the UI.
     * Populates the view with UI elements such as the title header and login prompt with user input handling for the login process.
     *
     * @param parentView The parent {@link View} to which the login page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        MenuView menuView = (MenuView) parentView;

        MenuView.MenuSection telemedSection = menuView.addSection("Telemedicine Services");
        telemedSection.addOption(1, "View List of Patients - To view patient information");
        telemedSection.addOption(2, "View Appointment - To view new / scheduled appointments for teleconsultation");
        menuView.attachMenuOptionInput(1, "View List of Patients", str -> ToPage(new PatientInfoPage()));
        menuView.attachMenuOptionInput(2, "View Appointment", str -> ToPage(new ViewAppointmentPage()));

        MenuView.MenuSection outpatientSection = menuView.addSection("Outpatient Management Services");
        outpatientSection.addOption(3, "View Outpatient Cases");
        menuView.attachMenuOptionInput(3, "View Outpatient Cases", str -> ToPage(new ConsultationInfoPage()));

        canvas.setRequireRedraw(true);
    }
}