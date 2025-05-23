package org.bee.hms.medical;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.Coverage;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONSerializable;
import org.bee.utils.jackson.PrescriptionMapDeserializer;
import org.bee.utils.jackson.PrescriptionMapSerializer;

/**
 * Represents an inpatient hospital visit.
 * The class encapsulates information related to the visit, including ward stays,
 * procedures performed, attending medical personnel, and the status of the visit.
 */
public class Visit implements JSONSerializable {
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
     * <p>
     * This list is used to calculate the charges for all inpatient procedures during the visit
     * and to provide a detailed breakdown of the medical services rendered.
     */
    private List<ProcedureCode> inpatientProcedures;

    /**
     * Represents a list of diagnostic codes associated with a visit.
     * <p>
     * This list holds diagnostic codes, where each code corresponds to a specific diagnosis
     * classified under the ICD-10 standard. These codes are used for billing, medical
     * documentation, and other healthcare-related purposes.
     */
    private List<DiagnosticCode> diagnosticCodes;

    /** The doctor assigned for patient treatment */
    private Doctor attendingDoc;

    /**
     * The nursing staff assigned to assist with the patient's care during this visit
     * <p>
     * This list contains all nurses responsible for providing nursing care,
     * administering treatments, and monitoring the patient's condition
     * </p>
     */
    private List<Nurse> attendingNurses;

    /**
     * A map representing the prescriptions associated with a visit.
     * Each entry maps a specific medication to its prescribed quantity.
     * <p>
     * Key: {@link Medication} instance representing the prescribed medication.
     * Value: An {@code Integer} indicating the quantity prescribed.
     */
    @JsonSerialize(using = PrescriptionMapSerializer.class)
    @JsonDeserialize(using = PrescriptionMapDeserializer.class)
    @JsonProperty("prescriptions")
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

    /** The patient associated with this medical record */
    private Patient patient;

    /**
     * Default constructor for JSON deserialization.
     */
    public Visit() {
        // Default constructor for JSON deserialization
    }

    /**
     * Constructs a new Visit instance with the provided admission date and patient details.
     * This constructor sets the initial visit status to ADMITTED and initializes all related data structures.
     *
     * @param admissionDateTime The date and time when the patient is admitted.
     * @param patient The patient associated with the visit.
     */
    Visit(LocalDateTime admissionDateTime, Patient patient) {
        this.visitId = generateVisitId();
        this.admissionDateTime = admissionDateTime;
        this.patient = patient;
        this.status = VisitStatus.ADMITTED;
        this.wardStays = new ArrayList<>();
        this.inpatientProcedures = new ArrayList<>();
        this.diagnosticCodes = new ArrayList<>();
        this.attendingNurses = new ArrayList<>();
        this.prescriptions = new HashMap<>();
    }

    /**
     * Generates a unique visit ID using the current system time and a random integer.
     *
     * @return A unique visit ID string.
     */
    private String generateVisitId() {
        return "V" + System.currentTimeMillis() +
                String.format("%04d", DataGenerator.generateRandomInt(10000));
    }


    /**
     * Creates a new visit for a patient
     *
     * @param admissionDateTime The admission date and time
     * @param patient           The patient being admitted
     * @return A new Visit instance
     */
    public static Visit createNew(LocalDateTime admissionDateTime, Patient patient) {
        return new Visit(admissionDateTime, patient);
    }


    protected static <T extends Visit> T populateWithRandomData(T visit) {

        visit.assignDoctor(Doctor.builder().withRandomBaseData().build());
        int nurseCount = DataGenerator.generateRandomInt(1, 4);
        for (int i = 0; i < nurseCount; i++) {
            visit.assignNurse(Nurse.builder().withRandomBaseData().build());
        }

        int medicationCount = DataGenerator.generateRandomInt(1, 5);
        for (int i = 0; i < medicationCount; i++) {
            visit.prescribeMedicine(DataGenerator.getRandomMedication(),
                    DataGenerator.generateRandomInt(1, 10));
        }

        int procedureCount = DataGenerator.generateRandomInt(1, 5);
        for (int i = 0; i < procedureCount; i++) {
            visit.procedure(ProcedureCode.getRandomCode());
        }

        int diagnoseCount = DataGenerator.generateRandomInt(1, 5);
        for (int i = 0; i < diagnoseCount; i++) {
            visit.diagnose(DiagnosticCode.getRandomCode());
        }

        return visit;
    }

