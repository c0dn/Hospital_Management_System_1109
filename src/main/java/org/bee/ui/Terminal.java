package org.bee.ui;

import java.util.Scanner;

/**
 * A generic class for terminal access that provides low-level operations for console I/O.
 * <p>
 * This class should not be accessed manually, but only through the Canvas class which provides
 * higher-level abstractions for UI operations.
 * <p>
 * The Terminal provides methods for text manipulation, cursor positioning, and user input through the console.
 */
public class Terminal {
    /**
     * Scanner object used to read user input from the terminal.
     */
    Scanner scanner = new Scanner(System.in);

    /**
     * Sets the text color for subsequent terminal output.
     * <p>
     * Applies the ANSI color code associated with the specified color.
     * </p>
     *
     * @param color The color to apply to subsequent text output
     */
    protected void setTextColor(Color color) {
        System.out.print(color.getAnsiCode());
    }

    /**
     * Resets the terminal text color to the default.
     * <p>
     * Uses the ANSI reset code to return terminal text to its default color.
     * </p>
     */
    protected void resetColor() {
        System.out.print("\u001b[0m"); // ANSI code to reset
    }

    /**
     * Clears the terminal screen.
     * <p>
     * Uses ANSI escape sequences to clear the entire screen and reset cursor position.
     * </p>
     */
    protected void clearScreen() {
        System.out.print("\u001b[2J"); // ANSI code to clear screen
        System.out.print("\u001b[H"); // Move cursor to top-left
    }

    /**
     * Positions the cursor at the specified coordinates.
     * <p>
     * Uses ANSI escape sequences to move the cursor to the given x,y position.
     * Note that the position is 0-based, but ANSI positions are 1-based,
     * so this method applies the appropriate adjustment.
     * </p>
     *
     * @param x The x-coordinate (horizontal, 0-based)
     * @param y The y-coordinate (vertical, 0-based)
     */
    protected void setCursorPosition(int x, int y) {
        System.out.printf("\u001b[%d;%dH", y + 1, x + 1); // ANSI code to move cursor
    }

    /**
     * Writes text to the terminal without a line break.
     *
     * @param text The text to write to the terminal
     */
    protected void write(String text) {
        System.out.print(text);
    }

    /**
     * Writes text to the terminal followed by a line break.
     *
     * @param text The text to write to the terminal
     */
    protected void writeln(String text) {
        System.out.println(text);
    }

    /**
     * Flushes the terminal output stream.
     * <p>
     * Ensures that all buffered output is immediately written to the terminal.
     * </p>
     */
    protected void flush(){
        System.out.flush();
    }

    /**
     * Reads and returns the user input as a string.
     * If an error occurs during input reading, {@code null} is returned.
     *
     * @return The user input as a string, or {@code null} if an error occurs.
     */
    public String getUserInput() {
        try {
            return scanner.nextLine();
        }catch(Exception e) {
            return null;
        }
    }
}
