package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;
import org.bee.ui.View;

import java.util.*;

/**
 * A specialized view for displaying menus with sections and options.
 * Provides a structured format for displaying grouped menu options with headers.
 */
public class MenuView extends View {
    private final List<MenuSection> sections = new ArrayList<>();
    private boolean showCompactFooter;
    private boolean showDetailedOptions;
    private final Map<Character, UserInput> letterActions = new HashMap<>();

    public MenuView(Canvas canvas, String title, Color color, boolean showDetailedOptions, boolean showCompactFooter) {
        super(canvas, title, "", color);
        this.showDetailedOptions = showDetailedOptions;
        this.showCompactFooter = showCompactFooter;
    }

    /**
     * Control whether to show the detailed options in the menu body
     */
    public void setShowDetailedOptions(boolean show) {
        this.showDetailedOptions = show;
    }

    /**
     * Control whether to show the compact options in the footer
     */
    public void setShowCompactFooter(boolean show) {
        this.showCompactFooter = show;
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
     */
    @Override
    public boolean handleDirectInput(String input) {
        if (input != null && input.length() == 1) {
            char key = Character.toLowerCase(input.charAt(0));
            if (letterActions.containsKey(key)) {
                UserInput action = letterActions.get(key);
                action.lambda().onInput(input);
                return true;
            }
        }
        return false;
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

        for (int i = 0; i < sections.size(); i++) {
            MenuSection section = sections.get(i);

            if (i > 0) {
                sb.append("\n");
            }

            if (section.getTitle() != null && !section.getTitle().isEmpty()) {
                sb.append(TextStyle.BOLD.getAnsiCode())
                        .append(section.getTitle())
                        .append(TextStyle.RESET.getAnsiCode())
                        .append("\n");
            }

            for (MenuOption option : section.getOptions()) {
                sb.append(option.getIndex())
                        .append(". ")
                        .append(option.getText())
                        .append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String getFooter() {
        if (showDetailedOptions && !showCompactFooter) {
            int maxOption = 0;
            for (MenuSection section : sections) {
                for (MenuOption option : section.getOptions()) {
                    if (option.getIndex() > maxOption) {
                        maxOption = option.getIndex();
                    }
                }
            }

            StringBuilder sb = new StringBuilder("\ne: Go Back");

            if (!letterActions.isEmpty()) {
                for (Map.Entry<Character, UserInput> entry : letterActions.entrySet()) {
                    sb.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue().promptText());
                }
            }

            sb.append(" | q: Quit App\nSelect option (1-").append(maxOption).append("): ");
            return sb.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n | e: Go Back");

        List<Integer> keys = new ArrayList<>();
        for (Enumeration<Integer> e = inputOptions.keys(); e.hasMoreElements();) {
            Integer key = e.nextElement();
            if (key > 0) {
                keys.add(key);
            }
        }

        Collections.sort(keys);

        for (Integer key : keys) {
            UserInput input = inputOptions.get(key);
            if (input != null) {
                sb.append(" | ");
                sb.append(key);
                sb.append(": ");
                sb.append(input.promptText());
            }
        }

        if (!letterActions.isEmpty()) {
            for (Map.Entry<Character, UserInput> entry : letterActions.entrySet()) {
                sb.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue().promptText());
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
            // Don't override the back button
            return;
        }

        boolean found = false;
        for (MenuSection section : sections) {
            for (MenuOption option : section.getOptions()) {
                if (option.getIndex() == optionIndex) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        setUserInputByIndex(optionIndex, optionText, lambda);
    }

    /**
     * Represents a section in the menu with a title and list of options.
     */
    public static class MenuSection {
        private String title;
        private List<MenuOption> options = new ArrayList<>();

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

    /**
     * Represents a single menu option with an index and text.
     */
    public static class MenuOption {
        private final int index;
        private final String text;

        public MenuOption(int index, String text) {
            this.index = index;
            this.text = text;
        }

        public int getIndex() {
            return index;
        }

        public String getText() {
            return text;
        }
    }
}