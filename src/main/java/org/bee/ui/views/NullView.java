package org.bee.ui.views;


import org.bee.ui.Canvas;
import org.bee.ui.View;

/**
 * Null-object pattern to ensure the backstack never encounters a NullPointerException
 */
public class NullView extends View {
    public NullView(Canvas canvas) {
        super(canvas, "", "");
    }
}
