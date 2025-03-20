package org.bee.execeptions;

public class ZoomApiException extends Exception {
    public ZoomApiException(String message) {
        super(message);
    }

    public ZoomApiException(String message, Throwable cause) {
        super(message, cause);
    }
}