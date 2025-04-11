package org.bee.pages;

import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.ObjectDetailsView;

/**
 * A generic page for displaying detailed information about an object.
 * This page uses an adapter to configure the details view for the specific object type.
 *
 * @param <T> The type of object to display details for
 */
public class ObjectDetailsPage<T> extends UiBase {

    private final T objectToDisplay;
    private final IObjectDetailsAdapter<T> adapter;
    private final Runnable onEditCallback;

    /**
     * Creates a new ObjectDetailsPage.
     *
     * @param objectToDisplay The object to display details for
     * @param adapter         The adapter that configures the details view
     */
    public ObjectDetailsPage(T objectToDisplay, IObjectDetailsAdapter<T> adapter) {
        this(objectToDisplay, adapter, null);
    }

    /**
     * Creates a new ObjectDetailsPage with a callback for when editing is requested.
     *
     * @param objectToDisplay The object to display details for
     * @param adapter         The adapter that configures the details view
     * @param onEditCallback  A callback to execute when the user wants to edit the object
     */
    public ObjectDetailsPage(T objectToDisplay, IObjectDetailsAdapter<T> adapter, Runnable onEditCallback) {
        this.objectToDisplay = objectToDisplay;
        this.adapter = adapter;
        this.onEditCallback = onEditCallback;
    }

    @Override
    public View createView() {
        String title = adapter.getObjectTypeName() + " Details";

        CompositeView compositeView = new CompositeView(this.canvas, title, Color.CYAN);

        ObjectDetailsView detailsView = new ObjectDetailsView(
                this.canvas,
                title,
                objectToDisplay,
                Color.CYAN
        );

        adapter.configureView(detailsView, objectToDisplay);

        compositeView.addView(detailsView);

        return compositeView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        CompositeView compositeView = (CompositeView) parentView;

        if (onEditCallback != null) {
            compositeView.attachUserInput("Edit " + adapter.getObjectTypeName(), userInput -> {
                onEditCallback.run();
            });
        }

        canvas.setRequireRedraw(true);
    }
}
