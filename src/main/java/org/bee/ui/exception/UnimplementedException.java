package org.bee.ui.exception;

public class UnimplementedException extends Exception {
    public UnimplementedException(String message) {
        super("NOT IMPLEMENTED: " + message);
    }
}
