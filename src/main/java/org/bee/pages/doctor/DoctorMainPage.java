package org.bee.pages.doctor;

import org.bee.controllers.ConsultationController;
import org.bee.controllers.HumanController;
import org.bee.hms.auth.SystemUser;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.*;
import org.bee.ui.*;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;
import org.bee.utils.InfoUpdaters.ConsultationUpdater;
import org.bee.utils.InfoUpdaters.PatientUpdater;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorMainPage extends UiBase {

    private static final HumanController humanController = HumanController.getInstance();
    private static final ConsultationController consultationController = ConsultationController.getInstance();
    private static Consultation consultation;
    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public View OnCreateView() {
        ListView lv = new ListView(this.canvas, Color.GREEN);
        lv.setTitleHeader("Main");
        return lv;
    }

    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;
        HumanController controller = HumanController.getInstance();
        lv.setTitleHeader(controller.getUserGreeting());
        lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
        lv.addItem(new TextView(this.canvas, "Telemedicine Services", Color.GREEN, TextStyle.BOLD));
        lv.addItem(new TextView(this.canvas, "1. View List of Patients - To view patient information ", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "2. View Appointment - To view new / scheduled appointments for teleconsultation ", Color.GREEN));

        lv.addItem(new TextView(this.canvas, "", Color.GREEN)); // Another empty line
        lv.addItem(new TextView(this.canvas, "Outpatient Management Services", Color.GREEN, TextStyle.BOLD));
//        lv.addItem(new TextView(this.canvas, "3. Create New Outpatient Case ", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "3. View List of Outpatient Cases ", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "4. Update Outpatient Case ", Color.GREEN));

        lv.attachUserInput("View List of Patients ", str -> ToPage(new PatientInfoPage()));
        lv.attachUserInput("View Appointment ", str -> ToPage(new ViewAppointmentPage()));
//        lv.attachUserInput("Create New Outpatient Case ", str -> ToPage(new CreateOutpatientCase()));
        lv.attachUserInput("View List of Outpatient Cases ", str -> ToPage(new OutpatientPatientInfoPage()));
        lv.attachUserInput("Update Outpatient Case ", str -> ToPage(new UpdateOutpatientCase()));

    }
}