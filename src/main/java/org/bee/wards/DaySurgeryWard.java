package org.bee.wards;

/**
 * Represents a specialized ward for day surgery procedures.
 */
public class DaySurgeryWard extends AbstractWard{
    /**
     * Constructs a Day Surgery Ward with the specified name, classification, and bed count.
     *
     * @param wardName      The name of the ward.
     * @param wardClassType The classification of the ward.
     * @param numberOfBeds  The number of beds in the ward.
     */
    public DaySurgeryWard(String wardName, WardClassType wardClassType, int numberOfBeds) {
        super(wardName, wardClassType, numberOfBeds);
    }

}
