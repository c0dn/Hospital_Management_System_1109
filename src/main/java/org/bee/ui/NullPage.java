package org.bee.ui;


import org.bee.ui.views.TextView;

/**
 * Null page that implements the null object pattern
 * This avoids cases where the backstack is empty.
 */
public class NullPage extends UiBase {
    /**
     * Creates a view for the `NullPage`.
     * <p>
     * This method returns a `TextView` with an error message indicating that the application has encountered
     * an unexpected state.
     * </p>
     *
     * @return A `TextView` instance containing the error message.
     */
    @Override
    public View createView() {
        return new TextView(this.canvas, "If you see this screen, this should not happen, exit the app immediately", Color.RED);
    }

    @Override
    public void OnViewCreated(View parentView) {

    }
}
