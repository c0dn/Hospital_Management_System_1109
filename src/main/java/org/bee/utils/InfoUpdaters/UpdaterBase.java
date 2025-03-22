package org.bee.utils.InfoUpdaters;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstract base class for updating Human entities in the system.
 * Provides a foundation for building specific updaters (Patient, Doctor, etc.)
 * using a consistent builder pattern approach.
 *
 * @param <T> The type of Human entity this updater works with
 * @param <S> The concrete updater type (for method chaining)
 */
public abstract class UpdaterBase<T, S extends UpdaterBase<T, S>> {

    protected Map<String, String> validationErrors = new HashMap<>();



    /**
     * Applies all the specified updates to the given entity.
     * Only non-null values will be applied.
     *
     * @param entity The entity to update
     */
    public void applyTo(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        // Apply specific field updates defined in subclasses
        applySpecificUpdates(entity);
    }

    /**
     * Helper method to apply updates only if the value is non-null.
     *
     * @param <V> The type of the value
     * @param value The value to check
     * @param setter The setter method to call
     */
    protected <V> void ifPresent(V value, Consumer<V> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Template method to be implemented by subclasses for applying
     * entity-specific field updates.
     *
     * @param entity The entity to update
     */
    protected abstract void applySpecificUpdates(T entity);


    /**
     * Checks if there are any validation errors.
     * @return true if all fields are valid, false otherwise
     */
    public boolean isValid() {
        return validationErrors.isEmpty();
    }

    /**
     * Gets the validation error for a specific field.
     * @param fieldName The name of the field
     * @return The error message, or null if the field is valid
     */
    public String getValidationError(String fieldName) {
        return validationErrors.get(fieldName);
    }

    /**
     * Gets all validation errors.
     * @return A map of field names to error messages
     */
    public Map<String, String> getValidationErrors() {
        return new HashMap<>(validationErrors);
    }

    /**
     * Clears all validation errors.
     */
    public void clearValidationErrors() {
        validationErrors.clear();
    }
}