package wardsAmelia;

public enum WardClassRate {
    LABOUR_CLASS_A(1500),
    LABOUR_CLASS_B1(1000),
    LABOUR_CLASS_B2(500),
    LABOUR_CLASS_C(250),

    ICU_CLASS_A(200),
    ICU_CLASS_B1(1500),
    ICU_CLASS_B2(1250),
    ICU_CLASS_C(1000),

    DAY_SURGERY_CLASS_A(300.0),
    DAY_SURGERY_CLASS_B1(250.0),
    DAY_SURGERY_CLASS_B2(200.0),
    DAY_SURGERY_CLASS_C(150.0),

    GENERAL_CLASS_A(500),
    GENERAL_CLASS_B1(250),
    GENERAL_CLASS_B2(200),
    GENERAL_CLASS_C(150);
    private final double dailyRate;

    WardClassRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    // Optional: Method to get the rate by ward and class type.
    public static WardClassRate getRate(String wardType, WardClass wardClass) {
        String key = wardType.toUpperCase() + "_" + wardClass.name();
        for (WardClassRate rate : WardClassRate.values()) {
            if (rate.name().equals(key)) {
                return rate;
            }
        }
        throw new IllegalArgumentException("Invalid ward type or class.");
    }
}
