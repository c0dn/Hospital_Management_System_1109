package org.bee;

import org.bee.pages.LoginPage;
import org.bee.ui.ApplicationContext;
import org.bee.ui.Canvas;

/**
 * The main entry point for the insurance system.
 * This class initializes the system and processes user input.
 */

public class Main {
    /**
     * This method demonstrates how to create a medication from its code, check its category,
     * and print information about the medication. It also retrieves a list of medications
     * from a specific category.
     * @param args
     */
    public static void main(String[] args) {
        var canvas = new Canvas();
        ApplicationContext applicationContext = new ApplicationContext(canvas);
        applicationContext.startApplication(new LoginPage());
    }
}
