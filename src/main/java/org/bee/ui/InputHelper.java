package org.bee.ui;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Helper class which contains logic to prompt the user for inputs and handles error handling.
 */
public class InputHelper {
    /**
     * Prompts the user to enter an integer index within a specified range.
     * Handles invalid input (non-integer, out-of-range) gracefully.
     * prompt will be appended as the prefix. i.e. your prompt "Select patient" + " (0 - 6))"
     *
     * @param prompt  The message to display to the user.
     * @param min     The minimum valid index (inclusive).
     * @param max     The maximum valid index (inclusive).
     * @return The valid integer index entered by the user.
     */
    public static int getValidIndex(String prompt, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum index cannot be greater than maximum index.");
        }

        // Create a new Scanner for each call to avoid conflicts with shared System.in.
        // Do NOT close this Scanner, as it would close System.in, breaking the CLI framework.
        Scanner scanner = new Scanner(System.in);

        int selectedIndex = -1;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println(prompt + " (" + min + " - " + max + ")");
                selectedIndex = scanner.nextInt();
                scanner.nextLine();

                if (selectedIndex >= min && selectedIndex <= max) {
                    validInput = true;
                } else {
                    System.out.println("Invalid index. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                // Consume the invalid input
                scanner.next();
            }
        }

        return selectedIndex;
    }

    /**
     * Overloaded getValidIndex to work more seamlessly with List objects.
     * Automatically determines the valid index range based on the list size.
     * prompt will be appended as the prefix. i.e. your prompt "Select patient" + " (0 - 6))"
     *
     * @param prompt  The message to display to the user.
     * @param list The list object
     * @return The valid integer index entered by the user.
     */
    public static int getValidIndex(String prompt, List<?> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty.");
        }
        return getValidIndex(prompt, 0, list.size() - 1);
    }
}
