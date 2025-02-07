package wards;

import java.util.HashMap;

/**
 * Represents a Class B1 hospital ward.
 */

public class ClassB1Ward extends Ward {
    public ClassB1Ward(String name) {
        super(name, 250.0);
        this.beds = new HashMap<>(4);
    }
}
