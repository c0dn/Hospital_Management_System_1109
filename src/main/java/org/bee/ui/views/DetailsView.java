package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.View;
import org.bee.ui.TextStyle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic view for displaying detailed information in a key-value format
 * with support for sections, formatting, and navigation.
 *
 * @param <T> The type of the data object being displayed (optional)
 */
public class DetailsView<T> extends View {
    private final Map<String, List<DetailItem>> sections = new LinkedHashMap<>();
    private String header;
    private String footer = "\nOptions:\n | e: Go Back | q: Quit App\nYour input: ";
    private T dataObject;
    private View previousView;

    // Default section name for items added without a specific section
    private static final String DEFAULT_SECTION = "Details";

    public DetailsView(Canvas canvas, Color color) {
        super(canvas, "", "", color);
        initialize();
    }

    public DetailsView(Canvas canvas, String title, Color color) {
        super(canvas, "", "", color);
        this.header = title;
        initialize();
    }

    public DetailsView(Canvas canvas, String title, T dataObject, Color color) {
        this(canvas, title, color);
        this.dataObject = dataObject;
    }

    private void initialize() {
        sections.put(DEFAULT_SECTION, new ArrayList<>());
        setupNavigation();
    }

    /**
     * Represents a single detail item with label and value
     */
    public static class DetailItem {
        private final String label;
        private final String value;
        private final TextStyle labelStyle;
        private final TextStyle valueStyle;

        public DetailItem(String label, String value) {
            this(label, value, TextStyle.BOLD, TextStyle.RESET);
        }

        public DetailItem(String label, String value, TextStyle labelStyle, TextStyle valueStyle) {
            this.label = label;
            this.value = value;
            this.labelStyle = labelStyle;
            this.valueStyle = valueStyle;
        }

        @Override
        public String toString() {
            return labelStyle.getAnsiCode() + label + ": " + TextStyle.RESET.getAnsiCode() +
                    valueStyle.getAnsiCode() + value + TextStyle.RESET.getAnsiCode();
        }
    }

    /**
     * Add a detail item to the default section
     */
    public void addDetail(String label, String value) {
        addDetail(DEFAULT_SECTION, label, value);
    }

    /**
     * Add a detail item to a specific section
     */
    public void addDetail(String section, String label, String value) {
        addDetail(section, new DetailItem(label, value));
    }

    /**
     * Add a pre-formatted detail item to a specific section
     */
    public void addDetail(String section, DetailItem detailItem) {
        sections.computeIfAbsent(section, k -> new ArrayList<>()).add(detailItem);
    }

    /**
     * Add multiple details from a Map to the default section
     */
    public void addDetails(Map<String, String> details) {
        details.forEach(this::addDetail);
    }

    /**
     * Add multiple details from a Map to a specific section
     */
    public void addDetails(String section, Map<String, String> details) {
        details.forEach((k, v) -> addDetail(section, k, v));
    }

    /**
     * Set the header text (shown above all sections)
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Set the footer text (override default navigation options)
     */
    public void setFooter(String footer) {
        this.footer = footer;
    }

    /**
     * Get the associated data object
     */
    public T getDataObject() {
        return dataObject;
    }

    /**
     * Set the previous view for navigation
     */
    public void setPreviousView(View previousView) {
        this.previousView = previousView;
    }

    /**
     * Clear all details and sections (except default)
     */
    public void clearDetails() {
        sections.clear();
        sections.put(DEFAULT_SECTION, new ArrayList<>());
    }

    /**
     * Remove a specific section
     */
    public void removeSection(String section) {
        sections.remove(section);
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        if (header != null && !header.isEmpty()) {
            sb.append(TextStyle.BOLD.getAnsiCode())
                    .append(header)
                    .append(TextStyle.RESET.getAnsiCode())
                    .append("\n");
        }

        sections.forEach((sectionName, items) -> {
            if (!sectionName.equals(DEFAULT_SECTION) || !items.isEmpty()) {
                sb.append(TextStyle.BOLD.getAnsiCode())
                        .append(TextStyle.ESCAPE.getAnsiCode())
                        .append(sectionName)
                        .append(TextStyle.RESET.getAnsiCode())
                        .append("\n");
            }

            for (DetailItem item : items) {
                sb.append("  ").append(item).append("\n");
            }

            // Only add extra line if there are more sections coming
            if (hasMoreSections(sectionName)) {
                sb.append("\n");
            }
        });

        return sb.toString();
    }

    private boolean hasMoreSections(String currentSection) {
        boolean foundCurrent = false;
        for (String section : sections.keySet()) {
            if (foundCurrent && !sections.get(section).isEmpty()) {
                return true;
            }
            if (section.equals(currentSection)) {
                foundCurrent = true;
            }
        }
        return false;
    }

    @Override
    public String getFooter() {
        return footer;
    }

    /**
     * Setup default navigation options
     */
    protected void setupNavigation() {
        attachUserInput("Go Back", input -> {
            if (input.equalsIgnoreCase("e") && previousView != null) {
                canvas.setCurrentView(previousView);
                canvas.setRequireRedraw(true);
            }
        });
    }

    /**
     * Add a custom action to the view
     * @param prompt The prompt shown in the footer
     * @param key The key to trigger the action
     * @param action The action to execute
     */
    public void addAction(String prompt, String key, Runnable action) {
        // Update footer to show the new option
        if (!footer.contains(" | " + key + ": " + prompt)) {
            int lastNewline = footer.lastIndexOf("\n");
            if (lastNewline > 0) {
                footer = footer.substring(0, lastNewline) +
                        " | " + key + ": " + prompt +
                        footer.substring(lastNewline);
            }
        }

        // Add the action handler
        attachUserInput(prompt, input -> {
            if (input.equalsIgnoreCase(key)) {
                action.run();
            }
        });
    }
}