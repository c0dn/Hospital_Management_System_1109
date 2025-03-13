package org.bee.medical;

import java.time.LocalDateTime;

import org.bee.humans.Patient;
import org.bee.policy.AccidentType;
import org.bee.utils.DataGenerator;


/**
 * Represents an emergency visit for a patient, which includes details about the accident type,
 * police report number (if applicable), and the admission details.
 * This class extends {@link Visit} to inherit general visit-related properties.
 */

public class EmergencyVisit extends Visit {
    private AccidentType accidentType;
    // For hospital reference, should law enforcement or the courts require this information
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
        DataGenerator gen = DataGenerator.getInstance();
        LocalDateTime admissionTime = LocalDateTime.now()
                .minusDays(gen.generateRandomInt(1, 30));
        Patient randomPatient = Patient.builder().withRandomBaseData().patientId(gen.generatePatientId()).build();
        AccidentType accidentType = gen.getRandomElement(AccidentType.values());

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
                DataGenerator.getInstance().generateRandomInt(1000, 9999),
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
