package org.bee.pages;

import org.bee.ui.*;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.FormValidators;
import org.bee.ui.views.FormView;
import org.bee.ui.views.TextView;

import java.util.Arrays;
import java.util.Map;

public class TestPage extends UiBase {

    @Override
    public View createView() {
        return new TextView(this.canvas, "Input Validation Examples", Color.CYAN, TextStyle.BOLD);
    }

    @Override
    public void OnViewCreated(View parentView) {
        parentView.attachUserInput("Simple Range Input", input -> demonstrateRangeInput());

        parentView.attachUserInput("String Validation", input -> demonstrateStringValidation());

        parentView.attachUserInput("Form Input", input -> demonstrateFormInput());

        parentView.attachUserInput("Selection from List", input -> demonstrateSelectionInput());
    }

    private void demonstrateRangeInput() {
        TextView view = new TextView(this.canvas, "Range Input Example", Color.GREEN);
        this.canvas.setCurrentView(view);

        view.attachUserInput("Enter a value between 1 and 10", input -> {
            Terminal terminal = this.canvas.getTerminal();
            int value = InputHelper.getValidIndex(terminal, "Enter a number between 1 and 10:", 1, 10);

            TextView resultView = new TextView(this.canvas,
                    "You entered: " + value + "\nThank you!", Color.GREEN);
            navigateToView(resultView);
        });
    }


    private void demonstrateStringValidation() {
        TextView view = new TextView(this.canvas, "String Validation Example", Color.BLUE);
        this.canvas.setCurrentView(view);

        view.attachUserInput("Validate Email", input -> {
            Terminal terminal = this.canvas.getTerminal();
            String email = InputHelper.getValidString(
                    terminal,
                    "Please enter a valid email:",
                    "^[A-Za-z0-9+_.-]+@(.+)$",
                    "Invalid email format. Please try again."
            );

            TextView resultView = new TextView(this.canvas,
                    "You entered: " + email + "\nThank you!", Color.BLUE);
            navigateToView(resultView);
        });

        view.attachUserInput("Custom Validation", input -> {
            Terminal terminal = this.canvas.getTerminal();
            String password = InputHelper.getValidString(
                    terminal,
                    "Create a password (at least 8 chars with 1 number):",
                    s -> s.length() >= 8 && s.matches(".*\\d.*"),
                    "Password must be at least 8 characters and contain at least one number."
            );

            TextView resultView = new TextView(this.canvas,
                    "Password accepted!\nThank you!", Color.BLUE);
            navigateToView(resultView);
        });
    }

    private void demonstrateFormInput() {
        try {
            // Create a form for user registration
            FormView formView = new FormView(this.canvas, "User Registration Form", Color.MAGENTA);

            // Add fields with validation
            formView.addField(new FormField<>(
                    "Name",
                    "Enter your full name:",
                    FormValidators.combine(
                            FormValidators.notEmpty(),
                            FormValidators.minLength(2)
                    ),
                    "Name must be at least 2 characters long.",
                    FormValidators.stringParser()
            ));

            formView.addField(new FormField<>(
                    "Age",
                    "Enter your age:",
                    FormValidators.combine(
                            FormValidators.numeric(),
                            input -> {
                                try {
                                    int age = Integer.parseInt(input);
                                    return age >= 1 && age <= 120;
                                } catch (NumberFormatException e) {
                                    return false;
                                }
                            }
                    ),
                    "Age must be a number between 18 and 120.",
                    FormValidators.intParser()
            ));

            formView.addField(new FormField<>(
                    "Email",
                    "Enter your email address:",
                    FormValidators.email(),
                    "Please enter a valid email address.",
                    FormValidators.stringParser()
            ));

            // Set submission handler
            formView.setOnSubmitCallback(formData -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Form Submitted Successfully!\n\n");

                for (Map.Entry<String, Object> entry : formData.entrySet()) {
                    sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }

                TextView resultView = new TextView(this.canvas, sb.toString(), Color.GREEN);
                navigateToView(resultView);
            });

            // Setup the form and show it
            formView.setupForm();
            navigateToView(formView);
            canvas.setRequireRedraw(true);
        } catch (Exception e) {
            // Debug - print any exceptions
            System.err.println("Form error: " + e.getMessage());

            TextView errorView = new TextView(this.canvas,
                    "Error creating form: " + e.getMessage(), Color.RED);
            navigateToView(errorView);
        }
    }


    private void demonstrateSelectionInput() {
        TextView view = new TextView(this.canvas, "Selection Example", Color.YELLOW);
        navigateToView(view);

        view.attachUserInput("Select from options", input -> {
            // Use InputHelper for selection from list
            Terminal terminal = this.canvas.getTerminal();
            String selection = InputHelper.getSelection(
                    terminal,
                    "Choose your favorite color:",
                    Arrays.asList("Red", "Green", "Blue", "Yellow", "Purple")
            );

            TextView resultView = new TextView(this.canvas,
                    "You selected: " + selection + "\nExcellent choice!", Color.YELLOW);
            navigateToView(resultView);
        });
    }

    @Override
    public void OnBackPressed() {
        System.out.println("[DEBUG] TestPage.OnBackPressed() called");
        // Call the parent implementation which handles the backstack navigation
        super.OnBackPressed();
    }
}
