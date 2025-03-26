    package org.bee.pages.clerk;

    import org.bee.controllers.AppointmentController;
    import org.bee.controllers.ConsultationController;
    import org.bee.controllers.HumanController;
    import org.bee.hms.medical.*;
    import org.bee.pages.GenericUpdatePage;
    import org.bee.ui.*;
    import org.bee.ui.views.ListView;
    import org.bee.ui.views.MenuView;
    import org.bee.utils.formAdapters.ConsultationFormAdapter;

    import java.util.*;

    /**
     * Represents the main page for the Clerk.
     * This page displays a menu of options for the user to navigate to different sections of the application.
     * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
     */
    public class ClerkMainPage extends UiBase {

        private static final AppointmentController appointmentController = AppointmentController.getInstance();
        private static final ConsultationController consultationController = ConsultationController.getInstance();
        private Consultation consultation;
        private static final Scanner scanner = new Scanner(System.in);


        /**
         * Called when the main page's view is created.
         * Creates a {@link ListView} to hold the main menu options.
         * Sets the title header to "Main".
         *
         * @return A new {@link ListView} instance representing the main page's view.
         */
        @Override
        public View createView() {
            return new MenuView(this.canvas, "Main", Color.GREEN, true, false);
        }

        /**
         * This method is called after the view has been created and attached to the UI.
         * It populates the `ListView` with the main menu options and attaches user input
         * handlers to navigate to the corresponding pages or perform actions.
         *
         * The main menu includes options for viewing and managing telemedicine cases,
         * outpatient services, and insurance claims.
         *
         * Each menu option is associated with a specific user action, such as viewing
         * existing cases, submitting a new claim, or changing the status of a claim.
         *
         * @param parentView The parent {@link View} that represents the main screen
         *                   of the UI. This should be a {@link ListView} that will
         *                   contain the menu items and allow the user to interact with them.
         */
        @Override
        public void OnViewCreated(View parentView) {
            MenuView menuView = (MenuView) parentView;
            HumanController controller = HumanController.getInstance();
            menuView.setTitleHeader(controller.getUserGreeting());

            MenuView.MenuSection telemedSection = menuView.addSection("Telemedicine Services");
            telemedSection.addOption(1, "View All Telemedicine cases - To view all telemed cases");

            MenuView.MenuSection outpatientSection = menuView.addSection("Outpatient Management Services");
            outpatientSection.addOption(2, "View All Outpatient Cases - To view all outpatient cases");
            outpatientSection.addOption(3, "Update Fields For Outpatient cases - To update outpatient cases");

            MenuView.MenuSection insuranceSection = menuView.addSection("Insurance Claim Management");
            insuranceSection.addOption(4, "New Claim - Submit new claim");
            insuranceSection.addOption(5, "Manage Claim - Manage existing claims");
            insuranceSection.addOption(6, "Claim Status - Check existing claim status");
            insuranceSection.addOption(7, "Change Claim Status - Update existing claim status");

            menuView.attachMenuOptionInput(1, "View All Telemedicine cases", str -> viewAllAppointments());
            menuView.attachMenuOptionInput(2, "View All Outpatient Cases", str -> viewAllOutpatientCases());
            menuView.attachMenuOptionInput(3, "Update Fields For Outpatient Cases", str -> updateOutpatientCase());
//            menuView.attachMenuOptionInput(4, "New Claim", str -> ToPage(new NewClaimPage()));
//            menuView.attachMenuOptionInput(5, "Manage Claim", str -> ToPage(new ManageClaimPage()));
//            menuView.attachMenuOptionInput(6, "Claim Status", str -> ToPage(new ClaimStatusPage()));
//            menuView.attachMenuOptionInput(7, "Change Claim Status", str -> ToPage(new ChangeClaimStatusPage()));

            canvas.setRequireRedraw(true);
        }


        private void viewAllAppointments() {
            appointmentController.viewAllAppointments();
        }

        private void viewAllOutpatientCases() {
            consultationController.viewAllOutpatientCases();
        }

        private Consultation selectConsultation() {
            List<Consultation> consultations = consultationController.getAllOutpatientCases();
            if (consultations.isEmpty()) {
                System.out.println("No outpatient cases found.");
                return null;
            }

            System.out.println("Select a consultation to update:");
            for (int i = 0; i < consultations.size(); i++) {
                Consultation c = consultations.get(i);
                System.out.printf("%d. %s - %s\n", i + 1, c.getConsultationId(), c.getPatient().getName());
            }

            int choice = InputHelper.getValidIndex(canvas.getTerminal(), "Enter your choice", 1, consultations.size());
            return consultations.get(choice - 1);
        }

        private void updateOutpatientCase() {
            consultation = selectConsultation();

            if (consultation == null) {
                System.out.println("No consultation selected. Returning to main menu.");
                return;
            }

            ConsultationFormAdapter adapter = new ConsultationFormAdapter();

            GenericUpdatePage<Consultation> updatePage = new GenericUpdatePage<>(
                    consultation,
                    adapter,
                    () -> System.out.println("Consultation information updated successfully!")
            );

            ToPage(updatePage);
        }

    }