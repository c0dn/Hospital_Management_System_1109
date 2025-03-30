package org.bee.ui.views;

import org.bee.ui.*;
import org.bee.ui.forms.FormField;

import java.util.*;

/**
 * A composite view that combines multiple child views into a single unified display.
 * <p>
 * CompositeView allows the creation of complex UI layouts by combining multiple
 * view components that will be rendered sequentially. The composite automatically
 * merges user input options from all child views and handles input delegation.
 * </p>
 * <p>
 * This class implements the Composite design pattern for the UI component hierarchy,
 * enabling the construction of nested view structures while maintaining a consistent
 * input handling mechanism.
 * </p>
 */
public class CompositeView extends View {
    private final List<View> childViews = new ArrayList<>();
    private String separator = "\n";

    /**
     * Creates a new CompositeView with the specified title and color.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title header displayed at the top of the view
     * @param color The color for the view's text
     */
    public CompositeView(Canvas canvas, String titleHeader, Color color) {
        super(canvas, titleHeader, "", color);
    }

    /**
     * Adds a child view to this composite view and merges its input options.
     * <p>
     * Each added view will be rendered in sequence, separated by the configured separator.
     * Input options from the child view are automatically merged into this composite.
     * </p>
     *
     * @param view The view to add as a child
     * @return This CompositeView for method chaining
     */
    public CompositeView addView(View view) {
        childViews.add(view);
        mergeUserInputs(view);
        return this;
    }

    /**
     * Sets the separator string to use between child views.
     * <p>
     * The default separator is a newline character ("\n").
     * </p>
     *
     * @param separator The separator string
     * @return This CompositeView for method chaining
     */
    public CompositeView setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Merges user input options from the given view into this composite view.
     * <p>
     * This method ensures that input option keys remain unique by reassigning
     * keys if conflicts are detected. The back button (key 0) is never merged.
     * </p>
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
                    continue; // Never merge the back button
                }

                int assignedKey = key;
                while (this.inputOptions.get(assignedKey) != null) {
                    assignedKey++; // Find next available key
                }

                this.inputOptions.put(assignedKey, input);
            }
        }
    }

    /**
     * Handles direct input by delegating to child views in priority order.
     * <p>
     * The input handling priority is:
     * <ol>
     *   <li>FormViews that are awaiting value input</li>
     *   <li>This view's own direct input handlers</li>
     *   <li>All other child views</li>
     * </ol>
     * </p>
     *
     * @param input The input string from the user
     * @return true if the input was handled by this view or any child view, false otherwise
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

        // Try handling with this view's own input handlers
        if (super.handleDirectInput(input)) {
            return true;
        }

        // Try all other child views that aren't awaiting form input
        for (View child : childViews) {
            if (!(child instanceof FormView formView && formView.isAwaitingValue())) {
                if (child.handleDirectInput(input)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Generates the combined text content of all child views.
     * <p>
     * The content from each child view is concatenated with the configured separator
     * between views. Empty child content is skipped.
     * </p>
     *
     * @return The combined text content of all child views
     */
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

    /**
     * Generates the footer content with appropriate context-sensitive options.
     * <p>
     * The footer display prioritizes:
     * <ol>
     *   <li>FormView fields that are awaiting input</li>
     *   <li>MenuView footer if present</li>
     *   <li>Default view footer</li>
     * </ol>
     * </p>
     *
     * @return The footer content with user input options
     */
    @Override
    public String getFooter() {
        // Check for form views awaiting input first
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

        // If no form field is active, try to find a MenuView to provide footer
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

    /**
     * {@inheritDoc}
     * <p>
     * Attaches a user input option to this composite view.
     * </p>
     */
    @Override
    public void attachUserInput(String option, UserInputResult lambda) {
        super.attachUserInput(option, lambda);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns all input options for this composite view, including merged options
     * from child views.
     * </p>
     */
    @Override
    public Dictionary<Integer, UserInput> getInputOptions() {
        return super.getInputOptions();
    }
}