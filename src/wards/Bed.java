package wards;

import humans.Patient;

public class Bed {
    private int bedNumber;
    private Patient currentPatient;
    private boolean isOccupied;

    public boolean isOccupied() {
        return currentPatient != null;
    }

    public void assignPatient(Patient patient) {
        // Assign patient to bed
    }
}