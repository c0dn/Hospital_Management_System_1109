package org.bee.ui.views;

/**
 * Represents a user input option within the BEE UI framework.
 *
 * <p>This record pairs a display text shown to users with a callback function that executes
 * when the option is selected. UserInput objects are stored in a View's inputOptions dictionary
 * and are typically displayed in the footer section of the UI.</p>
 *
 * <p>The Canvas mainLoop processes these inputs when users enter corresponding numeric
 * indices or letter shortcuts in the terminal interface.</p>
 *
 * <p>UserInput instances are primarily created through methods like:</p>
 * <ul>
 *   <li>{@link org.bee.ui.View#attachUserInput(String, UserInputResult)}</li>
 *   <li>{@link org.bee.ui.View#setUserInputByIndex(int, String, UserInputResult)}</li>
 *   <li>{@link org.bee.ui.views.MenuView#attachLetterOption(char, String, UserInputResult)}</li>
 * </ul>
 *
 * @param promptText The user-friendly option text displayed in menus and footers
 * @param lambda The callback function executed when this option is selected
 *
 * @see org.bee.ui.views.UserInputResult
 * @see org.bee.ui.views.MenuView
 * @see org.bee.ui.Canvas#mainLoop()
 */
public record UserInput(String promptText, UserInputResult lambda) {
}