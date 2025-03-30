package org.bee.ui;

/**
 * A generic functional interface for callback methods within the UI framework.
 * <p>
 * This interface provides a common pattern for event handlers and callbacks
 * throughout the application, allowing for consistent implementation of event-driven
 * programming patterns.
 */
@FunctionalInterface
public interface IGenericCallbackInterface {
    void callback();
}
