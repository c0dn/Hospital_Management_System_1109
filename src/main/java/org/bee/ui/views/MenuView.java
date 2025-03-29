package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A specialized view for displaying menus with sections and options.
 * Provides a structured format for displaying grouped menu options with headers.
 */
public class MenuView extends View {
    private final List<MenuSection> sections = new ArrayList<>();
    private boolean showCompactFooter;
    private boolean showDetailedOptions;
    private int numericOptionMaxRange = 0;
    private boolean showNumericHintOnly = false; // New flag for special footer
    private final Map<Character, UserInput> letterActions = new TreeMap<>();

    public MenuView(Canvas canvas, String title, Color color, boolean showDetailedOptions, boolean showCompactFooter) {
        super(canvas, title, "", color);
        this.showDetailedOptions = showDetailedOptions;
        this.showCompactFooter = showCompactFooter;
    }

    public void setShowNumericHintOnly(boolean show) {
        this.showNumericHintOnly = show;
    }

    public void setNumericOptionMaxRange(int maxRange) {
        this.numericOptionMaxRange = maxRange;
    }


    /**
     * Adds a new section to the menu with a header.
     *
     * @param title The title of the section
     * @return The newly created section
     */
    public MenuSection addSection(String title) {
        MenuSection section = new MenuSection(title);
        sections.add(section);
        return section;
    }

    /**
     * Attach an action to a letter key
     *
     * @param letter The letter key to bind the action to
     * @param optionText The text description of the action
     * @param lambda The action to execute when the key is pressed
     */
    public void attachLetterOption(char letter, String optionText, UserInputResult lambda) {
        letterActions.put(Character.toLowerCase(letter), new UserInput(optionText, lambda));
    }

    /**
     * Handle direct letter inputs
     * This method processes any non-numeric input (single letters or multi-character strings)
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



    public Map<Character, UserInput> getLetterActions() {
        return Collections.unmodifiableMap(this.letterActions);
    }

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
     * Attach a user input to a specific menu option index
     * This automatically connects the display index with the input handler
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
     * Represents a section in the menu with a title and list of options.
     */
    public static class MenuSection {
        private final String title;
        private final List<MenuOption> options = new ArrayList<>();

        public MenuSection(String title) {
            this.title = title;
        }

        /**
         * Adds a new option to this section.
         *
         * @param index The index/number of the option
         * @param text The text for the option
         * @return This section for method chaining
         */
        public MenuSection addOption(int index, String text) {
            options.add(new MenuOption(index, text));
            return this;
        }

        public String getTitle() {
            return title;
        }

        public List<MenuOption> getOptions() {
            return options;
        }
    }

    public record MenuOption(int index, String text) {
    }
}