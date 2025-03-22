package org.bee.ui;

import java.util.Scanner;

/**
 * A generic class for terminal access, should not be accessed manually, only through the Canvas class.
 */
public class Terminal {
    Scanner scanner = new Scanner(System.in);
    // In the real implementation, use ANSI escape codes:
    // Example: "\u001b[31m" for red text, "\u001b[0m" to reset
    protected void setTextColor(Color color) {
        System.out.print(color.getAnsiCode());
    }

    protected void resetColor() {
        System.out.print("\u001b[0m"); // ANSI code to reset
    }

    protected void clearScreen() {
        System.out.print("\u001b[2J"); // ANSI code to clear screen
        System.out.print("\u001b[H"); // Move cursor to top-left
    }

    protected void setCursorPosition(int x, int y) {
        System.out.printf("\u001b[%d;%dH", y + 1, x + 1); // ANSI code to move cursor
    }

    protected void write(String text) {
        System.out.print(text);
    }

    protected void writeln(String text) {
        System.out.println(text);
    }

    protected void flush(){
        System.out.flush();
    }

    public String getUserInput() {
        try {
            return scanner.nextLine();
        }catch(Exception e) {
            return null;
        }
    }
}
