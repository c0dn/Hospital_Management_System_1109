package wards;

import java.util.HashMap;

/**
 * Represents a Class C hospital ward.
 * <p>
 *     It is associated with a daily rate and a set of beds.
 *     The beds are stored in a HashMap with a single entry for initialization.
 * </p>
 * @see Ward
 */

public class ClassCWard extends Ward {
    /**
     * Constructs a new {@code ClassCWard} with the Ward name.
     * <p>
     * Initializes the ward with a daily rate of 50.0 and creates an empty map for the beds.
     * </p>
     *
     * @param name The name of the ward.
     */
    public ClassCWard(String name) {
        super(name, 50.0);
        this.beds = new HashMap<>(8);
    }
}
