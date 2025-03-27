package org.bee.pages;

import java.util.Optional;
import java.util.Scanner;

import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Clerk;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.pages.clerk.ClerkMainPage;
import org.bee.pages.doctor.DoctorMainPage;
import org.bee.pages.patient.PatientMainPage;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.MenuView;

/**
 * Represents the login page of the Hospital Management System.
 * This class extends {@link UiBase} and provides the UI elements and logic for user authentication.
 */
public class LoginPage extends UiBase {

    /**
     * The {@link HumanController} instance used for user authentication.
     * This field is static, meaning there's only one HumanController shared across all LoginPage instances.
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Called when the login page's view is created.
     * Creates a {@link ListView} to hold the login page's UI elements.
     * 
     * @return A new {@link ListView} instance representing the login page's view.
     */
    @Override
    public View createView() {
        return new MenuView(this.canvas, "Welcome to Hospital Management System", Color.GREEN, false, true);
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
        MenuView.MenuSection authSection = menuView.addSection("Authentication");
        authSection.addOption(1, "Login to the system");
        menuView.attachMenuOptionInput(1, "Login", x -> performLogin());
        canvas.setRequireRedraw(true);
    }


    private void performLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your StaffID or NRIC (patients): ");
        String username = scanner.nextLine();

        Optional<SystemUser> userOpt = humanController.findUserByUsername(username);

        if (userOpt.isPresent()) {
            SystemUser user = userOpt.get();

            canvas.setSystemMessage("Login successful", SystemMessageStatus.SUCCESS);

            humanController.authenticate(user);

            switch (user) {
                case Doctor ignored -> ToPage(new DoctorMainPage());
                case Nurse ignored -> ToPage(new TestPage());
                case Patient ignored -> ToPage(new PatientMainPage());
                case Clerk ignored -> ToPage(new ClerkMainPage());
                default -> {
                }
            }
        } else {
            canvas.setSystemMessage("User not found!", SystemMessageStatus.ERROR);
        }
        canvas.setRequireRedraw(true);
    }
}
