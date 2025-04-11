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

    /**
     * Helper method to add multiple detail properties to a view from a properties array.
     * Each property in the array should contain display name, property name, and fallback value.
     *
     * @param <Z> The type of the object to extract properties from
     * @param view The DetailsView to add details to
     * @param sectionName The name of the section to add details to
     * @param obj The object to extract properties from
     * @param properties A 2D array where each inner array contains [displayName, propertyName, fallbackValue]
     */
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