    /**
     * Creates a visit with randomly generated data.
     * <p>
     * This method generates a random patient, sets an admission time within the past 30 days,
     * and populates the visit with random medical data including procedures, prescriptions,
     * diagnostic codes, and medical staff.
     * </p>
     *
     * @return A randomly populated Visit instance
     * @see #withRandomData(Patient) to create a random visit for a specific patient
     */
    public static Visit withRandomData() {
        LocalDateTime admissionTime = LocalDateTime.now()
                .minusDays(DataGenerator.generateRandomInt(1, 30));
        Patient randomPatient = Patient
                .builder()
                .patientId(DataGenerator.generatePatientId())
                .withRandomBaseData()
                .build();

        return populateWithRandomData(new Visit(admissionTime, randomPatient));
    }

    /**
     * Creates a visit with randomly generated data for a specific patient.
     * <p>
     * This method sets an admission time within the past 30 days and populates
     * the visit with random medical data including procedures, prescriptions,
     * diagnostic codes, and medical staff, while using the provided patient.
     * </p>
     *
     * @param patient The patient for whom the visit is created (must not be null)
     * @return A randomly populated Visit instance for the specified patient
     * @throws NullPointerException if the patient is null
     * @see #withRandomData() to create a random visit with a random patient
     */
    public static Visit withRandomData(Patient patient) {
        Objects.requireNonNull(patient, "Patient cannot be null");

        LocalDateTime admissionTime = LocalDateTime.now()
                .minusDays(DataGenerator.generateRandomInt(1, 30));

        return populateWithRandomData(new Visit(admissionTime, patient));
    }
    
