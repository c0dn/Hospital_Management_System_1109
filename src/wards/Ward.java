package wards;

import java.util.Map;

/**
 * This class represents the ward types available in the insurance system.
 * It stores information such as the ward name, daily rate and bed number.
 * <p>
 *     The {@link Ward} class serves as a base class for specific ward types, such as
 *     {@link ClassAWard}, {@link ClassB1Ward}, {@link ClassB2Ward}, {@link ClassCWard}
 *     with properties and behaviors common to all wards.
 * </p>
 */

public abstract class Ward {
    protected String name;
    protected double dailyRate;
    protected Map<Integer, Bed> beds;


    /**
     * Constructs a new {@code Ward} with the Ward name and daily rate.
     * <p>
     * Initializes the ward name and daily rate, but does not initialize the collection of beds, which will be handled in subclasses.
     * </p>
     *
     * @param name The name of the ward.
     * @param dailyRate The daily rate for the ward's services.
     */
    public Ward(String name, double dailyRate) {
        this.name = name;
        this.dailyRate = dailyRate;
    }

    /**
     * Returns the daily rate for the ward's services.
     *
     * @return The daily rate of the ward.
     */
    public double getDailyRate() {
        return dailyRate;
    }

    /**
     * Returns a unique identifier for the ward, combining the ward's name and the number of beds.
     *
     * @return A string representing the unique identifier of the ward.
     */
    public String getWardIdentifier() {
        return String.format("%s-%s", name, beds.size());
    }

    /**
     * Returns the number of beds available in the ward.
     *
     * @return the count of beds in the ward.
     */
    public int getBedCount() {
        return beds.size();
    }
}


