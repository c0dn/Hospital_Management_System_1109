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
     * <p>The input handling priority is:</p>
     * <ol>
     * <li>FormViews that are awaiting value input</li>
     * <li>Paginated views for pagination-specific keys ('n', 'p', 'j')</li>
     * <li>This view's own direct input handlers</li>
     * <li>All other child views</li>
     * </ol>
     *
     * @param input The input string from the user
     * @return true if the input was handled by this view or any child view, false otherwise
     */
    @Override
    public boolean handleDirectInput(String input) {
        // 1. Priority: FormView awaiting input
        for (View child : childViews) {
            if (child instanceof FormView formView && formView.isAwaitingValue()) {
                formView.processValueInput(input);
                return true;
            }
            if (child instanceof FormView formView &&
                    (formView.currentState == FormView.FormState.COLLECTION_ADDING ||
                            formView.currentState == FormView.FormState.COLLECTION_REMOVING)) {
                if (formView.handleDirectInput(input)) {
                    return true;
                }
            }
        }

        // 2. Priority: Paginated views for pagination keys (n, p, j)
        if (input != null && input.length() == 1) {
            char key = Character.toLowerCase(input.charAt(0));
            if (key == 'n' || key == 'p' || key == 'j') {
                for (View child : childViews) {
                    if (child instanceof PaginatedMenuView paginatedMenu) {
                        if (paginatedMenu.handleDirectInput(input)) {
                            return true;
                        }
                    } else if (child instanceof AbstractPaginatedView<?> paginatedView) {
                        if (paginatedView.handleDirectInput(input)) {
                            return true;
                        }
                    }
                }
            }
        }

        // 3. Priority: This view's own handlers (if any)
        if (super.handleDirectInput(input)) {
            return true;
        }

        // 4. Priority: All other child views (that aren't FormView awaiting input)
        for (View child : childViews) {
            boolean alreadyHandledForm = child instanceof FormView formView &&
                    (formView.isAwaitingValue() ||
                            formView.currentState == FormView.FormState.COLLECTION_ADDING ||
                            formView.currentState == FormView.FormState.COLLECTION_REMOVING);

            boolean isPaginationKey = input != null && input.length() == 1 && "npj".contains(input.toLowerCase());
            boolean alreadyHandledPagination = isPaginationKey && child instanceof PaginatedMenuView;

            if (!alreadyHandledForm && !alreadyHandledPagination) {
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
     * <p>The footer display prioritizes:</p>
     * <ol>
     * <li>FormView fields that are awaiting input</li>
     * <li>MenuView footer if present</li>
     * <li>Default view footer</li>
     * </ol>
     * <p>Ensures "Your input" prompt is always included at the end.</p>
     *
     * @return The footer content with user input options and the input prompt.
     */
    @Override
    public String getFooter() {
        for (View child : childViews) {
            if (child instanceof FormView formView && formView.isAwaitingValue()) {
                FormField<?> field = formView.getSelectedField();
                if (field != null) {
                    String formOptions = "\nOptions:\n | e: Go Back | q: Quit App\n";
                    return formOptions + field.getPrompt() + " ";
                }
            }
            else if (child instanceof FormView formView &&
                    (formView.currentState == FormView.FormState.COLLECTION_ADDING ||
                            formView.currentState == FormView.FormState.COLLECTION_REMOVING ||
                            formView.currentState == FormView.FormState.COLLECTION_BROWSING)) {
                return formView.getFooter();
            }
        }


        MenuView menuViewChild = null;
        for (View child : childViews) {
            if (child instanceof MenuView) {
                menuViewChild = (MenuView) child;
                break;
            }
        }

        String baseFooter;
        if (menuViewChild != null) {
            baseFooter = menuViewChild.getFooter();
            return baseFooter;
        } else {
            baseFooter = super.getFooter();
            return baseFooter + "\nYour input: ";
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

    /**
     * Gets the list of child views contained within this composite view.
     *
     * @return An unmodifiable list of the child views.
     */
    public List<View> getChildViews() {
        return Collections.unmodifiableList(childViews);
    }
}