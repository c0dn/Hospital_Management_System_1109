package org.bee.hms.humans;

import org.bee.utils.JSONReadable;
import org.bee.utils.JSONWritable;

/**
 * Represents a clerk in the hospital management system.
 * Clerks are administrative staff members responsible for managing
 * paperwork, appointments, and other administrative tasks.
 */
public class Clerk extends Staff implements JSONReadable, JSONWritable{
    /**
     * Creates a new instance of ClerkBuilder to construct a Clerk object.
     *
     * @return A new ClerkBuilder instance
     */
    public static ClerkBuilder builder() {
        return ClerkBuilder.builder();
    }

    /**
     * Constructs a Clerk object using the provided StaffBuilder.
     * Uses the parent Staff class constructor to initialize staff-specific
     * details and inherited human attributes.
     *
     * @param builder The builder instance of type {@code StaffBuilder<?>} used to initialize
     *               the Clerk object.
     */
    Clerk(StaffBuilder<?> builder) {
        super(builder);
        this.type = "clerk";
    }
}
