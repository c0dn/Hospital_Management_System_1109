package org.bee.ui.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A specific implementation of CollectionFormField for managing a List of items.
 *
 * @param <T> The type of elements stored in the list.
 */
public class ListFormField<T> extends CollectionFormField<T, List<T>> {

    // Parses a string input into an element of type T
    private final FormInputParser<T> elementParser;

    /**
     * Constructs a ListFormField.
     *
     * @param name          The internal name of the field.
     * @param displayName   The name displayed to the user.
     * @param prompt        The prompt shown when adding an item.
     * @param validator     A predicate to validate the raw string input *before* parsing an item (can be simple like notEmpty).
     * @param errorMessage  The error message for the validator.
     * @param elementParser The parser to convert string input into an item of type T.
     * @param isRequired    Whether the list itself must contain at least one item upon submission.
     * @param initialValue  The initial list of items.
     */
    public ListFormField(String name, String displayName, String prompt, Predicate<String> validator,
                         String errorMessage, FormInputParser<T> elementParser, boolean isRequired, List<T> initialValue) {
        super(name, displayName, prompt, validator, errorMessage, input -> initialValue, isRequired,
                initialValue != null ? new ArrayList<>(initialValue) : new ArrayList<>());
        this.elementParser = elementParser;
        // Ensure the initial value held by FormField is mutable
        if (getValue() == null) {
            setValueInternal(new ArrayList<>());
        }
    }

    /**
     * Parses the input string using the elementParser and adds the resulting item to the list.
     *
     * @param input The string representation of the item to add.
     * @throws IllegalArgumentException if the elementParser fails.
     */
    @Override
    public void addItem(String input) {
        if (getValue() == null) {
            setValueInternal(new ArrayList<>());
        }
        try {
            T item = elementParser.parse(input);
            getValue().add(item);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid format for item: " + e.getMessage(), e);
        }
    }

    /**
     * Removes the item at the specified index from the list.
     *
     * @param index The 0-based index of the item to remove.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    @Override
    public void removeItem(int index) {
        List<T> list = getValue();
        if (list != null && index >= 0 && index < list.size()) {
            list.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " for list size: " + (list == null ? 0 : list.size()));
        }
    }

    /**
     * Gets a list of string representations of the items currently in the list.
     * Uses the toString() method of each item.
     *
     * @return A list of strings for display.
     */
    @Override
    public List<String> getDisplayItems() {
        List<T> list = getValue();
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(item -> item != null ? item.toString() : "[null item]")
                .collect(Collectors.toList());
    }

    /**
     * Helper method to set the internal value managed by FormField.
     * Necessary because FormField.setValue(String) expects string input.
     *
     * @param value The list value to set.
     */
    private void setValueInternal(List<T> value) {
        try {
            java.lang.reflect.Field valueField = FormField.class.getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Reflection failed in ListFormField.setValueInternal: " + e.getMessage());
            // Handle error appropriately, maybe throw new RuntimeException(e);
        }
    }
}