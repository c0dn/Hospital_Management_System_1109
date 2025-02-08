package medical;

import billing.BillableItem;
import humans.Doctor;
import humans.Nurse;
import wardsAmelia.Ward;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                    items.add(new MedicationBillableItem(medication, quantity)));
        }

        return items;
    }


}

/**
 * Represents a patient's stay in a hospital ward, including the ward information and the duration
 * of the stay. This class is used to track ward-specific stays, including transfers to different wards.
 */
record WardStay(Ward ward,
                LocalDateTime startDateTime,
                LocalDateTime endDateTime) {

    public BigDecimal calculateCharges() {
        long daysStayed = ChronoUnit.DAYS.between(
                startDateTime.toLocalDate(),
                endDateTime.toLocalDate());
        return BigDecimal.valueOf(ward.getDailyRate())
                .multiply(BigDecimal.valueOf(Math.max(1, daysStayed)));
    }
}

