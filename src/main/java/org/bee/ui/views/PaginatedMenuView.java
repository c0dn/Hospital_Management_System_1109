package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.TextStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialized view for paginated menus with keyboard navigation controls.
 * Uses 'n' for the next page, 'p' for the previous page, 'j' for jump to page, and numeric inputs for selection.
 */
public class PaginatedMenuView extends AbstractPaginatedView<AbstractPaginatedView.MenuOption> {
    private final List<MenuOption> options;
    private final String sectionTitle;

    /**
     * Creates a new paginated menu view.
     *
     * @param canvas The canvas to render on
     * @param titleHeader The title header for the view
     * @param sectionTitle The title for the menu section
     * @param options The list of menu options to paginate
     * @param itemsPerPage Number of options to display per page
     * @param color The color for the view
     */
    public PaginatedMenuView(Canvas canvas, String titleHeader, String sectionTitle,
                             List<MenuOption> options, int itemsPerPage, Color color) {
        super(canvas, titleHeader, itemsPerPage, options.size(), color);
        this.options = new ArrayList<>(options);
        this.sectionTitle = sectionTitle;
        initializeNavigation();
    }

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

    @Override
    protected List<MenuOption> getCurrentPageItems() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, options.size());

        if (options.isEmpty()) {
            return List.of();
        }

        return options.subList(startIndex, endIndex);
    }

    @Override
    protected void addCustomFooterOptions(StringBuilder sb) {
        sb.append(" | Enter a number to select an option");
    }

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
     * Override to ensure navigation options are properly refreshed
     */
    @Override
    public void nextPage() {
        super.nextPage();
        setupNavigation();
        canvas.setRequireRedraw(true);
    }

    /**
     * Override to ensure navigation options are properly refreshed
     */
    @Override
    public void previousPage() {
        super.previousPage();
        setupNavigation();
        canvas.setRequireRedraw(true);
    }

    /**
     * Override to ensure navigation options are properly refreshed
     */
    @Override
    public void jumpToPage(int page) {
        super.jumpToPage(page);
        setupNavigation();
        canvas.setRequireRedraw(true);
    }
}