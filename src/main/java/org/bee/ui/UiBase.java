package org.bee.ui;


import org.bee.ui.views.CompositeView;
import org.bee.ui.views.MenuView;
import org.bee.ui.views.TextView;
import org.jetbrains.annotations.NotNull;

public abstract class UiBase {
    protected Canvas canvas;
    // the application context
    protected ApplicationContext context;
    protected View lastCreatedView;

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    protected abstract View createView();

    /**
     * Base method for OnCreateView, here you will define your views, must return compatible View object
     *
     * @return returns a view for the framework to utilize
     */
    public View OnCreateView() {
        View view = createView();
        this.lastCreatedView = view;
        return view;
    }


    /**
     * Any other pre-execution you may want to perform before the view is executed. Such as initializing objects.
     *
     * @param parentView the Parent view of the UiBase class, provided in OnCreateView
     */
    public abstract void OnViewCreated(View parentView);

    /**
     * Navigate to a view and add current page to backstack
     *
     * @param view The view to navigate to
     */
    protected void navigateToView(View view) {
        // Don't add to backstack, just change the view
        canvas.setCurrentView(view);
    }

    /**
     * Called when the back button is pressed.
     */
    public void OnBackPressed() {
//        System.out.println("[DEBUG] OnBackPressed called in UiBase");

        if (context.backStack.size() <= 1) {
//            System.out.println("[DEBUG] Backstack is empty or only has one item. Cannot go back.");
            return;
        }

        UiBase currentPage = context.backStack.pop();
//        System.out.println("[DEBUG] Popped page: " + currentPage.getClass().getSimpleName());

        UiBase previousPage = context.backStack.peek();
//        System.out.println("[DEBUG] Previous page: " + previousPage.getClass().getSimpleName());

        if (previousPage instanceof NullPage) {
//            System.out.println("[DEBUG] Reached NullPage, pushing current page back");
            context.backStack.push(currentPage);
            return;
        }

//        System.out.println("[DEBUG] Setting up view for previous page");
        View view = previousPage.OnCreateView();
        previousPage.OnViewCreated(view);
        canvas.setCurrentView(view);
    }

    /**
     * Called when the application is exited.
     */
    protected void OnApplicationExit() {
    }

    /**
     * Navigates to the next page and adds it to the backstack
     *
     * @param page the UiBase child class to provide.
     */
    public void ToPage(UiBase page) {
        page.setCanvas(canvas);
        canvas.navigateToPage(page);
    }


    /**
     * Wraps text with ANSI color codes
     * This works with the terminal's color support to render colored text
     */
    protected String colorText(String text, Color color) {
        return color.getAnsiCode() + text + Color.ESCAPE.getAnsiCode();
    }

    /**
     * Extending the color functionality with more flexible styling
     * Allows combining colors with text styles
     */
    protected String styledText(String text, Color color, TextStyle style) {
        return style.getAnsiCode() + color.getAnsiCode() + text + TextStyle.RESET.getAnsiCode();
    }


    /**
     * Formats enum values to make them more readable
     * Converts SNAKE_CASE to Title Case with spaces
     */
    protected String formatEnum(String enumValue) {
        if (enumValue == null) return "Unknown";

        String[] words = enumValue.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase());
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }

        return result.toString().trim();
    }


    @NotNull
    protected CompositeView getBlankListView(String titleHeader, String content) {
        CompositeView compositeView = new CompositeView(this.canvas, titleHeader, Color.YELLOW);

        TextView messageView = new TextView(
                canvas,
                content,
                Color.YELLOW
        );
        compositeView.addView(messageView);

        MenuView menuView = new MenuView(canvas, "", Color.WHITE, false, true);

        compositeView.addView(menuView);
        return compositeView;
    }


}