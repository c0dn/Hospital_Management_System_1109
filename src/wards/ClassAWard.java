package wards;

import java.util.HashMap;

/**
 * Represents a Class A hospital ward.
 * <p>
 *     It is associated with a daily rate and a set of beds.
 *     The beds are stored in a HashMap with a single entry for initialization.
 * </p>
 * @see Ward
 */

public class ClassAWard extends Ward {
    /**
     * Constructs a new {@code ClassAWard} with the Ward name.
     * <p>
     * Initializes the ward with a daily rate of 500.0 and creates an empty map for the beds.
     * </p>
     *
     * @param name The name of the ward.
     */
    public ClassAWard(String name) {
        super(name, 500.0);
        this.beds = new HashMap<>(1);
    }
}
