package wards;

import humans.Patient;

/**
 * Represents a hospital bed assigned to a patient.
 * <p>
 *     Each bed has a unique bed number and can be assigned to a {@link Patient}. The bed can be either occupied or unoccupied.
 * </p>
 *
 * <p>
 *     <li>Tracks the bed number.</li>
 *     <li>Checks if the bed is occupied.</li>
 *     <li>Allows assigning a patient to the bed.</li>
 * </p>
 */

public class Bed {
    /** The bed number. */
    private int bedNumber;
    /** The patient assigned to the bed. */
    private Patient currentPatient;

    /**
     * Constructs a new Bed with a specified bed number.
     * Initially, the bed is unoccupied.
     *
     * @param bedNumber The unique identifier for the bed.
     */
    public Bed(int bedNumber) {
        this.bedNumber = bedNumber;
        this.currentPatient = null;
    }

    /**
     * Checks if the bed is currently occupied by a patient.
     *
     * @return {@code true} if a patient is assigned to the bed, {@code false} otherwise.
     */
    public boolean isOccupied() {
        return currentPatient != null;
    }

    /**
     * Assigns a patient to this bed.
     *
     * @param patient The patient to be assigned to the bed.
     */
    public void assignPatient(Patient patient) {
        this.currentPatient = patient;
    }
}