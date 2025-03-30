package org.bee.ui;


import org.bee.ui.views.NullView;
import org.bee.ui.views.UserInput;

import java.util.*;

/**
 * Central class for rendering UI components and managing the application lifecycle.
 * <p>
 * Canvas handles page rendering, view lifecycle, and callbacks for application events such as
 * back navigation and application exit. It manages the current view, processes user input,
 * and coordinates the display of system messages.
 * <p>
 * The class provides methods for drawing text with specific formatting and positioning,
 * and maintains the main application loop that drives the UI interactions.
 */
public class Canvas {
    /**
     * Functional interface for handling page navigation events.
     * Implementations define how to navigate to a new page in the application.
     */
    @FunctionalInterface
    public interface IPageNavigationCallback {

        /**
         * Navigates to a specified page.
         *
         * @param newPage The new page to navigate to.
         */
        void navigateToPage(UiBase newPage);
    }

    /**
     * Record class representing a system message with its associated status.
     * System messages are displayed to the user with color-coding based on their status.
     *
     * @param message The text content of the message
     * @param status The status determining the message's color and importance
     */
    private record SystemMessage(String message, SystemMessageStatus status) {}


    private final Terminal terminal;
    private SystemMessage systemMessage;
    private View currentView;
    private boolean requireRedraw = false;
    private IGenericCallbackInterface backNavigationCallback = null;
    private IPageNavigationCallback pageNavigationCallback;

    protected List<IGenericCallbackInterface> backPressedCallback = new ArrayList<>();
    protected List<IGenericCallbackInterface> applicationStopCallback = new ArrayList<>();
    protected boolean stopCanvas = false;

    /**
     * Constructs a new Canvas instance.
     * Initializes a terminal for user interaction and sets the current view to a NullView.
     */
    public Canvas() {
        this.terminal = new Terminal(); // Handles terminal interaction (see notes below)
        this.currentView = new NullView(this);
    }

    /**
     * Draws text at a specific position in the terminal with the specified color.
     * This method allows precise placement of text in the terminal interface.
     * @param text the text to draw.
     * @param color the color of the text.
     * @param x the x coordinate of the text.
     * @param y the y coordinate of the text.
     */
    public void drawText(String text, Color color, int x, int y) {
        terminal.setCursorPosition(x, y);
        terminal.setTextColor(color);
        terminal.write(text);
        terminal.resetColor();
    }

    /**
     * Draws text on the current line of the terminal with the specified color.
     * This method appends text at the current cursor position with the given color.
     *
     * @param text The text to draw
     * @param color The color to use for the text
     */
    public void drawText(String text, Color color) {
        terminal.setTextColor(color);
        terminal.write(text);
        terminal.resetColor();
    }

    /**
     * Gets the current view being displayed by the canvas.
     *
     * @return The current View instance
     */
    public View getCurrentView() {
        return this.currentView;
    }

    /**
     * Sets the callback for page navigation events.
     * This callback is invoked when the application needs to navigate to a new page.
     *
     * @param callback The callback to invoke when navigating to a new page
     */
    public void setPageNavigationCallback(IPageNavigationCallback callback) {
        this.pageNavigationCallback = callback;
    }

    /**
     * Sets the callback for back navigation events.
     * This callback is invoked when the user requests to navigate back to the previous page.
     *
     * @param callback The callback to invoke when back navigation occurs
     */
    public void setBackNavigationCallback(IGenericCallbackInterface callback) {
        this.backNavigationCallback = callback;
    }

    /**
     * Navigates to a new page using the registered page navigation callback.
     * If no callback is registered, this method has no effect.
     *
     * @param newPage The page to navigate to
     */
    public void navigateToPage(UiBase newPage) {
        if (pageNavigationCallback != null) {
            pageNavigationCallback.navigateToPage(newPage);
        }
    }


    /**
     * Adds an on back press callback for the current page
     * @param callback callback to add
     */
    public void addOnBackPressedCallback(IGenericCallbackInterface callback) {
        this.backPressedCallback.add(callback);
    }

