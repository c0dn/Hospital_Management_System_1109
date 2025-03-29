package org.bee.ui;

/**
 * Simple enum that contains the color ANSI codes.
 */
public enum Color {
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    UND_RED("\u001b[4;31m"),
    GREEN("\u001b[32m"),
    UND_GREEN("\u001b[4;32m"),
    YELLOW("\u001b[33m"),
    UND_YELLOW("\u001b[4;33m"),
    ORANGE("\u001b[38;5;208m"),
    UND_ORANGE("\u001b[4;38;5;208m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),
    LAVENDER("\u001b[38;5;183m"),
    BRIGHT_WHITE("\u001b[97m"),
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