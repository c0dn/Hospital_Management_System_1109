package org.bee.ui.details;

import org.bee.ui.views.DetailsView;
import org.bee.utils.ReflectionHelper;

/**
 * Interface for adapters that configure the display of detail views.
 * This interface defines methods to populate a DetailsView with
 * appropriate sections and details for a specific object type.
 *
 * @param <T> The type of object to display details for
 */
public interface IDetailsViewAdapter<T> {

    /**
     * Configures a DetailsView for the given object.
     * Implementations should add sections and detail items to the view based on the object's properties.
     *
     * @param view   The DetailsView to configure
     * @param object The object to display details for
     * @return The configured DetailsView
     */
    DetailsView<T> configureView(DetailsView<T> view, T object);

    /**
     * Gets a descriptive name for the object type.
     * This can be used for display purposes.
     *
     * @return A string describing the object type
     */
    String getObjectTypeName();

    default <Z> void addDetailsFromProperties(DetailsView<Z> view, String sectionName, Z obj, String[][] properties) {
        for (String[] property : properties) {
            String displayName = property[0];
            String propertyName = property[1];
            String fallback = property[2];

            String value = ReflectionHelper.stringPropertyAccessor(propertyName, fallback).apply(obj);
            view.addDetail(sectionName, displayName, value);
        }
    }
}