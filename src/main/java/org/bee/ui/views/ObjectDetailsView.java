package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;
import org.bee.utils.ReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A view that displays detailed information about an object in a structured format with sections and fields.
 * This view is designed to be used with any object type and can be configured with custom field accessors.
 */
public class ObjectDetailsView extends View {

    /**
     * Represents a section in the details view.
     */
    public static class Section {
        private final String title;
        private final List<Field<?>> fields = new ArrayList<>();

        /**
         * Creates a new section with the given title.
         *
         * @param title The title of the section
         */
        public Section(String title) {
            this.title = title;
        }

        /**
         * Adds a field to this section.
         *
         * @param field The field to add
         * @return This section for method chaining
         */
        public Section addField(Field<?> field) {
            fields.add(field);
            return this;
        }

        /**
         * Gets the title of this section.
         *
         * @return The section title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the fields in this section.
         *
         * @return The list of fields
         */
        public List<Field<?>> getFields() {
            return fields;
        }
    }

    /**
     * Represents a field in a details section.
     *
     * @param <T> The type of object the field is for
     */
    public static class Field<T> {
        private final String name;
        private final Function<T, String> valueAccessor;

        /**
         * Creates a new field with a name and value accessor.
         *
         * @param name          The name of the field
         * @param valueAccessor Function to extract the field value as a string
         */
        public Field(String name, Function<T, String> valueAccessor) {
            this.name = name;
            this.valueAccessor = valueAccessor;
        }

        /**
         * Gets the name of this field.
         *
         * @return The field name
         */
        public String getName() {
            return name;
        }

        /**
         * Extracts the value from the given object.
         *
         * @param object The object to extract the value from
         * @return The extracted value as a string
         */
        @SuppressWarnings("unchecked")
        public String getValue(Object object) {
            try {
                return valueAccessor.apply((T) object);
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
    }

    private final Object targetObject;
    private final String title;
    private final List<Section> sections = new ArrayList<>();
    private int sectionWidth = 70;
    private String sectionDivider = "=".repeat(sectionWidth);
    private String fieldDivider = "-".repeat(sectionWidth);

    /**
     * Creates a new ObjectDetailsView.
     *
     * @param canvas       The canvas to render on
     * @param title        The title for the view
     * @param targetObject The object to display details for
     * @param color        The color for the view
     */
    public ObjectDetailsView(Canvas canvas, String title, Object targetObject, Color color) {
        super(canvas, title, "", color);
        this.targetObject = targetObject;
        this.title = title;
    }

    /**
     * Sets the width of the section display.
     *
     * @param width The width in characters
     * @return This view for method chaining
     */
    public ObjectDetailsView setSectionWidth(int width) {
        this.sectionWidth = width;
        this.sectionDivider = "=".repeat(width);
        this.fieldDivider = "-".repeat(width);
        return this;
    }

    /**
     * Adds a section to the details view.
     *
     * @param title The title for the section
     * @return The newly created section
     */
    public Section addSection(String title) {
        Section section = new Section(title);
        sections.add(section);
        return section;
    }

    /**
     * Helper method to create a field from a property name.
     *
     * @param displayName  The display name for the field
     * @param propertyName The object property name
     * @param fallback     The fallback value if the property can't be accessed
     * @param <T>          The object type
     * @return A new Field instance
     */
    public <T> Field<T> createField(String displayName, String propertyName, String fallback) {
        return new Field<>(displayName, obj ->
                ReflectionHelper.stringPropertyAccessor(propertyName, fallback).apply(obj));
    }

    /**
     * Helper method to create a nested field.
     *
     * @param displayName    The display name for the field
     * @param parentProperty The parent property name
     * @param childProperty  The child property name
     * @param fallback       The fallback value if the property can't be accessed
     * @param <T>            The object type
     * @return A new Field instance
     */
    public <T> Field<T> createNestedField(String displayName, String parentProperty,
                                          String childProperty, String fallback) {
        return new Field<>(displayName, obj ->
                ReflectionHelper.nestedStringPropertyAccessor(parentProperty, childProperty, fallback).apply(obj));
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        sb.append("Current Particulars:\n\n");

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);

            sb.append(sectionDivider).append("\n");
            sb.append(centerText(section.getTitle().toUpperCase(), sectionWidth)).append("\n");
            sb.append(sectionDivider).append("\n");

            for (Field<?> field : section.getFields()) {
                String value = field.getValue(targetObject);
                sb.append(field.getName()).append(": ").append(value).append("\n");
            }

            if (i < sections.size() - 1) {
                sb.append(fieldDivider).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Centers text within a specified width.
     *
     * @param text  The text to center
     * @param width The width to center within
     * @return The centered text
     */
    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }

        int leftPadding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - leftPadding;

        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }
}