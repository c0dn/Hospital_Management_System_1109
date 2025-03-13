package org.bee.hms.wards;

import java.util.Map;

/**
 * Represents a general hospital ward.
 * This interface defines the essential methods that all ward types must implement.
 */
public interface Ward {
    /**
     * Retrieves the name of the ward.
     *
     * @return The ward name.
     */
    String getWardName();
    /**
     * Retrieves the daily rate for staying in this ward.
     *
     * @return The daily rate of the ward.
     */
    double getDailyRate();
    /**
     * Retrieves a map of bed numbers to their corresponding beds.
     *
     * @return A map containing bed numbers and bed objects.
     */
    Map<Integer, Bed> getBeds();
}
