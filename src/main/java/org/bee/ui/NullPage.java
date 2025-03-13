package org.bee.ui;


import org.bee.ui.views.TextView;

/**
 * Null page that implements the null object pattern
 * This avoids cases where the backstack is empty.
 */
public class NullPage extends UiBase {

    @Override
    public View OnCreateView() {
        return new TextView(this.canvas, "If you see this screen, this should not happen, exit the app immediately", Color.RED);
    }

    @Override
    public void OnViewCreated(View parentView) {

    }
}
