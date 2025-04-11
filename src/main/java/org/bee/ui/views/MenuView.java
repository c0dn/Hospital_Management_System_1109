package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A specialized view for displaying structured menus with sections and options.
 * <p>
 * The MenuView provides a hierarchical menu structure with sections containing
 * numbered options and letter-based shortcut actions. It supports different display
 * modes including compact and detailed views.
 * <p>
 * This class extends {@link View} and integrates with the terminal-based UI framework
 * to display interactive menus with proper formatting, styling, and navigation options.
 */
public class MenuView extends View {
    private final List<MenuSection> sections = new ArrayList<>();
    private boolean showCompactFooter;
    private boolean showDetailedOptions;
    private int numericOptionMaxRange = 0;
    private boolean showNumericHintOnly = false; // New flag for special footer
    private final Map<Character, UserInput> letterActions = new TreeMap<>();

    /**
     * Creates a new MenuView with specified display settings.
     *
     * @param canvas The canvas to render the menu on
     * @param title The title for this menu view
     * @param color The color for the menu text
     * @param showDetailedOptions Whether to show the full menu structure with section titles
     * @param showCompactFooter Whether to display a compact footer with all options
     */
    public MenuView(Canvas canvas, String title, Color color, boolean showDetailedOptions, boolean showCompactFooter) {
        super(canvas, title, "", color);
        this.showDetailedOptions = showDetailedOptions;
        this.showCompactFooter = showCompactFooter;
    }

    /**
     * Sets whether to show only numeric hints in the footer.
     * <p>
     * When enabled, the footer will display a hint about available number options
     * instead of listing each numbered option individually.
     *
     * @param show True to show only numeric hints, false for standard footer
     */
    public void setShowNumericHintOnly(boolean show) {
        this.showNumericHintOnly = show;
    }

    /**
     * Sets the maximum range for numeric options.
     * <p>
     * This affects how the numeric option range is displayed in the footer.
     *
     * @param maxRange The highest numeric option available
     */
    public void setNumericOptionMaxRange(int maxRange) {
        this.numericOptionMaxRange = maxRange;
    }


    /**
     * Adds a new section to the menu with the specified title.
     * <p>
     * Sections are used to group related menu options under a header.
     *
     * @param title The title of the section
     * @return The newly created section that can be used to add options
     */
    public MenuSection addSection(String title) {
        MenuSection section = new MenuSection(title);
        sections.add(section);
        return section;
    }

    /**
     * Attaches an action to a letter key for direct keyboard access.
     * <p>
     * Letter options are displayed in the footer and can be activated by
     * pressing the corresponding key.
     *
     * @param letter The letter key to bind the action to
     * @param optionText The descriptive text for the action
     * @param lambda The action to execute when the key is pressed
     */
    public void attachLetterOption(char letter, String optionText, UserInputResult lambda) {
        letterActions.put(Character.toLowerCase(letter), new UserInput(optionText, lambda));
    }

    /**
     * Handles direct letter inputs and pagination controls.
     * <p>
     * This method processes single-character inputs, including pagination controls
     * ('n' for next page, 'p' for previous page, 'j' for jump to page) and any
     * registered letter actions.
     *
     * @param input The input string from the user
     * @return true if the input was handled, false otherwise
     */
    @Override
    public boolean handleDirectInput(String input) {
        if (super.handleDirectInput(input)) {
            return true;
        }

        if (input != null && input.length() == 1) {
            char key = Character.toLowerCase(input.charAt(0));

            // Special case for pagination controls
            if (key == 'n' && isPaginationOption("Next Page")) {
                executeOption("Next Page");
                return true;
            }

            if (key == 'p' && isPaginationOption("Previous Page")) {
                executeOption("Previous Page");
                return true;
            }

            if (key == 'j' && isPaginationOption("Jump to Page")) {
                executeOption("Jump to Page");
                return true;
            }

            if (letterActions.containsKey(key)) {
                UserInput action = letterActions.get(key);
                action.lambda().onInput(input);
                return true;
            }
        }

        return false;
    }

    private boolean isPaginationOption(String optionName) {
        for (Enumeration<Integer> e = inputOptions.keys(); e.hasMoreElements();) {
            Integer key = e.nextElement();
            UserInput option = inputOptions.get(key);
            if (option != null && option.promptText().equalsIgnoreCase(optionName)) {
                return true;
            }
        }
        return false;
    }

    private void executeOption(String optionName) {
        for (Enumeration<Integer> e = inputOptions.keys(); e.hasMoreElements();) {
            Integer key = e.nextElement();
            UserInput option = inputOptions.get(key);
            if (option != null && option.promptText().equalsIgnoreCase(optionName)) {
                try {
                    option.lambda().onInput(String.valueOf(key));
                } catch (Exception ex) {
                    System.out.println("[DEBUG] Exception executing option '" + optionName + "': " + ex.getMessage());
                }
                break;
            }
        }
    }


    /**
     * Returns an unmodifiable map of the letter actions registered with this menu.
     *
     * @return An unmodifiable map of letter keys to their associated UserInput actions
     */
    public Map<Character, UserInput> getLetterActions() {
        return Collections.unmodifiableMap(this.letterActions);
    }

