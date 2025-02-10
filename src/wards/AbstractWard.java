package wards;

import java.util.HashMap;
import java.util.Map;

public class AbstractWard implements Ward {
    protected String wardName;
    private WardClassType wardClassType;
    private Map<Integer, Bed> beds;

    public AbstractWard(String wardName, WardClassType wardClassType, int numberOfBeds) {
        this.wardName = wardName;
        this.wardClassType = wardClassType;
        this.beds = new HashMap<>();

        for (int i = 1; i <= numberOfBeds; i++) {
            beds.put(i, new Bed(i));
        }
    }

    @Override
    public String getWardName() {
        return wardName;
    }

    @Override
    public double getDailyRate() {
        return wardClassType.getDailyRate();
    }


    @Override
    public Map<Integer, Bed> getBeds() {
        return beds;
    }

}
