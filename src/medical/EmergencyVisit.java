package medical;

import humans.Patient;
import policy.AccidentType;
import utils.DataGenerator;

import java.time.LocalDateTime;

public class EmergencyVisit extends Visit {
    private AccidentType accidentType;
    // For hospital reference, should law enforcement or the courts require this information
    private String policeReportNumber;

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

    // Specific accident-related methods
    public AccidentType getAccidentType() {
        return accidentType;
    }

}