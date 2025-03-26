package org.bee.ui.views;

import org.bee.ui.Canvas;
import org.bee.ui.Color;
import org.bee.ui.Terminal;
import org.bee.ui.View;
import org.bee.ui.forms.FormField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A form view for collecting multiple pieces of related data with validation.
 */
public class FormView extends View {
    private final List<FormField<?>> fields = new ArrayList<>();
    private FormSubmitCallback onSubmit;
    private int currentFieldIndex = 0;
    private boolean isFormComplete = false;
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
            sb.append(i + 1).append(". ").append(field.getName());

            if (field.getValue() != null) {
                sb.append(": ").append(field.getValue());
            } else {
                sb.append(": [Not set]");
            }

            if (i == currentFieldIndex && !isFormComplete) {
                sb.append(" <- Current field");
            }

            sb.append("\n");
        }

        sb.append("\n");

        if (!isFormComplete) {
            FormField<?> currentField = fields.get(currentFieldIndex);
            sb.append(currentField.getPrompt()).append("\n");

            if (!lastErrorMessage.isEmpty()) {
                sb.append("\nError: ").append(lastErrorMessage).append("\n");
                lastErrorMessage = "";
            }
        } else {
            sb.append("Form complete. Submit or make changes.\n");
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

    /**
     * Interface for handling form submission.
     */
    @FunctionalInterface
    public interface FormSubmitCallback {
        void onSubmit(Map<String, Object> formData);
    }
}