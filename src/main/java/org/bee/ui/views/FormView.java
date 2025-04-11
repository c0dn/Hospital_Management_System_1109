package org.bee.ui.views;

import java.util.*;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.View;
import org.bee.ui.forms.CollectionFormField;
import org.bee.ui.forms.FormField;

/**
 * A form view for collecting and validating user input across multiple fields.
 * <p>
 * FormView implements an interactive form with field selection, validation, and submission
 * capabilities. It manages the entire form lifecycle including:
 * <ul>
 *   <li>Field display and formatting</li>
 *   <li>Field selection and highlighting</li>
 *   <li>Input validation and error handling</li>
 *   <li>Form submission and data collection</li>
 * </ul>
 *
 * <p>
 * The view maintains an internal state to track the currently selected field and
 * whether it's awaiting user input. It can be integrated with MenuView for
 * additional navigation controls.
 * </p>
 */
public class FormView extends View {
    private final List<FormField<?>> fields = new ArrayList<>();
    private FormSubmitCallback onSubmit;
    private int selectedFieldIndex = -1;
    private SystemMessageStatus lastMessageStatus = SystemMessageStatus.INFO;
    private boolean showRequiredFieldMessage = false;

    /**
     * Represents the possible states of the form.
     */
    enum FormState {
        /**
         * No field is selected or active
         */
        IDLE,
        /**
         * A field has been selected but is not yet being edited
         */
        FIELD_SELECTED,
        /**
         * A field is currently awaiting user input
         */
        AWAITING_VALUE,
        COLLECTION_BROWSING,
        COLLECTION_ADDING,
        COLLECTION_REMOVING
    }

    FormState currentState = FormState.IDLE;

    /**
     * Creates a new form view with the specified title and color.
     *
     * @param canvas    The canvas to render on
     * @param formTitle The title of the form
     * @param color     The color for the form's text
     */
    public FormView(Canvas canvas, String formTitle, Color color) {
        super(canvas, formTitle, "", color);
    }

    /**
     * Adds a field to the form.
     * <p>
     * Fields are displayed in the order they are added.
     * </p>
     *
     * @param <T>   The type of value stored in the field
     * @param field The form field to add
     */
    public <T> void addField(FormField<T> field) {
        fields.add(field);
    }

    /**
     * Sets the callback to be invoked when the form is submitted.
     * <p>
     * The callback receives a map of field names to their values.
     * </p>
     *
     * @param callback The callback to handle form submission
     */
    public void setOnSubmitCallback(FormSubmitCallback callback) {
        this.onSubmit = callback;
    }

    /**
     * Checks if the form is currently awaiting input for a field.
     * <p>
     * This is used by composite views and other containers to properly
     * route input to this form when a field is being edited.
     * </p>
     *
     * @return true if a field is actively awaiting input, false otherwise
     */
    public boolean isAwaitingValue() {
        return currentState == FormState.AWAITING_VALUE;
    }

