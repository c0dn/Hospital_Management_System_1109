package org.bee.ui.forms;

import org.bee.ui.Color;
import java.util.function.Predicate;

/**
 * A form field with validation for building interactive forms.
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

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean validate(String input) {
        if (!isRequired && (input == null || input.trim().isEmpty())) {
            return true;
        }
        if (input == null) return false;
        return validator.test(input);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

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


    public boolean isRequired() {
        return isRequired;
    }

    public T getInitialValue() {
        return initialValue;
    }

    /**
     * Gets the display string for the field, optionally highlighting it.
     * @param isHighlighted true if the field should be highlighted (e.g., selected)
     * @return The formatted display string.
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

    public String getDisplayString() {
        return getDisplayString(false);
    }


    public T getValue() {
        return value;
    }

    /**
     * Interface for parsing user input to the desired type.
     */
    @FunctionalInterface
    public interface FormInputParser<T> {
        T parse(String input);
    }
}