package wardsAmelia;

import java.util.HashMap;
import java.util.Map;

public class AbstractWard implements Ward{
    protected String wardName;
    private WardClass wardClass;
    private double dailyRate;
    private Map<Integer, Bed> beds;

    public AbstractWard(String wardName, WardClass wardClass, double dailyRate) {
        this.wardName = wardName;
        this.wardClass = wardClass;
        this.dailyRate = dailyRate;
        this.beds = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            beds.put(i, new Bed(i));  // Assuming 5 beds for example
        }
    }

    @Override
    public String getWardName() { return wardName; }

    @Override
    public double getDailyRate() { return dailyRate; }

    @Override
    public Map<Integer, Bed> getBeds() { return beds; }

//    public void setWardName(String wardName) {
//        this.wardName = wardName;
//    }
}
