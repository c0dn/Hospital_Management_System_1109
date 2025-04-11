package org.bee.ui;

import java.util.Stack;

/**
 * Manages the application lifecycle and navigation stack.
 * <p>
 * The ApplicationContext class serves as the central controller for the application,
 * maintaining the navigation history through a stack of UI pages and handling the
 * logic for navigation between pages. It acts as the bridge between the Canvas
 * rendering system and the UI pages.
 * </p>
 * <p>
 * Key responsibilities:
 * <ul>
 *   <li>Initializing the application with an entry point</li>
 *   <li>Managing the back stack for navigation history</li>
 *   <li>Coordinating page transitions and view creation</li>
 *   <li>Setting up navigation callbacks for the Canvas</li>
 *   <li>Ensuring the application has a fallback page (NullPage) to prevent empty states</li>
 * </ul>
 * */
public class ApplicationContext {
    /**
     * The Canvas instance responsible for rendering the UI.
     */
    protected Canvas canvas;

    /**
     * Stack that keeps track of page navigation history.
     * The top of the stack represents the currently active page.
     */
    protected Stack<UiBase> backStack = new Stack<>();
    /**
     * A placeholder page that serves as the bottom of the navigation stack.
     * This prevents empty stack situations and provides a fallback.
     */
    protected NullPage nullPage;
    /**
     * Creates a new ApplicationContext associated with the provided Canvas.
     * <p>
     * Initializes the navigation stack with a NullPage at the bottom to ensure
     * there is always at least one page in the stack, preventing empty stack exceptions.
     * </p>
     *
     * @param canvas The Canvas instance to use for rendering the UI
     */
    public ApplicationContext(Canvas canvas) {
        this.canvas = canvas;

        nullPage = new NullPage();
        nullPage.setCanvas(canvas);
        nullPage.setApplicationContext(this);
        backStack.push(nullPage);
//        System.out.println("[DEBUG] ApplicationContext: Initialized with NullPage at bottom of stack");
    }

    /**
     * Starts the application with the specified initial UI page.
     * <p>
     * This method:
     * <ol>
     *   <li>Sets up navigation callbacks for the Canvas</li>
     *   <li>Initializes the first UI page and adds it to the navigation stack</li>
     *   <li>Creates and renders the initial view</li>
     *   <li>Sets up back navigation handling logic</li>
     *   <li>Starts the main application loop</li>
     * </ol>
     * <p>
     * The back navigation logic handles:
     * <ul>
     *   <li>Internal view navigation within a page</li>
     *   <li>Navigation between pages in the stack</li>
     *   <li>Proper restoration of previous pages' views</li>
     *   <li>Boundary conditions (such as being on the first page)</li>
     * </ul>
     *
     * @param initialUi The entry point UI page to start the application with
     * @throws IllegalArgumentException If initialUi is null
     */
    public void startApplication(UiBase initialUi) {
        canvas.setPageNavigationCallback(newPage -> {
            newPage.setCanvas(canvas);

//            System.out.println("[DEBUG] ToPage: Pushing new page: " + newPage.getClass().getSimpleName());
            backStack.push(newPage);
            newPage.setApplicationContext(this);

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
                    canvas.setSystemMessage("You are at the main page. Please login or quit.", SystemMessageStatus.ERROR);
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