package wards;

import java.util.HashMap;

public class ClassAWard extends Ward {
    public ClassAWard(String name) {
        super(name, 500.0);
        this.beds = new HashMap<>(1);
    }
}