    /**
     * Creates a visit that is compatible with the given coverage.
     * This method ensures the visit will have appropriate diagnoses, procedures,
     * and ward stays that align with the provided insurance coverage.
     * NOTE: This method is used for testing purposes
     *
     * @param coverage The insurance coverage
     * @param patient The patient for this visit
     * @param availableDoctors List of doctors to choose from
     * @param availableNurses List of nurses to choose from
     * @return A visit that will be covered by the policy
     */
    public static Visit createCompatibleVisit(Coverage coverage, Patient patient,
                                              List<Doctor> availableDoctors,
                                              List<Nurse> availableNurses) {

        LocalDateTime admissionTime = LocalDateTime.now().minusDays(DataGenerator.generateRandomInt(30, 90));
        Visit visit = Visit.createNew(admissionTime, patient);

        Doctor randomDoctor = DataGenerator.getRandomElement(availableDoctors);
        Nurse randomNurse = DataGenerator.getRandomElement(availableNurses);

        // Assign medical staff
        visit.assignDoctor(randomDoctor);
        visit.assignNurse(randomNurse);

        Set<BenefitType> coveredBenefits = coverage.getCoveredBenefits();
        BigDecimal deductible = coverage.getDeductibleAmount();
        BigDecimal minTarget = deductible.multiply(new BigDecimal("1.5"));
        BigDecimal maxTarget = deductible.multiply(new BigDecimal("3.0"));
        // Add a tolerance of 5% to the max target to prevent unnecessary adjustments
        BigDecimal maxTargetWithTolerance = maxTarget.multiply(new BigDecimal("1.05"));

        // Continue adding items until we reach the target
        BigDecimal totalCharges = BigDecimal.ZERO;

        while (totalCharges.compareTo(minTarget) < 0) {
            // We are attempting to create a visit that will have items that is covered
            BenefitType selectedBenefitType = DataGenerator.getRandomElement(coveredBenefits);

            // Add diagnostic code for the selected benefit type
            try {
                DiagnosticCode diagnosticCode = DiagnosticCode.getRandomCodeForBenefitType(selectedBenefitType, true);
                visit.diagnose(diagnosticCode);
            } catch (IllegalArgumentException e) {
                DiagnosticCode diagnosticCode = DiagnosticCode.getRandomCode();
                visit.diagnose(diagnosticCode);
            }

            // Add procedure code for the selected benefit type
            try {
                ProcedureCode procedureCode = ProcedureCode.getRandomCodeForBenefitType(selectedBenefitType, true);
                visit.procedure(procedureCode);
            } catch (IllegalArgumentException e) {
                ProcedureCode procedureCode = ProcedureCode.getRandomCode();
                visit.procedure(procedureCode);
            }

            // Add medication
            Medication medication = DataGenerator.getRandomMedication();
            visit.prescribeMedicine(medication, 1);

            visit.addRandomWardStay(selectedBenefitType);

            totalCharges = visit.calculateCharges();
        }

        // Continue adding items until we reach the minimum target
        int attemptCount = 0;
        final int MAX_ATTEMPTS = 50; // Prevent infinite loops

        while (totalCharges.compareTo(minTarget) < 0 && attemptCount < MAX_ATTEMPTS) {
            attemptCount++;
            try {
                // Get a random covered benefit type
                BenefitType selectedBenefitType = DataGenerator.getRandomElement(coveredBenefits);

                // Add diagnostic code for the selected benefit type
                try {
                    DiagnosticCode diagnosticCode = DiagnosticCode.getRandomCodeForBenefitType(selectedBenefitType, true);
                    visit.diagnose(diagnosticCode);
                } catch (Exception e) {
                    try {
                        DiagnosticCode diagnosticCode = DiagnosticCode.getRandomCode();
                        visit.diagnose(diagnosticCode);
                    } catch (Exception ex) {
                        continue;
                    }
                }

                // Add procedure code for the selected benefit type
                try {
                    ProcedureCode procedureCode = ProcedureCode.getRandomCodeForBenefitType(selectedBenefitType, true);
                    visit.procedure(procedureCode);
                } catch (Exception e) {
                    try {
                        ProcedureCode procedureCode = ProcedureCode.getRandomCode();
                        visit.procedure(procedureCode);
                    } catch (Exception ex) {
                        continue;
                    }
                }

                // Add medication
                try {
                    Medication medication = DataGenerator.getRandomMedication();
                    visit.prescribeMedicine(medication, 1);
                } catch (Exception e) {
                    continue;
                }

                try {
                    visit.addRandomWardStay(selectedBenefitType);
                } catch (Exception e) {
                    continue;
                }

                totalCharges = visit.calculateCharges();
            } catch (Exception e) {
                System.err.println("Error adding items to visit: " + e.getMessage());
            }
        }

        // Only adjust if we're significantly over the max target (using the tolerance)
        if (totalCharges.compareTo(maxTargetWithTolerance) > 0) {
            List<BillableItem> items = visit.getRelatedBillableItems();

            // Remove most expensive items until we're under the max target with tolerance
            attemptCount = 0;
            while (totalCharges.compareTo(maxTargetWithTolerance) > 0 && !items.isEmpty() && attemptCount < MAX_ATTEMPTS) {
                attemptCount++;

                int mostExpensiveIndex = getMostExpensiveIndex(items, totalCharges, minTarget);

                if (mostExpensiveIndex >= 0) {
                    BillableItem itemToRemove = items.remove(mostExpensiveIndex);

                    if (itemToRemove instanceof DiagnosticCode) {
                        if (visit.diagnosticCodes.size() > 1) {
                            visit.diagnosticCodes.remove(itemToRemove);
                        }
                    } else if (itemToRemove instanceof ProcedureCode) {
                        if (visit.inpatientProcedures.size() > 1) {
                            visit.inpatientProcedures.remove(itemToRemove);
                        }
                    } else if (itemToRemove instanceof WardStay) {
                        try {
                            visit.wardStays.remove((WardStay) itemToRemove);
                        } catch (Exception e) {
                            continue;
                        }
                    }

                    totalCharges = visit.calculateCharges();

                    if (totalCharges.compareTo(minTarget) < 0) {
                        break;
                    }
                } else {
                    // Can't find an item to remove without going below min
                    break;
                }
            }
        }
        return visit;
    }

