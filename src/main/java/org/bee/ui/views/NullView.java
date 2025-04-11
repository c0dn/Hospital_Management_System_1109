package org.bee.ui.views;


import org.bee.ui.Canvas;
import org.bee.ui.View;

/**
 * A view implementation of the Null Object pattern to prevent NullPointerExceptions in the view backstack.
 * <p>
 * The NullView serves as a placeholder in the UI framework when no real view is present,
 * allowing the application to safely handle view navigation without null checks.
 * It creates an empty view with no title and no content, ensuring that methods like
 * {@link View#getText()} and {@link View#getTitleHeader()} return empty strings
 * rather than null.
 * <p>
 * This class is primarily used in conjunction with {@link Canvas} and
 * {@link org.bee.ui.ApplicationContext} to maintain a consistent view hierarchy
 * and prevent application crashes when the view stack is manipulated.
 *
 * @see org.bee.ui.View
 * @see org.bee.ui.Canvas
 * @see org.bee.ui.NullPage
 */
public class NullView extends View {

    /**
     * Creates a new NullView with empty title and content.
     *
     * @param canvas The canvas to render on (although rendering will produce nothing visible)
     */
    public NullView(Canvas canvas) {
        super(canvas, "", "");
    }
}