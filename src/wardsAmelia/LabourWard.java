package wardsAmelia;

import java.util.HashMap;
import java.util.Map;

public class LabourWard extends AbstractWard{
    public LabourWard(String wardName, WardClass wardClass, double dailyRate) {
        super(wardName, wardClass, dailyRate);
        this.wardName = "Labour Ward";
    }

}
