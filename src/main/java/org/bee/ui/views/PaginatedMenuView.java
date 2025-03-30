package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialized view for displaying paginated menus with keyboard navigation controls.
 * <p>
 * This view extends {@link AbstractPaginatedView} to provide a menu-specific implementation
 * that displays a list of selectable options across multiple pages. It offers intuitive
 * keyboard navigation through pagination controls:
 * <ul>
 *   <li>'n' - navigate to the next page</li>
 *   <li>'p' - navigate to the previous page</li>
 *   <li>'j' - jump to a specific page</li>
 *   <li>numeric inputs - select the corresponding menu option</li>
 * </ul>
 * <p>
 * The view integrates with the terminal-based UI framework to provide consistent styling,
 * navigation feedback, and selection handling. It is commonly used for displaying lists of
 * items that require pagination, such as patients, appointments, or other domain objects.
 *
 * @see AbstractPaginatedView
 * @see MenuView
 * @see org.bee.ui.Canvas
 */
public class PaginatedMenuView extends AbstractPaginatedView<AbstractPaginatedView.MenuOption> {
    private final List<MenuOption> options;
    private final String sectionTitle;

    /**
     * Creates a new paginated menu view with the specified options and display settings.
     * <p>
     * This constructor initializes the view with the provided options and configures
     * pagination based on the specified number of items per page.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title header for the view
     * @param sectionTitle The title for the menu section (displayed above options)
     * @param options The list of menu options to paginate
     * @param itemsPerPage Number of options to display per page
     * @param color The color for the view's text
     */
    public PaginatedMenuView(Canvas canvas, String titleHeader, String sectionTitle,
                             List<MenuOption> options, int itemsPerPage, Color color) {
        super(canvas, titleHeader, itemsPerPage, options.size(), color);
        this.options = new ArrayList<>(options);
        this.sectionTitle = sectionTitle;
        initializeNavigation();
    }
    /**
     * Sets up input handlers for each option on the current page.
     * <p>
     * This method creates numeric input handlers for selecting menu options,
     * with indices adjusted for the current page position.
     */
    @Override
    protected void setupContentOptions() {
        List<MenuOption> currentPageOptions = getCurrentPageItems();
        for (int i = 0; i < currentPageOptions.size(); i++) {
            final int index = i;
            int displayIndex = (currentPage * itemsPerPage) + i + 1;

            setUserInputByIndex(displayIndex, String.valueOf(displayIndex), input -> {
                if (selectionCallback != null) {
                    selectionCallback.accept(currentPageOptions.get(index));
                }
            });
        }
    }

    /**
     * Attaches a custom option to the view that doesn't interfere with pagination controls.
     * <p>
     * Custom options are assigned indices starting from 100 to avoid conflicts with
     * pagination controls and menu selection options.
     *
     * @param optionName The display name of the custom option
     * @param lambda The action to perform when the option is selected
     */
    public void attachCustomOption(String optionName, org.bee.ui.views.UserInputResult lambda) {
        int nextIndex = 100;
        for (java.util.Enumeration<Integer> e = inputOptions.keys(); e.hasMoreElements();) {
            Integer key = e.nextElement();
            if (key > 0 && key < 100) {
                nextIndex = Math.max(nextIndex, key + 1);
            }
        }

        inputOptions.put(nextIndex, new org.bee.ui.views.UserInput(optionName, lambda));

        setupNavigation();
    }
    /**
     * Retrieves the menu options for the current page.
     * <p>
     * This method calculates the appropriate slice of options to display
     * based on the current page number and items per page setting.
     *
     * @return A list of menu options for the current page
     */
    @Override
    protected List<MenuOption> getCurrentPageItems() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, options.size());

        if (options.isEmpty()) {
            return List.of();
        }

        return options.subList(startIndex, endIndex);
    }
    /**
     * Adds a hint about numeric selection to the footer.
     * <p>
     * This method appends a user instruction for selecting options by number
     * to the standard footer content.
     *
     * @param sb The StringBuilder to append the custom footer content to
     */
    @Override
    protected void addCustomFooterOptions(StringBuilder sb) {
        sb.append(" | Enter a number to select an option");
    }
    /**
     * Generates the formatted text content for the paginated menu.
     * <p>
     * The output includes pagination information, section title (if present),
     * and numbered menu options for the current page.
     *
     * @return The formatted text representation of the menu
     */

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        String paginationInfo = getPaginationInfo();
        if (!paginationInfo.isEmpty()) {
            sb.append(paginationInfo).append("\n\n");
        }

        if (sectionTitle != null && !sectionTitle.isEmpty()) {
            sb.append(TextStyle.BOLD.getAnsiCode())
                    .append(sectionTitle)
                    .append(TextStyle.RESET.getAnsiCode())
                    .append("\n");
        }

        List<MenuOption> currentPageOptions = getCurrentPageItems();
        for (int i = 0; i < currentPageOptions.size(); i++) {
            MenuOption option = currentPageOptions.get(i);
            int displayIndex = (currentPage * itemsPerPage) + i + 1;
            sb.append(displayIndex).append(". ").append(option.getText()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Navigates to the next page and refreshes navigation options.
     * <p>
     * This override ensures that input handlers are properly updated
     * when the page changes, and triggers a redraw of the view.
     */
    @Override
    public void nextPage() {
        super.nextPage();
        setupNavigation();
        canvas.setRequireRedraw(true);
    }

    /**
     * Navigates to the previous page and refreshes navigation options.
     * <p>
     * This override ensures that input handlers are properly updated
     * when the page changes, and triggers a redraw of the view.
     */
    @Override
    public void previousPage() {
        super.previousPage();
        setupNavigation();
        canvas.setRequireRedraw(true);
    }

    /**
     * Jumps to a specific page and refreshes navigation options.
     * <p>
     * This override ensures that input handlers are properly updated
     * when the page changes, and triggers a redraw of the view.
     *
     * @param page The zero-based page index to jump to
     */
    @Override
    public void jumpToPage(int page) {
        super.jumpToPage(page);
        setupNavigation();
        canvas.setRequireRedraw(true);
    }
}