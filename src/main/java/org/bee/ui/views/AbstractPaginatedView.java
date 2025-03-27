package org.bee.ui.views;

import org.bee.ui.*;

import java.util.function.Consumer;

/**
 * Abstract base class for paginated views that provides common pagination functionality.
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
     * Must be called by subclasses after their initialization is complete
     */
    protected void initializeNavigation() {
        setupNavigation();
    }

    /**
     * Represents a menu option with an ID and text.
     */
    public static class MenuOption {
        private final String id;
        private final String text;
        private final Object data;

        private int selectedRowNumber = -1; // Track selected row

        public MenuOption(String id, String text) {
            this(id, text, null);
        }

        public MenuOption(String id, String text, Object data) {
            this.id = id;
            this.text = text;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public Object getData() {
            return data;
        }
        public int getRowNumber() {
            return selectedRowNumber;
        }

    }


    /**
     * Set a callback to be invoked when an option is selected
     */
    public void setSelectionCallback(Consumer<MenuOption> callback) {
        this.selectionCallback = callback;
        setupNavigation();
    }

    /**
     * Calculate the total number of pages needed
     */
    protected int calculateTotalPages(int totalItems) {
        if (totalItems <= 0) return 1;
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    /**
     * Sets up the navigation options for the paginated view
     */
    protected void setupNavigation() {
        clearUserInputs();

        setupContentOptions();

        setupNavigationOptions();
    }

    /**
     * Set up pagination navigation options (next, previous, jump)
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
     * Prompt the user to enter a page number for jumping
     */
    protected void promptForPageJump() {
        canvas.drawText("\nEnter page number (1-" + totalPages + "): ", Color.CYAN);
        String response = canvas.getTerminal().getUserInput();
        try {
            int page = Integer.parseInt(response) - 1;
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
     * Setup content-specific options - implemented by subclasses
     */
    protected abstract void setupContentOptions();

    /**
     * Get the items for the current page - implemented by subclasses
     */
    protected abstract Iterable<T> getCurrentPageItems();

    /**
     * Navigate to the next page if available
     */
    public void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            setupNavigation();
        }
    }

    /**
     * Navigate to the previous page if available
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            setupNavigation();
        }
    }

    /**
     * Jump to a specific page
     */
    public void jumpToPage(int page) {
        if (page >= 0 && page < totalPages) {
            currentPage = page;
            setupNavigation();
        }
    }

    /**
     * Get the pagination information text
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
     * Allow subclasses to add their custom footer options
     */
    protected void addCustomFooterOptions(StringBuilder sb) {}
}