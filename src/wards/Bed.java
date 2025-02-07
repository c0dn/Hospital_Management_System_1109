package wards;

import humans.Patient;

/**
 * Represents a hospital bed assigned to a patient.
 */

public class Bed {
    private int bedNumber;
    private Patient currentPatient;

    public Bed(int bedNumber) {
        this.bedNumber = bedNumber;
        this.currentPatient = null;
    }

    public boolean isOccupied() {
        return currentPatient != null;
    }

    public void assignPatient(Patient patient) {
        this.currentPatient = patient;
    }
}