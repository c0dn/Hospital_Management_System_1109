package org.bee.ui.views;

import org.bee.ui.*;

import java.util.function.Consumer;

/**
 * Abstract base class for paginated views that provides common pagination functionality.
 * <p>
 * This class implements core pagination features including:
 * <ul>
 *   <li>Page navigation (next, previous, jump to page)</li>
 *   <li>Page calculation based on number of items</li>
 *   <li>Navigation controls in footer</li>
 *   <li>Selection callback handling</li>
 * </ul>
 * </p>
 * <p>
 * Concrete subclasses must implement methods to provide content-specific options
 * and retrieve the items for the current page.
 * </p>
 *
 * @param <T> The type of items being paginated
 */
public abstract class AbstractPaginatedView<T> extends View {
    protected final int itemsPerPage;
    protected int currentPage = 0;
    protected final int totalPages;
    protected Consumer<MenuOption> selectionCallback;

    /**
     * Creates a new abstract paginated view.
     * <p>
     * Initializes the view with pagination settings and calculates the total number
     * of pages based on the provided items count and page size.
     * </p>
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title for the view
     * @param itemsPerPage Number of items to display per page
     * @param totalItems Total number of items
     * @param color The color for the view
     */
    public AbstractPaginatedView(Canvas canvas, String titleHeader, int itemsPerPage, int totalItems, Color color) {
        super(canvas, titleHeader, "", color);
        this.itemsPerPage = itemsPerPage;
        this.totalPages = calculateTotalPages(totalItems);
    }

    /**
     * Initializes navigation options after subclass setup is complete.
     * <p>
     * This method must be called by subclasses after their specific initialization
     * to set up the navigation controls properly.
     * </p>
     */
    protected void initializeNavigation() {
        setupNavigation();
    }

    /**
     * Represents a menu option with an ID, display text, and optional associated data.
     * <p>
     * This class is used to create selectable options in paginated menus and
     * can carry additional data associated with each option.
     * </p>
     */
    public static class MenuOption {
        private final String id;
        private final String text;
        private final Object data;

        /**
         * Creates a menu option with an ID and text.
         *
         * @param id The unique identifier for this option
         * @param text The display text for this option
         */
        public MenuOption(String id, String text) {
            this(id, text, null);
        }

        /**
         * Creates a menu option with an ID, text, and associated data.
         *
         * @param id The unique identifier for this option
         * @param text The display text for this option
         * @param data Optional associated data object
         */
        public MenuOption(String id, String text, Object data) {
            this.id = id;
            this.text = text;
            this.data = data;
        }

        /**
         * Gets the unique identifier for this menu option.
         *
         * @return The option's ID
         */
        public String getId() {
            return id;
        }

        /**
         * Gets the display text for this menu option.
         *
         * @return The option's display text
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the data object associated with this menu option.
         *
         * @return The associated data object, or null if none
         */
        public Object getData() {
            return data;
        }
    }

    /**
     * Sets a callback to be invoked when a menu option is selected.
     * <p>
     * After setting the callback, navigation options are refreshed to ensure
     * proper handling of selection events.
     * </p>
     *
     * @param callback The consumer function that will handle selected menu options
     */
    public void setSelectionCallback(Consumer<MenuOption> callback) {
        this.selectionCallback = callback;
        setupNavigation();
    }

