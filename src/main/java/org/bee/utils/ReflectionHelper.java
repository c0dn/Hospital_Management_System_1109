package org.bee.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * A utility class for reflection-based property access.
 * Provides methods to safely access object properties even when direct getter methods are not available.
 */
public final class ReflectionHelper {

    private ReflectionHelper() {
        throw new AssertionError("Utility class should not be instantiated");
    }


    /**
     * Creates a property accessor function that tries multiple strategies to get a property value:
     * 1. First tries the traditional getter method (getPropertyName)
     * 2. If that fails, tries an "is" getter for boolean properties (isPropertyName)
     * 3. If that fails, tries direct field access
     * 4. If all fail, returns the fallback value
     *
     * @param <T> The type of the object being accessed
     * @param <R> The return type of the property
     * @param propertyName The name of the property to access
     * @param fallbackValue The value to return if the property cannot be accessed
     * @return A function that extracts the property value from an object
     */
    public static <T, R> Function<T, R> propertyAccessor(String propertyName, R fallbackValue) {
        return obj -> {
            if (obj == null) return fallbackValue;

            // Convert property name to camel case for method access
            String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            String isGetterName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

            try {
                Method getter = obj.getClass().getMethod(getterName);
                @SuppressWarnings("unchecked")
                R result = (R) getter.invoke(obj);
                return result != null ? result : fallbackValue;
            } catch (Exception e1) {
                try {
                    Method isGetter = obj.getClass().getMethod(isGetterName);
                    @SuppressWarnings("unchecked")
                    R result = (R) isGetter.invoke(obj);
                    return result != null ? result : fallbackValue;
                } catch (Exception e2) {
                    try {
                        Field field = findField(obj.getClass(), propertyName);
                        if (field != null) {
                            field.setAccessible(true);
                            @SuppressWarnings("unchecked")
                            R result = (R) field.get(obj);
                            return result != null ? result : fallbackValue;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            return fallbackValue;
        };
    }

    /**
     * A variant of propertyAccessor that returns a string representation.
     * Useful for table columns where everything needs to be converted to string.
     *
     * @param <T> The type of the object being accessed
     * @param propertyName The name of the property to access
     * @param fallback The fallback string to use if the property cannot be accessed
     * @return A function that extracts the property value and converts it to a string
     */
    public static <T> Function<T, String> stringPropertyAccessor(String propertyName, String fallback) {
        return obj -> {
            Object value = propertyAccessor(propertyName, null).apply(obj);
            return value != null ? value.toString() : fallback;
        };
    }

    /**
     * Creates a nested property accessor function that handles null values.
     *
     * @param <T> The type of the parent object
     * @param <I> The type of the intermediate object
     * @param <R> The return type of the property
     * @param parentProperty The path to the parent object
     * @param childProperty The property to access on the parent object
     * @param fallbackValue The value to return if any part of the chain is null
     * @return A function that safely extracts the nested property value
     */
    public static <T, I, R> Function<T, R> nestedPropertyAccessor(
            String parentProperty, String childProperty, R fallbackValue) {
        return obj -> {
            if (obj == null) return fallbackValue;

            try {
                Object parent = propertyAccessor(parentProperty, null).apply(obj);
                if (parent == null) return fallbackValue;

                @SuppressWarnings("unchecked")
                R result = (R) propertyAccessor(childProperty, null).apply((I) parent);
                return result != null ? result : fallbackValue;
            } catch (Exception e) {
                return fallbackValue;
            }
        };
    }

    /**
     * A variant of nestedPropertyAccessor that returns a string representation.
     *
     * @param <T> The type of the parent object
     * @param parentProperty The path to the parent object
     * @param childProperty The property to access on the parent object
     * @param fallback The fallback string to use if any part of the chain is null
     * @return A function that safely extracts the nested property and converts it to a string
     */
    public static <T> Function<T, String> nestedStringPropertyAccessor(
            String parentProperty, String childProperty, String fallback) {
        return obj -> {
            Object value = nestedPropertyAccessor(parentProperty, childProperty, null).apply(obj);
            return value != null ? value.toString() : fallback;
        };
    }

    /**
     * Helper method to find a field in a class or its superclasses.
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }
}