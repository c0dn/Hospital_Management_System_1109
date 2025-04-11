package org.bee.execeptions;

/**
 * Class that represents an exception that will occur when interacting with the Zoom API
 * <p>
 * This exception is thrown when there is a problem with API calls or responses
 * from the Zoom service. It display error messages and the
 * underlying cause of the exception.
*/
public class ZoomApiException extends Exception {
    /**
     * Construct a new ZoomApiException with the specific message
     *
     * @param message the detail message
     */
    public ZoomApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new Zoom API exception with the specified detail message and cause.
     *
     * @param message the detail message (
     * @param cause the cause
     */
    public ZoomApiException(String message, Throwable cause) {
        super(message, cause);
    }
}