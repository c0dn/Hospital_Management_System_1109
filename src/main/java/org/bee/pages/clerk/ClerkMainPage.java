    package org.bee.pages.clerk;

    import org.bee.controllers.AppointmentController;
    import org.bee.controllers.ConsultationController;
    import org.bee.controllers.HumanController;
    import org.bee.hms.medical.*;
    import org.bee.ui.*;
    import org.bee.ui.views.ListView;
    import org.bee.ui.views.TextView;
    import org.bee.utils.InfoUpdaters.ConsultationUpdater;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
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
            HumanController controller = HumanController.getInstance();
            lv.setTitleHeader(controller.getUserGreeting());
            // Menu options
            //lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
            //lv.addItem(new TextView(this.canvas, "Telemedicine Services", Color.GREEN, TextStyle.BOLD));

            lv.addItem(new TextView(this.canvas, "1. View All Telemedicine cases- To view all telemed cases ", Color.GREEN));
            //lv.addItem(new TextView(this.canvas, "2. View Billing For Telemedicine Service - To view billing for teleconsultation ", Color.GREEN));

            lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
            lv.addItem(new TextView(this.canvas, "Outpatient Management Services", Color.GREEN, TextStyle.BOLD));

            lv.addItem(new TextView(this.canvas, "3. View All Outpatient Cases - To view all outpatient cases", Color.GREEN));
            lv.addItem(new TextView(this.canvas, "4. Update Fields For Outpatient cases - To update outpatient cases", Color.GREEN));
            //lv.addItem(new TextView(this.canvas, "5. View Billing For Telemedicine Service - To view billing outpatient ", Color.GREEN));

            lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
            lv.addItem(new TextView(this.canvas, "Insurance Claim Management", Color.GREEN, TextStyle.BOLD));
            lv.addItem(new TextView(this.canvas, "6. New Claim - Submit new claim", Color.GREEN));
            lv.addItem(new TextView(this.canvas, "7. Manage Claim - Manage existing claims", Color.GREEN));
            lv.addItem(new TextView(this.canvas, "8. Claim Status - Check existing claim status", Color.GREEN));
            lv.addItem(new TextView(this.canvas, "9. Change Claim Status - Update existing claim status", Color.GREEN));

            // Attach user input handlers for navigation
            lv.attachUserInput("View All Telemedicine cases ", str -> viewAllAppointments());
            lv.attachUserInput("View All Outpatient Cases ", str -> viewAllOutpatientCases());
            lv.attachUserInput("Update Fields For Outpatient Cases ", str -> updateOutpatientCase());
            lv.attachUserInput("New Claim ", str -> ToPage(new NewClaimPage()));
            lv.attachUserInput("Manage Claim ", str -> ToPage(new ManageClaimPage()));
            lv.attachUserInput("Claim Status ", str -> ToPage(new ClaimStatusPage()));
            lv.attachUserInput("Change Claim Status ", str -> ToPage(new ChangeClaimStatusPage()));

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

            int choice = InputHelper.getValidIndex("Enter your choice", 1, consultations.size());
            return consultations.get(choice - 1);
        }

        private void updateOutpatientCase() {
            consultation = selectConsultation();

            if (consultation == null) {
                System.out.println("No consultation selected. Returning to main menu.");
                return;
            }

            boolean continueUpdating = true;
            while (continueUpdating) {
                try {
                    System.out.println("\nCurrent Particulars:");
                    consultation.displayConsultation();

                    displayUpdateMenu();
                    int choice = InputHelper.getValidIndex("Enter your choice", 1, 2);

                    String consultationId = consultation.getConsultationId();
                    ConsultationUpdater updater = ConsultationUpdater.builder();
                    boolean updateNeeded = true;

                    switch (choice) {
                        case 1:
                            updater = updateFollowUpDateWithValidation(scanner, updater);
                            break;
                        case 2:
                            updateNeeded = false;
                            continueUpdating = false;
                            break;
                    }

                    if (updateNeeded && updater.isValid()) {
                        consultationController.updateConsultation(consultationId, updater);
                        System.out.println("\nConsultation information updated successfully!");
                    } else if (choice != 12) {
                        System.out.println("\nNo changes were made.");
                    }

                    if (continueUpdating) {
                        System.out.println("Do you want to make another update? (Y/N)");
                        continueUpdating = scanner.nextLine().trim().equalsIgnoreCase("Y");
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                    System.out.println("Returning to main menu.");
                    continueUpdating = false;
                }
            }
            canvas.setRequireRedraw(true);
        }

        private void displayUpdateMenu() {
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Follow Up Date");
            System.out.println("2. Return to Main Menu");
        }

        private ConsultationUpdater updateDiagnosticCodeWithValidation(Scanner scanner, ConsultationUpdater updater) {
            boolean isValid = false;

            while (!isValid) {
                System.out.println("Enter diagnostic codes (comma-separated):");
                String diagnosticCodeInput = scanner.nextLine();

                String[] diagnosticCodeArray = diagnosticCodeInput.split(",");
                List<DiagnosticCode> diagnosticCodes = new ArrayList<>();
                for (String diagnosticCode : diagnosticCodeArray) {
                    diagnosticCodes.add(DiagnosticCode.createFromCode(diagnosticCode.trim()));
                }

                updater = updater.diagnosticCodes(diagnosticCodes);

                if (updater.getValidationError("diagnosticCodes") != null) {
                    System.out.println("Error: " + updater.getValidationError("diagnosticCodes"));
                    System.out.println("Would you like to try again? (Y/N)");
                    String response = scanner.nextLine().trim().toUpperCase();
                    if (!response.equals("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Diagnostic Codes updated successfully!");
                    isValid = true;
                }
            }

            return updater;
        }

        private ConsultationUpdater updateFollowUpDateWithValidation(Scanner scanner, ConsultationUpdater updater) {
            boolean isValid = false;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Define the desired format for the input

            while (!isValid) {
                System.out.println("Enter follow-up date and time (format: yyyy-MM-dd HH:mm):");
                String followUpDateInput = scanner.nextLine().trim();

                try {
                    // Parse the input string into a LocalDateTime object
                    LocalDateTime followUpDate = LocalDateTime.parse(followUpDateInput, formatter);

                    // Update the updater with the new follow-up date
                    updater = updater.followUpDate(followUpDate);

                    // Check for validation errors
                    if (updater.getValidationError("followUpDate") != null) {
                        System.out.println("Error: " + updater.getValidationError("followUpDate"));
                        System.out.println("Would you like to try again? (Y/N)");
                        String response = scanner.nextLine().trim().toUpperCase();

                        if (!response.equals("Y")) {
                            break; // Exit if the user doesn't want to try again
                        }
                    } else {
                        System.out.println("Follow-up date updated successfully!");
                        isValid = true; // Exit the loop if update is successful
                    }
                } catch (Exception e) {
                    // Handle invalid date format
                    System.out.println("Invalid date format. Please enter a valid date and time in the format yyyy-MM-dd HH:mm.");
                    System.out.println("Would you like to try again? (Y/N)");
                    String response = scanner.nextLine().trim().toUpperCase();

                    if (!response.equals("Y")) {
                        break; // Exit if the user doesn't want to try again
                    }
                }
            }

            return updater; // Return the updated ConsultationUpdater
        }
    }