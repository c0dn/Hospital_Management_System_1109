package wards;

import people.Patient;

/**
 * Represents a hospital bed that can be assigned to a patient.
 */
public class Bed {
    /** The bed number within the ward. */
    private int bedNumber;
    /** The patient currently occupying the bed (null if unoccupied). */
    private Patient currentPatient;
    /**
     * Constructs a new bed with the given bed number.
     *
     * @param bedNumber The bed number assigned to this bed.
     */
    public Bed(int bedNumber) {
        this.bedNumber = bedNumber;
        this.currentPatient = null;
    }

    /**
     * Checks if the bed is currently occupied.
     *
     * @return {@code true} if the bed has a patient assigned, otherwise {@code false}.
     */
    public boolean isOccupied() {
        return currentPatient != null;
    }

    /**
     * Assigns a patient to this bed.
     *
     * @param patient The patient to be assigned.
     */
    public void assignPatient(Patient patient) {
        this.currentPatient = patient;
    }

    /**
     * Returns a string representation of the bed and its current occupancy.
     *
     * @return A string displaying the bed number and assigned patient (if any).
     */
    @Override
    public String toString() {
        String patientName = (currentPatient != null) ? currentPatient.getName() : "No patient assigned";
        return "Bed " + bedNumber + " - " + patientName;
    }
}