    /**
     * Adds a callback to be executed when the application is exiting.
     * Multiple callbacks can be registered and will be executed in the order they were added.
     *
     * @param callback The callback to add
     */
    public void addApplicationStopCallback(IGenericCallbackInterface callback) {
        this.applicationStopCallback.add(callback);
    }


    /**
     * Sets a system message with the specified status.
     * The message will be displayed with a color corresponding to its status.
     * Setting a message automatically triggers a redraw of the canvas.
     *
     * @param message The message text to be displayed
     * @param status The status determining the message's color and importance
     */
    public void setSystemMessage(String message, SystemMessageStatus status) {
        if (message == null || message.isBlank()) {
            this.systemMessage = null;
        } else {
            this.systemMessage = new SystemMessage(message, status);
        }
        this.setRequireRedraw(true);
    }

    /**
     * Sets a system message with the default INFO status.
     * This is a convenience method that calls {@link #setSystemMessage(String, SystemMessageStatus)}
     * with {@link SystemMessageStatus#INFO} as the status.
     *
     * @param message The message text to be displayed
     */
    public void setSystemMessage(String message) {
        setSystemMessage(message, SystemMessageStatus.INFO);
    }

    /**
     * Clears the current system message.
     * After this method is called, no system message will be displayed until a new one is set.
     */
    public void clearSystemMessage() {
        this.systemMessage = null;
    }


    /**
     * Clears all callback registrations for the current page.
     * This includes both back navigation and application stop callbacks.
     */
    protected void clearCallbacks(){
        this.backPressedCallback.clear();
        this.applicationStopCallback.clear();
    }

    /**
     * Ensures the back button is present for the view.
     * If the view doesn't already have a back navigation option registered at index 0,
     * this method adds one that will trigger the appropriate callbacks.
     *
     * @param view The view to ensure has a back button
     */
    private void ensureBackButton(View view) {
        if(view.getInputOptions().get(0) == null) {
            view.inputOptions.put(0, new UserInput("Go Back", str -> {
//                System.out.println("[DEBUG] Back button pressed. Executing callbacks.");

                view.OnBackPressed();

                for (IGenericCallbackInterface callback : backPressedCallback) {
                    callback.callback();
                }

                if (backNavigationCallback != null) {
//                    System.out.println("[DEBUG] Executing back navigation callback");
                    backNavigationCallback.callback();
                }
            }));
        }
    }

    /**
     * Sets up callbacks and prepares the view for rendering.
     * This method registers the view's lifecycle methods as callbacks and
     * marks the canvas for redrawing.
     *
     * @param view The view to setup callbacks for
     */
    private void setupViewCallbacks(View view) {
        this.clearCallbacks();

        this.addOnBackPressedCallback(view::OnBackPressed);
        this.addApplicationStopCallback(view::OnApplicationExit);

        this.setRequireRedraw(true);
    }

    /**
     * Sets the current view to be displayed on the Canvas.
     * <p>
     * This method updates the `currentView` field to the provided view, ensures that a back button is present
     * for navigation, and sets up necessary callbacks for the new view. It also triggers a redraw of the Canvas
     * to reflect the updated view.
     * </p>
     *
     * @param view The new view to set as the current view. Must not be null.
     */
    public void setCurrentView(View view) {
        this.currentView = view;
        ensureBackButton(view);
        setupViewCallbacks(view);
    }

    /**
     * Sets the redraw flag to indicate whether the view needs to be redrawn.
     * Call this method when updates to the view content require a refresh of the display.
     *
     * @param requireRedraw true if the view needs to be redrawn, false otherwise
     */
    public void setRequireRedraw(boolean requireRedraw) {
        this.requireRedraw = requireRedraw;
    }

