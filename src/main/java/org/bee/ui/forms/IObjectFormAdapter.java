package org.bee.ui.forms;

import org.bee.utils.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Interface for adapters that convert domain objects to form representations and back.
 * <p>
 * This interface provides functionality to:
 * <ul>
 *   <li>Generate form fields from domain objects</li>
 *   <li>Apply form data back to domain objects</li>
 *   <li>Save updated objects to their data stores</li>
 * </ul>
 *
 * <p>Implementations of this interface serve as a bridge between the UI form system
 * and domain model objects, handling the bidirectional mapping of data.</p>
 *
 *
 * @param <T> The type of domain object this adapter handles
 */
public interface IObjectFormAdapter<T> {
    /**
     * Generates form fields for the given object.
     * <p>
     * This method analyzes the provided object and creates appropriate form fields
     * with validators, parsers, and initial values based on the object's current state.
     * </p>
     *
     * @param object The object to generate form fields for
     * @return A list of form fields representing the object's editable properties
     */
    List<FormField<?>> generateFields(T object);

    /**
     * Gets a descriptive name for the object type.
     * <p>
     * Used for display purposes in UI elements such as form headers and titles.
     * </p>
     *
     * @return A user-friendly name describing the object type
     */
    String getObjectTypeName();

    /**
     * Applies form values to the object.
     * <p>
     * The default implementation uses reflection to set field values,
     * attempting to use setter methods before resorting to direct field access.
     * </p>
     *
     * @param object The object to update
     * @param formData A map of field names to their new values
     * @return The updated object
     */
    default T applyUpdates(T object, Map<String, Object> formData) {
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            setFieldValue(object, entry.getKey(), entry.getValue());
        }
        return object;
    }

    /**
     * Saves the updated object.
     * <p>
     * This method must be implemented to handle controller interactions for each
     * object type, typically by calling the appropriate service or repository method.
     * </p>
     *
     * @param object The object to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveObject(T object);

    /**
     * Sets a field value in an object using reflection.
     * <p>
     * This method first attempts to use setter methods before resorting to direct field access.
     * It handles both primitive types and objects appropriately.
     * </p>
     *
     * @param object The object to modify
     * @param fieldName The name of the field to set
     * @param value The new value for the field
     */
    default void setFieldValue(Object object, String fieldName, Object value) {
        try {
            String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    if (value == null || paramType.isInstance(value)) {
                        method.invoke(object, value);
                        return;
                    }
                }
            }

            Field field = ReflectionHelper.findField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(object, value);
                return;
            }

            System.err.println("No setter or field found for: " + fieldName);
        } catch (Exception e) {
            System.err.println("Error setting field " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * <p>Generic implementation for saving an object using a controller class.</p>
     * This method:
     * <ol>
     *   <li>Gets the controller instance</li>
     *   <li>Finds the object in the controller's collection using its ID field</li>
     *   <li>Copies non-null fields from source to existing object</li>
     *   <li>Calls the save method on the controller</li>
     * </ol>
     *
     * @param object The object to save
     * @param controllerClass The controller class that manages this object type
     * @param controllerMethodName The name of the save method on the controller
     * @return true if save was successful, false otherwise
     */
    default boolean genericSaveObject(T object, Class<?> controllerClass, String controllerMethodName) {
        try {
            Method getInstanceMethod = controllerClass.getMethod("getInstance");
            Object controller = getInstanceMethod.invoke(null);

            Method saveDataMethod = controllerClass.getMethod(controllerMethodName);

            Method getAllMethod = null;
            for (Method method : controllerClass.getMethods()) {
                if (method.getName().startsWith("getAll") &&
                        method.getReturnType().equals(List.class)) {
                    getAllMethod = method;
                    break;
                }
            }

            if (getAllMethod != null) {
                @SuppressWarnings("unchecked")
                List<T> allObjects = (List<T>)getAllMethod.invoke(controller);

                String idFieldName = findIdFieldName(object.getClass());
                if (idFieldName != null) {
                    Object objectId = getFieldValue(object, idFieldName);

                    T existingObject = null;
                    for (T candidate : allObjects) {
                        Object candidateId = getFieldValue(candidate, idFieldName);
                        if (objectId.equals(candidateId)) {
                            existingObject = candidate;
                            break;
                        }
                    }

                    if (existingObject != null) {
                        copyNonNullFields(object, existingObject);

                        saveDataMethod.invoke(controller);
                        return true;
                    }
                }
            }

            System.err.println("Could not find or update object in controller");
            return false;
        } catch (Exception e) {
            System.err.println("Error in genericSaveObject: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to find the ID field name based on common naming patterns.
     * <p>
     * Checks for common ID field names like "id", "patientId".
     * </p>
     *
     * @param clazz The class to search for ID fields
     * @return The name of the identified ID field, or null if none found
     */
    default String findIdFieldName(Class<?> clazz) {
        String[] commonIdFields = {"id", "patientId", "consultationId", "staffId", "appointmentId", "claimId"};

        for (String fieldName : commonIdFields) {
            if (hasField(clazz, fieldName) || hasGetter(clazz, fieldName)) {
                return fieldName;
            }
        }

        return null;
    }

    /**
     * Checks if a class has a field with the given name.
     *
     * @param clazz The class to check
     * @param fieldName The field name to look for
     * @return true if the field exists, false otherwise
     */
    default boolean hasField(Class<?> clazz, String fieldName) {
        return ReflectionHelper.findField(clazz, fieldName) != null;
    }

    /**
     * Checks if a class has a getter method for the given field name.
     *
     * @param clazz The class to check
     * @param fieldName The field name to look for a getter
     * @return true if the getter exists, false otherwise
     */
    default boolean hasGetter(Class<?> clazz, String fieldName) {
        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            clazz.getMethod(getterName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Copies all non-null fields from source to target object.
     * <p>
     * This method is used to update an existing object with new values,
     * preserving existing values where the source has null fields.
     * </p>
     *
     * @param source The object containing the new values
     * @param target The object to update
     */
    default void copyNonNullFields(Object source, Object target) {
        Class<?> clazz = source.getClass();
        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                System.err.println("Error copying field " + field.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gets all fields from a class including those from its superclasses.
     *
     * @param clazz The class to get fields from
     * @return A list of all fields declared in the class hierarchy
     */
    default List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    /**
     * Gets a field value from an object using reflection.
     * <p>
     * This method attempts to use getter methods first before trying direct field access.
     * It checks for both standard getters and boolean isGetter methods.
     * </p>
     *
     * @param object The object to get the value from
     * @param fieldName The name of the field to get
     * @return The field value, or null if not found or an error occurs
     */
    default Object getFieldValue(Object object, String fieldName) {
        try {
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(getterName) && method.getParameterCount() == 0) {
                    return method.invoke(object);
                }
            }

            String isGetterName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            for (Method method : methods) {
                if (method.getName().equals(isGetterName) && method.getParameterCount() == 0) {
                    return method.invoke(object);
                }
            }

            Field field = ReflectionHelper.findField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }

            System.err.println("No getter or field found for: " + fieldName);
            return null;
        } catch (Exception e) {
            System.err.println("Error getting field " + fieldName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a generic form field with the specified properties.
     * <p>
     * The ignoredObject parameter is included to maintain consistent method signatures
     * across helper methods but is not used in the implementation.
     * </p>
     *
     * @param <V> The type of value the field will hold
     * @param name The field identifier name
     * @param displayName The human-readable field name
     * @param prompt The input prompt shown to the user
     * @param ignoredObject Unused parameter (maintained for signature consistency)
     * @param validator The validation predicate
     * @param errorMessage The error message shown on validation failure
     * @param parser The function to parse input strings to the value type
     * @param isRequired Whether the field is required
     * @param initialValue The initial value of the field
     * @return A configured FormField instance
     */
    default <V> FormField<V> createField(
            String name,
            String displayName,
            String prompt,
            T ignoredObject,
            Predicate<String> validator,
            String errorMessage,
            FormField.FormInputParser<V> parser,
            boolean isRequired,
            V initialValue) {
        return new FormField<>(
                name,
                displayName,
                prompt,
                validator,
                errorMessage,
                parser,
                isRequired,
                initialValue
        );
    }

    /**
     * Creates a text field with the specified properties.
     * <p>
     * This is a convenience method for creating string-based form fields.
     * </p>
     *
     * @param name The field identifier name
     * @param displayName The human-readable field name
     * @param prompt The input prompt shown to the user
     * @param object The source object (unused but maintained for consistency)
     * @param validator The validation predicate
     * @param errorMessage The error message shown on validation failure
     * @param isRequired Whether the field is required
     * @param initialValue The initial value of the field
     * @return A configured string FormField instance
     */
    default FormField<String> createTextField(
            String name, String displayName, String prompt, T object,
            Predicate<String> validator, String errorMessage, boolean isRequired,
            String initialValue) {
        return createField(name, displayName, prompt, object, validator, errorMessage,
                FormValidators.stringParser(), isRequired, initialValue);
    }

    /**
     * Creates a numeric field for double values with the specified properties.
     * <p>
     * This is a convenience method for creating double-based form fields.
     * </p>
     *
     * @param name The field identifier name
     * @param displayName The human-readable field name
     * @param prompt The input prompt shown to the user
     * @param object The source object (unused but maintained for consistency)
     * @param validator The validation predicate
     * @param errorMessage The error message shown on validation failure
     * @param isRequired Whether the field is required
     * @param initialValue The initial value of the field
     * @return A configured double FormField instance
     */
    default FormField<Double> createDoubleField(
            String name, String displayName, String prompt, T object,
            Predicate<String> validator, String errorMessage, boolean isRequired,
            Double initialValue) {
        return createField(name, displayName, prompt, object, validator, errorMessage,
                s -> (s == null || s.trim().isEmpty()) ? null : Double.parseDouble(s),
                isRequired, initialValue);
    }

    /**
     * Creates an enum field with the specified properties.
     * <p>
     * This method handles enum parsing and validation, providing user-friendly
     * error messages that include the list of valid enum values.
     * </p>
     *
     * @param <E> The enum type for the field
     * @param name The field identifier name
     * @param displayName The human-readable field name
     * @param prompt The input prompt shown to the user
     * @param object The source object (unused but maintained for consistency)
     * @param enumType The Class object for the enum type
     * @param validator The validation predicate
     * @param errorMessage The error message shown on validation failure
     * @param isRequired Whether the field is required
     * @param initialValue The initial value of the field
     * @return A configured enum FormField instance
     */
    default <E extends Enum<E>> FormField<E> createEnumField(
            String name, String displayName, String prompt, T object, Class<E> enumType,
            Predicate<String> validator, String errorMessage, boolean isRequired,
            E initialValue) {

        FormField.FormInputParser<E> parser = input -> {
            if (input == null || input.trim().isEmpty()) {
                if (isRequired) { throw new IllegalArgumentException(displayName + " is required."); }
                else { return null; }
            }
            try { return Enum.valueOf(enumType, input.trim().toUpperCase()); }
            catch (IllegalArgumentException e) {
                String validOptions = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Invalid selection for " + displayName + ". Valid options are: " + validOptions);
            }
        };

        return createField(name, displayName, prompt, object, validator, errorMessage,
                parser, isRequired, initialValue);
    }


    /**
     * Creates a form field for editing a list of elements.
     *
     * @param <E> The type of elements in the list
     * @param name The field identifier name
     * @param displayName The human-readable field name
     * @param prompt The input prompt shown to the user
     * @param object The source object (of type T)
     * @param validator The validation predicate
     * @param errorMessage The error message shown on validation failure
     * @param itemParser The parser for list elements (type E)
     * @param isRequired Whether the field is required
     * @param initialValue The initial list of elements
     * @return A configured ListFormField instance
     */
    default <E> FormField<List<E>> createListField(
            String name, String displayName, String prompt, T object,
            Predicate<String> validator, String errorMessage,
            FormField.FormInputParser<E> itemParser, boolean isRequired,
            List<E> initialValue) {

        return new ListFormField<>(
                name, displayName, prompt, validator,
                errorMessage, itemParser, isRequired,
                initialValue != null ? new ArrayList<>(initialValue) : new ArrayList<>());
    }


    /**
     * Creates a form field for editing a map.
     *
     * @param <K> The key type in the map
     * @param <V> The value type in the map
     * @param name The field identifier name
     * @param displayName The human-readable field name
     * @param prompt The input prompt shown to the user
     * @param object The source object (of type T)
     * @param validator The validation predicate
     * @param errorMessage The error message shown on validation failure
     * @param keyParser The parser for map keys
     * @param valueParser The parser for map values
     * @param isRequired Whether the field is required
     * @param initialValue The initial map
     * @return A configured MapFormField instance
     */
    default <K, V> FormField<Map<K, V>> createMapField(
            String name, String displayName, String prompt, T object,
            Predicate<String> validator, String errorMessage,
            FormField.FormInputParser<K> keyParser,
            FormField.FormInputParser<V> valueParser,
            boolean isRequired, Map<K, V> initialValue) {

        return new MapFormField<>(
                name, displayName, prompt, validator,
                errorMessage, keyParser, valueParser, isRequired,
                initialValue != null ? new HashMap<>(initialValue) : new HashMap<>());
    }

}