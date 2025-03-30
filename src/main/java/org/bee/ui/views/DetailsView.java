package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.View;
import org.bee.ui.TextStyle;
import org.bee.ui.details.IDetailsViewAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic view for displaying detailed information in a key-value format
 * with support for sections, formatting, and navigation.
 *
 * @param <T> The type of the data object being displayed
 */
public class DetailsView<T> extends View {
    private final Map<String, List<DetailItem>> sections = new LinkedHashMap<>();
    private String header;
    private String footer = "\nOptions:\n | e: Go Back | q: Quit App\nYour input: ";
    private final T dataObject;
    private IDetailsViewAdapter<T> adapter;

    // Default section name for items added without a specific section
    private static final String DEFAULT_SECTION = "Details";

    /**
     * Creates a new DetailsView with a color and no data object.
     * Note: This constructor is kept for backward compatibility but
     * should generally not be used with adapters.
     */
    public DetailsView(Canvas canvas, Color color) {
        super(canvas, "", "", color);
        this.dataObject = null;
        initialize();
    }

    /**
     * Creates a new DetailsView with a title and color, but no data object.
     * Note: This constructor is kept for backward compatibility but
     * should generally not be used with adapters.
     */
    public DetailsView(Canvas canvas, String title, Color color) {
        super(canvas, "", "", color);
        this.header = title;
        this.dataObject = null;
        initialize();
    }

    /**
     * Creates a new DetailsView with a title, data object, and color.
     * This is the preferred constructor for use without an adapter.
     */
    public DetailsView(Canvas canvas, String title, T dataObject, Color color) {
        super(canvas, "", "", color);
        this.header = title;
        this.dataObject = dataObject;
        initialize();
    }

    /**
     * Creates a new DetailsView with a title, data object, color, and adapter.
     * This constructor automatically configures the view using the provided adapter.
     */
    public DetailsView(Canvas canvas, String title, T dataObject, Color color, IDetailsViewAdapter<T> adapter) {
        super(canvas, "", "", color);
        this.header = title;
        this.dataObject = dataObject;
        this.adapter = adapter;
        initialize();
        if (adapter != null && dataObject != null) {
            try {
                adapter.configureView(this, dataObject);
            } catch (Exception e) {
                addDetail("Error", "Failed to load details: " + e.getMessage());
                canvas.setSystemMessage("Error loading details: " + e.getMessage(), SystemMessageStatus.ERROR);
            }
        }
    }

    private void initialize() {
        sections.put(DEFAULT_SECTION, new ArrayList<>());
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
     * Set or update the adapter for this view
     *
     * @param adapter The adapter to use for configuring the view
     * @return This view for method chaining
     */
    public DetailsView<T> setAdapter(IDetailsViewAdapter<T> adapter) {
        this.adapter = adapter;
        if (adapter != null && dataObject != null) {
            clearDetails();
            try {
                adapter.configureView(this, dataObject);
                canvas.setRequireRedraw(true);
            } catch (Exception e) {
                addDetail("Error", "Failed to load details: " + e.getMessage());
                canvas.setSystemMessage("Error loading details: " + e.getMessage(), SystemMessageStatus.ERROR);
            }
        }
        return this;
    }

    /**
     * Get the current adapter
     */
    public IDetailsViewAdapter<T> getAdapter() {
        return adapter;
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
     * Clear all details and sections (except default)
     */
    public void clearDetails() {
        sections.clear();
        sections.put(DEFAULT_SECTION, new ArrayList<>());
    }


    /**
     * Reload details using the adapter
     */
    public void refreshDetails() {
        if (adapter != null && dataObject != null) {
            clearDetails();
            adapter.configureView(this, dataObject);
            canvas.setRequireRedraw(true);
        }
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
     * Gets the data object associated with this DetailsView.
     *
     * @return The data object of type T being displayed, or null if none was provided.
     */
    public T getDataObject() {
        return dataObject;
    }
}