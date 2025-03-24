package org.bee.ui.forms;

import java.util.function.Predicate;

/**
 * A form field with validation for building interactive forms.
 */
public class FormField<T> {
    private String name;
    private String prompt;
    private Predicate<String> validator;
    private String errorMessage;
    private FormInputParser<T> parser;
    private T value;

    public FormField(String name, String prompt, Predicate<String> validator,
                     String errorMessage, FormInputParser<T> parser) {
        this.name = name;
        this.prompt = prompt;
        this.validator = validator;
        this.errorMessage = errorMessage;
        this.parser = parser;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public boolean validate(String input) {
        return validator.test(input);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setValue(String input) {
        this.value = parser.parse(input);
    }

    public T getValue() {
        return value;
    }

    /**
     * Interface for parsing user input to the desired type.
     */
    public interface FormInputParser<T> {
        T parse(String input);
    }
}
