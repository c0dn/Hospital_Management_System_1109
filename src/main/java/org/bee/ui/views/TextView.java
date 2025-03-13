package org.bee.ui.views;


import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

/**
 * Simple view that displays a simple text, Can add a text format as well.
 */
public class TextView extends View {
    public TextStyle textStyle;

    /**
     * Constructor overload which sets textStyle as none (default)
     * @param canvas canvas to render on
     * @param content the content of the textview
     * @param color the color of the textview
     */
    public TextView(Canvas canvas, String content, Color color) {
        super(canvas, content, color);
        this.textStyle = TextStyle.NONE;
    }

    /**
     * Constructor overload which allows you to set a textStyle
     * @param canvas canvas to render on
     * @param content the content of the textview
     * @param color the color of the textview
     * @param textStyle the text style of the textview
     */
    public TextView(Canvas canvas, String content, Color color, TextStyle textStyle) {
        super(canvas, content, color);
        this.textStyle = textStyle;
    }

    /**
     * Textview is a simple view that includes Text styling.
     * @return Returns a printable String with formatting. null if otherwise, please handle the null case gracefully.
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
