package org.bee.pages;

import org.bee.ui.Color;
import org.bee.ui.SystemMessageStatus;
import org.bee.ui.UiBase;
import org.bee.ui.View;
import org.bee.ui.views.CompositeView;
import org.bee.ui.views.FormView;
import org.bee.ui.views.MenuView;
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

    private FormView formDisplayView;
    private MenuView actionMenuView;

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
        String title = "\nUpdate " + adapter.getObjectTypeName();

        formDisplayView = new FormView(this.canvas, "", Color.CYAN);
        List<FormField<?>> fields = adapter.generateFields(objectToUpdate);
        for (FormField<?> field : fields) {
            formDisplayView.addField(field);
        }
        formDisplayView.setOnSubmitCallback(this::handleFormSubmission);

        actionMenuView = new MenuView(this.canvas, "", Color.CYAN, false, true);

        formDisplayView.setupActionMenu(actionMenuView);

        CompositeView compositeView = new CompositeView(this.canvas, title, Color.CYAN);
        compositeView.setSeparator("\n");

        compositeView.addView(formDisplayView);

        compositeView.addView(actionMenuView);

        return compositeView;
    }

    @Override
    public void OnViewCreated(View parentView) {
        canvas.setRequireRedraw(true);
    }

    private void handleFormSubmission(Map<String, Object> formData) {
        T updatedObject = adapter.applyUpdates(objectToUpdate, formData);
        boolean saved = adapter.saveObject(updatedObject);
        if (saved) {
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            } else {
                canvas.setSystemMessage(adapter.getObjectTypeName() + " updated successfully!", SystemMessageStatus.SUCCESS);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) { }
            }
            OnBackPressed();
        } else {
            canvas.setSystemMessage("Error saving " + adapter.getObjectTypeName() + ". Please try again.", SystemMessageStatus.ERROR);
            canvas.setRequireRedraw(true);
        }
    }
}