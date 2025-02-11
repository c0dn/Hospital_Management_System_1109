package medical;

import billing.BillableItem;
import humans.Doctor;
import humans.Nurse;
import humans.Patient;
import utils.DataGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an inpatient hospital visit.
 * The class encapsulates information related to the visit, including ward stays,
 * procedures performed, attending medical personnel, and the status of the visit.
 */
public class Visit {
    /**
     * A unique identifier for the visit.
     * This identifier is used to distinguish each hospital visit
     * and associate it with relevant records, such as ward stays,
     * procedures, prescriptions, and the attending medical staff.
     */
    private String visitId;
    /**
     * The date and time when the patient was admitted for the visit.
     */
    private LocalDateTime admissionDateTime;
    /**
     * Records the date and time when the patient was discharged from their hospital visit.
     */
    private LocalDateTime dischargeDateTime;
    /**
     * Represents a collection of ward stays during a patient's visit.
     * Each ward stay captures the details of the patient's stay in a specific hospital ward,
     * encompassing information such as the ward, start, and end times.
     * This list is used to track all ward transfers and stays for the duration of the visit.
     */
    private List<WardStay> wardStays;
    /**
     * A collection of procedure codes representing the medical procedures performed during an inpatient visit.
     * Each procedure is identified by a {@link ProcedureCode} object, which includes details like
     * code, description, category, and associated cost.
     *
     * This list is used to calculate the charges for all inpatient procedures during the visit
     * and to provide a detailed breakdown of the medical services rendered.
     */
    private List<ProcedureCode> inpatientProcedures;

    /**
     * Represents a list of diagnostic codes associated with a visit.
     *
     * This list holds diagnostic codes, where each code corresponds to a specific diagnosis
     * classified under the ICD-10 standard. These codes are used for billing, medical
     * documentation, and other healthcare-related purposes.
     */
    private List<DiagnosticCode> diagnosticCodes;

    /**
     * Represents the primary attending doctor supervising the inpatient's care
     */
    private Doctor AttendingDoctor;
    /**
     * Represents the collection of nurses who are assigned to attend to the patient
     */
    private List<Nurse> AttendingNurses;
    /**
     * A map representing the prescriptions associated with a visit.
     * Each entry maps a specific medication to its prescribed quantity.
     *
     * Key: {@link Medication} instance representing the prescribed medication.
     * Value: An {@code Integer} indicating the quantity prescribed.
     */
    private Map<Medication, Integer> prescriptions;
    /**
     * Represents the current status of a hospital visit.
     *
     * <ul>
     *     <li>ADMITTED: The patient has been admitted to the hospital.</li>
     *     <li>IN_PROGRESS: The visit is currently ongoing.</li>
     *     <li>DISCHARGED: The patient has been discharged from the hospital.</li>
     *     <li>CANCELLED: The visit has been cancelled.</li>
     * </ul>
     * This field is used to track and update the lifecycle of the hospital visit.
     */
    private VisitStatus status;
    private Patient patient;


    Visit(LocalDateTime admissionDateTime, Patient patient) {
        this.visitId = generateVisitId();
        this.admissionDateTime = admissionDateTime;
        this.patient = patient;
        this.status = VisitStatus.ADMITTED;
        this.wardStays = new ArrayList<>();
        this.inpatientProcedures = new ArrayList<>();
        this.diagnosticCodes = new ArrayList<>();
        this.AttendingNurses = new ArrayList<>();
        this.prescriptions = new HashMap<>();
    }


    private String generateVisitId() {
        return "V" + System.currentTimeMillis() +
                String.format("%04d", DataGenerator.getInstance().generateRandomInt(10000));
    }


    /**
     * Creates a new visit for a patient
     *
     * @param admissionDateTime The admission date and time
     * @param patient The patient being admitted
     * @return A new Visit instance
     */
    public static Visit createNew(LocalDateTime admissionDateTime, Patient patient) {
        return new Visit(admissionDateTime, patient);
    }