    /**
     * Calculates the total number of pages needed based on item count.
     * <p>
     * This method uses ceiling division to ensure even a partial page of items
     * gets its own page. Returns at least 1 page even for empty collections.
     * </p>
     *
     * @param totalItems The total number of items to paginate
     * @return The number of pages needed, minimum of 1
     */
    protected int calculateTotalPages(int totalItems) {
        if (totalItems <= 0) return 1;
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    /**
     * Sets up the navigation options for the paginated view.
     * <p>
     * This method clears existing user inputs, then configures both
     * content-specific options and pagination navigation controls.
     * </p>
     */
    protected void setupNavigation() {
        clearUserInputs();
        setupContentOptions();
        setupNavigationOptions();
    }

    /**
     * Sets up pagination navigation options (next, previous, jump).
     * <p>
     * This method adds appropriate navigation controls based on the current page
     * and total number of pages, including:
     * <ul>
     *   <li>"n" for next page (if not on the last page)</li>
     *   <li>"p" for previous page (if not on the first page)</li>
     *   <li>"j" for jump to page (if more than 3 pages)</li>
     * </ul>
     * </p>
     */
    protected void setupNavigationOptions() {
        if (totalPages > 1) {
            if (currentPage < totalPages - 1) {
                attachUserInput("Next Page", input -> {
                    if (input.equalsIgnoreCase("n")) {
                        nextPage();
                        canvas.setRequireRedraw(true);
                    }
                });
            }

            if (currentPage > 0) {
                attachUserInput("Previous Page", input -> {
                    if (input.equalsIgnoreCase("p")) {
                        previousPage();
                        canvas.setRequireRedraw(true);
                    }
                });
            }

            if (totalPages > 3) {
                attachUserInput("Jump to Page", input -> {
                    if (input.equalsIgnoreCase("j")) {
                        promptForPageJump();
                    }
                });
            }
        }
    }

    /**
     * Prompts the user to enter a page number for direct navigation.
     * <p>
     * Displays an input prompt, validates the entered page number,
     * and jumps to the requested page if valid. Shows error messages
     * for invalid input.
     * </p>
     */
    protected void promptForPageJump() {
        canvas.drawText("\nEnter page number (1-" + totalPages + "): ", Color.CYAN);
        String response = canvas.getTerminal().getUserInput();
        try {
            int page = Integer.parseInt(response) - 1; // Convert to 0-based index
            if (page >= 0 && page < totalPages) {
                jumpToPage(page);
                canvas.setRequireRedraw(true);
            } else {
                canvas.setSystemMessage("Invalid page number", SystemMessageStatus.ERROR);
                canvas.setRequireRedraw(true);
            }
        } catch (NumberFormatException e) {
            canvas.setSystemMessage("Invalid input", SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }

    /**
     * Sets up content-specific options for the current page.
     * <p>
     * This abstract method must be implemented by subclasses to add
     * options specific to the content being displayed.
     * </p>
     */
    protected abstract void setupContentOptions();

    /**
     * Gets the items for the current page.
     * <p>
     * This abstract method must be implemented by subclasses to provide
     * the appropriate subset of items for the current page.
     * </p>
     *
     * @return An iterable of items for the current page
     */
    protected abstract Iterable<T> getCurrentPageItems();

    /**
     * Navigates to the next page if available.
     * <p>
     * Increments the current page counter and refreshes navigation options.
     * Does nothing if already on the last page.
     * </p>
     */
    public void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            setupNavigation();
        }
    }

    /**
     * Navigates to the previous page if available.
     * <p>
     * Decrements the current page counter and refreshes navigation options.
     * Does nothing if already on the first page.
     * </p>
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            setupNavigation();
        }
    }

    /**
     * Jumps to a specific page.
     * <p>
     * Sets the current page to the specified index and refreshes navigation options.
     * Only takes effect if the provided page index is valid.
     * </p>
     *
     * @param page The 0-based page index to jump to
     */
    public void jumpToPage(int page) {
        if (page >= 0 && page < totalPages) {
            currentPage = page;
            setupNavigation();
        }
    }

    /**
     * Gets the pagination information text.
     * <p>
     * Returns a formatted string showing the current page number and total pages
     * if there is more than one page, or an empty string otherwise.
     * </p>
     *
     * @return A formatted string with pagination information or empty string
     */
    protected String getPaginationInfo() {
        if (totalPages > 1) {
            return TextStyle.BOLD.getAnsiCode() +
                    "Page " + (currentPage + 1) + " of " + totalPages +
                    TextStyle.RESET.getAnsiCode();
        } else {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overrides the base getFooter method to provide pagination controls
     * in addition to standard options.
     * </p>
     */
    @Override
    public String getFooter() {
        StringBuilder sb = new StringBuilder("\nOptions:\n | e: Go Back");

        if (totalPages > 1) {
            if (currentPage > 0) {
                sb.append(" | p: Previous Page");
            }
            if (currentPage < totalPages - 1) {
                sb.append(" | n: Next Page");
            }
            if (totalPages > 3) {
                sb.append(" | j: Jump to Page");
            }
        }

        addCustomFooterOptions(sb);

        sb.append(" | q: Quit App\nYour input: ");
        return sb.toString();
    }

    /**
     * Allows subclasses to add their custom footer options.
     * <p>
     * This method can be overridden by subclasses to add additional
     * options to the footer display.
     * </p>
     *
     * @param sb The StringBuilder containing the footer options
     */
    protected void addCustomFooterOptions(StringBuilder sb) {}
}