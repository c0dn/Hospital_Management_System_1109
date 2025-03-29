package org.bee.ui.views;

import org.bee.ui.*;
import org.bee.ui.forms.FormField;

import java.util.*;

/**
 * A view that combines multiple views, displaying them in sequence and
 * merging their options.
 */
public class CompositeView extends View {
    private final List<View> childViews = new ArrayList<>();
    private String separator = "\n";

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
                    assignedKey++;
                }

                this.inputOptions.put(assignedKey, input);
            }
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
        // First check if any FormView is awaiting input, as it has priority
        for (View child : childViews) {
            if (child instanceof FormView formView && formView.isAwaitingValue()) {
                formView.processValueInput(input);
                return true;
            }
        }

        if (super.handleDirectInput(input)) {
            return true;
        }

        for (View child : childViews) {
            if (!(child instanceof FormView formView && formView.isAwaitingValue())) {
                if (child.handleDirectInput(input)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getText() {
        StringBuilder content = new StringBuilder();

        content.append(TextStyle.RESET.getAnsiCode())
                .append(Color.ESCAPE.getAnsiCode())
                .append('\n');

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
        for (View child : childViews) {
            if (child instanceof FormView formView && formView.isAwaitingValue()) {
                FormField<?> field = formView.getSelectedField();
                if (field != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\nOptions:\n | e: Go Back");

                    if (formView.getInputOptions() != null && !Collections.list(formView.getInputOptions().keys()).isEmpty()) {
                        sb.append(" | Select field (1-").append(Collections.list(formView.getInputOptions().keys()).size()).append(")");
                    }

                    sb.append(" | s: Submit Changes | u: Update Selected Field | q: Quit App\n");
                    sb.append(field.getPrompt()).append(" ");

                    return sb.toString();
                }
            }
        }

        MenuView menuViewChild = null;
        for (View child : childViews) {
            if (child instanceof MenuView) {
                menuViewChild = (MenuView) child;
                break;
            }
        }

        if (menuViewChild != null) {
            return menuViewChild.getFooter();
        } else {
            return super.getFooter();
        }
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