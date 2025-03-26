package org.bee.ui;


import org.bee.ui.views.NullView;
import org.bee.ui.views.UserInput;

import java.util.*;

/** handles page rendering, lifecycle, and callbacks (application exit and onBackPressed callbacks).
 * Also handles view backstack, but backstack management will be removed from canvas in the near future.
 */
public class Canvas {

    @FunctionalInterface
    public interface IPageNavigationCallback {
        void navigateToPage(UiBase newPage);
    }

    private final Terminal terminal;
    private String systemMessage = "";
    private View currentView;
    private boolean requireRedraw = false;
    private IGenericCallbackInterface backNavigationCallback = null;
    private IPageNavigationCallback pageNavigationCallback;

    protected List<IGenericCallbackInterface> backPressedCallback = new ArrayList<>();
    protected List<IGenericCallbackInterface> applicationStopCallback = new ArrayList<>();
    protected boolean stopCanvas = false;

    public Canvas() {
        this.terminal = new Terminal(); // Handles terminal interaction (see notes below)
        this.currentView = new NullView(this);
    }

    /**
     * drawText overload method, that allows drawing at a specified x and y coordinate in the terminal
     * @param text the text to draw.
     * @param color the color of the text.
     * @param x the x coordinate of the text.
     * @param y the y coordinate of the text.
     */
    public void drawText(String text, Color color, int x, int y) {
        terminal.setCursorPosition(x, y);
        terminal.setTextColor(color); // Assuming Terminal handles ANSI color codes
        terminal.write(text);
        terminal.resetColor(); // Reset to default color after printing
    }

    /**
     * drawText overload method, that draws on the current line of the terminal.
     * @param text The text to draw.
     * @param color The color of the text.
     */
    public void drawText(String text, Color color) {
        terminal.setTextColor(color); // Assuming Terminal handles ANSI color codes
        terminal.write(text);
        terminal.resetColor(); // Reset to default color after printing
    }

    /**
     * Gets the current page from the Stack.
     * @return the current page.
     */
    public View getCurrentView() {
        return this.currentView;
    }


    public void setPageNavigationCallback(IPageNavigationCallback callback) {
        this.pageNavigationCallback = callback;
    }

    public void setBackNavigationCallback(IGenericCallbackInterface callback) {
        this.backNavigationCallback = callback;
    }


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
     * Adds an app exit callback for the current page
     * @param callback callback to add
     */
    public void addApplicationStopCallback(IGenericCallbackInterface callback) {
        this.applicationStopCallback.add(callback);
    }


    public void setSystemMessage(String message) {
        this.systemMessage = message;
    }

    /**
     * clears all the callbacks for the current page.
     */
    protected void clearCallbacks(){
        this.backPressedCallback.clear();
        this.applicationStopCallback.clear();
    }

    /**
     * Ensures the back button is present for the view
     * @param view the view to ensure has a back button
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
     * Sets up callbacks and prepares the view for rendering
     * @param view the view to setup callbacks for
     */
    private void setupViewCallbacks(View view) {
        this.clearCallbacks();

        this.addOnBackPressedCallback(view::OnBackPressed);
        this.addApplicationStopCallback(view::OnApplicationExit);

        this.setRequireRedraw(true);
    }


    public void setCurrentView(View view) {
        this.currentView = view;
        ensureBackButton(view);
        setupViewCallbacks(view);
    }

    /**
     * Simple utility function that tells the main loop to redraw the current page.
     * Call this when updates are required.
     * @param requireRedraw boolean value to set the requireRedraw flag
     */
    public void setRequireRedraw(boolean requireRedraw) {
        this.requireRedraw = requireRedraw;
    }

    /**
     * The program's main loop, handles logic for the user interface, including inputs, rudimentary error handling and input validation
     * As well as gracefully stopping the application.
     */
    public void mainLoop(){
        while(!stopCanvas) {
            if (currentView instanceof NullView) {
                continue;
            }
            if(requireRedraw || !systemMessage.isBlank()){
                renderView();
            }
            terminal.flush();
            String response = terminal.getUserInput();

            if(response != null && !response.isEmpty() && response.toLowerCase().charAt(0) == 'q'){
                stopCanvas = true;
                break;
            }

            if(response != null && !response.isEmpty() && response.toLowerCase().charAt(0) == 'e'){
                if (backNavigationCallback != null) {
                    backNavigationCallback.callback();
                    continue;
                }
            }


            if(response != null && !response.isEmpty() && Character.isLetter(response.charAt(0))) {
                boolean inputHandled = false;

                // Check for letter-based input handlers
                for (Enumeration<Integer> e = currentView.getInputOptions().keys(); e.hasMoreElements();) {
                    Integer key = e.nextElement();
                    UserInput inputOption = currentView.getInputOptions().get(key);

                    if (inputOption != null &&
                            (inputOption.promptText().equalsIgnoreCase("Next Page") && response.equalsIgnoreCase("n") ||
                                    inputOption.promptText().equalsIgnoreCase("Previous Page") && response.equalsIgnoreCase("p") ||
                                    inputOption.promptText().equalsIgnoreCase("Jump to Page") && response.equalsIgnoreCase("j"))) {

                        try {
                            inputOption.lambda().onInput(response);
                            inputHandled = true;
                            break;
                        } catch (Exception ex) {
                            System.out.println("[DEBUG] Exception in letter input handler: " + ex.getMessage());
                        }
                    }
                }

                if (inputHandled) {
                    continue;
                }
            }

            int responseInt;
            try {
                responseInt = Integer.parseInt(Objects.requireNonNull(response));
            } catch (Exception e) {
                systemMessage = "Invalid input, please try again.";
                setRequireRedraw(true);
                continue;
            }


            if (currentView.getInputOptions() != null && currentView.getInputOptions().get(responseInt) == null) {
                systemMessage = "Invalid input, please try again.";
                setRequireRedraw(true);
                continue;
            }

            systemMessage = "";

            try {
                UserInput inputOptions = currentView.getInputOptions().get(responseInt);
//                System.out.println("[DEBUG] Processing input option: " + responseInt + " - " + inputOptions.promptText());
                inputOptions.lambda().onInput(response);
            }catch (Exception e){
//                System.out.println("[DEBUG] Exception in input handler: " + e.getMessage());
            }
        }

        for (IGenericCallbackInterface callback: applicationStopCallback) {
            callback.callback();
        }
        System.out.println("Bye! Have a Nice Day!");
    }

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

        if (!systemMessage.isBlank()) {
            System.out.println();
            drawText(systemMessage, Color.RED);
        }

        Optional.ofNullable(currentView.getFooter())
                .ifPresent(footer -> drawText(footer, pageColor));

        terminal.flush();
        this.setRequireRedraw(false);
    }

    /**
     * Gets the terminal object.
     * @return terminal object.
     */
    public Terminal getTerminal() {
        return terminal;
    }
}