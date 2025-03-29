package org.bee.ui.views;

import java.util.*;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.View;
import org.bee.ui.forms.FormField;

/**
 * A form view for collecting multiple pieces of related data with validation.
 */
public class FormView extends View {
    private final List<FormField<?>> fields = new ArrayList<>();
    private FormSubmitCallback onSubmit;
    private int selectedFieldIndex = -1;
    private SystemMessageStatus lastMessageStatus = SystemMessageStatus.INFO;
    private boolean showRequiredFieldMessage = false;


    private enum FormState { IDLE, FIELD_SELECTED, AWAITING_VALUE }
    private FormState currentState = FormState.IDLE;


    public FormView(Canvas canvas, String formTitle, Color color) {
        super(canvas, formTitle, "", color);
    }

    public <T> void addField(FormField<T> field) {
        fields.add(field);
    }

    public void setOnSubmitCallback(FormSubmitCallback callback) {
        this.onSubmit = callback;
    }

    public boolean isAwaitingValue() {
        return currentState == FormState.AWAITING_VALUE;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            FormField<?> field = fields.get(i);
            if (field == null) continue;
            boolean isHighlighted = (i == selectedFieldIndex && (currentState == FormState.FIELD_SELECTED || currentState == FormState.AWAITING_VALUE));
            sb.append(i + 1).append(". ").append(field.getDisplayString(isHighlighted));
            sb.append("\n");
        }
        sb.append("\n");

        if (currentState == FormState.FIELD_SELECTED && selectedFieldIndex != -1) {
            sb.append(Color.CYAN.getAnsiCode())
                    .append("Selected Field: ").append(selectedFieldIndex + 1).append(". ")
                    .append(fields.get(selectedFieldIndex).getDisplayName())
                    .append(" (Press 'u' to update)")
                    .append(Color.ESCAPE.getAnsiCode())
                    .append("\n");
        }

        return sb.toString();
    }

    @Override
    public String getFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n | e: Go Back");

        List<Integer> numericKeys = Collections.list(inputOptions.keys()).stream()
                .filter(key -> key > 0)
                .sorted()
                .toList();

        if (!numericKeys.isEmpty()) {
            sb.append(" | Select field you wish to update (1-").append(fields.size()).append(")");
        }

        sb.append(" | s: Submit Changes | u: Update Selected Field");

        if (currentState == FormState.AWAITING_VALUE && selectedFieldIndex != -1) {
            FormField<?> currentField = fields.get(selectedFieldIndex);
            String currentFieldPrompt = currentField.getPrompt();
            sb.append(" | q: Quit App\n").append(currentFieldPrompt).append(": ");
        } else {
            sb.append(" | q: Quit App\nYour input: ");
        }

        return sb.toString();
    }

    /**
     * Sets up the actions available in the associated MenuView.
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
     * @param valueInput The user's input for the field value.
     */
    public void processValueInput(String valueInput) {
        if (currentState != FormState.AWAITING_VALUE) {
            System.err.println("WARN: processValueInput called in incorrect state: " + currentState);
            return;
        }

        if (selectedFieldIndex < 0 || selectedFieldIndex >= fields.size()) {
            lastMessageStatus = SystemMessageStatus.ERROR;
            canvas.setSystemMessage("Internal Error: No field selected to receive value.",lastMessageStatus);
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
            if (currentField.isRequired() || (valueInput != null && !valueInput.trim().isEmpty()) ) {
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
     * Handles the 'u' command trigger.
     */
    private void handleUpdateTrigger() {
        if (currentState == FormState.FIELD_SELECTED) {
            currentState = FormState.AWAITING_VALUE;
            canvas.setRequireRedraw(true);
        } else {
            lastMessageStatus = SystemMessageStatus.ERROR;
            currentState = FormState.IDLE;
            selectedFieldIndex = -1;
            canvas.setSystemMessage("Please select a field number (1-" + fields.size() + ") first before pressing 'u'.", lastMessageStatus);
            canvas.setRequireRedraw(true);
        }
    }

    @Override
    public boolean handleDirectInput(String input) {
        if (currentState == FormState.AWAITING_VALUE) {
            processValueInput(input);
            return true;
        }
        return false;
    }

    public FormField<?> getSelectedField() {
        return fields.get(selectedFieldIndex);
    }


    private void handleSubmit() {
        currentState = FormState.IDLE;
        selectedFieldIndex = -1;

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

        Map<String, Object> results = new HashMap<>();
        for (FormField<?> field : fields) {
            results.put(field.getName(), field.getValue());
        }

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
     */
    @FunctionalInterface
    public interface FormSubmitCallback {
        void onSubmit(Map<String, Object> formData);
    }
}