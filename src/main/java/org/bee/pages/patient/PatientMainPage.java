package org.bee.pages.patient;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Patient;

import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.pages.GenericUpdatePage;
import org.bee.pages.doctor.ViewAppointmentPage;
import org.bee.ui.*;
import org.bee.ui.views.ListView;
import org.bee.ui.views.MenuView;
import org.bee.ui.views.TableView;
import org.bee.utils.ReflectionHelper;
import org.bee.utils.formAdapters.PatientFormAdapter;

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
    private static final HumanController humanController = HumanController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    /**
     * Called when the main page's view is created.
     * Creates a {@link MenuView} to hold the main menu options.
     * Sets the title header to "Main".
     *
     * @return A new {@link MenuView} instance representing the main page's view.
     */

    @Override
    public View createView() {
        return new MenuView(this.canvas, "Patient Portal", Color.GREEN, true, false);

    }

    /**
     * This method is called after the view has been created and attached to the UI.
     * It populates the main view with a list of menu options, such as viewing/updating
     * user particulars, booking appointments, and viewing or changing existing appointments.
     * It also attaches input handlers to each menu option to navigate to the corresponding
     * actions or pages when selected by the user.
     *
     * @param parentView The parent {@link View} to which the main page's UI elements are added.
     *                   This should be a {@link ListView}, which will display the available menu items.
     */
    @Override
    public void OnViewCreated(View parentView) {
        MenuView menuView = (MenuView) parentView;
        HumanController controller = HumanController.getInstance();
        menuView.setTitleHeader(controller.getUserGreeting());

        MenuView.MenuSection patientSection = menuView.addSection("Patient Services");
        patientSection.addOption(1, "View/Update Particulars - To update user particular");
        patientSection.addOption(2, "Book Appointment - To schedule teleconsult appointment");
        patientSection.addOption(3, "View/Change Appointment - To view or reschedule an existing teleconsult appointment");
        menuView.attachMenuOptionInput(1, "View/Update Particulars", str -> viewPatientDetails());
        menuView.attachMenuOptionInput(2, "Book Appointment", str -> bookAppointmentPrompt());
        menuView.attachMenuOptionInput(3, "View/Change Appointment", str -> ToPage(new org.bee.pages.patient.ViewAppointmentPage()));

        MenuView.MenuSection infoSection = menuView.addSection("Information Services");
        infoSection.addOption(4, "View Billing - To view unpaid bills");
        infoSection.addOption(5, "View Appointment Summary");
        menuView.attachMenuOptionInput(4, "View Billing", str -> ToPage(new ViewAppointmentPage()));
        menuView.attachMenuOptionInput(5, "View Appointment Summary", str -> ToPage(new ViewAppointmentSummaryPage()));

        canvas.setRequireRedraw(true);
    }

    public void viewPatientDetails() {
        try {
            HumanController humanController = HumanController.getInstance();
            Patient currentPatient = (Patient) humanController.getLoggedInUser();

            PatientDetailsPage detailsPage = new PatientDetailsPage(currentPatient);
            ToPage(detailsPage);

        } catch (Exception e) {
            System.err.println("Error viewing patient details: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to book a new teleconsultation appointment.
     * The user is asked to provide a reason for the consultation, medical history,
     * and select an appointment date and time slot. The method validates the input and
     * adds the appointment to the system if all details are provided correctly.
     *
     * @throws IllegalStateException if the current user is not a patient.
     */
    private void bookAppointmentPrompt() {
        appointmentController.getAllAppointments();
        Terminal terminal = canvas.getTerminal();
        System.out.println("\nEnter reason to consult: ");
        String reason = terminal.getUserInput();
        System.out.println("Do you have any Medical History?: ");
        String history = terminal.getUserInput();

        System.out.println("Select your appointment date in this format (DD-MM-YYYY): ");
        LocalDate date = null; // safe to initialise as null, as it will never be null after the prompt.
        boolean validDate = false;
        String appointmentDate;
        while (!validDate) {
            appointmentDate = terminal.getUserInput();
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

        int selectedSlot = InputHelper.getValidIndex(terminal, "Select your appointment timeslot", 1, dateTimeDictionary.size());

        LocalDateTime selectedDateTime = dateTimeDictionary.get(selectedSlot);

        System.out.println("You have requested for an appointment on " + formatter.format(selectedDateTime) + " at index " + selectedSlot);

        SystemUser systemUser = humanController.getLoggedInUser();
        if (systemUser instanceof Patient patient) {
            Appointment appointment = new Appointment(null, patient, reason, selectedDateTime, AppointmentStatus.PENDING);


            // check if consent is already given before asking.
            if (!patient.getPatientConsent()) {
                String consentString = """
                        Telemedicine Consent Form
                        
                        Purpose: This telemedicine session is for a general checkup.
                        
                        Procedure:  This session will use live video and audio to connect you with the provider.  You may be asked to share information about your health, and the provider may provide advice or recommendations.
                        
                        Recording: This session will not be recorded. If this session is recorded for any purpose, it will be made known to you and separate verbal consent will be required during the call.
                        
                        Confidentiality: Your personal health information is protected by Singapore privacy laws.  We will take reasonable steps to protect your privacy.
                        
                        Risks and Limitations: Telemedicine is not a substitute for in-person care.  Some conditions cannot be diagnosed or treated remotely.  Technical issues (e.g., poor internet connection) may affect the quality of the session.  In case of an emergency, please call 911 or go to the nearest emergency room.
                        
                        Alternatives: You have the option to schedule an in-person appointment instead of using telemedicine.
                        
                        Rights: You have the right to refuse or withdraw consent at any time. You have the right to ask questions about this session and your health information.
                        
                        By Agreeing, you confirm that you have read, understood, and agree to the terms of this telemedicine consent (Y/N).\s
                        
                        Technical requirements: A laptop or mobile device (such as phone or tablet) with Zoom Meetings app installed""";
                System.out.println(consentString);
                boolean validInput = false;
                while (!validInput) {
                    System.out.println("Do you wish to proceed with this appointment? (Y/N)");
                    String s = terminal.getUserInput();
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

    /**
     * Allows the user (patient) to view, change, or cancel an existing appointment.
     * This method displays a list of the patient's current appointments and allows the user to select one to view or modify.
     * The user can choose to change the appointment date and time, cancel the appointment, or return to the main menu.
     * If changing the appointment, the user is prompted to select a new date and time slot. The new appointment time
     * must be in the future. If the user decides to cancel the appointment, the appointment is removed from the system.
     * <p>
     * The available time slots for appointment changes start at 8:00 AM and are incremented hourly, with 9 available slots.
     *
     * @throws IllegalStateException if the logged-in user is not a patient.
     */
    private void changeAppointmentPrompt() {
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
                System.out.println("\nYour Appointments:");
                for (int i = 0; i < appointments.size(); i++) {
                    Appointment appointment = appointments.get(i);
                    System.out.println((i + 1) + ". " + appointment.getAppointmentTime() + " - " + appointment.getReason());
                }

                System.out.println("Select appointment to view details or change:");
                int choice = InputHelper.getValidIndex(canvas.getTerminal(), "Enter your choice", 1, appointments.size());

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

                int optionChoice = InputHelper.getValidIndex(canvas.getTerminal(), "Enter your option", 1, 4);

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

                    int selectedSlot = InputHelper.getValidIndex(canvas.getTerminal(), "Select your new appointment timeslot", 1, timeSlots.size());
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

    private TableView<Appointment> createAppointmentTableView(List<Appointment> appointments) {
        TableView<Appointment> tableView = new TableView<>(canvas, "", Color.CYAN);

        tableView.showRowNumbers(true)
                .addColumn("Appointment ID", 15, a -> (String) ReflectionHelper.propertyAccessor("appointmentId", null).apply(a))

                .addColumn("Consult Reason", 25, a -> {
                    String reason = (String) ReflectionHelper.propertyAccessor("reason", null).apply(a);
                    return reason != null ? reason : "Not specified";
                })
                .addColumn("Date & Time", 20, a -> {
                    LocalDateTime time = (LocalDateTime) ReflectionHelper.propertyAccessor("appointmentTime", null).apply(a);
                    if (time == null) return "Not scheduled";

                    String formattedDate = dateFormatter.format(time);
                    LocalDateTime now = LocalDateTime.now();

                    // Color appointments based on timing
                    if (time.isBefore(now)) {
                        return colorText(formattedDate, Color.RED);
                    } else if (time.isBefore(now.plusDays(1))) {
                        return colorText(formattedDate, Color.YELLOW);
                    } else if (time.isBefore(now.plusDays(3))) {
                        return colorText(formattedDate, Color.GREEN);
                    }
                    return formattedDate;
                })
                .addColumn("Status", 15, a -> {
                    AppointmentStatus status = (AppointmentStatus) ReflectionHelper.propertyAccessor("appointmentStatus", null).apply(a);
                    if (status == null) return "Unknown";

                    String statusStr = formatEnum(status.toString());

                    return switch (status) {
                        case COMPLETED -> colorText(statusStr, Color.GREEN);
                        case ACCEPTED -> colorText(statusStr, Color.CYAN);
                        case PENDING -> colorText(statusStr, Color.YELLOW);
                        case DECLINED, CANCELED -> colorText(statusStr, Color.RED);
                        case PAYMENT_PENDING -> colorText(statusStr, Color.UND_RED);
                        case PAID -> colorText(statusStr, Color.UND_GREEN);
                    };
                })
                .setData(appointments);

        return tableView;

    }
}