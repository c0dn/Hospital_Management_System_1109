package medical;

import billing.BillableItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.DataGenerator;

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
     * Creates a consultation with random data for testing purposes
     * @return A randomly populated Consultation instance
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

    public BigDecimal calculateCharges() {
        BigDecimal total = consultationFee;

        if (diagnosticCodes != null) {
            total = total.add(diagnosticCodes.stream()
                    .map(DiagnosticCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (procedureCodes != null) {
            total = total.add(procedureCodes.stream()
                    .map(ProcedureCode::getUnsubsidisedCharges)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (prescriptions != null) {
            total = total.add(prescriptions.entrySet().stream()
                    .map(entry -> entry.getKey().calculateCost(entry.getValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return total;
    }

    public String getCategory() {
        return switch (type) {
            case EMERGENCY -> "EMERGENCY_CONSULTATION";
            case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
            case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
            case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
        };
    }
}
