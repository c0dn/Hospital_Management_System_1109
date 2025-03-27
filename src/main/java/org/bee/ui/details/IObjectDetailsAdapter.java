package org.bee.ui.details;

import org.bee.ui.views.ObjectDetailsView;

/**
 * Interface for adapters that configure the display of object details.
 * This interface defines methods to populate an ObjectDetailsView with
 * appropriate sections and fields for a specific object type.
 *
 * @param <T> The type of object to display details for
 */
public interface IObjectDetailsAdapter<T> {

    /**
     * Configures an ObjectDetailsView for the given object.
     * Implementations should add sections and fields to the view based on the object's properties.
     *
     * @param view   The ObjectDetailsView to configure
     * @param object The object to display details for
     * @return The configured ObjectDetailsView
     */
    ObjectDetailsView configureView(ObjectDetailsView view, T object);

    /**
     * Gets a descriptive name for the object type.
     * This can be used for display purposes.
     *
     * @return A string describing the object type
     */
    String getObjectTypeName();
}