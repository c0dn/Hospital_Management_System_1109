package wards;

import java.util.Map;

public abstract class Ward {
    protected String name;
    protected double dailyRate;
    protected Map<Integer, Bed> beds;


    public Ward(String name, double dailyRate) {
        this.name = name;
        this.dailyRate = dailyRate;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public String getWardIdentifier() {
        return String.format("%s-%s", name, beds.size());
    }

    public int getBedCount() {
        return beds.size();
    }
}


