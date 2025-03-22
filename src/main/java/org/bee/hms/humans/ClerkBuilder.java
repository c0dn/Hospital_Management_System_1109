package org.bee.hms.humans;

/**
 * Builder class for creating Clerk objects.
 * Extends StaffBuilder to inherit staff-specific building capabilities.
 */
public class ClerkBuilder extends StaffBuilder<ClerkBuilder> {
    /**
     * Creates a new instance of ClerkBuilder.
     */
    public static ClerkBuilder builder() {
        return new ClerkBuilder();
    }

    @Override
    public ClerkBuilder self() {
        return this;
    }

    @Override
    public Clerk build() {
        validateRequiredFields();
        return new Clerk(this);
    }
}
