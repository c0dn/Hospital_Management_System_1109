package org.bee.hms.wards;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an abstract ward in a hospital.
 * Implements common ward functionalities and attributes.
 */
public class AbstractWard implements Ward {
    /** The name of the ward. */
    protected String wardName;
    /** The classification type of the ward. */
    private final WardClassType wardClassType;
    /** A mapping of bed numbers to bed objects in the ward. */
    private final Map<Integer, Bed> beds;

    /**
     * The type of ward, used for JSON serialization/deserialization.
     */
    protected String type;

    /**
     * Constructs an AbstractWard with the given name, type, and number of beds.
     *
     * @param wardName      The name of the ward.
     * @param wardClassType The classification of the ward.
     * @param numberOfBeds  The number of beds available in the ward.
     */
    public AbstractWard(String wardName, WardClassType wardClassType, int numberOfBeds) {
        this.wardName = wardName;
        this.wardClassType = wardClassType;
        this.beds = new HashMap<>();

        for (int i = 1; i <= numberOfBeds; i++) {
            beds.put(i, new Bed(i));
        }
    }

    /**
     * Retrieves the name of the ward.
     *
     * @return The ward name.
     */
    @Override
    public String getWardName() {
        return wardName;
    }

    /**
     * Retrieves the daily rate for staying in this ward.
     *
     * @return The daily rate of the ward.
     */
    @Override
    public double getDailyRate() {
        return wardClassType.getDailyRate();
    }

    /**
     * Retrieves a map of bed numbers to beds.
     *
     * @return A map containing bed numbers and their corresponding bed objects.
     */
    @Override
    public Map<Integer, Bed> getBeds() {
        return beds;
    }

}
