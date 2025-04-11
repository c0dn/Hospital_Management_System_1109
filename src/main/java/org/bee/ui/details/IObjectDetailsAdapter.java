package org.bee.ui.details;

import org.bee.ui.views.ObjectDetailsView;

/**
 * Interface for adapters that configure the display of object details.
 * This interface defines methods to populate an ObjectDetailsView with
 * appropriate sections and fields for a specific object type.
 *
 * <p>Implementations of this interface provide structured display of object
 * information in the UI by using the ObjectDetailsView's section and field system.
 * This creates a consistent layout for displaying detailed information across
 * different object types in the application.</p>
 *
 * @param <T> The type of object to display details for
 * @see org.bee.ui.views.ObjectDetailsView
 */
public interface IObjectDetailsAdapter<T> {

    /**
     * Configures an ObjectDetailsView for the given object.
     * Implementations should add sections and fields to the view based on the object's properties.
     *
     * <p>This method is responsible for organizing and populating all relevant
     * details from the object into appropriate sections in the view.</p>
     *
     * @param view   The ObjectDetailsView to configure
     * @param object The object to display details for
     * @return The configured ObjectDetailsView
     */
    ObjectDetailsView configureView(ObjectDetailsView view, T object);

    /**
     * Gets a descriptive name for the object type.
     * This can be used for display purposes such as titles and headers.
     *
     * @return A string describing the object type
     */
    String getObjectTypeName();
}