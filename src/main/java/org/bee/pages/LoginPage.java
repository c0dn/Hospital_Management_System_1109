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
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

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
    public View OnCreateView() {
        return new ListView(this.canvas, Color.GREEN);
    }

    /**
     * Called after the view has been created and attached to the UI.
     * Populates the view with UI elements such as the title header and login prompt with user input handling for the login process.
     * 
     * @param parentView The parent {@link View} to which the login page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;
        lv.setTitleHeader("Welcome to Hospital Management System");
        lv.addItem(new TextView(this.canvas, "To use our system, please kindly login by pressing 1", Color.GREEN));

        lv.attachUserInput("Login ", x -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your StaffID or NRIC (patients): ");
            String username = scanner.nextLine();

            // Authenticate user
            Optional<SystemUser> userOpt = humanController.findUserByUsername(username);
            
            if (userOpt.isPresent()) {
                SystemUser user = userOpt.get();

                System.out.println("Login successful!");
                humanController.authenticate(user);

                switch (user) {
                    case Doctor doctor -> ToPage(new DoctorMainPage());
                    case Nurse nurse -> System.out.println("Welcome, Nurse!");

                    // ToPage(new NurseMainPage());
                    case Patient patient -> ToPage(new PatientMainPage());

                    case Clerk clerk -> ToPage(new ClerkMainPage());


                    // ToPage(new PatientMainPage());
                    default -> {
                    }
                }
            } else {
                System.out.println("User not found!");
            }
            
            canvas.setRequireRedraw(true);
        });
        
        canvas.setRequireRedraw(true);
    }
}
