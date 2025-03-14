package org.bee.hms.medical;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bee.hms.billing.BillableItem;
import org.bee.utils.DataGenerator;

/**
 * Represents a medical consultation.
 * <p>
 * The {@link Consultation} class encapsulates details about a consultation, including the type of consultation,
 * associated doctor, consultation time, diagnostic codes, procedure codes, prescribed medications, and associated
 * charges. It also provides methods to generate random consultations, calculate charges, and retrieve related billable items.
 * </p>
 */
public class Consultation {

    /** The unique consultation ID */
    private String consultationId;

    /** The type of the consultation (e.g., emergency, regular, specialized) */
    private ConsultationType type;

    /** The ID of the doctor who performed the consultation */
    private String doctorId;

    /** The date and time the consultation took place */
    private LocalDateTime consultationTime;

    /** The fee for the consultation */
    private BigDecimal consultationFee;

    /** The list of diagnostic codes associated with the consultation */
    private List<DiagnosticCode> diagnosticCodes;

    /** The list of procedure codes associated with the consultation */
    private List<ProcedureCode> procedureCodes;

    /** The list of medications prescribed during the consultation with their quantities */
    private Map<Medication, Integer> prescriptions;

    /** Additional notes regarding the consultation */
    private String notes;

    /**
     * Creates a consultation with random data for testing purposes.
     * <p>
     * This method generates a {@link Consultation} instance with random values for various fields, including
     * consultation ID, type, doctor ID, consultation time, diagnostic codes, procedure codes, prescriptions, and notes.
     * </p>
     *
     * @return A randomly populated {@link Consultation} instance.
     */
    public static Consultation withRandomData() {
        DataGenerator gen = DataGenerator.getInstance();
        Consultation consultation = new Consultation();

        // Set basic fields
        consultation.consultationId = "C" + System.currentTimeMillis() +
                String.format("%04d", gen.generateRandomInt(10000));
        consultation.type = gen.getRandomEnum(ConsultationType.class);
        consultation.doctorId = "D" + gen.generateRandomInt(1000, 9999);
        consultation.consultationTime = LocalDateTime.now()
                .minusDays(gen.generateRandomInt(1, 30));
        consultation.consultationFee = new BigDecimal(gen.generateRandomInt(50, 300));
        consultation.notes = "Consultation notes for patient visit #" + gen.generateRandomInt(1000, 9999);

        // Add random diagnostic codes
        consultation.diagnosticCodes = new ArrayList<>();
        int diagCount = gen.generateRandomInt(1, 3);
        for (int i = 0; i < diagCount; i++) {
            consultation.diagnosticCodes.add(DiagnosticCode.getRandomCode());
        }

        // Add random procedure codes
        consultation.procedureCodes = new ArrayList<>();
        int procCount = gen.generateRandomInt(0, 2);
        for (int i = 0; i < procCount; i++) {
            consultation.procedureCodes.add(ProcedureCode.getRandomCode());
        }

        // Add random prescriptions
        consultation.prescriptions = new HashMap<>();
        int medCount = gen.generateRandomInt(1, 4);
        for (int i = 0; i < medCount; i++) {
            consultation.prescriptions.put(
                    gen.getRandomMedication(),
                    gen.generateRandomInt(1, 10)
            );
        }

        return consultation;
    }

    /**
     * Returns all related charges as separate {@link BillableItem} instances.
     * <p>
     * This method returns a list of billable items related to the consultation. This includes diagnostic codes,
     * procedure codes, and prescribed medications with their quantities.
     * </p>
     *
     * @return A list of related {@link BillableItem} instances.
     */
    public List<BillableItem> getRelatedBillableItems() {
        List<BillableItem> items = new ArrayList<>();

        // Add diagnostics
        if (diagnosticCodes != null) {
            items.addAll(diagnosticCodes);
        }

        // Add procedures
        if (procedureCodes != null) {
            items.addAll(procedureCodes);
        }

        // Add medications with their quantities as MedicationBillableItem
        if (prescriptions != null) {
            prescriptions.forEach((medication, quantity) ->
                    items.add(new MedicationBillableItem(medication, quantity, true)));
        }

        return items;
    }

    /**
     * Calculates the total charges for the consultation.
     * <p>
     * This method calculates the total charges by summing up the consultation fee along with the charges
     * for the related diagnostic codes, procedure codes, and prescriptions.
     * </p>
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

        // Add prescription charges
        if (prescriptions != null) {
            total = total.add(prescriptions.entrySet().stream()
                    .map(entry -> entry.getKey().calculateCost(entry.getValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return total;
    }

    /**
     * Returns the category of the consultation.
     * <p>
     * This method returns the category of the consultation based on its type. The possible categories
     * are: "EMERGENCY_CONSULTATION", "REGULAR_CONSULTATION", "SPECIALIZED_CONSULTATION", and "FOLLOW_UP_CONSULTATION".
     * </p>
     *
     * @return A string representing the category of the consultation.
     */
    public String getCategory() {
        return switch (type) {
            case EMERGENCY -> "EMERGENCY_CONSULTATION";
            case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
            case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
            case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
        };
    }
}
