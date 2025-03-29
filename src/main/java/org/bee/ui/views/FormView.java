package org.bee.ui.views;

import org.bee.ui.*;
import org.bee.ui.forms.FormField;

import java.util.*;

/**
 * A form view for collecting multiple pieces of related data with validation.
 */
public class FormView extends View {
    private final List<FormField<?>> fields = new ArrayList<>();
    private FormSubmitCallback onSubmit;
    private int currentFieldIndex = 0;
    private boolean isFormComplete = false;
    private MenuView actionMenuView;
    private final Terminal terminal;
    private String lastErrorMessage = "";

    public FormView(Canvas canvas, String formTitle, Color color) {
        super(canvas, formTitle, "", color);
        this.terminal = canvas.getTerminal();
    }

    public <T> void addField(FormField<T> field) {
        fields.add(field);
    }

    public void setOnSubmitCallback(FormSubmitCallback callback) {
        this.onSubmit = callback;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            FormField<?> field = fields.get(i);
            String fieldName = (field != null && field.getName() != null) ? field.getName() : "Field " + (i + 1);
            sb.append(i + 1).append(". ").append(fieldName);

            Object value = (field != null) ? field.getValue() : null;
            if (value != null) {
                sb.append(": ").append(value);
            } else {
                sb.append(": [Not set]");
            }
            if (i == currentFieldIndex && !isFormComplete) {
                sb.append(" <- Current field");
            }
            sb.append("\n");
        }

        sb.append("\n");

        if (!lastErrorMessage.isEmpty()) {
            sb.append(Color.RED.getAnsiCode())
                    .append("Error: ").append(lastErrorMessage)
                    .append(Color.ESCAPE.getAnsiCode())
                    .append("\n\n");
        } else if (isFormComplete) {
            sb.append("Form complete. Select an action below.\n");
        }

