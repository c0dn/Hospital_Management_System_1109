package org.bee.ui;

/**
 * Enum that encapsulates ANSI escape codes for text formatting styles.
 * <p>
 * Available text styles:
 * <ul>
 *   <li>RESET - Resets all formatting to default</li>
 *   <li>BOLD - Bold text formatting</li>
 *   <li>ITALIC - Italic text formatting</li>
 *   <li>ESCAPE - Underlined text formatting</li>
 *   <li>NONE - No specific formatting</li>
 * </ul>
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

    /**
     * Retrieves the ANSI escape code for the text style.
     *
     * @return The ANSI escape code associated with the text style.
     */
    public String getAnsiCode() {
        return ansiCode;
    }
}
