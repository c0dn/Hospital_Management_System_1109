package org.bee.ui;


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
     * FIXED: Complete rewrite of the back button logic to fix the issues
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

}