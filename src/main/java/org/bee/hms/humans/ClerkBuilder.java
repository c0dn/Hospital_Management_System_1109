package org.bee.hms.humans;

/**
 * Builder class for creating Clerk objects.
 * Extends StaffBuilder to inherit staff-specific building capabilities.
 */
public class ClerkBuilder extends StaffBuilder<ClerkBuilder> {
    /**
     * Creates a new instance of ClerkBuilder
     * @return a new instance of ClerkBuilder object
     */
    public static ClerkBuilder builder() {
        return new ClerkBuilder();
    }

    /**
     * Returns the builder instance
     *
     * @return The ClerkBuilder instance
     */
    @Override
    public ClerkBuilder self() {
        return this;
    }

    /**
     * Builds and returns a new Clerk instance
     * Validates required fields before creating the Clerk object
     *
     * @return A new Clerk instance with the configured properties
     */
    @Override
    public Clerk build() {
        validateRequiredFields();
        return new Clerk(this);
    }
}