        return sb.toString();
    }


    @Override
    public void attachUserInput(String option, UserInputResult lambda) {
        super.attachUserInput(option, lambda);
    }

    public void setupForm() {
        clearUserInputs();

        if (isFormComplete) {
            attachUserInput("Submit Form", input -> {
                Map<String, Object> results = new HashMap<>();
                for (FormField<?> field : fields) {
                    results.put(field.getName(), field.getValue());
                }

                if (onSubmit != null) {
                    onSubmit.onSubmit(results);
                }
            });

            for (int i = 0; i < fields.size(); i++) {
                final int fieldIndex = i;
                attachUserInput("Edit " + fields.get(i).getName() + " ", input -> {
                    currentFieldIndex = fieldIndex;
                    isFormComplete = false;
                    setupForm();
                    canvas.setRequireRedraw(true);
                });
            }
        } else {
            attachUserInput("Enter value", input -> {
                FormField<?> currentField = fields.get(currentFieldIndex);

                System.out.println(currentField.getPrompt());
                System.out.flush();
                String userInput = terminal.getUserInput();

                if (currentField.validate(userInput)) {
                    currentField.setValue(userInput);

                    currentFieldIndex++;
                    if (currentFieldIndex >= fields.size()) {
                        isFormComplete = true;
                        currentFieldIndex = 0;
                    }
                } else {
                    lastErrorMessage = currentField.getErrorMessage();
                }

                setupForm();
                canvas.setRequireRedraw(true);
            });

            attachUserInput("Skip this field", input -> {
                currentFieldIndex++;
                if (currentFieldIndex >= fields.size()) {
                    isFormComplete = true;
                    currentFieldIndex = 0;
                }

                setupForm();
                canvas.setRequireRedraw(true);
            });
        }
    }


    public void setupActionMenu(MenuView menuView) {
        this.actionMenuView = menuView;
        menuView.clearUserInputs();
        if (isFormComplete) {
            MenuView.MenuSection completeSection = menuView.addSection("Form Actions");
            completeSection.addOption(1, "Submit Form");
            menuView.attachMenuOptionInput(1, "Submit Form", input -> handleSubmit());

            for (int i = 0; i < fields.size(); i++) {
                final int fieldIndex = i;
                FormField<?> currentField = fields.get(i);
                if (currentField != null && currentField.getName() != null && !currentField.getName().isEmpty()) {
                    completeSection.addOption(i + 2, "Edit " + currentField.getName());
                    menuView.attachMenuOptionInput(i + 2, "Edit " + currentField.getName(), input -> handleEdit(fieldIndex));
                }
            }
        } else if (currentFieldIndex < fields.size()) {
            MenuView.MenuSection inputSection = menuView.addSection("Field Actions");
            inputSection.addOption(1, "Enter value");
            inputSection.addOption(2, "Skip this field");
            menuView.attachMenuOptionInput(1, "Enter value", input -> handleEnterValue());
            menuView.attachMenuOptionInput(2, "Skip this field", input -> handleSkipField());
        }
    }


    private void handleEnterValue() {
        if (currentFieldIndex >= fields.size()) {
            canvas.setSystemMessage("Form Error: Invalid field index.", SystemMessageStatus.ERROR);
            isFormComplete = true;
            if (this.actionMenuView != null) {
                setupActionMenu(this.actionMenuView);
            }
            canvas.setRequireRedraw(true);
            return;
        }

        FormField<?> currentField = fields.get(currentFieldIndex);
        if (currentField == null) {
            canvas.setSystemMessage("Form Error: Current field is null.", SystemMessageStatus.ERROR);
            isFormComplete = true;
            if (this.actionMenuView != null) {
                setupActionMenu(this.actionMenuView);
            }
            canvas.setRequireRedraw(true);
            return;
        }

        Object currentValue = currentField.getValue();
        String displayValue = currentValue != null ? currentValue.toString() : "[Not set]";
        System.out.println("\nField: " + currentField.getName());
        System.out.println("[Current: " + displayValue + "]");
        System.out.println(currentField.getPrompt());
        System.out.flush();

        String userInput = terminal.getUserInput();

        if (userInput == null) {
            canvas.setSystemMessage("Input error.", SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
            return;
        }

        if (currentField.validate(userInput)) {
            try {
                currentField.setValue(userInput);
                lastErrorMessage = "";
                currentFieldIndex++;
                if (currentFieldIndex >= fields.size()) {
                    isFormComplete = true;
                    currentFieldIndex = 0;
                }
            } catch (Exception e) {
                lastErrorMessage = "Invalid format. Please check your input.";
            }
        } else {
            lastErrorMessage = currentField.getErrorMessage();
        }

        if (this.actionMenuView != null) {
            setupActionMenu(this.actionMenuView);
        }
        canvas.setRequireRedraw(true);
    }


    private void handleSkipField() {
        if (currentFieldIndex >= fields.size()) return;
        lastErrorMessage = "";
        currentFieldIndex++;
        if (currentFieldIndex >= fields.size()) {
            isFormComplete = true;
            currentFieldIndex = 0;
        }

        if (this.actionMenuView != null) {
            setupActionMenu(this.actionMenuView);
        }
        canvas.setRequireRedraw(true);
    }


    private void handleSubmit() {
        Map<String, Object> results = new HashMap<>();
        for (FormField<?> field : fields) {
            results.put(field.getName(), field.getValue());
        }
        if (onSubmit != null) {
            onSubmit.onSubmit(results);
        }
    }

    private void handleEdit(int fieldIndex) {
        currentFieldIndex = fieldIndex;
        isFormComplete = false;
        lastErrorMessage = "";

        if (this.actionMenuView != null) {
            setupActionMenu(this.actionMenuView);
        }
        canvas.setRequireRedraw(true);
    }


    /**
     * Interface for handling form submission.
     */
    @FunctionalInterface
    public interface FormSubmitCallback {
        void onSubmit(Map<String, Object> formData);
    }
}