package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A versatile paginated view that can wrap any type of view (TableView, FormView, etc.)
 * to provide pagination functionality.
 *
 * @param <T> The type of data items being paginated
 * @param <V> The type of view used to display the data
 */
public class PaginatedView<T, V extends View> extends AbstractPaginatedView<T> {
    private final List<T> items;
    private final BiFunction<List<T>, Integer, V> viewFactory;
    private String paginationInfo = "";
    private V currentContentView;

    /**
     * Creates a new paginated view with a flexible view factory.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title for the view
     * @param items The list of items to paginate
     * @param itemsPerPage Number of items to display per page
     * @param viewFactory Function to create a view for displaying a page of items
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
     * Updates the pagination info text
     */
    private void updatePaginationInfo() {
        this.paginationInfo = getPaginationInfo();
    }

    /**
     * Updates the content view to reflect the current page
     */
    private void updateContentView() {
        updatePaginationInfo();
        List<T> pageItems = getCurrentPageItems();
        currentContentView = viewFactory.apply(pageItems, currentPage);
    }

    @Override
    protected void setupContentOptions() {}

    @Override
    protected List<T> getCurrentPageItems() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        if (items.isEmpty()) {
            return List.of();
        }

        return items.subList(startIndex, endIndex);
    }

    @Override
    public void nextPage() {
        super.nextPage();
        updateContentView();
    }

    @Override
    public void previousPage() {
        super.previousPage();
        updateContentView();
    }

    @Override
    public void jumpToPage(int page) {
        super.jumpToPage(page);
        updateContentView();
    }

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
     * Get the current content view
     */
    public V getContentView() {
        return currentContentView;
    }
}