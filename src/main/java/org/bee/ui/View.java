package org.bee.ui;


import org.bee.ui.views.UserInput;
import org.bee.ui.views.UserInputResult;

import java.util.Dictionary;
import java.util.Hashtable;

public abstract class View {
    protected String titleHeader;
    protected Canvas canvas;
    protected Color color;
    protected String text;
    protected Dictionary<Integer, UserInput> inputOptions = new Hashtable<>();

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
        if(text == null) return null;
        // canvas handles the colouring
        return Color.ESCAPE.getAnsiCode() + color.getAnsiCode() + text + Color.ESCAPE.getAnsiCode(); // Add color codes
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
     * Footer contains all the user input options by default, override this method otherwise.
     * @return String representation of the user input options.
     */
    public String getFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n");
        // it is far more efficient to use string builder compared to concatenating strings
        for (int i = 0; i < inputOptions.size(); i++){
            sb.append(" | ");
            sb.append(i);
            sb.append(": ");
            sb.append(inputOptions.get(i).promptText());
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
    public void attachUserInput(String option, UserInputResult lambda){
        // input option 0 is reserved for the back button
        inputOptions.put(inputOptions.size() + 1, new UserInput(option, lambda));
    }

    /**
     * Clears all user inputs from the view/page
     */
    public void clearUserInputs(){
        var back = inputOptions.get(0);
        // just re-create the hash table
        inputOptions = new Hashtable<>();
        inputOptions.put(0, back);
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
