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
    }

    /**
     * Sets the entry point of the application.
     * @param initialUi the UiBase class entry point
     */
    public void startApplication(UiBase initialUi) {
        initialUi.setCanvas(canvas);
        initialUi.setApplicationContext(this);
        initialUi.ToPage(initialUi);
        canvas.mainLoop();
    }
}