    protected static <T extends Visit> T populateWithRandomData(T visit) {
        DataGenerator gen = DataGenerator.getInstance();

        // Assign staff
        visit.assignDoctor(Doctor.builder().withRandomBaseData().build());
        int nurseCount = gen.generateRandomInt(1, 4);
        for (int i = 0; i < nurseCount; i++) {
            visit.assignNurse(Nurse.builder().withRandomBaseData().build());
        }

        // Add medications
        int medicationCount = gen.generateRandomInt(1, 5);
        for (int i = 0; i < medicationCount; i++) {
            visit.prescribeMedicine(gen.getRandomMedication(),
                    gen.generateRandomInt(1, 10));
        }

        return visit;
    }

    /**
     * Creates a regular visit with random data
     *
     * @return A randomly populated Visit instance
     */
    public static Visit withRandomData() {
        DataGenerator gen = DataGenerator.getInstance();
        LocalDateTime admissionTime = LocalDateTime.now()
                .minusDays(gen.generateRandomInt(1, 30));
        Patient randomPatient = Patient.builder().withRandomBaseData().build();

        return populateWithRandomData(new Visit(admissionTime, randomPatient));
    }




    /**
     * Prescribes medication for the patient during their visit.
     *
     * @param medication The medication to prescribe
     * @param quantity The quantity to prescribe
     * @throws IllegalStateException if the visit is not in an active state
     */
    public void prescribeMedicine(Medication medication, int quantity) {
        if (status != VisitStatus.ADMITTED && status != VisitStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot prescribe medication for a non-active visit");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        prescriptions.merge(medication, quantity, Integer::sum);
    }

    /**
     * Adds a diagnostic code to the visit record.
     *
     * @param diagnosticCode The diagnostic code to add
     * @throws IllegalStateException if the visit is not in an active state
     */
    public void diagnose(DiagnosticCode diagnosticCode) {
        if (status != VisitStatus.ADMITTED && status != VisitStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot add diagnosis for a non-active visit");
        }

        if (!diagnosticCodes.contains(diagnosticCode)) {
            diagnosticCodes.add(diagnosticCode);
        }
    }

    /**
     * Adds a procedure to the visit record.
     *
     * @param procedureCode The procedure to be performed
     * @throws IllegalStateException if the visit is not in an active state
     */
    public void procedure(ProcedureCode procedureCode) {
        if (status != VisitStatus.ADMITTED && status != VisitStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot add procedure for a non-active visit");
        }

        inpatientProcedures.add(procedureCode);
    }

    /**
     * Assigns a nurse to attend to the patient.
     *
     * @param nurse The nurse to be assigned
     * @throws IllegalStateException if the visit is not in an active state
     */
    public void assignNurse(Nurse nurse) {
        if (status != VisitStatus.ADMITTED && status != VisitStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot assign nurse for a non-active visit");
        }

        if (!AttendingNurses.contains(nurse)) {
            AttendingNurses.add(nurse);
        }
    }

    /**
     * Assigns a doctor as the primary attending physician.
     *
     * @param doctor The doctor to be assigned
     * @throws IllegalStateException if the visit is not in an active state
     */
    public void assignDoctor(Doctor doctor) {
        if (status != VisitStatus.ADMITTED && status != VisitStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot assign doctor for a non-active visit");
        }

        this.AttendingDoctor = doctor;
    }



    public BigDecimal calculateCharges() {
        BigDecimal total = BigDecimal.ZERO;

        if (wardStays != null) {
            total = total.add(wardStays.stream()
                    .map(WardStay::calculateCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (inpatientProcedures != null) {
            total = total.add(inpatientProcedures.stream()
                    .map(ProcedureCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (prescriptions != null) {
            total = total.add(prescriptions.entrySet().stream()
                    .map(entry -> {
                        Medication med = entry.getKey();
                        int quantity = entry.getValue();
                        return med.calculateCost(quantity);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return total;
    }


    /**
     * Returns all related charges as separate BillableItems
     * This includes diagnostics, procedures, and medications with their quantities
     */
    public List<BillableItem> getRelatedBillableItems() {
        List<BillableItem> items = new ArrayList<>();

        // Add diagnostics
        if (diagnosticCodes != null) {
            items.addAll(diagnosticCodes);
        }

        // Add procedures
        if (inpatientProcedures != null) {
            items.addAll(inpatientProcedures);
        }

        // Add medications with their quantities as MedicationBillableItem
        if (prescriptions != null) {
            prescriptions.forEach((medication, quantity) ->
                    items.add(new MedicationBillableItem(medication, quantity, true)));
        }

        return items;
    }


}

