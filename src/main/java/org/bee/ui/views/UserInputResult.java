package org.bee.ui.views;

/**
 * Functional interface that defines a callback method for handling user input in the BEE UI framework.
 * This interface is implemented when attaching user input handlers to View components.
 *
 * <p>The interface is primarily used with menu options, form fields, and interactive UI elements
 * to process user responses. It serves as the connection point between user actions in the
 * terminal interface and the application's business logic.</p>
 *
 * <p>Typically used through lambda expressions when attaching input handlers to views:</p>
 * <pre>
 *     view.attachUserInput("Option Name", input -> {
 *         // Handle the input here
 *     });
 * </pre>
 *
 * @see org.bee.ui.views.UserInput
 * @see org.bee.ui.View#attachUserInput(String, UserInputResult)
 * @see org.bee.ui.views.MenuView
 * @see org.bee.ui.views.FormView
 */
public interface UserInputResult {
    /**
     * Processes user input provided through the terminal interface.
     *
     * <p>This method is invoked by the Canvas main loop when a user selects the
     * corresponding option or provides input for a selected field. The implementation
     * determines what actions to take based on the input value.</p>
     *
     * @param userInput The string value of the user's input, typically representing
     *                  a menu selection, form field value, or direct key press
     */

    void onInput(String userInput);
}
