package org.bee.ui;

/**
 * Enum representing different status types for system messages.
 * Each status corresponds to a different level of importance and is mapped to a specific color.
 * <p>
 * Available statuses:
 * <ul>
 *   <li>INFO - General information messages</li>
 *   <li>WARNING - Warning messages that require attention</li>
 *   <li>ERROR - Error messages indicating failures</li>
 *   <li>SUCCESS - Success messages confirming completed operations</li>
 * </ul>
 */
public enum SystemMessageStatus {
    INFO,
    WARNING,
    ERROR,
    SUCCESS
}
