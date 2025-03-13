package org.bee.ui;


public abstract class UiBase {
    protected Canvas canvas;
    // variable to keep track of canvas backstack size, so we know we really went back or not
    protected int startPageSize;
    // the application context
    protected ApplicationContext context;

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Base method for OnCreateView, here you will define your views, must return compatible View object
     *
     * @return returns a view for the framework to utilize
     */
    public abstract View OnCreateView();

    /**
     * Any other pre-execution you may want to perform before the view is executed. Such as initializing objects.
     * @param parentView the Parent view of the UiBase class, provided in OnCreateView
     */
    public abstract void OnViewCreated(View parentView);

    /**
     * Called when the back button is pressed.
     */
    public void OnBackPressed(){
        if(this.canvas.getBackstackSize() != context.backStack.size()){
            UiBase poppedPage = context.backStack.pop();
            if(this.context.backStack.peek() instanceof NullPage){
                context.backStack.push(poppedPage);
                return;
            }
            // we now know the backstack has been updated, tell context.
            UiBase page = context.backStack.peek();

            // the canvas will handle the backstack rendering independently. So no need to manipulate.
            var view = page.OnCreateView();
            page.OnViewCreated(view);

            // sometimes canvas may not correctly render the new page
            // since I carelessly let canvas have its own backstack.
            // so sometimes the old view may be an old reference.
            // TODO remove backstack support from canvas.
            canvas.newInPlacePage(view);
            page.setApplicationContext(context);
            canvas.clearCallbacks();
            canvas.addOnBackPressedCallback(page::OnBackPressed);
            canvas.addApplicationStopCallback(page::OnApplicationExit);
            page.OnPageEntry();
            canvas.setRequireRedraw(true);
        }
    }

    /**
     * Called when the application is exited.
     */
    protected void OnApplicationExit(){}

    /**
     * Executes after all the view instantiation and canvas manipulation.
     * Sets the startPageSize property to keep track of the backstack manipulation.
     * Sort of a band-aid solution. But because of my poor hindsight, causing the weak-base phenomenon.
     * And not wanting to make unnecessary breaking changes.
     */
    protected void OnPageEntry(){
        this.startPageSize = canvas.getBackstackSize();
    }

    /**
     * Navigates to the next page
     * @param page the UiBase child class to provide.
     */
    public void ToPage(UiBase page) {
        // dependency inject required objects
        page.setCanvas(canvas);
        page.setApplicationContext(context);
        context.backStack.push(page);
        var view = page.OnCreateView();
        page.OnViewCreated(view);
        canvas.pushPage(view);
        canvas.addOnBackPressedCallback(page::OnBackPressed);
        canvas.addApplicationStopCallback(page::OnApplicationExit);
        page.OnPageEntry();
    }

}
