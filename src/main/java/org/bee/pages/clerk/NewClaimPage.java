package org.bee.pages.clerk;

import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Represents submit new claim for the clerk.
 * This page allows a clerk to simulate a insurance claim process.
 * It extends {@link UiBase} and uses a {@link ListView} to present the menu items.
 */
public class NewClaimPage extends UiBase {

    @Override
    public View createView() {
        return new ListView(this.canvas, Color.GREEN);
    }

    /**
     * Called after the view has been created and attached to the UI.
     * Populates the view with Submit New Claim.
     * Attaches user input handlers to each menu option to navigate to the corresponding pages.
     *
     * @param parentView The parent {@link View} to which the main page's UI elements are added. This should be a ListView.
     */
    @Override
    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView; // Cast the parent view to a list view
        lv.setTitleHeader("Clerk's Portal | Submit New Claim");

        // Menu options
        lv.addItem(new TextView(this.canvas, "Submit New Claim", Color.GREEN));

        lv.attachUserInput("Submit New Claim", str -> submitNewClaim());

        canvas.setRequireRedraw(true);
    }

    private void submitNewClaim() {
        // Create a scanner object to collect user input from the console.
        Scanner scanner = new Scanner(System.in);

        // Input for the patient's name.
        System.out.println("Enter Patient's Name: ");
        String name = scanner.nextLine();

        // Input for the ICD code (disease classification code).
        System.out.println("Enter ICD Code: ");
        String icdCode = scanner.nextLine();

        // Input for the submission date of the claim.
        System.out.println("Enter Submission Date (DD-MM-YYYY): ");
        LocalDate submissionDate = null;
        boolean validDate = false;
        String submissionDateStr;

        // Loop until a valid date is provided.
        while (!validDate) {
            submissionDateStr = scanner.nextLine();
            try {
                submissionDate = LocalDate.parse(submissionDateStr, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                validDate = true;
            } catch (Exception e) {
                // If the date format is incorrect, prompt the user to enter the date again.
                System.out.println("Invalid date format. Please enter a date in the format (DD-MM-YYYY):");
            }
        }

        // Mock insurance provider name.
        System.out.println("Enter Insurance Provider: ");
        String insuranceProviderStr = scanner.nextLine();

        // Mock insurance policy number.
        System.out.println("Enter Insurance Policy Number: ");
        String policyNumber = scanner.nextLine();

        // Mock claim amount.
        System.out.println("Enter Claim Amount (e.g., 1000.00): ");
        BigDecimal claimAmount = new BigDecimal(scanner.nextLine());

        // Mock verification, trying to link to insuranceclaim.java
        System.out.println("\nNew Claim Submitted Successfully!");
        System.out.println("\n====================================================");
        System.out.println("                INSURANCE CLAIM RECORD               ");
        System.out.println("====================================================");
        System.out.println("Claim Details:");
        System.out.println("Name: " + name);
        System.out.println("ICD Code: " + icdCode);
        System.out.println("Submission Date: " + submissionDate);
        System.out.println("Insurance Provider: " + insuranceProviderStr);
        System.out.println("Policy Number: " + policyNumber);
        System.out.println("Claim Amount: " + claimAmount);
    }
}