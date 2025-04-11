package org.bee.ui.views;


import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

/**
 * A simple view component that displays text with optional styling and coloring.
 * <p>
 * TextView is the most basic display component in the UI framework, designed to render
 * single-block text content with configurable visual formatting. It extends the base
 * {@link View} class and enhances it with text styling capabilities through the
 * {@link TextStyle} enum.
 * <p>
 * This view is often used for:
 * <ul>
 *   <li>Displaying headings and labels</li>
 *   <li>Showing messages, notifications, and informational text</li>
 *   <li>Rendering plain text content with consistent styling</li>
 *   <li>Building blocks within {@link CompositeView} components</li>
 * </ul>
 * <p>
 * The class handles ANSI escape sequences automatically to apply colors and text styles
 * (bold, italic, etc.) in terminal output.
 *
 * @see View
 * @see TextStyle
 * @see Color
 * @see CompositeView
 */
public class TextView extends View {
    /**
     * The {@link TextStyle} applied to the text.
     * This field determines how the text is styled, such as bold, italic, etc.
     */
    public TextStyle textStyle;

    /**
     * Creates a TextView with default text style (NONE).
     * <p>
     * This constructor initializes a simple text view with the specified content
     * and color, but without any additional text styling.
     *
     * @param canvas Canvas to render on
     * @param content The text content to display
     * @param color The color to apply to the text
     */
    public TextView(Canvas canvas, String content, Color color) {
        super(canvas, content, color);
        this.textStyle = TextStyle.NONE;
    }

    /**
     * Creates a TextView with custom text style.
     * <p>
     * This constructor allows specifying both color and text style (bold, italic, etc.)
     * to be applied to the content text.
     *
     * @param canvas Canvas to render on
     * @param content The text content to display
     * @param color The color to apply to the text
     * @param textStyle The text style to apply (bold, italic, etc.)
     */
    public TextView(Canvas canvas, String content, Color color, TextStyle textStyle) {
        super(canvas, content, color);
        this.textStyle = textStyle;
    }

    /**
     * Generates the formatted text content with proper styling.
     * <p>
     * This override applies the configured text style and color to the content
     * using ANSI escape sequences. If the text style is set to NONE, it delegates
     * to the parent class implementation.
     * <p>
     * The method preserves null handling from the parent class implementation.
     *
     * @return The formatted text with appropriate ANSI styling codes, or null if the base text is null
     */
    @Override
    public String getText() {
        if(textStyle == TextStyle.NONE){
            return super.getText();
        }
        if(super.getText() == null) return null;
        return Color.ESCAPE.getAnsiCode() + textStyle.getAnsiCode() + color.getAnsiCode() + text + TextStyle.RESET.getAnsiCode() + Color.ESCAPE.getAnsiCode();
    }
}