    /**
     * The main application loop that handles UI interactions.
     * <p>
     * This method continuously processes user input, manages view rendering, handles navigation
     * and exit events. It runs until the application is explicitly stopped via the 'q' command
     * or when the stopCanvas flag is set to true.
     * </p>
     * <p>
     * The loop performs the following operations:
     * <ul>
     *   <li>Renders the current view if updates are required</li>
     *   <li>Handles special command inputs ('q' for quit, 'e' for back navigation)</li>
     *   <li>Processes view-specific direct inputs</li>
     *   <li>Handles numeric option selections</li>
     *   <li>Displays appropriate error messages for invalid inputs</li>
     *   <li>Executes application stop callbacks when exiting</li>
     * </ul>
     */
    public void mainLoop(){
        while(!stopCanvas) {
            if (currentView instanceof NullView) {
                continue;
            }
            if(requireRedraw) {
                renderView();
            }
            terminal.flush();

            clearSystemMessage();
            String response = terminal.getUserInput();


            if(response == null || response.isEmpty()) {
                continue;
            }

            if(response.toLowerCase().charAt(0) == 'q'){
                stopCanvas = true;
                break;
            }

            if(response.toLowerCase().charAt(0) == 'e'){
                if (backNavigationCallback != null) {
                    backNavigationCallback.callback();
                    continue;
                }
            }

            if (currentView.handleDirectInput(response)) {
                setRequireRedraw(true);
                continue;
            }

            try {
                int responseInt = Integer.parseInt(response);

                if (currentView.getInputOptions() != null && currentView.getInputOptions().get(responseInt) != null) {
                    UserInput inputOption = currentView.getInputOptions().get(responseInt);
                    inputOption.lambda().onInput(response);
                } else {
                    setSystemMessage("Invalid input, please try again.", SystemMessageStatus.ERROR);
                    setRequireRedraw(true);
                }
            } catch (NumberFormatException e) {
                setSystemMessage("Invalid input, please try again.", SystemMessageStatus.ERROR);
                setRequireRedraw(true);
            } catch (Exception e) {
                System.out.println("[DEBUG] Exception in input handler: " + e.getMessage());
                setSystemMessage("An error occurred processing your input.", SystemMessageStatus.ERROR);
                setRequireRedraw(true);
            }
        }

        for (IGenericCallbackInterface callback: applicationStopCallback) {
            callback.callback();
        }
        System.out.println("Bye! Have a Nice Day!");
    }
    /**
     * Renders the current view on the terminal.
     * <p>
     * This method is responsible for:
     * <ul>
     *   <li>Clearing the screen</li>
     *   <li>Drawing the current view's title header if present</li>
     *   <li>Drawing the view's main content</li>
     *   <li>Drawing any active system messages with appropriate color coding</li>
     *   <li>Drawing the view's footer with navigation options</li>
     *   <li>Flushing the terminal to ensure all content is displayed</li>
     * </ul>
     *
     * After rendering is complete, the requireRedraw flag is reset to false.
     * If the current view is a NullView, this method does nothing.
     */

    public void renderView() {
        if(currentView instanceof NullView){
            return;
        }

        Color pageColor = currentView.color;
        terminal.clearScreen();

        Optional.ofNullable(currentView.getTitleHeader())
                .ifPresent(header -> drawText(header + "\n", pageColor));

        Optional.ofNullable(currentView.getText())
                .ifPresent(text -> drawText(text, pageColor));

        if (Objects.nonNull(systemMessage)) {
            Color messageColor = switch (systemMessage.status()) {
                case SUCCESS -> Color.GREEN;
                case ERROR -> Color.RED;
                case WARNING -> Color.YELLOW;
                case INFO -> Color.CYAN;
            };

            terminal.writeln("");
            drawText(systemMessage.message(), messageColor);
            terminal.writeln("");
        }

        Optional.ofNullable(currentView.getFooter())
                .ifPresent(footer -> drawText(footer, pageColor));

        terminal.flush();
        this.setRequireRedraw(false);
    }

    /**
     * Gets the terminal object used for input/output operations.
     *
     * @return The Terminal instance being used by this Canvas
     */
    public Terminal getTerminal() {
        return terminal;
    }

}