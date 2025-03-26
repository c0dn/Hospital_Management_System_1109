package org.bee.ui;

/**
 * Simple enum that contains the color ANSI codes.
 */
public enum Color {
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),
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
