package org.bee.ui.views;


import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple view that renders a list of child views. Can be used both vertically and horizontally.
 */
public class ListView extends View {
    protected List<View> views = new ArrayList<>();
    protected ListViewOrientation orientation = ListViewOrientation.VERTICAL;
    protected String separator = "\t";

    /**
     * Default overload, ListView is vertical by default, separated by newline.
     * @param canvas The base canvas.
     * @param color Base color of the list.
     */
    public ListView(Canvas canvas, Color color) {
        super(canvas, "", color);
    }

    /**
     * Constructor overload that supports setting orientation, used if you want a RowView instead.
     * @param canvas The base canvas.
     * @param color Base color of the list.
     * @param orientation Orientation of the list.
     */
    public ListView(Canvas canvas, Color color, ListViewOrientation orientation) {
        super(canvas, "", color);
        this.orientation = orientation;
    }

    /**
     * Constructor overload that supports setting orientation AND separator used if you want a RowView AND with custom separation.
     * @param canvas The base canvas.
     * @param color Base color of the list.
     * @param orientation Orientation of the list.
     * @param separator custom separation between items in the row.
     */
    public ListView(Canvas canvas, Color color, ListViewOrientation orientation, String separator) {
        super(canvas, "", color);
        this.orientation = orientation;
        this.separator = separator;
    }

    /**
     * Adds a single item to the listview
     * @param view child item to add
     */
    public void addItem(View view){
        this.views.add(view);
    }

    /**
     * Sets all children to the list of views. Overwrites everything already inside the list view.
     * @param views list of children to set
     */
    public void setItems(List<View> views){
        this.views.clear();
        this.views.addAll(views);
    }

    /**
     * Clears the ListView
     */
    public void clear(){
        this.views.clear();
    }

    /**
     * Listview overrides the getText method to provide the concatenated strings of all its children.
     * @return returns the strings that should be rendered
     */
    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        // oh cool java can do this now?
        for (View view : views) {
            sb.append(view.getText());
            sb.append(orientation == ListViewOrientation.VERTICAL ? "\n" : separator);
        }
        // escape the ascii colour
        return sb.toString();
    }
}
