package org.bee.ui.forms;

import org.bee.ui.Color;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Base class for form fields representing collections (Lists, Maps, etc.).
 * Extends FormField to leverage basic field properties and validation,
 * adding collection-specific management and display logic.
 *
 * @param <T> The type of individual items WITHIN the collection (e.g., DiagnosticCode, Map.Entry&lt;Medication, Integer&gt;)
 * @param <C> The type of the Collection itself (e.g., List&lt;DiagnosticCode&gt;, Map&lt;Medication, Integer&gt;)
 */
public abstract class CollectionFormField<T, C> extends FormField<C> {

    protected enum EditMode { Browse, ADDING, REMOVING, EDITING }
    protected EditMode mode = EditMode.Browse;

    /**
     * Constructs a new {@code CollectionFormField} with the specified parameters.
     *
     * @param name           The name of the form field, typically used as an identifier.
     * @param displayName    The name displayed on the user interface for this form field.
     * @param prompt         A prompt or instruction displayed to the user to guide the input.
     * @param validator      A {@link Predicate} used to validate the input entered by the user.
     * @param errorMessage   The error message to display if the input is invalid according to the {@code validator}.
     * @param parser         A {@link FormInputParser} that converts the input string into a suitable object of type {@code C}.
     * @param isRequired     A boolean indicating if the field is mandatory or optional.
     * @param initialValue   The initial value for the form field, of type {@code C}.
     */
    public CollectionFormField(String name, String displayName, String prompt, Predicate<String> validator,
                               String errorMessage, FormInputParser<C> parser, boolean isRequired, C initialValue) {
        super(name, displayName, prompt, validator, errorMessage, parser, isRequired, initialValue);
    }


    /**
     * Adds an item to the collection.
     *
     * @param input The item to be added.
     */
    public abstract void addItem(String input);

    /**
     * Removes an item at the specified index.
     *
     * @param index The index of the item to be removed.
     */
    public abstract void removeItem(int index);

    /**
     * Returns a list of items to be displayed.
     *
     * @return A list of display items as {@link List<String>}.
     */
    public abstract List<String> getDisplayItems();

    /**
     * Gets the current size of the collection.
     * Handles different collection types (List, Map) and null values.
     *
     * @return The number of items in the collection, or 0 if null or empty.
     */
    public int getCollectionSize() {
        C collection = getValue();
        return switch (collection) {
            case List<?> list -> list.size();
            case Map<?, ?> map -> map.size();
            case Collection<?> collection1 -> collection1.size();
            case null, default -> 0;
        };
    }


    /**
     * Gets a formatted display string for the collection field with optional highlighting.
     * The display includes the field name, item count (with color coding),
     * and an indicator for required/optional status.
     *
     * @param isHighlighted true if the field should be highlighted (e.g., when selected)
     * @return The formatted display string with ANSI color codes
     */
    @Override
    public String getDisplayString(boolean isHighlighted) {
        StringBuilder sb = new StringBuilder();

        if (isHighlighted) {
            sb.append(Color.BLUE.getAnsiCode());
        }

        sb.append(getDisplayName());

        int size = getCollectionSize();
        String valueStr;
        Color valueColor = Color.WHITE;
        if (size > 0) {
            valueStr = "[" + size + (size == 1 ? " Item" : " Items") + "]";
        } else {
            valueStr = "[EMPTY]";
            if (isRequired()) {
                valueColor = Color.RED;
            } else {
                valueColor = Color.YELLOW;
            }
        }

        sb.append(": ");
        if (valueColor != Color.WHITE) sb.append(valueColor.getAnsiCode());
        sb.append(valueStr);
        if (valueColor != Color.WHITE) sb.append(Color.ESCAPE.getAnsiCode());

        if (isRequired()) {
            sb.append(" ")
                    .append(Color.UND_RED.getAnsiCode())
                    .append("REQUIRED")
                    .append(Color.ESCAPE.getAnsiCode());
        } else {
            sb.append(" ")
                    .append(Color.UND_ORANGE.getAnsiCode())
                    .append("OPTIONAL")
                    .append(Color.ESCAPE.getAnsiCode());
        }

        if (isHighlighted) {
            sb.append(Color.ESCAPE.getAnsiCode());
        }

        return sb.toString();
    }
}