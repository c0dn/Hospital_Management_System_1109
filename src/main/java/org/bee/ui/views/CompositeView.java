package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;

import java.util.*;

/**
 * A view that combines multiple views, displaying them in sequence and
 * merging their options.
 */
public class CompositeView extends View {
    private final List<View> childViews = new ArrayList<>();
    private boolean useCompactFooter = true;
    private String separator = "\n\n";

    /**
     * Creates a new CompositeView.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title header
     * @param color The color for the view
     */
    public CompositeView(Canvas canvas, String titleHeader, Color color) {
        super(canvas, titleHeader, "", color);
    }

    /**
     * Adds a child view to this composite view.
     *
     * @param view The view to add
     * @return This CompositeView for method chaining
     */
    public CompositeView addView(View view) {
        childViews.add(view);
        mergeUserInputs(view);
        return this;
    }

    /**
     * Sets whether to use a compact footer style (like MenuView) or a standard footer.
     *
     * @param useCompactFooter True to use compact footer, false for standard
     * @return This CompositeView for method chaining
     */
    public CompositeView useCompactFooter(boolean useCompactFooter) {
        this.useCompactFooter = useCompactFooter;
        return this;
    }

    /**
     * Sets the separator to use between child views.
     *
     * @param separator The separator string
     * @return This CompositeView for method chaining
     */
    public CompositeView setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Adds user input options from the given view to this view.
     *
     * @param view The view to get options from
     */
    private void mergeUserInputs(View view) {
        Dictionary<Integer, UserInput> viewInputs = view.getInputOptions();
        if (viewInputs != null) {
//            System.out.println("[DEBUG mergeUserInputs] Merging options from: " + view.getClass().getSimpleName());
//            System.out.println("[DEBUG mergeUserInputs] Child keys: " + Collections.list(viewInputs.keys()));
//            System.out.println("[DEBUG mergeUserInputs] Composite keys BEFORE: " + Collections.list(this.inputOptions.keys()));

            Enumeration<Integer> keys = viewInputs.keys();
            while (keys.hasMoreElements()) {
                Integer key = keys.nextElement();
                UserInput input = viewInputs.get(key);

                if (key == 0) {
                    continue;
                }

                int assignedKey = key;
                int originalKey = key;
                while (this.inputOptions.get(assignedKey) != null) {
//                    System.out.println("[DEBUG mergeUserInputs] Clash for key " + originalKey + ". Trying key " + (assignedKey + 1));
                    assignedKey++;
                }

                if (assignedKey != originalKey) {
//                    System.out.println("[DEBUG mergeUserInputs] Remapped key " + originalKey + " to " + assignedKey);
                }

                this.inputOptions.put(assignedKey, input);
            }
//            System.out.println("[DEBUG mergeUserInputs] Composite keys AFTER: " + Collections.list(this.inputOptions.keys()));
        }
    }

    /**
     * Handle direct input by checking all child views.
     * This allows letter shortcuts from any child view to work.
     *
     * @param input The input string
     * @return true if any child view handled the input, false otherwise
     */
    @Override
    public boolean handleDirectInput(String input) {
        if (super.handleDirectInput(input)) {
            return true;
        }

        for (View child : childViews) {
            if (child.handleDirectInput(input)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getText() {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < childViews.size(); i++) {
            View view = childViews.get(i);

            String viewContent = view.getText();
            if (viewContent != null && !viewContent.isEmpty()) {
                content.append(viewContent);

                if (i < childViews.size() - 1) {
                    content.append(separator);
                }
            }
        }

        return content.toString();
    }

    @Override
    public String getFooter() {
        StringBuilder sb;
        if (!useCompactFooter) {
            sb = new StringBuilder(super.getFooter());

            boolean optionsExist = !sb.toString().trim().equals("Options:");
            String separator = optionsExist ? " | " : " ";

            Map<Character, String> mergedLetterPrompts = new TreeMap<>(); // Use TreeMap for sorted letters
            for (View child : childViews) {
                if (child instanceof MenuView menuChild) {
                    Map<Character, UserInput> childLetterActions = menuChild.getLetterActions();
                    if (childLetterActions != null) {
                        for (Map.Entry<Character, UserInput> entry : childLetterActions.entrySet()) {
                            char letterKey = Character.toLowerCase(entry.getKey());
                            if (!mergedLetterPrompts.containsKey(letterKey)) {
                                mergedLetterPrompts.put(letterKey, entry.getValue().promptText());
                            }
                        }
                    }
                }
            }
            for (Map.Entry<Character, String> entry : mergedLetterPrompts.entrySet()) {
                sb.append(separator).append(entry.getKey()).append(": ").append(entry.getValue());
                separator = " | ";
            }

            sb.append(separator).append("e: Go Back");
            separator = " | ";

            sb.append(separator).append("q: Quit App");

            sb.append("\nYour input: ");

        } else {
            sb = new StringBuilder();
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
                    sb.append(" | ").append(key).append(": ").append(input.promptText());
                }
            }

            Map<Character, String> mergedLetterPrompts = new TreeMap<>();
            for (View child : childViews) {
                if (child instanceof MenuView menuChild) { // Use pattern matching
                    Map<Character, UserInput> childLetterActions = menuChild.getLetterActions();
                    if (childLetterActions != null) {
                        for (Map.Entry<Character, UserInput> entry : childLetterActions.entrySet()) {
                            char letterKey = Character.toLowerCase(entry.getKey());
                            if (!mergedLetterPrompts.containsKey(letterKey)) {
                                mergedLetterPrompts.put(letterKey, entry.getValue().promptText());
                            }
                        }
                    }
                }
            }
            for (Map.Entry<Character, String> entry : mergedLetterPrompts.entrySet()) {
                sb.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue());
            }

            sb.append(" | q: Quit App\nYour input: ");
        }
        return sb.toString();
    }

    @Override
    public void attachUserInput(String option, UserInputResult lambda) {
        super.attachUserInput(option, lambda);
    }

    @Override
    public Dictionary<Integer, UserInput> getInputOptions() {
        return super.getInputOptions();
    }
}