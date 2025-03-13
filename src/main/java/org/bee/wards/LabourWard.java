package org.bee.wards;

/**
 * Represents a Labour Ward in a hospital.
 * This ward is designated for childbirth and maternal care.
 */
public class LabourWard extends AbstractWard{
    /**
     * Constructs a Labour Ward with the specified name, classification, and bed count.
     *
     * @param wardName      The name of the Labour ward.
     * @param wardClassType The classification of the ward.
     * @param numberOfBeds  The number of beds available in the Labour ward.
     */
    public LabourWard(String wardName, WardClassType wardClassType, int numberOfBeds) {
        super(wardName, wardClassType, numberOfBeds);
    }

}
