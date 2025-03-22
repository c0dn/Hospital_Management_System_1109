package org.bee.utils.InfoUpdaters;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A generic base class for entity updaters that follows the builder pattern.
 * <p>
 * This class provides a framework for implementing entity updaters that apply
 * non-null field values to existing entity objects. It follows a template method
 * pattern where subclasses implement the {@link #applySpecificUpdates(Object)}
 * method to define entity-specific update logic.
 * <p>
 * The generic type parameters allow for type-safe and fluent usage:
 * <ul>
 *   <li>{@code T} - The type of entity being updated</li>
 *   <li>{@code S} - The concrete updater type (self-referential for method chaining)</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>
 * public class PersonUpdater extends UpdaterBase&lt;Person, PersonUpdater&gt; {
 *     private String name;
 *     private Integer age;
 *
 *     public PersonUpdater withName(String name) {
 *         this.name = name;
 *         return this;
 *     }
 *
 *     public PersonUpdater withAge(Integer age) {
 *         this.age = age;
 *         return this;
 *     }
 *
 *     protected void applySpecificUpdates(Person person) {
 *         ifPresent(name, person::setName);
 *         ifPresent(age, person::setAge);
 *     }
 * }
 *
 * // Client code
 * Person person = new Person();
 * new PersonUpdater()
 *     .withName("John")
 *     .withAge(30)
 *     .applyTo(person);
 * </pre>
 *
 * @param <T> The type of entity this updater can modify
 * @param <S> The concrete updater type (self-referential for method chaining)
 *
 * @see Consumer
 */
public abstract class UpdaterBase<T, S extends UpdaterBase<T, S>> {

    /**
     * Map to store validation errors for fields.
     * Keys are field names, values are error messages.
     */
    protected Map<String, String> validationErrors = new HashMap<>();

    /**
     * Applies all the specified updates to the given entity.
     * Only non-null values will be applied, preserving existing
     * values for fields that were not explicitly set in this updater.
     * <p>
     * This method serves as the main entry point for clients to
     * apply accumulated changes to an entity instance.
     *
     * @param entity The entity to update
     * @throws IllegalArgumentException if the provided entity is null
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
     * <p>
     * This utility method simplifies the common pattern of checking
     * if a field has been set before applying it to the target entity,
     * reducing boilerplate code in subclasses.
     *
     * @param <V> The type of the value
     * @param value The value to check for null
     * @param setter The setter method to call if value is non-null
     */
    protected <V> void ifPresent(V value, Consumer<V> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Template method to be implemented by subclasses for applying
     * entity-specific field updates.
     * <p>
     * Subclasses should implement this method to define how their
     * specific fields are applied to the target entity, typically
     * using the {@link #ifPresent(Object, Consumer)} helper method.
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