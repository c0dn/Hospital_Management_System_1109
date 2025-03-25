package org.bee.ui;

import java.util.Stack;

/**
 * Defines the application start point. And gracefully starts the application.
 */
public class ApplicationContext {
    protected Canvas canvas;
    protected Stack<UiBase> backStack = new Stack<>();
    protected NullPage nullPage;

    public ApplicationContext(Canvas canvas) {
        this.canvas = canvas;

        nullPage = new NullPage();
        nullPage.setCanvas(canvas);
        nullPage.setApplicationContext(this);
        backStack.push(nullPage);
//        System.out.println("[DEBUG] ApplicationContext: Initialized with NullPage at bottom of stack");
    }

    /**
     * Sets the entry point of the application.
     * @param initialUi the UiBase class entry point
     */
    public void startApplication(UiBase initialUi) {
        canvas.setPageNavigationCallback(newPage -> {
            newPage.setCanvas(canvas);

            System.out.println("[DEBUG] ToPage: Pushing new page: " + newPage.getClass().getSimpleName());
            backStack.push(newPage);

            View view = newPage.OnCreateView();
            newPage.OnViewCreated(view);
            canvas.setCurrentView(view);
        });



        initialUi.setCanvas(canvas);
        initialUi.setApplicationContext(this);

//        System.out.println("[DEBUG] Starting application with " + initialUi.getClass().getSimpleName());
        backStack.push(initialUi);

        View view = initialUi.OnCreateView();
        initialUi.OnViewCreated(view);
        canvas.setCurrentView(view);

        canvas.clearCallbacks();

        canvas.setBackNavigationCallback(() -> {
//            System.out.println("[DEBUG] Custom back callback - Handling back navigation");

            UiBase currentPage = backStack.peek();

            View currentView = canvas.getCurrentView();

            if (currentView != currentPage.lastCreatedView) {
//                System.out.println("[DEBUG] Internal page navigation detected");
//                System.out.println("[DEBUG] Navigating back to main view of " + currentPage.getClass().getSimpleName());
                canvas.setCurrentView(currentPage.lastCreatedView);
                return;
            }

            // Check if we're at the first page (one after NullPage)
            if (backStack.size() == 2) {

                UiBase previousPage = backStack.elementAt(0);

                if (previousPage instanceof NullPage) {
//                    System.out.println("[DEBUG] At first page, can't go back further");
                    canvas.setSystemMessage("You are at the main page. Please login or quit.");
                    canvas.setRequireRedraw(true);
                    return;
                }
            }

            // Normal page handling
            if (backStack.size() > 1) {
                UiBase poppedPage = backStack.pop();
//                System.out.println("[DEBUG] Popped page: " + poppedPage.getClass().getSimpleName());

                UiBase previousPage = backStack.peek();
//                System.out.println("[DEBUG] Previous page: " + previousPage.getClass().getSimpleName());

                if (previousPage instanceof NullPage) {
//                    System.out.println("[DEBUG] Hit NullPage, pushing current page back");
                    backStack.push(poppedPage);
                    return;
                }

                View prevView = previousPage.OnCreateView();
                previousPage.OnViewCreated(prevView);
                canvas.setCurrentView(prevView);
            } else {
                System.out.println("[DEBUG] Backstack empty or only has one item");
            }
        });

        canvas.mainLoop();
    }
}