    /**
     * Generates the formatted menu text with sections and options.
     * <p>
     * If detailed options are disabled, returns an empty string.
     *
     * @return The formatted menu text with proper styling and layout
     */

    @Override
    public String getText() {
        if (!showDetailedOptions) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(TextStyle.RESET.getAnsiCode())
                .append(Color.ESCAPE.getAnsiCode());

        boolean firstSection = true;
        for (MenuSection section : sections) {
            if (!section.getOptions().isEmpty() || (section.getTitle() != null && !section.getTitle().isEmpty())) {
                if (!firstSection && section.getTitle() != null && !section.getTitle().isEmpty()) {
                    sb.append("\n");
                }

                if (section.getTitle() != null && !section.getTitle().isEmpty()) {
                    sb.append(TextStyle.BOLD.getAnsiCode())
                            .append(section.getTitle())
                            .append(TextStyle.RESET.getAnsiCode())
                            .append("\n");
                }

                for (MenuOption option : section.getOptions()) {
                    sb.append(option.index())
                            .append(". ")
                            .append(option.text())
                            .append("\n");
                }
                firstSection = false;
            }
        }

        return sb.toString();
    }

    /**
     * Generates the footer text with available options and navigation controls.
     * <p>
     * The footer format depends on the display settings (compact, numeric hints only)
     * and includes letter actions, pagination controls, and standard navigation options.
     *
     * @return The formatted footer text with option descriptions
     */
    @Override
    public String getFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n | e: Go Back");

        if (showNumericHintOnly) {
            if (numericOptionMaxRange > 0) {
                sb.append(" | Select field you wish to update (1-").append(numericOptionMaxRange).append(")");
            }
            if (!letterActions.isEmpty()) {
                for (Map.Entry<Character, UserInput> entry : letterActions.entrySet()) {
                    sb.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue().promptText());
                }
            }
        } else {
            List<Integer> numericKeys = Collections.list(inputOptions.keys()).stream()
                    .filter(key -> key > 0)
                    .sorted()
                    .toList();

            boolean numberedOptionsExist = !numericKeys.isEmpty();
            boolean numberedOptionsDisplayed = showDetailedOptions && sections.stream().anyMatch(s -> !s.getOptions().isEmpty());

            if (numberedOptionsExist) {
                if (showCompactFooter) {
                    for (Integer key : numericKeys) {
                        UserInput input = inputOptions.get(key);
                        if (input != null && input.promptText() != null && !input.promptText().isEmpty()) {
                            sb.append(" | ").append(key).append(": ").append(input.promptText());
                        }
                    }
                } else if (showDetailedOptions && numberedOptionsDisplayed) {
                    sb.append(" | Select option (1-").append(numericOptionMaxRange).append(")");
                } else if (!showDetailedOptions) {
                    sb.append(" | Select option (1-").append(numericOptionMaxRange).append(")");
                }
            }

            if (!letterActions.isEmpty()) {
                for (Map.Entry<Character, UserInput> entry : letterActions.entrySet()) {
                    sb.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue().promptText());
                }
            }
        }

        sb.append(" | q: Quit App\nYour input: ");
        return sb.toString();
    }

    /**
     * Attaches a user input handler to a specific menu option index.
     * <p>
     * This method connects a numeric menu option to its action handler, but only
     * if the option exists in one of the sections or if detailed options are not shown.
     *
     * @param optionIndex The index/number of the option to bind
     * @param optionText The descriptive text for the option
     * @param lambda The action to execute when the option is selected
     */
    public void attachMenuOptionInput(int optionIndex, String optionText, UserInputResult lambda) {
        if (optionIndex <= 0) {
            return;
        }

        boolean foundInSection = false;
        for (MenuSection section : sections) {
            for (MenuOption option : section.getOptions()) {
                if (option.index() == optionIndex) {
                    foundInSection = true;
                    break;
                }
            }
            if (foundInSection) break;
        }

        if (foundInSection || !showDetailedOptions) {
            setUserInputByIndex(optionIndex, optionText, lambda);
        }
    }

    /**
     * Represents a section in the menu with a title and a list of options.
     * <p>
     * Sections are used to group related menu options under a common header.
     */
    public static class MenuSection {
        private final String title;
        private final List<MenuOption> options = new ArrayList<>();
        /**
         * Creates a new menu section with the specified title.
         *
         * @param title The title for this section
         */

        public MenuSection(String title) {
            this.title = title;
        }

        /**
         * Adds a new option to this section.
         *
         * @param index The index/number of the option (used for selection)
         * @param text The text description of the option
         * @return This section for method chaining
         */
        public MenuSection addOption(int index, String text) {
            options.add(new MenuOption(index, text));
            return this;
        }

        /**
         * Returns the title of this section.
         *
         * @return The section title
         */

        public String getTitle() {
            return title;
        }

        /**
         * Returns the list of options in this section.
         *
         * @return The list of menu options
         */

        public List<MenuOption> getOptions() {
            return options;
        }
    }

    /**
     * Represents a menu option with an index number and display text.
     * <p>
     * This is a record class that simply holds the option data.
     *
     * @param index The numeric index of the option
     * @param text The display text of the option
     */
    public record MenuOption(int index, String text) {
    }
}