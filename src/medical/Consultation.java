package medical;

import billing.BillableItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a consultation in the healthcare system.
 * This includes details about the consultation type, doctor, diagnostic codes,
 * procedures, prescriptions, and associated fees.
 */
public class Consultation {
    private String consultationId;
    private ConsultationType type;
    private String doctorId;
    private LocalDateTime consultationTime;
    private BigDecimal consultationFee;
    private List<DiagnosticCode> diagnosticCodes;
    private List<ProcedureCode> procedureCodes;
    private Map<Medication, Integer> prescriptions;
    private String notes;

    /**
     * Constructor to initialize a Consultation object.
     *
     * @param consultationId   The unique ID of the consultation.
     * @param type             The type of the consultation (e.g., emergency, regular).
     * @param doctorId         The ID of the doctor conducting the consultation.
     * @param consultationTime The timestamp when the consultation took place.
     * @param consultationFee  The fee for the consultation.
     * @param diagnosticCodes  The list of diagnostic codes associated with the consultation.
     * @param procedureCodes   The list of procedure codes associated with the consultation.
     * @param prescriptions    The map of medications and their quantities prescribed.
     * @param notes            Any additional notes from the doctor.
     */
    public Consultation(String consultationId, ConsultationType type, String doctorId, LocalDateTime consultationTime,
                        BigDecimal consultationFee, List<DiagnosticCode> diagnosticCodes,
                        List<ProcedureCode> procedureCodes, Map<Medication, Integer> prescriptions, String notes) {
        this.consultationId = consultationId;
        this.type = type;
        this.doctorId = doctorId;
        this.consultationTime = consultationTime;
        this.consultationFee = consultationFee;
        this.diagnosticCodes = diagnosticCodes != null ? diagnosticCodes : new ArrayList<>();
        this.procedureCodes = procedureCodes != null ? procedureCodes : new ArrayList<>();
        this.prescriptions = prescriptions != null ? prescriptions : new HashMap<>();
        this.notes = notes;
    }

    // Getters and Setters

    /**
     * Returns the consultation ID.
     *
     * @return The consultation ID.
     */
    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    /**
     * Returns the consultation type.
     *
     * @return The consultation type.
     */
    public ConsultationType getType() {
        return type;
    }

    public void setType(ConsultationType type) {
        this.type = type;
    }

    /**
     * Returns the doctor ID conducting the consultation.
     *
     * @return The doctor ID.
     */
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Returns the timestamp of the consultation.
     *
     * @return The consultation timestamp.
     */
    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    public void setConsultationTime(LocalDateTime consultationTime) {
        this.consultationTime = consultationTime;
    }

    /**
     * Returns the consultation fee.
     *
     * @return The consultation fee.
     */
    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    /**
     * Returns the list of diagnostic codes associated with the consultation.
     *
     * @return The list of diagnostic codes.
     */
    public List<DiagnosticCode> getDiagnosticCodes() {
        return diagnosticCodes;
    }

    public void setDiagnosticCodes(List<DiagnosticCode> diagnosticCodes) {
        this.diagnosticCodes = diagnosticCodes;
    }

    /**
     * Returns the list of procedure codes associated with the consultation.
     *
     * @return The list of procedure codes.
     */
    public List<ProcedureCode> getProcedureCodes() {
        return procedureCodes;
    }

    public void setProcedureCodes(List<ProcedureCode> procedureCodes) {
        this.procedureCodes = procedureCodes;
    }

    /**
     * Returns the map of prescriptions with their quantities.
     *
     * @return The map of prescriptions and their quantities.
     */
    public Map<Medication, Integer> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(Map<Medication, Integer> prescriptions) {
        this.prescriptions = prescriptions;
    }

    /**
     * Returns additional notes from the doctor regarding the consultation.
     *
     * @return The consultation notes.
     */
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns all related billable items for this consultation.
     * This includes diagnostics, procedures, and medications with their quantities.
     *
     * @return A list of billable items.
     */
    public List<BillableItem> getRelatedBillableItems() {
        List<BillableItem> items = new ArrayList<>();

        // Add diagnostics (if any)
        if (diagnosticCodes != null) {
            diagnosticCodes.forEach(code -> items.add(code));
        }

        // Add procedures (if any)
        if (procedureCodes != null) {
            procedureCodes.forEach(code -> items.add(code));
        }

        // Add medications with their quantities (if any)
        if (prescriptions != null) {
            prescriptions.forEach((medication, quantity) ->
                    items.add(new MedicationBillableItem(medication, quantity, true)));
        }

        return items;
    }

    /**
     * Calculates the total charges for the consultation, including:
     * - Consultation fee
     * - Diagnostic codes charges
     * - Procedure codes charges
     * - Prescription charges based on medication and quantity
     *
     * @return The total charges for the consultation.
     */
    public BigDecimal calculateCharges() {
        BigDecimal total = consultationFee;

        // Add diagnostic charges
        if (diagnosticCodes != null) {
            total = total.add(diagnosticCodes.stream()
                    .map(DiagnosticCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        // Add procedure charges
        if (procedureCodes != null) {
            total = total.add(procedureCodes.stream()
                    .map(ProcedureCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        // Add prescription charges (medications and their quantities)
        if (prescriptions != null) {
            total = total.add(prescriptions.entrySet().stream()
                    .map(entry -> entry.getKey().calculateCost(entry.getValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return total;
    }

    /**
     * Returns the category of the consultation based on its type.
     *
     * @return The category of the consultation as a string.
     */
    public String getCategory() {
        return switch (type) {
            case EMERGENCY -> "EMERGENCY_CONSULTATION";
            case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
            case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
            case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
        };
    }

    /**
     * Returns a string representation of the consultation details.
     *
     * @return A string describing the consultation.
     */
    @Override
    public String toString() {
        return String.format("Consultation ID: %s, Type: %s, Doctor: %s, Time: %s, Fee: %s, Notes: %s",
                consultationId, type, doctorId, consultationTime, consultationFee, notes);
    }
}
