package org.bee.ui.exception;
/**
 * Exception thrown when attempting to use functionality that has not yet been implemented.
 * <p>
 * This exception is used throughout the UI framework to indicate methods or features
 * that are defined in interfaces or abstract classes but have not yet been fully
 * implemented. It helps identify placeholder implementations that need to be completed
 * before they can be used in production.
 * </p>
 * <p>
 * The exception automatically prefixes the provided message with "NOT IMPLEMENTED: "
 * to make it clear in stack traces and error logs that this is an implementation
 * issue rather than a runtime error.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * public void complexFeature() {
 *     throw new UnimplementedException("Complex feature will be available in next release");
 * }
 * </pre>
 */
public class UnimplementedException extends Exception {
    /**
     * Constructs a new UnimplementedException with a descriptive message.
     * <p>
     * The provided message should describe what functionality is missing or
     * not yet implemented, and optionally when it might be available.
     * </p>
     *
     * @param message A description of the unimplemented functionality
     */
    public UnimplementedException(String message) {
        super("NOT IMPLEMENTED: " + message);
    }
}
