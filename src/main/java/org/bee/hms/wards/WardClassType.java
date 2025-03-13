package org.bee.hms.wards;

/**
 * Defines different classifications of hospital wards and their respective daily rates.
 * Each ward class has a specific cost per day and a description.
 */
public enum WardClassType {
    /** Labour Ward Class A - Cost: $1500 per day. */
    LABOUR_CLASS_A(1500, "Labour Class A"),
    /** Labour Ward Class B1 - Cost: $1000 per day. */
    LABOUR_CLASS_B1(1000, "Labour Class B1"),
    /** Labour Ward Class B2 - Cost: $500 per day. */
    LABOUR_CLASS_B2(500, "Labour Class B2"),
    /** Labour Ward Class C - Cost: $250 per day. */
    LABOUR_CLASS_C(250, "Labour Class C"),

    /** Intensive Care Unit (ICU) - Cost: $2000 per day. */
    ICU(2000, "ICU"),

    /** Day Surgery Seater - Cost: $300 per day. */
    DAYSURGERY_CLASS_SEATER(300.0, "Day Surgery Seater"),
    /** Day Surgery Bed Cohort - Cost: $250 per day. */
    DAYSURGERY_CLASS_COHORT(250.0, "Day Surgery Bed Cohort"),
    /** Day Surgery Bed Single - Cost: $200 per day. */
    DAYSURGERY_CLASS_SINGLE(200.0, "Day Surgery Bed Single"),

    /** General Ward Class A - Cost: $500 per day. */
    GENERAL_CLASS_A(500, "General Class A"),
    /** General Ward Class B1 - Cost: $250 per day. */
    GENERAL_CLASS_B1(250, "General Class B1"),
    /** General Ward Class B2 - Cost: $200 per day. */
    GENERAL_CLASS_B2(200, "General Class B2"),
    /** General Ward Class C - Cost: $150 per day. */
    GENERAL_CLASS_C(150, "General Class C");

    /** The daily rate for the ward type. */
    private final double dailyRate;
    /** A description of the ward type. */
    private final String description;

    /**
     * Constructs a WardClassType with the specified daily rate and description.
     *
     * @param dailyRate   The cost per day for staying in the ward.
     * @param description A description of the ward class.
     */
    WardClassType(double dailyRate, String description) {
        this.dailyRate = dailyRate;
        this.description = description;
    }

    /**
     * Retrieves the daily rate of the ward.
     *
     * @return The daily rate.
     */
    public double getDailyRate() {
        return dailyRate;
    }

    /**
     * Retrieves the description of the ward class.
     *
     * @return The ward class description.
     */
    public String getDescription() {
        return description;
    }
}
