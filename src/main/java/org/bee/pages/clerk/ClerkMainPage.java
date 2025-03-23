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
        lv.addItem(new TextView(this.canvas, "Outpatient Management Services", Color.GREEN));

        lv.addItem(new TextView(this.canvas, "3. View All Outpatient Cases - To view all outpatient cases", Color.GREEN, TextStyle.BOLD));
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

    private void updateOutpatientCase() {

        Scanner scanner = null;
        try {
            System.out.println("\nCurrent Particulars:");
            consultation.displayConsultation();

            scanner = new Scanner(System.in);
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Diagnostic Code");
            System.out.println("2. Procedure Code");
            System.out.println("3. Prescription");
            System.out.println("4. Notes");
            System.out.println("5. Medical History");
            System.out.println("6. Diagnosis");
            System.out.println("7. Visit Reason");
            System.out.println("8. Follow Up Date");
            System.out.println("9. Instructions");
            System.out.println("10. Treatment");
            System.out.println("11. Lab Test");
            System.out.println("12. Return to Main Menu");

            int choice = InputHelper.getValidIndex("Enter your choice", 1, 12);

            String consultationId = consultation.getConsultationId();
            ConsultationUpdater updater = ConsultationUpdater.builder();
            boolean updateNeeded = true;

            switch(choice) {
                case 1:
                    updater = updateDiagnosticCodeWithValidation(scanner, updater);
                    break;
                case 2:
                    updater = updateProcedureCodeWithValidation(scanner, updater);
                    break;
                case 3:
                    updater = updatePrescriptionWithValidation(scanner, updater);
                    break;
                case 4:
                    updater = updateNotesWithValidation(scanner, updater);
                    break;
                case 5:
                    updater = updateMedicalHistoryWithValidation(scanner, updater);
                    break;
                case 6:
                    updater = updateDiagnosisWithValidation(scanner, updater);
                    break;
                case 7:
                    updater = updateVisitReasonWithValidation(scanner, updater);
                    break;
                case 8:
                    updater = updateFollowUpDateWithValidation(scanner, updater);
                    break;
                case 9:
                    updater = updateInstructionsWithValidation(scanner, updater);
                    break;
                case 10:
                    updater = updateTreatmentWithValidation(scanner, updater);
                    break;
                case 11:
                    updater = updateLabTestWithValidation(scanner, updater);
                    break;
                case 12:
                    updateNeeded = false;
                    break;
            }

            if (updateNeeded && updater.isValid()) {
                consultationController.updateConsultation(consultationId, updater);
                System.out.println("\nConsultation information updated successfully!");
            } else if (choice != 12) {
                System.out.println("\nNo changes were made.");
            }

        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        canvas.setRequireRedraw(true);

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

    private ConsultationUpdater updateProcedureCodeWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter procedure codes (comma-separated):");
            String procedureCodeInput = scanner.nextLine();

            String[] procedureCodeArray = procedureCodeInput.split(",");
            List<ProcedureCode> procedureCodes = new ArrayList<>();
            for (String procedureCode : procedureCodeArray) {
                procedureCodes.add(ProcedureCode.createFromCode(procedureCode.trim()));
            }

            updater = updater.procedureCodes(procedureCodes);

            if (updater.getValidationError("procedureCodes") != null) {
                System.out.println("Error: " + updater.getValidationError("procedureCodes"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Procedure Codes updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    private ConsultationUpdater updatePrescriptionWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter medications and their quantities (format: drugCode1-quantity1, drugCode2-quantity2):");
            String prescriptionInput = scanner.nextLine();

            String[] prescriptionArray = prescriptionInput.split(",");
            Map<Medication, Integer> prescriptionMap = new HashMap<>();

            try {
                for (String entry : prescriptionArray) {
                    String[] parts = entry.trim().split("-");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid format. Use drugCode-quantity (e.g., MED001-2).");
                    }

                    String drugCode = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());

                    if (quantity <= 0) {
                        throw new IllegalArgumentException("Quantity must be greater than zero.");
                    }

                    Medication medication = Medication.createFromCode(drugCode);
                    prescriptionMap.put(medication, quantity);
                }

                updater = updater.prescriptions(prescriptionMap);

                if (updater.getValidationError("prescription") != null) {
                    System.out.println("Error: " + updater.getValidationError("prescription"));
                    System.out.println("Would you like to try again? (Y/N)");
                    String response = scanner.nextLine().trim().toUpperCase();
                    if (!response.equals("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Prescription updated successfully!");
                    isValid = true;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            }
        }

        return updater;
    }

    private ConsultationUpdater updateNotesWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter new notes:");
            String notes = scanner.nextLine();
            updater = updater.notes(notes);

            if (updater.getValidationError("notes") != null) {
                System.out.println("Error:" + updater.getValidationError("notes"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Notes updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    private ConsultationUpdater updateMedicalHistoryWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter medical history:");
            String medicalHistory = scanner.nextLine();
            updater = updater.medicalHistory(medicalHistory);

            if (updater.getValidationError("medicalHistory") != null) {
                System.out.println("Error:" + updater.getValidationError("medicalHistory"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Medical History updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    private ConsultationUpdater updateDiagnosisWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter diagnosis:");
            String diagnosis = scanner.nextLine();
            updater = updater.diagnosis(diagnosis);

            if (updater.getValidationError("diagnosis") != null) {
                System.out.println("Error:" + updater.getValidationError("diagnosis"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Diagnosis updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    private ConsultationUpdater updateVisitReasonWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter visit reason:");
            String visitReason = scanner.nextLine();
            updater = updater.visitReason(visitReason);

            if (updater.getValidationError("visitReason") != null) {
                System.out.println("Error:" + updater.getValidationError("visitReason"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Visit Reason updated successfully!");
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

    private ConsultationUpdater updateInstructionsWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter visit reason:");
            String instructions = scanner.nextLine();
            updater = updater.instructions(instructions);

            if (updater.getValidationError("instructions") != null) {
                System.out.println("Error:" + updater.getValidationError("instructions"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Instructions updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    private ConsultationUpdater updateTreatmentWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter treatment IDs (comma-separated):");
            String treatmentIDInput = scanner.nextLine();

            // Split input by commas and trim each treatment ID
            String[] treatmentIDArray = treatmentIDInput.split(",");
            ArrayList<Treatment> treatments = new ArrayList<>(); // Initialize an ArrayList for treatments

            // Add each Treatment by looking up the Treatment by ID
            for (String treatmentID : treatmentIDArray) {
                Treatment treatment = Treatment.searchTreatmentByID(Integer.parseInt(treatmentID.trim()));
                if (treatment != null) {
                    treatments.add(treatment);
                } else {
                    System.out.println("Treatment with ID " + treatmentID.trim() + " not found.");
                }
            }

            // Update the updater with the new list of treatments
            updater = updater.treatments(treatments);

            // Check for validation errors
            if (updater.getValidationError("treatments") != null) {
                System.out.println("Error: " + updater.getValidationError("treatments"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();

                if (!response.equals("Y")) {
                    break; // Exit if the user doesn't want to try again
                }
            } else {
                System.out.println("Treatments updated successfully!");
                isValid = true; // Exit the loop if update is successful
            }
        }

        return updater; // Return the updated ConsultationUpdater
    }

    private ConsultationUpdater updateLabTestWithValidation(Scanner scanner, ConsultationUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter lab test ID (comma-separated):");
            String labTestIDInput = scanner.nextLine();

            // Split input by commas and trim each treatment ID
            String[] labTestIDArray = labTestIDInput.split(",");
            ArrayList<LabTest> labTests = new ArrayList<>(); // Initialize an ArrayList for treatments

            // Add each Treatment by looking up the Treatment by ID
            for (String labTestID : labTestIDArray) {
                LabTest labTest = LabTest.searchLabTestByID(Integer.parseInt(labTestID.trim()));
                if (labTest != null) {
                    labTests.add(labTest);
                } else {
                    System.out.println("Lab test with ID " + labTestID.trim() + " not found.");
                }
            }

            // Update the updater with the new list of treatments
            updater = updater.labTests(labTests);

            // Check for validation errors
            if (updater.getValidationError("labTests") != null) {
                System.out.println("Error: " + updater.getValidationError("labTests"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = scanner.nextLine().trim().toUpperCase();

                if (!response.equals("Y")) {
                    break; // Exit if the user doesn't want to try again
                }
            } else {
                System.out.println("Lab Tests updated successfully!");
                isValid = true; // Exit the loop if update is successful
            }
        }

        return updater; // Return the updated ConsultationUpdater
    }
}