    /**
     * Finds the index of the most expensive billable item that can be removed while maintaining
     * the minimum target charge amount
     * <p>
     * This helper method scans through a list of billable items to identify which item:
     * <ul>
     * <li>Has the highest individual cost</li>
     * <li>Can be removed while keeping the remaining total charges above the specified minimum</li>
     * </ul>
     * </p>
     * @param items The list of {@link BillableItem} objects to evaluate
     * @param totalCharges The current sum of all item charges
     * @param minTarget The minimum allowable charge amount after item removal
     * @return Index of the removable item with highest cost, or -1 if no item can be removed
     */
    private static int getMostExpensiveIndex(List<BillableItem> items, BigDecimal totalCharges, BigDecimal minTarget) {
        BillableItem mostExpensive = null;
        int mostExpensiveIndex = -1;

        for (int i = 0; i < items.size(); i++) {
            BillableItem item = items.get(i);
            BigDecimal itemCost = item.getUnsubsidisedCharges();

            if (totalCharges.subtract(itemCost).compareTo(minTarget) >= 0) {
                if (mostExpensive == null ||
                        itemCost.compareTo(mostExpensive.getUnsubsidisedCharges()) > 0) {
                    mostExpensive = item;
                    mostExpensiveIndex = i;
                }
            }
        }
        return mostExpensiveIndex;
    }

