package org.bee.hms.auth;

/**
 * Represents a user in the system with authentication
 */
public interface SystemUser {
    /**
     * Get the username used for authentication
     *
     * @return the user's username
     */
    String getUsername();
}
