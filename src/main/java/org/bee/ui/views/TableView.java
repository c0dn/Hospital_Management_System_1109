package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A view that displays data in a formatted table.
 * Supports custom column definitions and data formatting.
 */
public class TableView<T> extends View {


    public static class Column<T> {
        private final String header;
        private final int width;
        private final Function<T, String> valueExtractor;

        /**
         * Creates a new column definition.
         *
         * @param header The column header text
         * @param width The width of the column in characters
         * @param valueExtractor Function to extract the cell value from an item
         */
        public Column(String header, int width, Function<T, String> valueExtractor) {
            this.header = header;
            this.width = width;
            this.valueExtractor = valueExtractor;
        }

        public String getHeader() {
            return header;
        }

        public int getWidth() {
            return width;
        }

        public String extractValue(T item) {
            String value = valueExtractor.apply(item);
            return value != null ? value : "";
        }
    }

    private final List<Column<T>> columns = new ArrayList<>();
    private List<T> data = new ArrayList<>();
    private String emptyMessage = "No data available";
    private boolean showRowNumbers = false;
    private boolean showDivider = true;
    private final int rowNumWidth = 4;

    /**
     * Creates a new TableView.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title for the view
     * @param color The color for the view
     */
    public TableView(Canvas canvas, String titleHeader, Color color) {
        super(canvas, titleHeader, "", color);
    }

    /**
     * Adds a column to the table.
     *
     * @param header The column header text
     * @param width The width of the column in characters
     * @param valueExtractor Function to extract the cell value from an item
     * @return This TableView instance for method chaining
     */
    public TableView<T> addColumn(String header, int width, Function<T, String> valueExtractor) {
        columns.add(new Column<>(header, width, valueExtractor));
        return this;
    }

    /**
     * Sets whether to show row numbers in the table.
     *
     * @param show Whether to show row numbers
     * @return This TableView instance for method chaining
     */
    public TableView<T> showRowNumbers(boolean show) {
        this.showRowNumbers = show;
        return this;
    }

    /**
     * Sets whether to show dividers in the table.
     *
     * @param show Whether to show dividers
     * @return This TableView instance for method chaining
     */
    public TableView<T> showDivider(boolean show) {
        this.showDivider = show;
        return this;
    }

    /**
     * Sets the message to display when there is no data.
     *
     * @param message The message to display
     * @return This TableView instance for method chaining
     */
    public TableView<T> setEmptyMessage(String message) {
        this.emptyMessage = message;
        return this;
    }

    /**
     * Sets the data to display in the table.
     *
     * @param data The list of items to display
     * @return This TableView instance for method chaining
     */
    public TableView<T> setData(List<T> data) {
        this.data = new ArrayList<>(data);
        return this;
    }

    @Override
    public String getText() {
        if (columns.isEmpty()) {
            return "No columns defined";
        }

        if (data.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder table = new StringBuilder();

        if (showRowNumbers) {
            table.append(padRight("#", rowNumWidth)).append(" | ");
        }

        for (int i = 0; i < columns.size(); i++) {
            Column<T> column = columns.get(i);
            table.append(TextStyle.BOLD.getAnsiCode())
                    .append(padRight(column.getHeader(), column.getWidth()))
                    .append(TextStyle.RESET.getAnsiCode());

            if (i < columns.size() - 1) {
                table.append(" | ");
            }
        }

        table.append("\n");

        if (showDivider) {
            if (showRowNumbers) {
                table.append(repeat("-", rowNumWidth)).append("-|-");
            }

            for (int i = 0; i < columns.size(); i++) {
                Column<T> column = columns.get(i);
                table.append(repeat("-", column.getWidth()));

                if (i < columns.size() - 1) {
                    table.append("-|-");
                }
            }

            table.append("\n");
        }

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            T item = data.get(rowIndex);

            if (showRowNumbers) {
                table.append(padRight(String.valueOf(rowIndex + 1), rowNumWidth)).append(" | ");
            }

            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                Column<T> column = columns.get(colIndex);
                String cellValue = column.extractValue(item);

                boolean hasColorCodes = cellValue.contains("\u001b[");

                String formattedValue;
                if (hasColorCodes) {
                    String visibleText = stripAnsiCodes(cellValue);

                    if (visibleText.length() > column.getWidth()) {
                        formattedValue = truncateWithColorCodes(cellValue, column.getWidth());
                    } else {
                        int visibleLength = visibleText.length();
                        int paddingNeeded = column.getWidth() - visibleLength;
                        formattedValue = cellValue + repeat(" ", paddingNeeded);
                    }
                } else {
                    formattedValue = padRight(truncate(cellValue, column.getWidth()), column.getWidth());
                }

                table.append(formattedValue);

                if (colIndex < columns.size() - 1) {
                    table.append(" | ");
                }
            }

            if (rowIndex < data.size() - 1) {
                table.append("\n");
            }
        }

        return table.toString();
    }

    /**
     * Removes ANSI color codes from a string to get its visible length
     */
    private String stripAnsiCodes(String text) {
        return text.replaceAll("\u001b\\[[0-9;]*m", "");
    }

    /**
     * Truncates text that contains ANSI color codes, preserving the codes
     */
    private String truncateWithColorCodes(String text, int maxWidth) {
        StringBuilder result = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();
        StringBuilder visibleText = new StringBuilder();
        boolean inEscapeSequence = false;
        String colorReset = "\u001b[0m";

        List<String> colorCodes = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (inEscapeSequence) {
                currentCode.append(c);
                if (c == 'm') {
                    inEscapeSequence = false;
                    colorCodes.add(currentCode.toString());
                    result.append(currentCode);
                    currentCode = new StringBuilder();
                }
            } else if (c == '\u001b') {
                inEscapeSequence = true;
                currentCode.append(c);
            } else {
                visibleText.append(c);
                result.append(c);

                if (visibleText.length() >= maxWidth - 3) {
                    break;
                }
            }
        }

        if (visibleText.length() < stripAnsiCodes(text).length()) {
            result.append("...");

            if (!colorCodes.isEmpty()) {
                result.append(colorReset);
            }
        }

        return padRight(result.toString(), maxWidth + countAnsiCodeLength(result.toString()));
    }

    /**
     * Counts the total length of ANSI codes in a string
     */
    private int countAnsiCodeLength(String text) {
        int count = 0;
        boolean inEscapeSequence = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (inEscapeSequence) {
                count++;
                if (c == 'm') {
                    inEscapeSequence = false;
                }
            } else if (c == '\u001b') {
                inEscapeSequence = true;
                count++;
            }
        }

        return count;
    }

    private int calculateTotalWidth() {
        int width = 0;

        if (showRowNumbers) {
            width += rowNumWidth + 3;
        }

        for (int i = 0; i < columns.size(); i++) {
            width += columns.get(i).getWidth();
            if (i < columns.size() - 1) {
                width += 3;
            }
        }

        return width;
    }

    /**
     * Pads a string with spaces to the right to ensure it has the specified width.
     */
    private String padRight(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        return s + repeat(" ", width - s.length());
    }

    /**
     * Truncates a string to the specified width, adding "..." if necessary.
     */
    private String truncate(String s, int width) {
        if (s.length() <= width) {
            return s;
        }
        if (width <= 3) {
            return s.substring(0, width);
        }
        return s.substring(0, width - 3) + "...";
    }

    /**
     * Creates a string by repeating a character a specified number of times.
     */
    private String repeat(String s, int count) {
        return String.valueOf(s).repeat(Math.max(0, count));
    }
}