    /**
     * Prescribes medication for the patient during their visit.
     *
     * @param medication The medication to prescribe
     * @param quantity   The quantity to prescribe
     */
    public void prescribeMedicine(Medication medication, int quantity) {
        if (status != VisitStatus.ADMITTED && status != VisitStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot prescribe medication for a non-active visit");
        }

        if (quantity <= 0 || medication == null) {
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
        validateModifiable();
        if (diagnosticCode == null) {
            throw new IllegalArgumentException("Diagnostic code cannot be null");
        }
        if (diagnosticCodes == null) {
            diagnosticCodes = new ArrayList<>();
        }
        diagnosticCodes.add(diagnosticCode);
    }

    /**
     * Adds a procedure to the visit record.
     *
     * @param procedureCode The procedure to be performed
     * @throws IllegalStateException if the visit is not in an active state
     */
    public void procedure(ProcedureCode procedureCode) {
        validateModifiable();
        if (procedureCode == null) {
            throw new IllegalArgumentException("Procedure code cannot be null");
        }
        if (inpatientProcedures == null) {
            inpatientProcedures = new ArrayList<>();
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

        if (!attendingNurses.contains(nurse)) {
            attendingNurses.add(nurse);
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

        this.attendingDoc = doctor;
    }


    /**
     * Adds a ward stay to the visit.
     *
     * @param wardStay The ward stay to add
     * @throws IllegalStateException    if the visit is not modifiable
     * @throws IllegalArgumentException if ward stay is null
     */
    public void addWardStay(WardStay wardStay) {
        validateModifiable();
        if (wardStay == null) {
            throw new IllegalArgumentException("Ward stay cannot be null");
        }
        if (wardStays == null) {
            wardStays = new ArrayList<>();
        }
        wardStays.add(wardStay);
    }


    /**
     * Throws an exception if the visit is not modifiable.
     *
     * @throws IllegalStateException if the visit is finalized
     */
    private void validateModifiable() {
        if (!isModifiable()) {
            throw new IllegalStateException(
                    "Cannot modify visit with status " + status +
                            ". Visit is " + (isDischarged() ? "discharged" : "cancelled")
            );
        }
    }

    /**
     * Creates and adds a random ward stay to this visit based on the provided benefit type
     * This method is meant for use in testing purposes
     *
     * @param selectedBenefitType The benefit type to consider for stay duration
     */
    public void addRandomWardStay(BenefitType selectedBenefitType) {
        // Select a random ward type
        WardClassType[] wardTypes = WardClassType.values();
        WardClassType selectedWardType = wardTypes[DataGenerator.generateRandomInt(0, wardTypes.length - 1)];

        Ward ward = WardFactory.getWard("Hospital Ward", selectedWardType);

        // Some logic to generate believable stay duration
        int minStay = 1;
        int maxStay = 5;

        if (selectedBenefitType == BenefitType.MAJOR_SURGERY) {
            maxStay = 14;
        } else if (selectedBenefitType == BenefitType.ONCOLOGY_TREATMENTS) {
            maxStay = 10;
        }

        int daysStayed = DataGenerator.generateRandomInt(minStay, maxStay);

        LocalDateTime endDateTime = this.admissionDateTime.plusDays(daysStayed);

        WardStay wardStay = new WardStay(ward, this.admissionDateTime, endDateTime);
        this.addWardStay(wardStay);
    }

    /**
     * Checks if the visit is currently active.
     *
     * @return true if the visit is either ADMITTED or IN_PROGRESS
     */
    public boolean isActive() {
        return status == VisitStatus.ADMITTED || status == VisitStatus.IN_PROGRESS;
    }

    /**
     * Checks if the visit is finalized (completed or cancelled).
     *
     * @return true if the visit is either DISCHARGED or CANCELLED
     */
    public boolean isFinalized() {
        return status == VisitStatus.DISCHARGED || status == VisitStatus.CANCELLED;
    }

    /**
     * Checks if the visit was cancelled.
     *
     * @return true if the visit status is CANCELLED
     */
    public boolean isCancelled() {
        return status == VisitStatus.CANCELLED;
    }

    /**
     * Checks if the visit is in its initial state.
     *
     * @return true if the visit status is ADMITTED
     */
    public boolean isNewlyAdmitted() {
        return status == VisitStatus.ADMITTED;
    }

    /**
     * Checks if the visit is currently in progress.
     *
     * @return true if the visit status is IN_PROGRESS
     */
    public boolean isInProgress() {
        return status == VisitStatus.IN_PROGRESS;
    }

    /**
     * Checks if the patient has been discharged.
     *
     * @return true if the visit status is DISCHARGED
     */
    public boolean isDischarged() {
        return status == VisitStatus.DISCHARGED;
    }

    /**
     * Gets the current status of the visit.
     *
     * @return the current VisitStatus
     */
    public VisitStatus getStatus() {
        return status;
    }

    /**
     * Checks if the visit can be modified.
     *
     * @return true if the visit is not finalized
     */
    public boolean isModifiable() {
        return !isFinalized();
    }

    /**
     * Updates the visit status.
     *
     * @param newStatus The new status to set
     * @throws IllegalArgumentException if newStatus is null
     * @throws IllegalStateException    if trying to modify a finalized visit
     */
    public void updateStatus(VisitStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (isFinalized() && newStatus != status) {
            throw new IllegalStateException("Cannot change status of finalized visit");
        }
        this.status = newStatus;

        if (newStatus == VisitStatus.DISCHARGED || newStatus == VisitStatus.CANCELLED) {
            this.dischargeDateTime = LocalDateTime.now();
        }
    }


    /**
     * Gets the duration of the visit if it's completed.
     *
     * @return Optional containing the duration in hours, or empty if visit is not complete
     */
    public Optional<Long> getVisitDuration() {
        if (admissionDateTime != null && dischargeDateTime != null) {
            return Optional.of(java.time.Duration.between(
                    admissionDateTime,
                    dischargeDateTime
            ).toHours());
        }
        return Optional.empty();
    }

    /**
     * Retrieves the patient associated with this visit
     *
     * @return The {@link Patient} object for this visit
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Calculates the total charges for all visit components
     * <p> Sums charges from: </p>
     * <ul>
     *   <li>Ward stays </li>
     *   <li>Inpatient procedures </li>
     *   <li>Prescribed medications </li>
     *   <li>Diagnostic codes </li>
     * </ul>
     * All calculations use unsubsidized charge rates
     *
     * @return The total charges as a {@link BigDecimal}
     */
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

        if (diagnosticCodes != null) {
            total = total.add(diagnosticCodes.stream()
                    .map(DiagnosticCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return total;
    }


    /**
     * Returns all related charges as separate BillableItems
     * This includes diagnostics, procedures, medications with their quantities, and ward stays
     * @return List of all billable items associated with this treatment,
     *  *         or empty list if no items exist
     */
    public List<BillableItem> getRelatedBillableItems() {
        List<BillableItem> items = new ArrayList<>();

        if (diagnosticCodes != null) {
            items.addAll(diagnosticCodes);
        }

        if (inpatientProcedures != null) {
            items.addAll(inpatientProcedures);
        }

        if (prescriptions != null) {
            prescriptions.forEach((medication, quantity) ->
                    items.add(new MedicationBillableItem(medication, quantity)));
        }
        
        if (wardStays != null) {
            items.addAll(wardStays);
        }

        return items;
    }
}
