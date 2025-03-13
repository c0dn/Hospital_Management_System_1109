package org.bee.ui;


import org.bee.ui.views.NullView;
import org.bee.ui.views.UserInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** handles page rendering, lifecycle, and callbacks (application exit and onbackpressed callbacks).
 * Also handles view backstack, but backstack management will be removed from canvas in the near future.
 */
public class Canvas {
    private Terminal terminal;
    private Stack<View> canvasBackstack;
    private String systemMessage = "";
    private boolean requireRedraw = false;
    protected List<IGenericCallbackInterface> backPressedCallback = new ArrayList<>();
    protected List<IGenericCallbackInterface> applicationStopCallback = new ArrayList<>();
    protected boolean stopCanvas = false;

    public Canvas() {
        this.terminal = new Terminal(); // Handles terminal interaction (see notes below)
        this.canvasBackstack = new Stack<>();
        // null object pattern, avoid null object reference when hitting the root null page.
        canvasBackstack.push(new NullView(this));
    }

    public int getBackstackSize() {
        return canvasBackstack.size();
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
    public View getCurrentPage() {
        return this.canvasBackstack.peek();
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

    /**
     * clears all the callbacks for the current page.
     */
    protected void clearCallbacks(){
        this.backPressedCallback.clear();
        this.applicationStopCallback.clear();
    }

    /**
     * Pushes a new page onto the stack and tells the canvas to render it.
     * @param page the page to push.
     */
    public void pushPage(View page){
        this.canvasBackstack.push(page);
        View v = canvasBackstack.peek();
        if(v.getInputOptions().get(0) == null){
            // manually add input 0 to always be the back button
            v.inputOptions.put(0, new UserInput("Go Back", str->{
                v.OnBackPressed();
                this.previousPage();
            }));
        }
        // clear the back pressed callbacks when changing pages
        this.clearCallbacks();
        this.addOnBackPressedCallback(page::OnBackPressed);
        this.addApplicationStopCallback(page::OnApplicationExit);
        this.setRequireRedraw(true);
    }

    /**
     * Simple utility function that tells the mainloop to redraw the current page.
     * Call this when updates are required.
     * @param requireRedraw boolean value to set the requireRedraw flag
     */
    public void setRequireRedraw(boolean requireRedraw) {
        this.requireRedraw = requireRedraw;
    }

    public void previousPage() {
        View curPage = canvasBackstack.pop();

        // NEVER allow the initial NullView to be shown
        boolean clearCallbacks = true;
        if(canvasBackstack.peek() instanceof NullView){
            clearCallbacks = false;
            canvasBackstack.push(curPage);
        }
        // call all the callbacks
        for (IGenericCallbackInterface callback : backPressedCallback) {
            callback.callback();
        }
        if(clearCallbacks){
            View page = canvasBackstack.peek();
            this.clearCallbacks();
            this.addOnBackPressedCallback(page::OnBackPressed);
            this.addApplicationStopCallback(page::OnApplicationExit);
        }
        setRequireRedraw(true);
    }

    /**
     * Utility function to force an update of the current View in the canvas.
     * Contains logic from previousPage and pushPage but without calling the callbacks.
     */
    public void newInPlacePage(View v){
        canvasBackstack.pop();
        canvasBackstack.push(v);
        if(v.getInputOptions().get(0) == null){
            // manually add input 0 to always be the back button
            v.inputOptions.put(0, new UserInput("Go Back", str->{
                v.OnBackPressed();
                this.previousPage();
            }));
        }
        this.clearCallbacks();
        this.addOnBackPressedCallback(v::OnBackPressed);
        this.addApplicationStopCallback(v::OnApplicationExit);
        this.setRequireRedraw(true);
    }

    /**
     * The program's main loop, handles logic for the user interface, including inputs, rudimentary error handling and input validation
     * As well as gracefully stopping the application.
     */
    public void mainLoop(){
        while(!stopCanvas) {
            View currentPage = this.canvasBackstack.peek();
            if (currentPage instanceof NullView) {
                continue;
            }
            if(requireRedraw){
                renderView(currentPage);
            }
            terminal.flush();
            // start the scanner
            String response = terminal.getUserInput();
            int responseInt = -1;
            try{
                responseInt = Integer.parseInt(response);
            }catch (Exception e){
                if(response != null && !response.isEmpty() && response.toLowerCase().charAt(0) == 'q'){
                    stopCanvas = true;
                    break;
                }
                // invalid response
            }
            if (currentPage.getInputOptions() != null
                    && (responseInt == -1 || currentPage.getInputOptions().get(responseInt) == null)) {
                systemMessage = "Invalid input, please try again.";
                setRequireRedraw(true);
                continue;
            }
            // if the input was valid, it should pass through here, so clear the system message
            systemMessage = "";

            // call the lambda method
            try {
                UserInput inputOptions = currentPage.getInputOptions().get(responseInt);
                inputOptions.lambda().onInput(response);
            }catch (Exception e){
                // TODO handle logging with log4j to file
            }
        }

        for (IGenericCallbackInterface callback: applicationStopCallback) {
            callback.callback();
        }
        System.out.println("Bye! Have a Nice Day!");
    }

    /**
     * Renders the view into the terminal
     * @param v the view to render
     */
    public void renderView(View v) {
        if(v instanceof NullView){
            //drawText("The end of the backstack has been reached, this should NOT happen.", Color.WHITE);
            return;
        }

        Color pageColor = this.canvasBackstack.peek().color;
        terminal.clearScreen();

        // draw the header first
        if(v.getTitleHeader() != null){
            drawText(v.getTitleHeader()+"\n", pageColor);
        }

        String text = v.getText();
        if(text != null){
            drawText(text, pageColor);
        }

        // draw the footer last
        String footer = v.getFooter();
        if(footer != null){
            footer = footer + "| q: Quit App\nYour input: ";
            drawText(footer, pageColor);
        }

        // system messages
        if(!systemMessage.isBlank()){
            System.out.println();
            drawText(systemMessage, Color.RED);
        }
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
