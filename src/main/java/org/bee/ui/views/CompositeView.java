package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

/**
 * A view that combines multiple views together, displaying them in sequence and
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
            Enumeration<Integer> keys = viewInputs.keys();
            while (keys.hasMoreElements()) {
                Integer key = keys.nextElement();
                UserInput input = viewInputs.get(key);

                if (key == 0) {
                    continue;
                }

                int assignedKey = key;
                while (this.inputOptions.get(assignedKey) != null) {
                    assignedKey++;
                }

                this.inputOptions.put(assignedKey, input);
            }
        }
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
        if (!useCompactFooter) {
            return super.getFooter();
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

        java.util.Collections.sort(keys);

        for (Integer key : keys) {
            UserInput input = inputOptions.get(key);
            if (input != null) {
                sb.append(" | ");
                sb.append(key);
                sb.append(": ");
                sb.append(input.promptText());
            }
        }

        sb.append(" | q: Quit App\nYour input: ");
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