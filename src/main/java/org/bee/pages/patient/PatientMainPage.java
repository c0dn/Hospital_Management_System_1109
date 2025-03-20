package org.bee.pages.patient;

import org.bee.controllers.AppointmentController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Patient;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.ui.Color;
import org.bee.ui.InputHelper;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

/** Represents the main page of the Telemedicine Integration System.
 * This page displays a menu of options for the user to navigate to different sections of the application.
 * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.*/
public class PatientMainPage extends UiBase {
    ListView listView;
    private static final HumanController humanController = HumanController.getInstance();
    private static final AppointmentController appointmentController = AppointmentController.getInstance();

    /** Called when the main page's view is created.
     * Creates a {@link ListView} to hold the main menu options.
     * Sets the title header to "Main".
     * @return A new {@link ListView} instance representing the main page's view.
     */

    @Override
    public View OnCreateView() {
        ListView lv = new ListView(this.canvas, Color.GREEN);
        listView = lv;
        return lv;
    }

    /**Called after the view has been created and attached to the UI.
     * Populates the view with the main menu options, such as "View List of Patient", and "View Appointment".
     * Attaches user input handlers to each menu option to navigate to the corresponding pages.
     * @param parentView The parent {@link View} to which the main page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView; // Cast the parent view to a list view
        HumanController controller = HumanController.getInstance();
        lv.setTitleHeader(" Teleconsultation | Welcome back! " + controller.getUserGreeting());
        lv.addItem(new TextView(this.canvas, "1. Book Appointment - To schedule appointment to see doctors ", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "2. View Billing - To view due teleconsult bills ", Color.GREEN));

        lv.attachUserInput("Book Appointment", str -> bookAppointmentPrompt());

//        lv.attachUserInput("View Billing", str -> {
//            BillingPage.appointments = appointmentController.getAppointments();
//            ToPage(new BillingPage());
//        });


        canvas.setRequireRedraw(true);
    }

    private void bookAppointmentPrompt(){
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
        if(!patient.getPatientConsent()) {
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
        appointmentController.saveAppointments();
        canvas.setRequireRedraw(true);
    }
    }
}

