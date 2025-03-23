package org.bee.pages.patient;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.NokRelation;
import org.bee.hms.humans.Patient;

import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.ui.*;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;
import org.bee.utils.InfoUpdaters.PatientUpdater;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


/**
 * Represents the main page of the Telemedicine Integration System.
 * This page displays a menu of options for the user to navigate to different sections of the application.
 * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
 */
public class PatientMainPage extends UiBase {
    ListView listView;
    private static final HumanController humanController = HumanController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();

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
        listView = lv;
        return lv;
    }

    /**
     * Called after the view has been created and attached to the UI.
     * Populates the view with the main menu options, such as "View List of Patient", and "View Appointment".
     * Attaches user input handlers to each menu option to navigate to the corresponding pages.
     *
     * @param parentView The parent {@link View} to which the main page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView; // Cast the parent view to a list view
        HumanController controller = HumanController.getInstance();
        lv.setTitleHeader(controller.getUserGreeting());
        lv.addItem(new TextView(this.canvas, "1. View/Update Particulars - To update user particular ", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "2. Book Appointment - To schedule teleconsult appointment", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "3. View/Change Appointment - To view or reschedule an existing teleconsult appointment", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "4. View Billing - To view unpaid bills ", Color.GREEN));


        lv.attachUserInput("View/Update Particulars ", str -> viewUpdateParticularsPrompt());
        lv.attachUserInput("Book Appointment ", str -> bookAppointmentPrompt());
        lv.attachUserInput("View/Change Appointment ", str -> changeAppointmentPrompt());


//        lv.attachUserInput("View Billing", str -> {
//            BillingPage.appointments = appointmentController.getAppointments();
//            ToPage(new BillingPage());
//        });


        canvas.setRequireRedraw(true);
    }

    private void viewUpdateParticularsPrompt() {
        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Patient patient) {
            Terminal term = canvas.getTerminal();
            try {
                // Display current particulars
                System.out.println("\nCurrent Particulars:");
                patient.displayHuman(); // This will show all patient details

                System.out.println("\nWhat would you like to update?");
                System.out.println("1. Contact Information");
                System.out.println("2. Address");
                System.out.println("3. Height");
                System.out.println("4. Weight");
                System.out.println("5. Drug Allergies");
                System.out.println("6. Next of Kin Information");
                System.out.println("7. Next of Kin Relationship");
                System.out.println("8. Next of Kin Address");
                System.out.println("9. Return to Main Menu");

                int choice = InputHelper.getValidIndex("Enter your choice", 1, 9);

                String patientId = patient.getPatientId();
                PatientUpdater updater = PatientUpdater.builder();
                boolean updateNeeded = true;

                switch (choice) {
                    case 1:
                        updater = updateContactWithValidation(term, updater);
                        break;
                    case 2:
                        updater = updateAddressWithValidation(term, updater);
                        break;
                    case 3:
                        updater = updateHeightWithValidation(term, updater);
                        break;
                    case 4:
                        updater = updateWeightWithValidation(term, updater);
                        break;
                    case 5:
                        updater = updateDrugAllergiesWithValidation(term, updater);
                        break;
                    case 6:
                        updater = updateNokNameWithValidation(term, updater);
                        break;
                    case 7:
                        updater = updateNokRelationWithValidation(term, updater);
                        break;
                    case 8:
                        updater = updateNokAddressWithValidation(term, updater);
                        break;
                    case 9:
                        updateNeeded = false;
                        break;
                }

                if (updateNeeded && updater.isValid()) {
                    humanController.updatePatient(patientId, updater);
                    System.out.println("\nPatient information updated successfully!");
                } else if (choice != 9) {
                    System.out.println("\nNo changes were made.");
                }
            } finally {
                canvas.setRequireRedraw(true);
            }
        }
    }

    /**
     * Updates contact information with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateContactWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter new contact number:");
            String contact = terminal.getUserInput();
            updater = updater.contact(contact);

            if (updater.getValidationError("contact") != null) {
                System.out.println("Error: " + updater.getValidationError("contact"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Contact information updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    /**
     * Updates address with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateAddressWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter new address:");
            String address = terminal.getUserInput();
            updater = updater.address(address);

            if (updater.getValidationError("address") != null) {
                System.out.println("Error: " + updater.getValidationError("address"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Address updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    /**
     * Updates height with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateHeightWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter new height (in meters):");
            try {
                String heightInput = terminal.getUserInput();
                double height = Double.parseDouble(heightInput);

                updater = updater.height(height);

                if (updater.getValidationError("height") != null) {
                    System.out.println("Error: " + updater.getValidationError("height"));
                    System.out.println("Would you like to try again? (Y/N)");
                    String response = terminal.getUserInput().trim().toUpperCase();
                    if (!response.equals("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Height updated successfully!");
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format");
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            }
        }

        return updater;
    }

    /**
     * Updates weight with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateWeightWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter new weight (in kg):");
            try {
                String weightInput = terminal.getUserInput();
                double weight = Double.parseDouble(weightInput);

                updater = updater.weight(weight);

                if (updater.getValidationError("weight") != null) {
                    System.out.println("Error: " + updater.getValidationError("weight"));
                    System.out.println("Would you like to try again? (Y/N)");
                    String response = terminal.getUserInput().trim().toUpperCase();
                    if (!response.equals("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Weight updated successfully!");
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format");
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            }
        }

        return updater;
    }

    /**
     * Updates drug allergies with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateDrugAllergiesWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter drug allergies (comma-separated):");
            String allergiesInput = terminal.getUserInput();

            String[] allergiesArray = allergiesInput.split(",");
            List<String> drugAllergies = new ArrayList<>();
            for (String allergy : allergiesArray) {
                drugAllergies.add(allergy.trim());
            }

            updater = updater.drugAllergies(drugAllergies);

            if (updater.getValidationError("drugAllergies") != null) {
                System.out.println("Error: " + updater.getValidationError("drugAllergies"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Drug allergies updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    /**
     * Updates next of kin name with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateNokNameWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter next of kin name:");
            String nokName = terminal.getUserInput();

            updater = updater.nokName(nokName);

            if (updater.getValidationError("nokName") != null) {
                System.out.println("Error: " + updater.getValidationError("nokName"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Next of kin name updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    /**
     * Updates next of kin relationship with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateNokRelationWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter next of kin relationship (SPOUSE, PARENT, CHILD, SIBLING, OTHER):");
            String relationInput = terminal.getUserInput().toUpperCase();

            try {
                NokRelation nokRelation = NokRelation.valueOf(relationInput);
                updater = updater.nokRelation(nokRelation);

                if (updater.getValidationError("nokRelation") != null) {
                    System.out.println("Error: " + updater.getValidationError("nokRelation"));
                    System.out.println("Would you like to try again? (Y/N)");
                    String response = terminal.getUserInput().trim().toUpperCase();
                    if (!response.equals("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Next of kin relationship updated successfully!");
                    isValid = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Invalid relationship type");
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            }
        }

        return updater;
    }

    /**
     * Updates next of kin address with validation.
     *
     * @param terminal The terminal for user input
     * @param updater  The patient updater
     */
    private PatientUpdater updateNokAddressWithValidation(Terminal terminal, PatientUpdater updater) {
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter next of kin address:");
            String nokAddress = terminal.getUserInput();

            updater = updater.nokAddress(nokAddress);

            if (updater.getValidationError("nokAddress") != null) {
                System.out.println("Error: " + updater.getValidationError("nokAddress"));
                System.out.println("Would you like to try again? (Y/N)");
                String response = terminal.getUserInput().trim().toUpperCase();
                if (!response.equals("Y")) {
                    break;
                }
            } else {
                System.out.println("Next of kin address updated successfully!");
                isValid = true;
            }
        }

        return updater;
    }

    private void bookAppointmentPrompt() {
        appointmentController.getAllAppointments();
        Scanner scanner = new Scanner(System.in); // Create a new scanner object
        System.out.println("Enter reason to consult: ");
        String reason = scanner.nextLine(); //
        System.out.println("Do you have any Medical History?: ");
        String history = scanner.nextLine(); //

        System.out.println("Select your appointment date in this format (DD-MM-YYYY): ");
        LocalDate date = null; // safe to initialise as null, as it will never be null after the prompt.
        boolean validDate = false;
        String appointmentDate;
        while (!validDate) {
            appointmentDate = scanner.nextLine();
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                date = LocalDate.parse(appointmentDate, dateFormatter);

                System.out.println(appointmentDate);

                // Check if the date is in the future, don't want past appointments.
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("The appointment date must be in the future. Please enter a valid date (DD-MM-YYYY):");
                    continue;
                }
                validDate = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a date in the format (DD-MM-YYYY):");
            }
        }

        Dictionary<Integer, LocalDateTime> dateTimeDictionary = new Hashtable<>();
        // start the timeslot at 8:00am
        LocalDateTime startDate = date.atStartOfDay().withHour(8).withMinute(0).withSecond(0).withNano(0);

        // Define the time slots and their corresponding integer keys
        dateTimeDictionary.put(1, startDate);
        dateTimeDictionary.put(2, startDate.plusHours(1));
        dateTimeDictionary.put(3, startDate.plusHours(2));
        dateTimeDictionary.put(4, startDate.plusHours(3));
        dateTimeDictionary.put(5, startDate.plusHours(4));
        dateTimeDictionary.put(6, startDate.plusHours(5));
        dateTimeDictionary.put(7, startDate.plusHours(6));
        dateTimeDictionary.put(8, startDate.plusHours(7));
        dateTimeDictionary.put(9, startDate.plusHours(8));

        // Display available time slots (using StringBuilder)
        StringBuilder sb = new StringBuilder("Available timeslots: [");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 1; i <= dateTimeDictionary.size(); i++) {
            // display for the user in a nice fashion "1. 8:00 AM
            LocalDateTime time = dateTimeDictionary.get(i);
            String formattedTime = formatter.format(time);
            sb.append(i).append(". ").append(formattedTime);
            if (i < dateTimeDictionary.size()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        System.out.println(sb); // Convert StringBuilder to String and print

        System.out.print("Select your appointment timeslot (1-" + dateTimeDictionary.size() + "): ");

        int selectedSlot = InputHelper.getValidIndex("Select your appointment timeslot", 1, dateTimeDictionary.size());

        LocalDateTime selectedDateTime = dateTimeDictionary.get(selectedSlot);

        System.out.println("You have requested for an appointment on " + formatter.format(selectedDateTime) + " at index " + selectedSlot);

        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Patient) {
            Patient patient = (Patient) systemUser;
            Appointment appointment = new Appointment(patient, reason, selectedDateTime, AppointmentStatus.PENDING);


            // check if consent is already given before asking.
            if (!patient.getPatientConsent()) {
                String consentString = "Telemedicine Consent Form\n\n" +
                        "Purpose: This telemedicine session is for a general checkup.\n\n" +
                        "Procedure:  This session will use live video and audio to connect you with the provider.  You may be asked to share information about your health, and the provider may provide advice or recommendations.\n\n" +
                        "Recording: This session will not be recorded. If this session is recorded for any purpose, it will be made known to you and separate verbal consent will be required during the call.\n\n" +
                        "Confidentiality: Your personal health information is protected by Singapore privacy laws.  We will take reasonable steps to protect your privacy.\n\n" +
                        "Risks and Limitations: Telemedicine is not a substitute for in-person care.  Some conditions cannot be diagnosed or treated remotely.  Technical issues (e.g., poor internet connection) may affect the quality of the session.  In case of an emergency, please call 911 or go to the nearest emergency room.\n\n" +
                        "Alternatives: You have the option to schedule an in-person appointment instead of using telemedicine.\n\n" +
                        "Rights: You have the right to refuse or withdraw consent at any time. You have the right to ask questions about this session and your health information.\n\n" +
                        "By Agreeing, you confirm that you have read, understood, and agree to the terms of this telemedicine consent (Y/N). \n\n" +
                        "Technical requirements: A laptop or mobile device (such as phone or tablet) with Zoom Meetings app installed";
                System.out.println(consentString);
                boolean validInput = false;
                while (!validInput) {
                    System.out.println("Do you wish to proceed with this appointment? (Y/N)");
                    String s = scanner.nextLine();
                    if (s.equalsIgnoreCase("Y")) {
                        validInput = true;
                    } else if (s.equalsIgnoreCase("N")) {
                        System.out.println("Consent not recieved, terminating session. Your information will not be saved.");
                        canvas.setRequireRedraw(true);
                        return;
                    }
                }

                // set the consent.
                appointment.getPatient().setPatientConsent(true);
            }

            appointment.setHistory(history);
            appointmentController.addAppointment(appointment);
            canvas.setRequireRedraw(true);
        }
    }

    private void changeAppointmentPrompt() {
        // Get all appointments for the logged-in patient
        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Patient patient) {
            List<Appointment> appointments = appointmentController.getAppointmentsForPatient(patient);

            if (appointments.isEmpty()) {
                System.out.println("No appointments found.");
                return;
            }

            boolean viewingAppointments = true;
            while (viewingAppointments) {
                // Display appointments
                System.out.println("Your Appointments:");
                for (int i = 0; i < appointments.size(); i++) {
                    Appointment appointment = appointments.get(i);
                    System.out.println((i + 1) + ". " + appointment.getAppointmentTime() + " - " + appointment.getReason());
                }

                System.out.println("Select appointment to view details or change:");
                int choice = InputHelper.getValidIndex("Enter your choice", 1, appointments.size());

                Appointment selectedAppointment = appointments.get(choice - 1);

                System.out.println("Appointment Details:");
                System.out.println("Time: " + selectedAppointment.getAppointmentTime());
                System.out.println("Reason: " + selectedAppointment.getReason());
                System.out.println("Status: " + selectedAppointment.getAppointmentStatus());

                System.out.println("Options:");
                System.out.println("1. Change Appointment");
                System.out.println("2. Cancel Appointment");
                System.out.println("3. Back");
                System.out.println("4. Return to Main Page");

                int optionChoice = InputHelper.getValidIndex("Enter your option", 1, 4);

                if (optionChoice == 1) {
                    // Change appointment logic
                    Scanner scanner = new Scanner(System.in);
                    LocalDate newDate = null; // Initialize newDate as null
                    boolean validDate = false;
                    while (!validDate) {
                        System.out.println("Enter new appointment date (DD-MM-YYYY):");
                        String newDateStr = scanner.nextLine();
                        try {
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            newDate = LocalDate.parse(newDateStr, dateFormatter);
                            if (newDate.isBefore(LocalDate.now())) {
                                System.out.println("The appointment date must be in the future.");
                                continue;
                            }
                            validDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please use DD-MM-YYYY.");
                        }
                    }

                    Dictionary<Integer, LocalDateTime> timeSlots = new Hashtable<>();
                    LocalDateTime startDate = newDate.atStartOfDay().withHour(8).withMinute(0).withSecond(0).withNano(0);
                    for (int i = 1; i <= 9; i++) {
                        timeSlots.put(i, startDate.plusHours(i - 1));
                    }

                    System.out.println("Available timeslots:");
                    StringBuilder sb = new StringBuilder("[");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    for (int i = 1; i <= timeSlots.size(); i++) {
                        sb.append(i).append(". ").append(formatter.format(timeSlots.get(i)));
                        if (i < timeSlots.size()) {
                            sb.append(", ");
                        }
                    }
                    sb.append("]");
                    System.out.println(sb);

                    int selectedSlot = InputHelper.getValidIndex("Select your new appointment timeslot", 1, timeSlots.size());
                    LocalDateTime newTime = timeSlots.get(selectedSlot);

                    // Update the appointment time
                    selectedAppointment.setAppointmentTime(newTime);

                    // Save the changes
                    appointmentController.updateAppointment(selectedAppointment, selectedAppointment);

                    System.out.println("Appointment time updated successfully.");
                } else if (optionChoice == 2) {
                    // Cancel appointment logic
                    System.out.println("Are you sure you want to cancel this appointment? (Y/N)");
                    Scanner scanner = new Scanner(System.in);
                    String confirm = scanner.nextLine().trim().toUpperCase();
                    if (confirm.equals("Y")) {
                        // Update appointment status to CANCELED
                        selectedAppointment.setAppointmentStatus(AppointmentStatus.CANCELED);
                        appointmentController.updateAppointment(selectedAppointment, selectedAppointment);
                        appointmentController.removeAppointment(selectedAppointment);

                        // Update the appointments list in PatientMainPage
                        appointments.remove(selectedAppointment);

                        System.out.println("Appointment canceled successfully.");
                    } else {
                        System.out.println("Cancellation canceled.");
                    }
                } else if (optionChoice == 4) {
                    // Return to main page
                    viewingAppointments = false;
                    canvas.setRequireRedraw(true);
                }
            }
        }
    }
}