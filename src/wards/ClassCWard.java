package wards;

import java.util.HashMap;

/**
 * Represents a Class C hospital ward.
 */

public class ClassCWard extends Ward {
    public ClassCWard(String name) {
        super(name, 50.0);
        this.beds = new HashMap<>(8);
    }
}
