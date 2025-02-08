package wardsAmelia;

import java.util.HashMap;
import java.util.Map;

public class GeneralWard extends AbstractWard {
    public GeneralWard(String wardName, WardClass wardClass, double dailyRate) {
        super(wardName, wardClass, dailyRate);
        this.wardName = "General Ward";
    }

}
