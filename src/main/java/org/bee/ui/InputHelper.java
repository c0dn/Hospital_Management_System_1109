package org.bee.ui;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Utility class providing methods to prompt users for input with built-in validation.
 * <p>
 * This helper simplifies common input tasks by handling error checking, retrying on invalid
 * input, and providing type-specific validation for various kinds of data including:
 * <ul>
 *   <li>Integer values within ranges</li>
 *   <li>String input with regex pattern validation</li>
 *   <li>String input with custom predicate validation</li>
 *   <li>Decimal (double) values within ranges</li>
 *   <li>Yes/No binary choices</li>
 *   <li>Selection from a list of options</li>
 * </ul>
 * <p>
 * All methods handle validation errors by displaying appropriate messages and
 * repeatedly prompting the user until valid input is provided. This simplifies
 * application code by centralizing input validation logic.
 */

public class InputHelper {
    /**
     * Prompts the user to enter an integer index within a specified range.
     * <p>
     * This method will continue prompting until the user enters a valid integer
     * that falls within the specified range (inclusive). Error messages are displayed
     * for non-integer input and out-of-range values.
     * </p>
     *
     * @param terminal The terminal to use for input/output
     * @param prompt   The message to display to the user
     * @param min      The minimum valid index (inclusive)
     * @param max      The maximum valid index (inclusive)
     * @return The valid integer index entered by the user
     * @throws IllegalArgumentException If min is greater than max
     */
    public static int getValidIndex(Terminal terminal, String prompt, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum index cannot be greater than maximum index.");
        }

        int selectedIndex = -1;
        boolean validInput = false;

        while (!validInput) {
            terminal.writeln(prompt + " (" + min + " - " + max + ")");
            String input = terminal.getUserInput();

            try {
                selectedIndex = Integer.parseInt(input);

                if (selectedIndex >= min && selectedIndex <= max) {
                    validInput = true;
                } else {
                    terminal.writeln("Invalid index. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                terminal.writeln("Invalid input. Please enter a valid integer.");
            }
        }

        return selectedIndex;
    }

    /**
     * Prompts the user to enter an index for selecting an item from a list.
     * <p>
     * This method automatically determines the valid range based on the list size,
     * using zero-based indexing to match common list implementations. It will continue
     * prompting until a valid index is entered.
     * </p>
     *
     * @param terminal The terminal to use for input/output
     * @param prompt   The message to display to the user
     * @param list     The list from which the user is selecting an item
     * @return The valid list index entered by the user (0 to list.size()-1)
     * @throws IllegalArgumentException If the list is null or empty
     */
    public static int getValidIndex(Terminal terminal, String prompt, List<?> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty.");
        }
        return getValidIndex(terminal, prompt, 0, list.size() - 1);
    }

    /**
     * Prompts the user to enter a string that matches a given regex pattern.
     *
     * @param terminal     The terminal to use for input/output.
     * @param prompt       The message to display to the user.
     * @param pattern      The regex pattern that input must match.
     * @param errorMessage The message to display on invalid input.
     * @return The valid string entered by the user.
     */
    public static String getValidString(Terminal terminal, String prompt, String pattern, String errorMessage) {
        String input;
        Pattern regex = Pattern.compile(pattern);

        while (true) {
            terminal.writeln(prompt);
            input = terminal.getUserInput();

            if (regex.matcher(input).matches()) {
                return input;
            } else {
                terminal.writeln(errorMessage);
            }
        }
    }

    /**
     * Prompts the user to enter a string that passes a custom validation predicate.
     *
     * @param terminal     The terminal to use for input/output.
     * @param prompt       The message to display to the user.
     * @param validator    The predicate function to validate input.
     * @param errorMessage The message to display on invalid input.
     * @return The valid string entered by the user.
     */
    public static String getValidString(Terminal terminal, String prompt, Predicate<String> validator, String errorMessage) {
        String input;

        while (true) {
            terminal.writeln(prompt);
            input = terminal.getUserInput();

            if (validator.test(input)) {
                return input;
            } else {
                terminal.writeln(errorMessage);
            }
        }
    }

    /**
     * Prompts the user to enter a number within a specified range.
     *
     * @param terminal The terminal to use for input/output.
     * @param prompt   The message to display to the user.
     * @param min      The minimum valid value (inclusive).
     * @param max      The maximum valid value (inclusive).
     * @return The valid double entered by the user.
     */
    public static double getValidDouble(Terminal terminal, String prompt, double min, double max) {
        double value = 0;
        boolean validInput = false;

        while (!validInput) {
            terminal.writeln(prompt + " (" + min + " - " + max + ")");
            String input = terminal.getUserInput();

            try {
                value = Double.parseDouble(input);

                if (value >= min && value <= max) {
                    validInput = true;
                } else {
                    terminal.writeln("Invalid value. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                terminal.writeln("Invalid input. Please enter a valid number.");
            }
        }

        return value;
    }

    /**
     * Prompts the user for a yes/no response.
     *
     * @param terminal The terminal to use for input/output.
     * @param prompt   The question to ask the user.
     * @return true for yes, false for no.
     */
    public static boolean getYesNoInput(Terminal terminal, String prompt) {
        String input;

        while (true) {
            terminal.writeln(prompt + " (y/n)");
            input = terminal.getUserInput().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                terminal.writeln("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    /**
     * Gets a valid selection from a list of options.
     *
     * @param terminal The terminal to use for input/output.
     * @param prompt   The message to display to the user.
     * @param options  The list of options to choose from.
     * @return The selected option string.
     */
    public static String getSelection(Terminal terminal, String prompt, List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Options list cannot be null or empty.");
        }

        terminal.writeln(prompt);
        for (int i = 0; i < options.size(); i++) {
            terminal.writeln((i + 1) + ": " + options.get(i));
        }

        int selectedIndex = getValidIndex(terminal, "Enter your selection", 1, options.size());
        return options.get(selectedIndex - 1);
    }

    /**
     * For backwards compatibility - gets the terminal from the Canvas and calls the main method
     *
     * @param canvas  The canvas to get the terminal from
     * @param prompt  The message to display to the user
     * @param min     The minimum valid index (inclusive)
     * @param max     The maximum valid index (inclusive)
     * @return The valid integer index entered by the user
     */
    public static int getValidIndex(Canvas canvas, String prompt, int min, int max) {
        return getValidIndex(canvas.getTerminal(), prompt, min, max);
    }

    /**
     * For backwards compatibility - gets the terminal from the Canvas and calls the main method
     *
     * @param canvas  The canvas to get the terminal from
     * @param prompt  The message to display to the user
     * @param list    The list object
     * @return The valid integer index entered by the user
     */
    public static int getValidIndex(Canvas canvas, String prompt, List<?> list) {
        return getValidIndex(canvas.getTerminal(), prompt, list);
    }
}
