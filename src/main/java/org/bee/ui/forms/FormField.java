package org.bee.ui.forms;

import org.bee.ui.Color;
import java.util.function.Predicate;

/**
 * A form field with validation for building interactive forms.
 * <p>
 * FormField provides a complete representation of a single input field within a form,
 * including validation, parsing, display formatting, and state management. It supports
 * generic typing to handle various data types while maintaining a consistent user interface.
 * </p>
 * <p>
 * Each field manages its own:
 * <ul>
 *   <li>Validation logic</li>
 *   <li>Type conversion</li>
 *   <li>Display formatting (including highlighting and color-coding)</li>
 *   <li>Required/optional state</li>
 *   <li>Error messaging</li>
 * </ul>
 *
 * @param <T> The type that this field will store after parsing raw input
 */
public class FormField<T> {
    private final String name;
    private final String displayName;
    private final String prompt;
    private final Predicate<String> validator;
    private final String errorMessage;
    private final FormInputParser<T> parser;
    private T value;
    private final boolean isRequired;
    private final T initialValue;

    /**
     * Creates a new form field with validation and parsing capabilities.
     *
     * @param name The unique identifier for the field used for data binding
     * @param displayName The human-readable name shown in the UI
     * @param prompt The prompt shown to the user when entering a value
     * @param validator Predicate that tests if raw input is valid
     * @param errorMessage Message shown to user when validation fails
     * @param parser Function that converts valid string input to type T
     * @param isRequired Whether this field must have a non-empty value
     * @param initialValue The initial value of the field
     */
    public FormField(String name, String displayName, String prompt, Predicate<String> validator,
                     String errorMessage, FormInputParser<T> parser, boolean isRequired, T initialValue) {
        this.name = name;
        this.displayName = displayName;
        this.prompt = prompt;
        this.validator = validator;
        this.errorMessage = errorMessage;
        this.parser = parser;
        this.isRequired = isRequired;
        this.initialValue = initialValue;
        this.value = initialValue;
    }

    /**
     * Get the field's identifier name.
     * This name is used for data binding when submitting form data.
     *
     * @return The field's identifier name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the prompt text displayed to guide user input.
     * This prompt is typically shown when the field is actively being edited.
     *
     * @return The prompt text
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Gets the display name for the field.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Validates the provided input string against this field's validation rules.
     * For optional fields, empty input always validates as true.
     *
     * @param input The input string to validate
     * @return true if the input is valid, false otherwise
     */
    public boolean validate(String input) {
        if (!isRequired && (input == null || input.trim().isEmpty())) {
            return true;
        }
        if (input == null) return false;
        return validator.test(input);
    }

    /**
     * Gets the error message to display when validation fails.
     *
     * @return The validation error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the field's value by validating and parsing the input string.
     * If validation fails, the value remains unchanged.
     * For optional fields, empty input sets the value to null.
     *
     * @param input Raw user input string (null allowed for optional fields)
     */
    public void setValue(String input) {
        if (!isRequired && (input == null || input.trim().isEmpty())) {
            this.value = null;
        } else if (validate(input)) {
            try {
                this.value = parser.parse(input);
            } catch (Exception e) {
                System.err.println("WARN: Parser failed after validation passed for field '" + name + "': " + e.getMessage());
            }
        }
    }

    /**
     * Checks if this field requires a non-empty value.
     *
     * @return true if the field is required, false if optional
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Gets the initial value assigned to this field.
     *
     * @return The initial value
     */
    public T getInitialValue() {
        return initialValue;
    }

    /**
     * Gets a formatted display string for the field with optional highlighting.
     * The display includes the field name, current value (with color coding),
     * and an indicator for required/optional status.
     *
     * @param isHighlighted true if the field should be highlighted (e.g., when selected)
     * @return The formatted display string with ANSI color codes
     */
    public String getDisplayString(boolean isHighlighted) {
        StringBuilder sb = new StringBuilder();

        if (isHighlighted) {
            sb.append(Color.BLUE.getAnsiCode());
        }

        sb.append(this.getDisplayName());

        Object currentValue = this.getValue();
        String valueStr;
        Color valueColor = Color.WHITE;

        if (currentValue != null && !(currentValue instanceof String && ((String)currentValue).trim().isEmpty())) {
            valueStr = currentValue.toString();
        } else {
            valueStr = "[EMPTY]";
            if (this.isRequired()) {
                valueColor = Color.RED;
            } else {
                valueColor = Color.YELLOW;
            }
        }

        sb.append(": [");
        if (valueColor != Color.WHITE) sb.append(valueColor.getAnsiCode());
        sb.append(valueStr);
        if (valueColor != Color.WHITE) sb.append(Color.ESCAPE.getAnsiCode());
        sb.append("]");

        if (this.isRequired()) {
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

    /**
     * Gets a formatted display string for the field without highlighting.
     *
     * @return The formatted display string with ANSI color codes
     */
    public String getDisplayString() {
        return getDisplayString(false);
    }

    /**
     * Gets the current value of the field.
     *
     * @return The current parsed value or null if unparsed/optional
     */
    public T getValue() {
        return value;
    }

    /**
     * Interface for parsing user input strings to the desired type.
     * Implementations can throw exceptions for parsing errors, which will be
     * caught by the setValue method.
     *
     * @param <T> The type to parse the input string into
     */
    @FunctionalInterface
    public interface FormInputParser<T> {
        /**
         * Parses a string input into the target type.
         *
         * @param input The input string to parse
         * @return The parsed object of type T
         */
        T parse(String input);
    }
}