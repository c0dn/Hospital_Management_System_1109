package org.bee.ui;

/**
 * Simple enum that contains the text style ANSI codes.
 */
public enum TextStyle {
    RESET("\u001b[0m"),
    BOLD("\u001b[1m"),          // Bold
    ITALIC("\u001b[3m"),
    ESCAPE("\u001b[4m"),
    NONE("");        // Italic

    private final String ansiCode;

    TextStyle(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public String getAnsiCode() {
        return ansiCode;
    }
}
