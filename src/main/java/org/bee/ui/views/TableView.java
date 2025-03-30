package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A view that displays data in a formatted table with customizable columns and styling.
 * <p>
 * TableView provides a flexible way to display tabular data in the terminal UI, with
 * support for:
 * <ul>
 *   <li>Custom column definitions with fixed widths</li>
 *   <li>Dynamic data extraction from any object type</li>
 *   <li>Row selection with callback integration</li>
 *   <li>ANSI color and style preservation</li>
 *   <li>Automatic text truncation and padding</li>
 *   <li>Optional row numbers and header dividers</li>
 * </ul>
 * <p>
 * This view is commonly used within {@link PaginatedView} to display lists of domain
 * objects like patients, appointments, bills, etc. It integrates with the broader UI
 * framework to provide consistent styling and navigation.
 *
 * @param <T> The type of data items to be displayed in the table rows
 *
 * @see View
 * @see PaginatedView
 * @see org.bee.ui.Canvas
 */
public class TableView<T> extends View {
    private Consumer<BiConsumer<Integer, T>> selectionCallback;

    /**
     * Defines a column in the table with header text, width, and a value extractor function.
     * <p>
     * Each column is responsible for extracting and formatting a specific piece of data
     * from the row objects.
     *
     * @param <T> The type of data item the column extracts values from
     */
    public static class Column<T> {
        private final String header;
        private final int width;
        private final Function<T, String> valueExtractor;

        /**
         * Creates a new column definition with fixed width and value extraction.
         *
         * @param header The column header text displayed at the top of the table
         * @param width The fixed width of the column in characters
         * @param valueExtractor Function to extract and format cell values from data items
         */
        public Column(String header, int width, Function<T, String> valueExtractor) {
            this.header = header;
            this.width = width;
            this.valueExtractor = valueExtractor;
        }
        /**
         * Gets the header text for this column.
         *
         * @return The column header text
         */
        public String getHeader() {
            return header;
        }
        /**
         * Gets the configured width for this column.
         *
         * @return The column width in characters
         */
        public int getWidth() {
            return width;
        }
        /**
         * Extracts a formatted value from an item for display in this column.
         * <p>
         * If the extracted value is null, an empty string is returned.
         *
         * @param item The data item to extract a value from
         * @return The formatted string value for the cell, never null
         */
        public String extractValue(T item) {
            String value = valueExtractor.apply(item);
            return value != null ? value : "";
        }
    }
    /**
     * Sets a callback to be invoked when a row is selected.
     * <p>
     * This method both registers the callback and sets up the input handler to process
     * numeric inputs for row selection. The callback receives both the row index and
     * the row's data item.
     * <p>
     * This is primarily used by {@link PaginatedView} to handle row selection in paginated tables.
     *
     * @param callback The function to call when a row is selected, receiving the row index and data item
     */
    public void setSelectionCallback(BiConsumer<Integer, T> callback) {
        this.selectionCallback = (consumer) -> {
            // This allows the PaginatedView to set up its own callback
            consumer = callback;
        };

        // Example of how you might handle input for row selection
        attachUserInput("Select row", input -> {
            try {
                int row = Integer.parseInt(input) - 1;
                if (row >= 0 && row < data.size()) {
                    callback.accept(row, data.get(row));
                }
            } catch (NumberFormatException e) {
                // Handle invalid input
            }
        });
    }

    private final List<Column<T>> columns = new ArrayList<>();
    private List<T> data = new ArrayList<>();
    private String emptyMessage = "No data available";
    private boolean showRowNumbers = false;
    private boolean showDivider = true;
    private final int rowNumWidth = 4;

    /**
     * Creates a new empty TableView.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title for the table view
     * @param color The color for the table text
     */
    public TableView(Canvas canvas, String titleHeader, Color color) {
        super(canvas, titleHeader, "", color);
    }

    /**
     * Adds a column to the table with the specified properties.
     * <p>
     * This method creates and adds a column definition with custom header text,
     * width, and value extraction function.
     *
     * @param header The column header text
     * @param width The width of the column in characters
     * @param valueExtractor Function to extract cell values from data items
     * @return This TableView instance for method chaining
     */
    public TableView<T> addColumn(String header, int width, Function<T, String> valueExtractor) {
        columns.add(new Column<>(header, width, valueExtractor));
        return this;
    }

    /**
     * Sets whether to show row numbers in the leftmost column.
     * <p>
     * When enabled, each row is prefixed with its sequential number starting from 1.
     *
     * @param show True to show row numbers, false to hide them
     * @return This TableView instance for method chaining
     */
    public TableView<T> showRowNumbers(boolean show) {
        this.showRowNumbers = show;
        return this;
    }

    /**
     * Sets whether to show a divider line between the header and data rows.
     *
     * @param show True to show the divider, false to hide it
     * @return This TableView instance for method chaining
     */
    public TableView<T> showDivider(boolean show) {
        this.showDivider = show;
        return this;
    }

    /**
     * Sets the message to display when the table has no data.
     *
     * @param message The message to display when data is empty
     * @return This TableView instance for method chaining
     */
    public TableView<T> setEmptyMessage(String message) {
        this.emptyMessage = message;
        return this;
    }

    /**
     * Sets the data items to display in the table rows.
     * <p>
     * The data is copied to prevent external modifications affecting the display.
     *
     * @param data The list of items to display in the table
     * @return This TableView instance for method chaining
     */
    public TableView<T> setData(List<T> data) {
        this.data = new ArrayList<>(data);
        return this;
    }
    /**
     * Generates the formatted text representation of the table.
     * <p>
     * The output includes headers, column dividers, row numbers (if enabled),
     * and properly formatted and truncated cell values with ANSI color preservation.
     * <p>
     * If there is no data, the configured empty message is returned.
     *
     * @return The formatted table text ready for display
     */
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
     * Removes ANSI color codes from a string to determine its visible length.
     * <p>
     * This is important for proper truncation and padding calculations, as ANSI
     * color codes don't take up visible space in the terminal.
     *
     * @param text The text containing ANSI color codes
     * @return The text with all ANSI codes removed
     */
    private String stripAnsiCodes(String text) {
        return text.replaceAll("\u001b\\[[0-9;]*m", "");
    }

    /**
     * Truncates text containing ANSI color codes while preserving the codes.
     * <p>
     * This ensures that colored text is properly truncated at the visible character
     * limit without breaking color formatting.
     *
     * @param text The text containing ANSI color codes
     * @param maxWidth The maximum visible width in characters
     * @return The truncated text with color codes preserved
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
     * Counts the total length of ANSI escape sequences in a string.
     * <p>
     * This is used for calculating padding adjustments to account for invisible
     * color codes in the output.
     *
     * @param text The text containing ANSI color codes
     * @return The total number of characters in all ANSI sequences
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
    /**
     * Calculates the total display width of the table.
     * <p>
     * This includes all column widths plus dividers and row numbers if enabled.
     *
     * @return The total width of the table in characters
     */
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
     *
     * @param s The string to pad
     * @param width The desired width in characters
     * @return The padded string
     */
    private String padRight(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        return s + repeat(" ", width - s.length());
    }

    /**
     * Truncates a string to the specified width, adding "..." if necessary.
     *
     * @param s The string to truncate
     * @param width The maximum width in characters
     * @return The truncated string
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
     *
     * @param s The string to repeat
     * @param count The number of times to repeat
     * @return The repeated string
     */
    private String repeat(String s, int count) {
        return String.valueOf(s).repeat(Math.max(0, count));
    }
}