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
 * <p>
 * This view organizes object data into sections with labeled fields, each extracted from
 * the target object using accessor functions. It supports both direct property access and
 * nested property access through reflection, with configurable formatting and layout options.
 * <p>
 * The view is designed to be used with any object type and integrates with the
 * {@link org.bee.ui.details.IObjectDetailsAdapter} system to provide standardized display
 * of different domain objects like patients, consultations, appointments, etc.
 *
 * @see org.bee.ui.details.IObjectDetailsAdapter
 * @see org.bee.utils.ReflectionHelper
 * @see org.bee.utils.detailAdapters.PatientDetailsAdapter
 * @see org.bee.utils.detailAdapters.BillDetailsAdapter
 */

public class ObjectDetailsView extends View {

    /**
     * Represents a section in the details view that contains a group of related fields.
     * <p>
     * Each section has a title and contains multiple fields that display information
     * about the target object.
     */
    public static class Section {
        private final String title;
        private final List<Field<?>> fields = new ArrayList<>();

        /**
         * Creates a new section with the given title.
         *
         * @param title The title of the section to be displayed in the header
         */
        public Section(String title) {
            this.title = title;
        }

        /**
         * Adds a field to this section.
         *
         * @param field The field to add to this section
         * @return This section instance for method chaining
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
         * @return The list of fields contained in this section
         */
        public List<Field<?>> getFields() {
            return fields;
        }
    }

    /**
     * Represents a field in a details section with a name and a value accessor.
     * <p>
     * Fields use function-based value extraction to enable flexible access to object properties,
     * supporting both simple property access and complex transformations.
     *
     * @param <T> The type of object the field's accessor function handles
     */
    public static class Field<T> {
        private final String name;
        private final Function<T, String> valueAccessor;

        /**
         * Creates a new field with a name and value accessor function.
         *
         * @param name          The display name of the field
         * @param valueAccessor Function to extract and format the field value as a string
         */
        public Field(String name, Function<T, String> valueAccessor) {
            this.name = name;
            this.valueAccessor = valueAccessor;
        }

        /**
         * Gets the display name of this field.
         *
         * @return The field name that appears in the view
         */
        public String getName() {
            return name;
        }

        /**
         * Extracts the value from the given object using the field's accessor function.
         * <p>
         * This method safely handles type casting and exceptions, returning an error
         * message if value extraction fails.
         *
         * @param object The object to extract the value from
         * @return The extracted value formatted as a string, or an error message
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
     * Creates a new ObjectDetailsView for displaying information about the specified object.
     *
     * @param canvas       The canvas to render on
     * @param title        The title for the view
     * @param targetObject The object to display details for
     * @param color        The color for the view's text
     */
    public ObjectDetailsView(Canvas canvas, String title, Object targetObject, Color color) {
        super(canvas, title, "", color);
        this.targetObject = targetObject;
        this.title = title;
    }

    /**
     * Sets the width of the section display.
     * <p>
     * This affects the width of section headers and dividers to provide consistent formatting.
     *
     * @param width The width in characters for sections
     * @return This view instance for method chaining
     */
    public ObjectDetailsView setSectionWidth(int width) {
        this.sectionWidth = width;
        this.sectionDivider = "=".repeat(width);
        this.fieldDivider = "-".repeat(width);
        return this;
    }

    /**
     * Adds a new section to the details view.
     * <p>
     * Sections are displayed in the order they are added.
     *
     * @param title The title for the section
     * @return The newly created section that can be used to add fields
     */
    public Section addSection(String title) {
        Section section = new Section(title);
        sections.add(section);
        return section;
    }

    /**
     * Creates a field that accesses a property directly from the target object.
     * <p>
     * Uses the {@link ReflectionHelper} to safely access object properties through
     * getters or direct field access.
     *
     * @param displayName  The display name for the field
     * @param propertyName The object property name to access
     * @param fallback     The fallback value if the property can't be accessed
     * @param <T>          The object type
     * @return A new Field instance configured to access the specified property
     */
    public <T> Field<T> createField(String displayName, String propertyName, String fallback) {
        return new Field<>(displayName, obj ->
                ReflectionHelper.stringPropertyAccessor(propertyName, fallback).apply(obj));
    }

    /**
     * Creates a field that accesses a nested property (property of a property).
     * <p>
     * This is useful for accessing properties of embedded objects, like a patient's contact information.
     *
     * @param displayName    The display name for the field
     * @param parentProperty The parent property name (the containing object)
     * @param childProperty  The child property name (the property of the parent)
     * @param fallback       The fallback value if either property can't be accessed
     * @param <T>            The object type
     * @return A new Field instance configured to access the nested property
     */
    public <T> Field<T> createNestedField(String displayName, String parentProperty,
                                          String childProperty, String fallback) {
        return new Field<>(displayName, obj ->
                ReflectionHelper.nestedStringPropertyAccessor(parentProperty, childProperty, fallback).apply(obj));
    }

    /**
     * Generates the formatted text representation of the object details.
     * <p>
     * The output includes all sections with their titles and fields, formatted with
     * appropriate headers, dividers, and layout.
     *
     * @return The formatted text containing all object details
     */
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
     * Centers text within a specified width by adding padding on both sides.
     *
     * @param text  The text to center
     * @param width The width to center within
     * @return The centered text with appropriate padding
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