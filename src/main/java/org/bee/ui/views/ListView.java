package org.bee.ui.views;


import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A view that renders a collection of child views in either vertical or horizontal orientation.
 * <p>
 * ListView provides a simple container for organizing multiple views, arranging them
 * either vertically (like a traditional list) or horizontally (like a row of items).
 * Each child view is rendered in sequence with configurable separators between them.
 * </p>
 * <p> This view is useful for creating:</p>
 * <ul>
 *   <li>Simple lists of repeating elements</li>
 *   <li>Horizontal row layouts</li>
 *   <li>Composite layouts with consistent spacing</li>
 * </ul>
 */
public class ListView extends View {
    protected List<View> views = new ArrayList<>();
    protected ListViewOrientation orientation = ListViewOrientation.VERTICAL;
    protected String separator = "\t";

    /**
     * Creates a vertical list view with default newline separators.
     * <p>
     * This is the default constructor that creates a standard vertical list
     * where child views are stacked one below another.
     * </p>
     *
     * @param canvas The canvas to render on
     * @param color The color for the view's text
     */
    public ListView(Canvas canvas, Color color) {
        super(canvas, "", color);
    }

    /**
     * Creates a list view with the specified orientation.
     * <p>This constructor allows choosing between vertical and horizontal layouts:</p>
     * <ul>
     *   <li>VERTICAL: Items stacked top to bottom (default separator: newline)</li>
     *   <li>HORIZONTAL: Items arranged left to right (default separator: tab)</li>
     * </ul>
     *
     * @param canvas The canvas to render on
     * @param color The color for the view's text
     * @param orientation The orientation of the list (VERTICAL or HORIZONTAL)
     */
    public ListView(Canvas canvas, Color color, ListViewOrientation orientation) {
        super(canvas, "", color);
        this.orientation = orientation;
    }

    /**
     * Creates a list view with specified orientation and custom separator.
     * <p>
     * This constructor provides full control over both the layout direction and
     * the separator used between child views.
     * </p>
     *
     * @param canvas The canvas to render on
     * @param color The color for the view's text
     * @param orientation The orientation of the list (VERTICAL or HORIZONTAL)
     * @param separator The separator string to place between child views
     */
    public ListView(Canvas canvas, Color color, ListViewOrientation orientation, String separator) {
        super(canvas, "", color);
        this.orientation = orientation;
        this.separator = separator;
    }

    /**
     * Adds a single child view to the list.
     * <p>
     * The view will be appended to the end of the current list of children.
     * </p>
     *
     * @param view The child view to add
     */
    public void addItem(View view){
        this.views.add(view);
    }

    /**
     * Replaces all current child views with the provided list.
     * <p>
     * This method clears any existing views before adding the new ones.
     * </p>
     *
     * @param views The list of views to set as children
     */
    public void setItems(List<View> views){
        this.views.clear();
        this.views.addAll(views);
    }

    /**
     * Removes all child views from this list view.
     * <p>
     * After calling this method, the list view will be empty.
     * </p>
     */
    public void clear(){
        this.views.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Concatenates the text content of all child views, separated by either
     * newlines (vertical orientation) or the configured separator (horizontal orientation).
     * </p>
     */
    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        for (View view : views) {
            String viewText = view.getText();
            if (viewText != null) {
                sb.append(viewText);
                sb.append(orientation == ListViewOrientation.VERTICAL ? "\n" : separator);
            }
        }

        // If have any views, remove the trailing separator
        if (!views.isEmpty() && sb.length() > 0) {
            int separatorLength = (orientation == ListViewOrientation.VERTICAL) ? 1 : separator.length();
            sb.setLength(sb.length() - separatorLength);
        }

        return sb.toString();
    }
}