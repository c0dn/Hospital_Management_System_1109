package wards;

import java.util.HashMap;

/**
 * Represents a Class B2 hospital ward.
 * <p>
 *     It is associated with a daily rate and a set of beds.
 *     The beds are stored in a HashMap with a single entry for initialization.
 * </p>
 * @see Ward
 */

public class ClassB2Ward extends Ward {
    /**
     * Constructs a new {@code ClassB2Ward} with the Ward name.
     * <p>
     * Initializes the ward with a daily rate of 100.0 and creates an empty map for the beds.
     * </p>
     *
     * @param name The name of the ward.
     */
    public ClassB2Ward(String name) {
        super(name, 100.0);
        this.beds = new HashMap<>(6);
    }
}
