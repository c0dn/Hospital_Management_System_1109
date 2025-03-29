package org.bee.ui;


import org.bee.ui.views.UserInput;
import org.bee.ui.views.UserInputResult;

import java.util.*;

public abstract class View {
    protected String titleHeader;
    protected Canvas canvas;
    protected Color color;
    protected String text;
    protected Dictionary<Integer, UserInput> inputOptions = new Hashtable<>();
    protected Dictionary<Integer, UserInput> hiddenInputOptions = new Hashtable<>();

    /**
     * Constructor overload that specifies a header and content.
     * @param canvas canvas to render on
     * @param titleHeader header of the view (always bold by default)
     * @param content content of the view
     */
    public View(Canvas canvas, String titleHeader, String content) {
        this.canvas = canvas;
        this.titleHeader = titleHeader;
        text = content;
        this.color = Color.WHITE; // Default color if unspecified
    }

    /**
     * Constructor overload that defines color of the view.
     * @param canvas canvas to render on.
     * @param content content of the view.
     * @param color color of the view.
     */
    public View(Canvas canvas, String content, Color color) {
        this.canvas = canvas;
        this.text = content;
        this.color = color;
    }

    /**
     * Constructor overload that defines both the color and the header.
     * @param canvas canvas to render on.
     * @param titleHeader header of the view.
     * @param content content of the view.
     * @param color color of the view.
     */
    public View(Canvas canvas, String titleHeader, String content, Color color) {
        this.canvas = canvas;
        this.titleHeader = titleHeader;
        this.text = content;
        this.color = color;
    }

    /**
     * Gets the full text for canvas to render.
     * @return getText or null, please handle the null case correctly.
     */
    public String getText() {
        if (text == null) return null;

        return Color.ESCAPE.getAnsiCode() + color.getAnsiCode() + text + Color.ESCAPE.getAnsiCode();
    }

    /**
     * Gets the full text for the header. Header is always BOLD.
     * @return titleHeader or null, please handle the null case correctly.
     */
    public String getTitleHeader() {
        if(titleHeader == null) return null;
        return TextStyle.BOLD.getAnsiCode() + titleHeader + Color.ESCAPE.getAnsiCode();
    }

    /**
     * Any other pre-execution you may want to perform before the view goes back. Such as destroying or saving objects.
     */
    public void OnBackPressed() {
    }

    /**
     * Any other pre-execution you may want to perform before the app exits. Such as saving objects.
     */
    public void OnApplicationExit(){}

    /**
     * Handle direct input for single-character inputs, like shortcut keys.
     * This method should be overridden by subclasses that need to handle direct letter inputs.
     *
     * @param input The input string (typically a single character)
     * @return true if the input was handled, false otherwise
     */
    public boolean handleDirectInput(String input) {
        return false;
    }

    /**
     * Footer contains all the user input options by default, override this method otherwise.
     * @return String representation of the user input options.
     */
    public String getFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n");

        List<Integer> keys = new ArrayList<>();
        for (Enumeration<Integer> e = inputOptions.keys(); e.hasMoreElements();) {
            keys.add(e.nextElement());
        }

        Collections.sort(keys);

        for (Integer key : keys) {
            UserInput input = inputOptions.get(key);
            if (input != null) {
                sb.append(" | ");
                sb.append(key);
                sb.append(": ");
                sb.append(input.promptText());
            }
        }

        return sb.toString();
    }

    /**
     * Sets the title header of the view.
     * @param header title header
     */
    public void setTitleHeader(String header) {
        this.titleHeader = header;
    }

    /**
     * Attach a user input to the view/page
     * @param option User-friendly name of the input, this will be presented to the user.
     * @param lambda Lambda method, can be defined in-place or a method call provided.
     */
    public void attachUserInput(String option, UserInputResult lambda) {
        int nextIndex = 1;
        while (inputOptions.get(nextIndex) != null) {
            nextIndex++;
        }
        inputOptions.put(nextIndex, new UserInput(option, lambda));
    }

    /**
     * Clears all user inputs from the view/page
     */
    public void clearUserInputs(){
        inputOptions = new Hashtable<>();
    }

    /**
     * Sets the user input for the specific integer index
     * @param id index of the id
     * @param optionName name of the new option
     * @param lambda lambda function to call
     */
    public void setUserInputByIndex(int id, String optionName, UserInputResult lambda){
        if(id == 0){
            // NEVER allow people to override the back button
            return;
        }
        inputOptions.put(id, new UserInput(optionName, lambda));
    }

    /**
     * Gets the full dictionary of the user inputs
     * @return dictionary of user inputs
     */
    public Dictionary<Integer, UserInput> getInputOptions() {
        return this.inputOptions;
    }
}