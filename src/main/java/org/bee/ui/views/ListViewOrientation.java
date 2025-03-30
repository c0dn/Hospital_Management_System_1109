package org.bee.ui.views;

/**
 * Defines the orientation options for a ListView component.
 * <p>
 * This enum specifies whether child views within a ListView should be arranged
 * horizontally (side by side) or vertically (stacked). The orientation affects
 * both the layout and the separator used between items.
 * </p>
 * <p>Used by {@link ListView} to determine how to arrange and separate its child views:</p>
 * <ul>
 *   <li>HORIZONTAL: Items are arranged from left to right, typically separated by tabs or spaces</li>
 *   <li>VERTICAL: Items are stacked from top to bottom, typically separated by newlines</li>
 * </ul>
 *
 * @see ListView
 */
public enum ListViewOrientation {
    /**
     * Arranges items from left to right in a row format.
     * Typically used with tab or space separators.
     */
    HORIZONTAL,

    /**
     * Arranges items from top to bottom in a column format.
     * Typically used with newline separators.
     */
    VERTICAL
}