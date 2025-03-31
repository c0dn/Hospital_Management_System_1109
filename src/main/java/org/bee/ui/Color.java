package org.bee.ui;

/**
 * Enum that encapsulates ANSI color codes for text coloring in the console.
 * <p>
 * This enum provides a set of predefined colors for UI elements, including standard colors,
 * underlined variants, and special formatting options like ESCAPE for resetting colors.
 */
public enum Color {
    /**
     * Represents the color black in the console.
     */
    BLACK("\u001b[30m"),

    /**
     * Represents the color red in the console.
     */
    RED("\u001b[31m"),

    /**
     * Represents the underlined red color in the console.
     */
    UND_RED("\u001b[4;31m"),

    /**
     * Represents the color green in the console.
     */
    GREEN("\u001b[32m"),

    /**
     * Represents the underlined green color in the console.
     */
    UND_GREEN("\u001b[4;32m"),

    /**
     * Represents the color yellow in the console.
     */
    YELLOW("\u001b[33m"),

    /**
     * Represents the underlined yellow color in the console.
     */
    UND_YELLOW("\u001b[4;33m"),

    /**
     * Represents the color orange in the console.
     */
    ORANGE("\u001b[38;5;208m"),

    /**
     * Represents the underlined orange color in the console.
     */
    UND_ORANGE("\u001b[4;38;5;208m"),

    /**
     * Represents the color blue in the console.
     */
    BLUE("\u001b[34m"),

    /**
     * Represents the color magenta in the console.
     */
    MAGENTA("\u001b[35m"),

    /**
     * Represents the color cyan in the console.
     */
    CYAN("\u001b[36m"),

    /**
     * Represents the color white in the console.
     */
    WHITE("\u001b[37m"),

    /**
     * Represents the color lavender in the console.
     */
    LAVENDER("\u001b[38;5;183m"),

    /**
     * Represents the bright white color in the console.
     */
    BRIGHT_WHITE("\u001b[97m"),

    /**
     * Represents the escape sequence for resetting all text formatting in the console.
     */
    ESCAPE("\u001b[0m");

    private final String ansiCode;

    Color(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    /**
     * Gets the ansi code for the color
     * @return returns the ansi code
     */
    public String getAnsiCode() {
        return ansiCode;
    }
}
