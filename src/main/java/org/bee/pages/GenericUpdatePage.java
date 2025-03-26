package org.bee.pages;

import org.bee.ui.Color;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.FormView;
import org.bee.ui.views.TextView;
import org.bee.ui.forms.FormField;
import org.bee.ui.forms.IObjectFormAdapter;

import java.util.List;
import java.util.Map;

/**
 * A generic page for updating objects using form-based validation.
 * @param <T> The type of object being updated
 */
public class GenericUpdatePage<T> extends UiBase {

    private final T objectToUpdate;
    private final IObjectFormAdapter<T> adapter;
    private final Runnable onSuccessCallback;

    /**
     * Creates a new GenericUpdatePage.
     *
     * @param objectToUpdate The object to be updated
     * @param adapter The adapter that handles field generation and updates
     */
    public GenericUpdatePage(T objectToUpdate, IObjectFormAdapter<T> adapter) {
        this(objectToUpdate, adapter, null);
    }

    /**
     * Creates a new GenericUpdatePage with a callback on success.
     *
     * @param objectToUpdate The object to be updated
     * @param adapter The adapter that handles field generation and updates
     * @param onSuccessCallback A callback to execute after successful update
     */
    public GenericUpdatePage(T objectToUpdate, IObjectFormAdapter<T> adapter, Runnable onSuccessCallback) {
        this.objectToUpdate = objectToUpdate;
        this.adapter = adapter;
        this.onSuccessCallback = onSuccessCallback;
    }

    @Override
    public View createView() {
        String title = "Update " + adapter.getObjectTypeName();
        return new FormView(this.canvas, title, Color.CYAN);
    }

    @Override
    public void OnViewCreated(View parentView) {
        FormView formView = (FormView) parentView;

        List<FormField<?>> fields = adapter.generateFields(objectToUpdate);
        for (FormField<?> field : fields) {
            formView.addField(field);
        }

        formView.setOnSubmitCallback(this::handleFormSubmission);

        formView.setupForm();
        canvas.setRequireRedraw(true);
    }

    private void handleFormSubmission(Map<String, Object> formData) {
        T updatedObject = adapter.applyUpdates(objectToUpdate, formData);

        boolean saved = adapter.saveObject(updatedObject);

        if (saved) {
            TextView successView = new TextView(
                    this.canvas,
                    adapter.getObjectTypeName() + " updated successfully!",
                    Color.GREEN
            );
            navigateToView(successView);

            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

            try {
                // Return to the previous screen
                Thread.sleep(1500);
                OnBackPressed();
            } catch (InterruptedException ignored) {
            }
        } else {
            TextView errorView = new TextView(
                    this.canvas,
                    "Error saving " + adapter.getObjectTypeName() + ". Please try again.",
                    Color.RED
            );
            navigateToView(errorView);
        }
    }
}