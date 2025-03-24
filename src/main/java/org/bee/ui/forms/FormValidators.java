package org.bee.ui.forms;

import java.util.function.Predicate;

/**
 * Factory class with common validators and parsers.
 */
public class FormValidators {
    public static Predicate<String> notEmpty() {
        return s -> s != null && !s.trim().isEmpty();
    }

    public static Predicate<String> numeric() {
        return s -> s.matches("^\\d+$");
    }

    public static Predicate<String> decimal() {
        return s -> s.matches("^\\d+(\\.\\d+)?$");
    }

    public static Predicate<String> email() {
        return s -> s.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static Predicate<String> matches(String regex) {
        return s -> s.matches(regex);
    }

    public static Predicate<String> minLength(int min) {
        return s -> s.length() >= min;
    }

    public static Predicate<String> maxLength(int max) {
        return s -> s.length() <= max;
    }

    public static Predicate<String> combine(Predicate<String>... validators) {
        Predicate<String> result = s -> true;
        for (Predicate<String> validator : validators) {
            result = result.and(validator);
        }
        return result;
    }

    public static FormField.FormInputParser<String> stringParser() {
        return s -> s;
    }

    public static FormField.FormInputParser<Integer> intParser() {
        return Integer::parseInt;
    }

    public static FormField.FormInputParser<Double> doubleParser() {
        return Double::parseDouble;
    }

    public static FormField.FormInputParser<Boolean> booleanParser() {
        return s -> s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equals("1");
    }
}