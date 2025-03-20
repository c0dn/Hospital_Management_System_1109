package org.bee.pages.doctor;

import org.bee.controllers.HumanController;
import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

public class DoctorMainPage extends UiBase {
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
        lv.setTitleHeader("Welcome to Telemedicine Integration System |  " + controller.getUserGreeting());
        lv.addItem(new TextView(this.canvas, "1. View List of Patient - To view patient information ", Color.GREEN));
        lv.addItem(new TextView(this.canvas, "2. View Appointment - To view new / scheduled appointments for teleconsultation ", Color.GREEN));

        lv.attachUserInput("View List of Patient", str -> ToPage(new PatientInfoPage()));
//        lv.attachUserInput("View Appointment", str -> ToPage(new ViewAppointmentsPage()));

    }
}
