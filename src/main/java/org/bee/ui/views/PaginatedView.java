package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A versatile paginated view that wraps any view type to provide pagination functionality.
 * <p>
 * This generic implementation extends {@link AbstractPaginatedView} to enable pagination
 * for any type of view (e.g., {@link TableView}, {@link FormView}, {@link DetailsView})
 * through a view factory approach. Rather than implementing pagination directly within
 * each view type, this class delegates content rendering to a view factory function that
 * creates appropriate views for each page of data.
 * <p>
 * Key features include:
 * <ul>
 *   <li>Generic type parameters to support any data and view type</li>
 *   <li>Seamless integration with the standard pagination controls (n/p/j navigation)</li>
 *   <li>Automatic view recreation when navigating between pages</li>
 *   <li>Selection callback integration with wrapped views</li>
 * </ul>
 *
 * @param <T> The type of data items being paginated
 * @param <V> The type of view used to display the data (must extend {@link View})
 *
 * @see AbstractPaginatedView
 * @see TableView
 * @see PaginatedMenuView
 */
public class PaginatedView<T, V extends View> extends AbstractPaginatedView<T> {
    private final List<T> items;
    private final BiFunction<List<T>, Integer, V> viewFactory;
    private String paginationInfo = "";
    private V currentContentView;

    /**
     * Creates a new paginated view with the specified items and view factory.
     * <p>
     * The view factory is called whenever the current page changes to create
     * a new content view for the current page of items.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title for the view
     * @param items The list of items to paginate
     * @param itemsPerPage Number of items to display per page
     * @param viewFactory Function to create a view for displaying a page of items;
     *                    receives the current page's items and the page number
     * @param color The color for the view
     */

    public PaginatedView(Canvas canvas, String titleHeader, List<T> items, int itemsPerPage,
                         BiFunction<List<T>, Integer, V> viewFactory, Color color) {
        super(canvas, titleHeader, itemsPerPage, items.size(), color);
        this.items = new ArrayList<>(items);
        this.viewFactory = viewFactory;

        updateContentView();
        initializeNavigation();
    }

    /**
     * Updates the pagination information text based on the current page.
     * <p>
     * This method is called internally when the content view is updated.
     */
    private void updatePaginationInfo() {
        this.paginationInfo = getPaginationInfo();
    }

    /**
     * Updates the content view to reflect the current page.
     * <p>
     * Creates a new content view using the view factory with the current page's items.
     * This method is called automatically when navigating between pages.
     */
    private void updateContentView() {
        updatePaginationInfo();
        List<T> pageItems = getCurrentPageItems();
        currentContentView = viewFactory.apply(pageItems, currentPage);
    }
    /**
     * Sets up content-specific options based on the type of content view.
     * <p>
     * Currently provides special handling for {@link TableView} by connecting its
     * row selection callback to this view's selection callback.
     */
    @Override
    protected void setupContentOptions() {
        if (currentContentView instanceof TableView) {
            ((TableView<?>) currentContentView).setSelectionCallback((row, item) -> {
                if (this.selectionCallback != null) {
                    this.selectionCallback.accept(new MenuOption(String.valueOf(row),
                            "Row " + (row + 1), item));
                }
            });
        }
    }
    /**
     * Retrieves the items to display on the current page.
     * <p>
     * Calculates the correct slice of items based on the current page number
     * and the items per page setting.
     *
     * @return A list containing the items for the current page
     */
    @Override
    protected List<T> getCurrentPageItems() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        if (items.isEmpty()) {
            return List.of();
        }

        return items.subList(startIndex, endIndex);
    }
    /**
     * Navigates to the next page and updates the content view.
     * <p>
     * This override ensures that the content view is recreated when
     * the page changes.
     */

    @Override
    public void nextPage() {
        super.nextPage();
        updateContentView();
    }
    /**
     * Navigates to the previous page and updates the content view.
     * <p>
     * This override ensures that the content view is recreated when
     * the page changes.
     */
    @Override
    public void previousPage() {
        super.previousPage();
        updateContentView();
    }
    /**
     * Jumps to a specific page and updates the content view.
     * <p>
     * This override ensures that the content view is recreated when
     * the page changes.
     *
     * @param page The zero-based page index to jump to
     */
    @Override
    public void jumpToPage(int page) {
        super.jumpToPage(page);
        updateContentView();
    }
    /**
     * Generates the text representation of the paginated view.
     * <p>
     * Combines the pagination information with the text content from
     * the current content view.
     *
     * @return The formatted text containing pagination info and content view text
     */

    @Override
    public String getText() {
        if (currentContentView == null) {
            return "No content available";
        }

        StringBuilder content = new StringBuilder();

        if (!paginationInfo.isEmpty()) {
            content.append(paginationInfo).append("\n\n");
        }

        content.append(currentContentView.getText());

        return content.toString();
    }

    /**
     * Gets the current content view instance.
     * <p>
     * This provides access to the underlying view representing the current page,
     * allowing direct manipulation when needed.
     *
     * @return The content view for the current page
     */
    public V getContentView() {
        return currentContentView;
    }
}