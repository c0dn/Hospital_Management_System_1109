    package org.bee.pages.clerk;

    import org.bee.controllers.HumanController;
    import org.bee.pages.clerk.billing.ViewAllBillsPage;
    import org.bee.pages.clerk.insurance.ViewAllClaimsPage;
    import org.bee.ui.*;
    import org.bee.ui.views.*;

    /**
     * Represents the main page for the Clerk.
     * This page displays a menu of options for the user to navigate to different sections of the application.
     * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
     */
    public class ClerkMainPage extends UiBase {

        private static final HumanController humanController = HumanController.getInstance();

        /**
         * Called when the main page's view is created.
         * Creates a MenuView with styled sections for the clerk dashboard.
         *
         * @return A MenuView instance representing the main page.
         */
        @Override
        public View createView() {
            return new MenuView(this.canvas, humanController.getUserGreeting(), Color.CYAN, true, false);
        }

        /**
         * Called after the view has been created and attached to the UI.
         * Populates the view with structured menu options for different clerk functions.
         *
         * @param parentView The parent MenuView that represents the main dashboard
         */
        @Override
        public void OnViewCreated(View parentView) {
            MenuView menuView = (MenuView) parentView;

            // Telemedicine section
            MenuView.MenuSection telemedSection = menuView.addSection("Telemedicine Services");
            telemedSection.addOption(1, "View All Telemedicine Cases");
            menuView.attachMenuOptionInput(1, "View Telemedicine Cases", str -> ToPage(new TelemedicineAppointmentPage()));

            // Outpatient section
            MenuView.MenuSection outpatientSection = menuView.addSection("Outpatient Management");
            outpatientSection.addOption(2, "View All/Update Outpatient Cases");
            menuView.attachMenuOptionInput(2, "View All/Update Outpatient Cases", str -> ToPage(new OutpatientCasesPage()));


            // billing section
            MenuView.MenuSection billingSection = menuView.addSection("Billing and Invoicing");
            billingSection.addOption(3, "View All/Update bills");
            menuView.attachMenuOptionInput(3, "View All/Update bills", str -> ToPage(new ViewAllBillsPage()));

            // Insurance section
            MenuView.MenuSection insuranceSection = menuView.addSection("Insurance Claims");
            insuranceSection.addOption(4, "View All/Update Claims");
            menuView.attachMenuOptionInput(4, "View All/Update Claims", str -> ToPage(new ViewAllClaimsPage()));

            menuView.setNumericOptionMaxRange(4);

            canvas.setRequireRedraw(true);
        }

    }