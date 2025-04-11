package org.bee.ui.forms;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A specific implementation of CollectionFormField for managing a Map of key-value pairs.
 *
 * @param <K> The type of the keys in the map.
 * @param <V> The type of the values in the map.
 */
public class MapFormField<K, V> extends CollectionFormField<Map.Entry<K, V>, Map<K, V>> {
    private final FormInputParser<K> keyParser;
    private final FormInputParser<V> valueParser;
    private final String entrySeparator;
    
    

    /**
     * Constructs a MapFormField.
     *
     * @param name           The internal name of the field.
     * @param displayName    The name displayed to the user.
     * @param prompt         The prompt shown when adding an item (should mention format).
     * @param validator      A predicate to validate the raw string input *before* parsing (e.g., check for separator).
     * @param errorMessage   The error message for the validator.
     * @param keyParser      The parser to convert the key part of the string input into type K.
     * @param valueParser    The parser to convert the value part of the string input into type V.
     * @param isRequired     Whether the map itself must contain at least one entry upon submission.
     * @param initialValue   The initial map of entries.
     */
    public MapFormField(String name, String displayName, String prompt,
                        Predicate<String> validator, String errorMessage,
                        FormInputParser<K> keyParser, FormInputParser<V> valueParser,
                        boolean isRequired, Map<K, V> initialValue) {
        super(name, displayName, prompt, validator, errorMessage,
                input -> new HashMap<>(), // Dummy parser, we'll override addItem
                isRequired, initialValue);
        this.keyParser = keyParser;
        this.valueParser = valueParser;
        this.entrySeparator = ":";
    }

    /**
     * Constructs a {@code MapFormField} with the specified parameters and a custom entry separator.
     *
     * @param name            The name of the form field.
     * @param displayName     The name displayed to the user for this form field.
     * @param prompt          A prompt or instruction displayed to the user.
     * @param validator       A {@link Predicate} to validate the user input.
     * @param errorMessage    The error message to show if the input is invalid.
     * @param keyParser       A {@link FormInputParser} to parse the key of the map.
     * @param valueParser     A {@link FormInputParser} to parse the value of the map.
     * @param isRequired      A boolean indicating if the field is required.
     * @param initialValue    The initial value for the map field.
     * @param entrySeparator  The separator used to divide map entries in the input.
     */
    // Constructor with custom separator
    public MapFormField(String name, String displayName, String prompt,
                        Predicate<String> validator, String errorMessage,
                        FormInputParser<K> keyParser, FormInputParser<V> valueParser,
                        boolean isRequired, Map<K, V> initialValue,
                        String entrySeparator) {
        super(name, displayName, prompt, validator, errorMessage,
                input -> new HashMap<>(),
                isRequired, initialValue);
        this.keyParser = keyParser;
        this.valueParser = valueParser;
        this.entrySeparator = entrySeparator;
    }

    /**
     * Parses the input string and adds/updates the entry in the map.
     *
     * @param input The string representation of the key-value pair to add.
     * @throws IllegalArgumentException if the input format is invalid or parsing fails.
     */
    @Override
    public void addItem(String input) {
        try {
            String[] parts = input.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Input must be in format 'key:value'");
            }

            K key = keyParser.parse(parts[0].trim());
            V value = valueParser.parse(parts[1].trim());

            getValue().put(key, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error adding item: " + e.getMessage());
        }
    }
    /**
     * Removes the item corresponding to the specified display index from the map.
     * Since Maps aren't directly indexed, this relies on the order provided by getDisplayItems.
     *
     * @param index The 0-based display index of the item to remove.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    @Override
    public void removeItem(int index) {
        Map<K, V> map = getValue();
        if (map != null && index >= 0 && index < map.size()) {
            K keyToRemove = new ArrayList<>(map.keySet()).get(index);
            map.remove(keyToRemove);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " for map size: " + (map == null ? 0 : map.size()));
        }
    }

    /**
     * Gets a list of string representations of the key-value pairs currently in the map.
     * Iteration order is preserved due to using LinkedHashMap internally.
     *
     * @return A list of strings for display (e.g., ["Key1: Value1", "Key2: Value2"]).
     */
    @Override
    public List<String> getDisplayItems() {
        Map<K, V> map = getValue();
        if (map == null || map.isEmpty()) {
            return Collections.emptyList();
        }
        return map.entrySet().stream()
                .map(entry -> (entry.getKey() != null ? entry.getKey().toString() : "[null key]")
                        + entrySeparator + " "
                        + (entry.getValue() != null ? entry.getValue().toString() : "[null value]"))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to set the internal value managed by FormField.
     * Necessary because FormField.setValue(String) expects string input.
     * @param value The map value to set.
     */
    private void setValueInternal(Map<K, V> value) {
        try {
            java.lang.reflect.Field valueField = FormField.class.getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Reflection failed in MapFormField.setValueInternal: " + e.getMessage());
        }
    }
}