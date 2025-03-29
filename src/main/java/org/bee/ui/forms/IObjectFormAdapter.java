package org.bee.ui.forms;

import org.bee.ui.TextStyle;
import org.bee.ui.UiBase;
import org.bee.utils.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IObjectFormAdapter<T> {
    /**
     * Generate form fields for the given object.
     */
    List<FormField<?>> generateFields(T object);

    /**
     * Get a descriptive name for the object type.
     */
    String getObjectTypeName();

    /**
     * Apply form values to the object.
     * Default implementation uses reflection to set field values.
     */
    default T applyUpdates(T object, Map<String, Object> formData) {
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            setFieldValue(object, entry.getKey(), entry.getValue());
        }
        return object;
    }

    /**
     * Save the updated object.
     * This still needs implementation for each object type to handle controller interactions.
     */
    boolean saveObject(T object);

    /**
     * Set a field value in an object using reflection.
     * Function will attempt to use setter methods before resorting to direct field access
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
     * Helper method to find the ID field name based on common patterns
     */
    default String findIdFieldName(Class<?> clazz) {
        String[] commonIdFields = {"id", "patientId", "consultationId", "staffId", "appointmentId"};

        for (String fieldName : commonIdFields) {
            if (hasField(clazz, fieldName) || hasGetter(clazz, fieldName)) {
                return fieldName;
            }
        }

        return null;
    }

    /**
     * Check if a class has a field with the given name
     */
    default boolean hasField(Class<?> clazz, String fieldName) {
        return ReflectionHelper.findField(clazz, fieldName) != null;
    }

    /**
     * Check if a class has a getter for the given field name
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
     * Copy all non-null fields from source to target
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
     * Get all fields from a class including its superclasses
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
     * Get a field value from an object using reflection.
     * Function will attempt to use getter methods first before trying direct field access
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

    // ignored object is there to keep method signature same
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

    default FormField<String> createTextField(
            String name, String displayName, String prompt, T object,
            Predicate<String> validator, String errorMessage, boolean isRequired,
            String initialValue) {
        return createField(name, displayName, prompt, object, validator, errorMessage,
                FormValidators.stringParser(), isRequired, initialValue);
    }

    default FormField<Double> createDoubleField(
            String name, String displayName, String prompt, T object,
            Predicate<String> validator, String errorMessage, boolean isRequired,
            Double initialValue) {
        return createField(name, displayName, prompt, object, validator, errorMessage,
                s -> (s == null || s.trim().isEmpty()) ? null : Double.parseDouble(s),
                isRequired, initialValue);
    }

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

}