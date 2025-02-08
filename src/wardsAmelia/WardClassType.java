package wardsAmelia;

public enum WardClassType {
    LABOUR_CLASS_A(1500, "Labour Class A"),
    LABOUR_CLASS_B1(1000, "Labour Class B1"),
    LABOUR_CLASS_B2(500, "Labour Class B2"),
    LABOUR_CLASS_C(250, "Labour Class C"),

    ICU_CLASS_A(200, "ICU Class A"),
    ICU_CLASS_B1(1500, "ICU Class B1"),
    ICU_CLASS_B2(1250, "ICU Class B2"),
    ICU_CLASS_C(1000, "ICU Class C"),

    DAY_SURGERY_CLASS_A(300.0, "Day Surgery Class A"),
    DAY_SURGERY_CLASS_B1(250.0, "Day Surgery Class B1"),
    DAY_SURGERY_CLASS_B2(200.0, "Day Surgery Class B2"),
    DAY_SURGERY_CLASS_C(150.0, "Day Surgery Class C"),

    GENERAL_CLASS_A(500, "General Class A"),
    GENERAL_CLASS_B1(250, "General Class B1"),
    GENERAL_CLASS_B2(200, "General Class B2"),
    GENERAL_CLASS_C(150, "General Class C");

    private final double dailyRate;
    private final String description;

    WardClassType(double dailyRate, String description) {
        this.dailyRate = dailyRate;
        this.description = description;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public String getDescription() {
        return description;
    }
}
