package wards;

import humans.Patient;

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

    // Getter method to return bed number
    public int getBedNumber() {
        return bedNumber;
    }

    public void assignPatient(Patient patient) {
        this.currentPatient = patient;
    }

    @Override
    public String toString() {
        String patientName = (currentPatient != null) ? currentPatient.getName() : "No patient assigned";
        return "Bed " + bedNumber + " - " + patientName;
    }
}