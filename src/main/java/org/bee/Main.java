package org.bee;

import org.bee.controllers.*;
import org.bee.pages.LoginPage;
import org.bee.ui.ApplicationContext;
import org.bee.ui.Canvas;

/**
 * The main entry point for the Hospital management system
 * This class initializes the system and processes user input.
 */

public class Main {
    public static void main(String[] args) {
        // Init controllers first by getting instance
        HumanController.getInstance();
        PolicyController.getInstance();
        AppointmentController.getInstance();
        VisitController.getInstance();
        BillController.getInstance();
        ClaimController.getInstance();
        ConsultationController.getInstance();
        var canvas = new Canvas();
        ApplicationContext applicationContext = new ApplicationContext(canvas);
        applicationContext.startApplication(new LoginPage());
    }
}
