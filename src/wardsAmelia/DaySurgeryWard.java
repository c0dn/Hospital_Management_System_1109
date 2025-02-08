package wardsAmelia;

import java.util.HashMap;
import java.util.Map;

public class DaySurgeryWard extends AbstractWard{
    public DaySurgeryWard(String wardName, WardClass wardClass, double dailyRate) {
        super(wardName, wardClass, dailyRate);
        this.wardName = "Day Surgery Ward";
    }

}
