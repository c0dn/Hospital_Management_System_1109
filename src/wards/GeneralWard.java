package wards;

/**
 * Represents a general ward in a hospital.
 */
public class GeneralWard extends AbstractWard {
    /**
     * Constructs a General Ward with the specified name, classification, and bed count.
     *
     * @param wardName      The name of the ward.
     * @param wardClassType The classification of the ward.
     * @param numberOfBeds  The number of beds in the ward.
     */
    public GeneralWard(String wardName, WardClassType wardClassType, int numberOfBeds) {
        super(wardName, wardClassType, numberOfBeds);
    }

}
