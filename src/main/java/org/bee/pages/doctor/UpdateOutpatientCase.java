package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;
import org.bee.utils.InfoUpdaters.ConsultationUpdater;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A UI class that extends UiBase to handle updating outpatient case information.
 * This class allows doctors to view and update their assigned outpatient cases.
 */
public class UpdateOutpatientCase extends UiBase {

    /**
     * Singleton instance of the HumanController to manage user-related data.
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Singleton instance of the ConsultationController to manage consultation-related data
    */
    private static final ConsultationController consultationController = ConsultationController.getInstance();

    /**
     * The patient associated with the current consultation.
     */
    private Patient patient;

    /**
     * The consultation updated.
     */
    private Consultation consultation;


    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Creates and returns the view for the update outpatient case page.
     *
     * @return A ListView object initialized with a green background color.
     */
    @Override
    public View OnCreateView() {
        return new ListView(this.canvas, Color.GREEN);
    }

    /**
     * Called when the view is created. Displays a list of outpatient cases for the logged-in doctor
     * or initiates the update process for a specific consultation if already selected.
     *
     * @param parentView The parent view, a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;

        SystemUser systemUser = humanController.getLoggedInUser();
        List<Consultation> allCases = consultationController.getAllOutpatientCases();

        if (systemUser instanceof Doctor doctor) {
            String staffId = (String) doctor.getStaffId();

            if (consultation != null) {
                displayOutpatientCase(consultation, lv);
                return;
            }

            lv.setTitleHeader("List of Outpatient Cases");

            List<Consultation> cases = allCases.stream()
                    .filter(c -> c.getDoctor() != null &&
                            c.getDoctor().getStaffId().equals(staffId))
                    .collect(Collectors.toList());

            if (cases.isEmpty()) {
                System.out.println("No outpatient cases found.");
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();
                return;
            }

            int index = 0;

            // Show list of patient
            for (Consultation consultation : cases) {
                lv.addItem(new TextView(this.canvas, index + ". " + consultation.getPatient().getName(), Color.GREEN));
                index += 1;
            }

            // When selecting "Select Patient Index"
            lv.attachUserInput("Select Patient Index ", str -> {
                int selectedIndex = InputHelper.getValidIndex("Select Patient index", cases);
                consultation = cases.get(selectedIndex);

                try {
                    updateOutpatientCase();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }
    }

    /**
     * Displays detailed information about a specific outpatient case .
     *
     * @param consultation The Consultation details about the case.
     * @param lv           consultation details will be displayed.
     */
    private void displayOutpatientCase(Consultation consultation, ListView lv) {

        lv.clear();
        lv.setTitleHeader("Patient Information");
        lv.addItem(new TextView(this.canvas, "Case ID: " + consultation.getConsultationId(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Appointment Date: " + consultation.getAppointmentDate(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Patient ID: " + consultation.getPatient().getPatientId(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Patient Name: " + consultation.getPatient().getName(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Type: " + consultation.getConsultationType(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Status: " + consultation.getStatus(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Diagnosis: " + consultation.getDiagnosis(), Color.GREEN));
        lv.addItem(new TextView(this.canvas, "Doctor Name: " + consultation.getDoctor().getName(), Color.GREEN));
        //lv.addItem(new TextView(this.canvas, "Next of Kin: " + patient.get(), Color.GREEN));

        // Request UI redraw
        canvas.setRequireRedraw(true);
    }

    /**
     * Allows the logged-in doctor to select a consultation from their assigned outpatient cases.
     *
     * @return The selected Consultation object, or null if no cases are found or selected.
     */
    private Consultation selectConsultation() {
        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Doctor doctor) {
            String staffId = (String) doctor.getStaffId();
            List<Consultation> consultations = consultationController.getAllOutpatientCases()
                    .stream()
                    .filter(c -> c.getDoctor() != null &&
                            c.getDoctor().getStaffId().equals(staffId))
                    .collect(Collectors.toList());

            if (consultations.isEmpty()) {
                System.out.println("No outpatient cases found..");
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
        return null;
    }

    /**
     * Initiates the process of updating an outpatient case.
     * Prompts the user to select specific fields to update and validates input before applying changes.
     */
    private void updateOutpatientCase() {

        if (consultation == null) {
            System.out.println("No consultation selected. Returning to main menu.");
            return;
        }

        boolean continueUpdating = true;
        while (continueUpdating) {
            try {
                System.out.println("\nCurrent Particulars:");
                consultation.displayConsultation();

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
                System.out.println("12. Return to Main Menu\n");

                int choice = InputHelper.getValidIndex("Enter your choice", 1, 12);

                String consultationId = consultation.getConsultationId();
                ConsultationUpdater updater = ConsultationUpdater.builder();
                boolean updateNeeded = true;

                switch (choice) {
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

    /**
     * Updates the diagnostic codes for a consultation with user input validation.
     * Prompts the user to enter diagnostic codes and validates them before updating.
     *
     * @param scanner The Scanner object used to read user input.
     * @param updater The ConsultationUpdater object to apply updates to the consultation.
     * @return The updated ConsultationUpdater object with diagnostic codes applied.
     */
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

    /**
     * Updates the procedure codes for a consultation with user input validation.
     * Prompts the user to enter procedure codes and validates them before updating.
     *
     * @param scanner The Scanner object used to read user input.
     * @param updater The ConsultationUpdater object used to apply updates to the consultation.
     * @return The updated ConsultationUpdater object with procedure codes applied.
     */
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

    /**
     * Updates the prescription details for a consultation with user input validation.
     * Prompts the user to enter medications and their quantities in a specific format and validates them before updating.
     *
     * @param scanner The Scanner object used to read user input.
     * @param updater The ConsultationUpdater object used to apply updates to the consultation.
     * @return The updated ConsultationUpdater object with prescription details applied.
     */
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

    /**
     * Updates the notes for a consultation with user input validation.
     * Prompts the user to enter new notes and validates them before updating.
     *
     * @param scanner The Scanner object used to read user input.
     * @param updater The ConsultationUpdater object used to apply updates to the consultation.
     * @return The updated ConsultationUpdater object with new notes applied.
     */
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

    /**
     * Updates the medical history for a consultation with user input validation.
     * Prompts the user to enter medical history and validates it before updating.
     *
     * @param scanner The Scanner object used to read user input.
     * @param updater The ConsultationUpdater object used to apply updates to the consultation.
     * @return The updated ConsultationUpdater object with new medical history applied.
     */
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

    /**
     * Updates the diagnosis for a consultation with user input validation.
     * Prompts the user to enter a diagnosis and validates it before updating.
     *
     * @param scanner The Scanner object used to read user input.
     * @param updater The ConsultationUpdater object used to apply updates to the consultation.
     * @return The updated ConsultationUpdater object with new diagnosis applied.
     */
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

    /**
     * Updates the visit reason for a consultation.
     *
     * This method prompts the user to enter a new visit reason and validates the input.
     * It continues to ask for input until a valid reason is provided or the user chooses to cancel.
     *
     * @param scanner Used for reading user input
     * @param updater The consultation updater object
     * @return The updated ConsultationUpdater
     */
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

    /**
     * Sets a new follow-up date for the consultation.
     *
     * Asks the user to input a date and time in the format "yyyy-MM-dd HH:mm".
     * The method validates the input and updates the consultation if the format is correct.
     *
     * @param scanner For reading user input
     * @param updater The consultation updater
     * @return Updated ConsultationUpdater with the new follow-up date
     */
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

    /**
     * Updates the instructions for a consultation.
     *
     * Prompts for new instructions and validates the input.
     * Continues to ask until valid instructions are provided or the user cancels.
     *
     * @param scanner Used to read user input
     * @param updater The consultation updater
     * @return Updated ConsultationUpdater with new instructions
     */
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

    /**
     * Updates the treatments for a consultation.
     *
     * Asks the user to enter treatment IDs, separated by commas.
     * Validates each ID and adds corresponding treatments to the consultation.
     *
     * @param scanner For reading user input
     * @param updater The consultation updater
     * @return Updated ConsultationUpdater with new treatments
     */
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

    /**
     * Updates the lab tests for a consultation.
     *
     * Prompts for lab test IDs (comma-separated) and validates each one.
     * Adds valid lab tests to the consultation update.
     *
     * @param scanner Used to read user input
     * @param updater The consultation updater
     * @return Updated ConsultationUpdater with new lab tests
     */
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

    /**
     * This method is called when the user presses the back button.
     * It simply calls the superclass implementation.
     */
    @Override
    public void OnBackPressed(){
        super.OnBackPressed();
    }
}