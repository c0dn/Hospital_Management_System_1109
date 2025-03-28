package org.bee.hms.medical;

import java.time.LocalDateTime;

import org.bee.hms.humans.Patient;
import org.bee.hms.policy.AccidentType;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONSerializable;


/**
 * Represents an emergency visit for a patient, which includes details about the accident type,
 * police report number (if applicable), and the admission details.
 * This class extends {@link Visit} to inherit general visit-related properties.
 */

public class EmergencyVisit extends Visit implements JSONSerializable {
    /** The type of accident */
    private final AccidentType accidentType;
    // For hospital reference, should law enforcement or the courts require this information
    /**
     * Police report reference number for legal/hospital documentation purposes
     * For hospital reference, should law enforcement or the courts require this information
     */
    private String policeReportNumber;

    /**
     * Private constructor to initialize an EmergencyVisit with the given accident type,
     * admission date and time, and patient.
     *
     * @param type The type of accident that caused the emergency visit.
     * @param admissionDateTime The date and time of admission to the emergency department.
     * @param patient The patient associated with the emergency visit.
     */
    private EmergencyVisit(AccidentType type, LocalDateTime admissionDateTime, Patient patient) {
        super(admissionDateTime, patient);
        this.accidentType = type;
    }



    /**
     * Creates an emergency visit with random data
     *
     * @return A randomly populated EmergencyVisit instance
     */

    public static EmergencyVisit withRandomData() {
        LocalDateTime admissionTime = LocalDateTime.now()
                .minusDays(DataGenerator.generateRandomInt(1, 30));
        Patient randomPatient = Patient.builder().withRandomBaseData().patientId(DataGenerator.generatePatientId()).build();
        AccidentType accidentType = DataGenerator.getRandomElement(AccidentType.values());

        EmergencyVisit visit = new EmergencyVisit(accidentType,
                admissionTime, randomPatient);
        visit.policeReportNumber = visit.generatePoliceReportNumber();

        return populateWithRandomData(visit);
    }



    /**
     * Generates a random police report number
     *
     * @return A formatted police report number
     */
    private String generatePoliceReportNumber() {
        return String.format("PR%d/%d",
                DataGenerator.generateRandomInt(1000, 9999),
                LocalDateTime.now().getYear()
        );
    }

    /**
     * Gets the accident type associated with this emergency visit.
     *
     * @return The type of accident that caused the emergency visit.
     */
    public AccidentType getAccidentType() {
        return accidentType;
    }
}
