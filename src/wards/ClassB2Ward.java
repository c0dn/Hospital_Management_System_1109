package wards;

import java.util.HashMap;

/**
 * Represents a Class B2 hospital ward.
 */

public class ClassB2Ward extends Ward {
    public ClassB2Ward(String name) {
        super(name, 100.0);
        this.beds = new HashMap<>(6);
    }
}
