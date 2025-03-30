package org.bee.ui.forms;

import java.util.function.Predicate;

/**
 * Factory class providing common validation predicates and type parsers for form handling.
 * <p>
 * This utility class offers a collection of reusable validators and parsers that can be
 * used when creating {@link FormField} instances. The validators are implemented as
 * {@link Predicate} objects that can be combined to create complex validation rules.
 * </p>
 * <p>
 * The class also provides type-specific parser functions that convert validated string inputs
 * into their appropriate data types (String, Integer, Double, Boolean).
 * </p>
 */
public class FormValidators {

    /**
     * Creates a validator that checks if a string is not null or empty.
     *
     * @return A predicate that returns true if the string has content after trimming
     */
    public static Predicate<String> notEmpty() {
        return s -> s != null && !s.trim().isEmpty();
    }

    /**
     * Creates a validator that checks if a string contains only digits.
     *
     * @return A predicate that returns true if the string consists only of numeric digits
     */
    public static Predicate<String> numeric() {
        return s -> s.matches("^\\d+$");
    }

    /**
     * Creates a validator that checks if a string is a valid decimal number.
     * Accepts whole numbers and decimal numbers with optional decimal point.
     *
     * @return A predicate that returns true if the string is a valid decimal number
     */
    public static Predicate<String> decimal() {
        return s -> s.matches("^\\d+(\\.\\d+)?$");
    }

    /**
     * Creates a validator that checks if a string is a valid email address.
     * Uses a simple pattern matching approach for basic email validation.
     *
     * @return A predicate that returns true if the string appears to be an email address
     */
    public static Predicate<String> email() {
        return s -> s.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Creates a validator that checks if a string matches a provided regex pattern.
     *
     * @param regex The regular expression pattern to match against
     * @return A predicate that returns true if the string matches the pattern
     */
    public static Predicate<String> matches(String regex) {
        return s -> s.matches(regex);
    }

    /**
     * Creates a validator that checks if a string meets a minimum length requirement.
     *
     * @param min The minimum acceptable length
     * @return A predicate that returns true if the string length is at least the minimum
     */
    public static Predicate<String> minLength(int min) {
        return s -> s.length() >= min;
    }

    /**
     * Creates a validator that checks if a string doesn't exceed a maximum length.
     *
     * @param max The maximum acceptable length
     * @return A predicate that returns true if the string length doesn't exceed the maximum
     */
    public static Predicate<String> maxLength(int max) {
        return s -> s.length() <= max;
    }

    /**
     * Combines multiple validators into a single validator requiring all to pass.
     * This allows for building complex validation rules that check multiple conditions.
     *
     * @param validators The validators to combine with logical AND
     * @return A predicate that returns true only if all component validators return true
     */
    @SafeVarargs
    public static Predicate<String> combine(Predicate<String>... validators) {
        Predicate<String> result = s -> true;
        for (Predicate<String> validator : validators) {
            result = result.and(validator);
        }
        return result;
    }

    /**
     * Creates a parser that returns the input string unchanged.
     *
     * @return A parser function that accepts and returns a string
     */
    public static FormField.FormInputParser<String> stringParser() {
        return s -> s;
    }

    /**
     * Creates a parser that converts a string to an Integer.
     *
     * @return A parser function that converts a string to an Integer
     * @throws NumberFormatException if the string cannot be parsed as an integer
     */
    public static FormField.FormInputParser<Integer> intParser() {
        return Integer::parseInt;
    }

    /**
     * Creates a parser that converts a string to a Double.
     *
     * @return A parser function that converts a string to a Double
     * @throws NumberFormatException if the string cannot be parsed as a double
     */
    public static FormField.FormInputParser<Double> doubleParser() {
        return Double::parseDouble;
    }

    /**
     * Creates a parser that converts a string to a Boolean.
     * Recognizes "true", "yes", and "1" (case-insensitive) as true values.
     *
     * @return A parser function that converts a string to a Boolean
     */
    public static FormField.FormInputParser<Boolean> booleanParser() {
        return s -> s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equals("1");
    }
}