package wards;

import java.util.HashMap;

public class ClassCWard extends Ward {
    public ClassCWard(String name) {
        super(name, 50.0);
        this.beds = new HashMap<>(8);
    }
}