    /**
     * {@inheritDoc}
     * <p>Generates the form content showing all fields with appropriate highlighting
     * for the selected field. Also displays additional context information when
     * a field is selected.</p>
     */
    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            FormField<?> field = fields.get(i);
            if (field == null) continue;
            boolean isHighlighted = (i == selectedFieldIndex && (currentState == FormState.FIELD_SELECTED ||
                    currentState == FormState.AWAITING_VALUE || currentState == FormState.COLLECTION_BROWSING));
            sb.append(i + 1).append(". ").append(field.getDisplayString(isHighlighted));
            sb.append("\n");
        }
        sb.append("\n");

        if (currentState == FormState.COLLECTION_BROWSING && selectedFieldIndex != -1) {
            FormField<?> selectedFieldRaw = getSelectedField();
            if (selectedFieldRaw instanceof CollectionFormField<?, ?> field) {
                sb.append("\n").append(Color.CYAN.getAnsiCode());
                sb.append("--- Managing: ").append(field.getDisplayName()).append(" ---\n");
                List<String> items = field.getDisplayItems();
                if (items.isEmpty()) {
                    sb.append("  (No items)\n");
                } else {
                    for (int i = 0; i < items.size(); i++) {
                        sb.append("  ").append(i + 1).append(". ").append(items.get(i)).append("\n");
                    }
                }
                sb.append(Color.ESCAPE.getAnsiCode());
            }
        } else if (currentState == FormState.FIELD_SELECTED && selectedFieldIndex != -1) {
            sb.append(Color.CYAN.getAnsiCode())
                    .append("Selected Field: ").append(selectedFieldIndex + 1).append(". ")
                    .append(fields.get(selectedFieldIndex).getDisplayName())
                    .append(" (Press 'u' to update)")
                    .append(Color.ESCAPE.getAnsiCode())
                    .append("\n");
        }


        return sb.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Generates the footer with context-sensitive options based on the current form state.
     * When a field is awaiting input, the footer includes the field's prompt.</p>
     */
    @Override
    public String getFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n | e: Go Back");

        if (currentState == FormState.COLLECTION_BROWSING) {
            sb.append(" | a: Add Item | r: Remove Item | s: Submit Changes | q: Quit App\n");
            sb.append("Your input (a, r, e, s, q): ");

        } else if (currentState == FormState.COLLECTION_ADDING) {
            CollectionFormField<?, ?> field = (CollectionFormField<?, ?>) getSelectedField();
            sb.append(" | q: Quit App\n");
            sb.append("Enter new item for ").append(field.getDisplayName()).append(" (or 'e' to go back): ");

        } else if (currentState == FormState.COLLECTION_REMOVING) {
            CollectionFormField<?, ?> field = (CollectionFormField<?, ?>) getSelectedField();
            sb.append(" | q: Quit App\n");
            sb.append("Enter item number (1-").append(field.getCollectionSize()).append(") to remove (or 'e' to go back): ");

        } else if (currentState == FormState.AWAITING_VALUE) {
            if (selectedFieldIndex != -1) {
                FormField<?> currentField = fields.get(selectedFieldIndex);
                sb.append(" | q: Quit App\n");
                sb.append(currentField.getPrompt()).append(" ");
            } else {
                sb.append(" | Select field (1-").append(fields.size()).append(")");
                sb.append(" | s: Submit Changes | u: Update Selected Field | q: Quit App\n");
                sb.append("Your input: ");
            }
        } else {
            if (!fields.isEmpty()) {
                sb.append(" | Select field (1-").append(fields.size()).append(")");
            }
            sb.append(" | s: Submit Changes");
            if (selectedFieldIndex != -1) { // Only show 'u' if a field IS selected
                sb.append(" | u: Update Selected Field");
            }
            sb.append(" | q: Quit App\n");
            sb.append("Your input: ");
        }

        return sb.toString();
    }

    /**
     * Sets up the actions available in the associated MenuView.
     * <p>This method configures a MenuView to work with this form, adding options for:</p>
     * <ul>
     *   <li>Field selection</li>
     *   <li>Form submission</li>
     *   <li>Field updating</li>
     * </ul>
     * It also adds a warning message if required fields are empty.
     *
     * @param menuView The MenuView to configure with form actions
     */
    public void setupActionMenu(MenuView menuView) {
        boolean needsReqMessage = false;
        if (!showRequiredFieldMessage) {
            for (FormField<?> field : fields) {
                if (field != null && field.isRequired() && (field.getInitialValue() == null || field.getInitialValue().toString().trim().isEmpty())) {
                    needsReqMessage = true;
                    break;
                }
            }
            if (needsReqMessage) {
                canvas.setSystemMessage("NOTE: REQUIRED fields cannot be empty.", SystemMessageStatus.WARNING);
                showRequiredFieldMessage = true;
            }
        }

        for (int i = 0; i < fields.size(); i++) {
            final int fieldIndex = i;
            String optionText = fields.get(i).getDisplayName();

            this.setUserInputByIndex(i + 1, optionText, input -> {
                this.selectedFieldIndex = fieldIndex;
                this.currentState = FormState.FIELD_SELECTED;
                canvas.setSystemMessage("Field " + (fieldIndex + 1) + " selected. Press 'u' to update.", SystemMessageStatus.INFO);
                canvas.setRequireRedraw(true);
            });
        }

        menuView.attachLetterOption('s', "Submit Changes", input -> handleSubmit());
        menuView.attachLetterOption('u', "Update Selected Field", input -> handleUpdateTrigger());

        MenuView.MenuSection fieldSection = menuView.addSection("");
        for (int i = 0; i < fields.size(); i++) {
            fieldSection.addOption(i + 1, "");
        }

        menuView.setNumericOptionMaxRange(fields.size());
        menuView.setShowNumericHintOnly(true);
    }

    /**
     * Processes the value entered by the user when in AWAITING_VALUE state.
     * <p>
     * This method validates the input, updates the field value if valid,
     * and provides appropriate feedback through system messages. It handles
     * error cases such as validation failures and parsing errors.
     * </p>
     *
     * @param valueInput The user's input for the field value
     */
    public void processValueInput(String valueInput) {
        if (currentState != FormState.AWAITING_VALUE) {
            System.err.println("WARN: processValueInput called in incorrect state: " + currentState);
            return;
        }

        if (selectedFieldIndex < 0 || selectedFieldIndex >= fields.size()) {
            lastMessageStatus = SystemMessageStatus.ERROR;
            canvas.setSystemMessage("Internal Error: No field selected to receive value.", lastMessageStatus);
            currentState = FormState.IDLE;
            selectedFieldIndex = -1;
            canvas.setRequireRedraw(true);
            return;
        }

        FormField<?> currentField = fields.get(selectedFieldIndex);
        if (currentField.validate(valueInput)) {
            try {
                currentField.setValue(valueInput);
                lastMessageStatus = SystemMessageStatus.SUCCESS;
                canvas.setSystemMessage("Field " + (currentField.getDisplayName()) + " updated successfully.", lastMessageStatus);
                currentState = FormState.IDLE;
                selectedFieldIndex = -1;
            } catch (Exception e) {
                lastMessageStatus = SystemMessageStatus.ERROR;
                canvas.setSystemMessage("Error setting value: " + e.getMessage(), lastMessageStatus);
            }
        } else {
            if (currentField.isRequired() || (valueInput != null && !valueInput.trim().isEmpty())) {
                lastMessageStatus = SystemMessageStatus.ERROR;
                canvas.setSystemMessage(currentField.getErrorMessage(), lastMessageStatus);
            } else {
                try {
                    currentField.setValue(null);
                    lastMessageStatus = SystemMessageStatus.INFO;
                    canvas.setSystemMessage("Field " + (selectedFieldIndex + 1) + " cleared.", lastMessageStatus);
                    currentState = FormState.IDLE;
                    selectedFieldIndex = -1;
                } catch (Exception e) {
                    lastMessageStatus = SystemMessageStatus.ERROR;
                    canvas.setSystemMessage("Error clearing field: " + e.getMessage(), lastMessageStatus);
                }
            }
        }
        canvas.setRequireRedraw(true);
    }

    /**
     * Handles the 'u' command trigger for updating a field.
     * <p>
     * If a field is already selected, this transitions to the appropriate state:
     * - COLLECTION_Browse for collection fields (List, Map).
     * - AWAITING_VALUE for single-value fields.
     * If no field is selected, it shows an error message.
     * </p>
     */
    private void handleUpdateTrigger() {
        if (currentState == FormState.FIELD_SELECTED && selectedFieldIndex >= 0 && selectedFieldIndex < fields.size()) {
            FormField<?> selectedField = fields.get(selectedFieldIndex);

            if (selectedField instanceof CollectionFormField) {
                currentState = FormState.COLLECTION_BROWSING;
                canvas.setSystemMessage("Manage " + selectedField.getDisplayName() + ": (a: Add, r: Remove)", SystemMessageStatus.INFO);
            } else {
                currentState = FormState.AWAITING_VALUE;
            }
            canvas.setRequireRedraw(true);

        } else {
            lastMessageStatus = SystemMessageStatus.ERROR;
            currentState = FormState.IDLE;
            selectedFieldIndex = -1;
            canvas.setSystemMessage("Please select a field number (1-" + fields.size() + ") first before pressing 'u'.", lastMessageStatus);
            canvas.setRequireRedraw(true);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handles direct input by processing field values when in AWAITING_VALUE state.
     * </p>
     *
     * @param input The input string from the user
     * @return true if the input was handled, false otherwise
     */
    @Override
    public boolean handleDirectInput(String input) {
        if (currentState == FormState.COLLECTION_BROWSING) {
            CollectionFormField<?, ?> field = (CollectionFormField<?, ?>) getSelectedField();

            if (input.equalsIgnoreCase("a")) {
                // Switch to adding mode
                currentState = FormState.COLLECTION_ADDING;
                canvas.setRequireRedraw(true);
                return true;
            } else if (input.equalsIgnoreCase("r")) {
                // Switch to removing mode
                currentState = FormState.COLLECTION_REMOVING;
                canvas.setRequireRedraw(true);
                return true;
            }
        } else if (currentState == FormState.COLLECTION_ADDING) {
            CollectionFormField<?, ?> field = (CollectionFormField<?, ?>) getSelectedField();
            try {
                field.addItem(input);
                canvas.setSystemMessage("Item added successfully", SystemMessageStatus.SUCCESS);
                currentState = FormState.COLLECTION_BROWSING;
            } catch (Exception e) {
                canvas.setSystemMessage("Error adding item: " + e.getMessage(), SystemMessageStatus.ERROR);
            }
            canvas.setRequireRedraw(true);
            return true;
        } else if (currentState == FormState.COLLECTION_REMOVING) {
            try {
                int index = Integer.parseInt(input) - 1;
                CollectionFormField<?, ?> field = (CollectionFormField<?, ?>) getSelectedField();
                field.removeItem(index);
                canvas.setSystemMessage("Item removed successfully", SystemMessageStatus.SUCCESS);
            } catch (Exception e) {
                canvas.setSystemMessage("Error removing item: " + e.getMessage(), SystemMessageStatus.ERROR);
            }
            currentState = FormState.COLLECTION_BROWSING;
            canvas.setRequireRedraw(true);
            return true;
        }

        return super.handleDirectInput(input);
    }

    /**
     * Gets the currently selected field.
     * <p>
     * This method is used by composite views to access the field awaiting input.
     * </p>
     *
     * @return The currently selected field, or null if none is selected
     */
    public FormField<?> getSelectedField() {
        if (selectedFieldIndex >= 0 && selectedFieldIndex < fields.size()) {
            return fields.get(selectedFieldIndex);
        }
        return null;
    }

    /**
     * Handles the form submission process.
     * <p>
     * This method:
     * <ol>
     *   <li>Validates all required fields have values</li>
     *   <li>Collects all field values into a map</li>
     *   <li>Invokes the submission callback</li>
     *   <li>Handles error cases (missing values, no callback)</li>
     * </ol>
     * </p>
     */
    private void handleSubmit() {
        currentState = FormState.IDLE;
        selectedFieldIndex = -1;

        // Validate all required fields have values
        for (int i = 0; i < fields.size(); i++) {
            FormField<?> field = fields.get(i);
            Object value = field.getValue();
            boolean isEmpty = (value == null || value.toString().trim().isEmpty());

            if (field.isRequired() && isEmpty) {
                lastMessageStatus = SystemMessageStatus.ERROR;
                canvas.setSystemMessage("Cannot submit: Field '" + field.getDisplayName() + "' (" + (i + 1) + ") is required and is empty.", lastMessageStatus);
                canvas.setRequireRedraw(true);
                return;
            }
        }

        // Collect field values
        Map<String, Object> results = new HashMap<>();
        for (FormField<?> field : fields) {
            results.put(field.getName(), field.getValue());
        }

        // Submit or show error
        if (onSubmit != null) {
            onSubmit.onSubmit(results);
        } else {
            lastMessageStatus = SystemMessageStatus.ERROR;
            canvas.setSystemMessage("Error: Form submission handler not set.", lastMessageStatus);
            canvas.setRequireRedraw(true);
        }
    }

    /**
     * Interface for handling form submission.
     * <p>
     * Implementations receive a map of field names to their values when
     * the form is submitted.
     * </p>
     */
    @FunctionalInterface
    public interface FormSubmitCallback {
        /**
         * Called when the form is submitted with valid data.
         *
         * @param formData A map of field names to their values
         */
        void onSubmit(Map<String, Object> formData);
    }
}