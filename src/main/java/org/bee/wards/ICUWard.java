package org.bee.wards;

/**
 * Represents an Intensive Care Unit (ICU) Ward in a hospital.
 * This ward is meant for critically ill patients who require constant monitoring and intensive care.
 */
public class ICUWard extends AbstractWard{
    /**
     * Constructs an ICU Ward with the specified name, classification, and bed count.
     *
     * @param wardName      The name of the ICU ward.
     * @param wardClassType The classification of the ward.
     * @param numberOfBeds  The number of beds available in the ICU ward.
     */
    public ICUWard(String wardName, WardClassType wardClassType, int numberOfBeds) {
        super(wardName, wardClassType, numberOfBeds);
    }
}
