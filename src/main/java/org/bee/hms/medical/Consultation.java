package org.bee.hms.medical;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.Coverage;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONSerializable;
import org.bee.utils.jackson.PrescriptionMapDeserializer;
import org.bee.utils.jackson.PrescriptionMapSerializer;

/**
 * Represents a medical consultation.
 * <p>
 * The {@link Consultation} class encapsulates details about a consultation, including the type of consultation,
 * associated doctor, consultation time, diagnostic codes, procedure codes, prescribed medications, and associated
 * charges. It also provides methods to generate random consultations, calculate charges, and retrieve related billable items.
 * </p>
 */
public class Consultation implements JSONSerializable {

    /**
     * The unique consultation ID
     */
    private String consultationId;

    /**
     * The type of the consultation (e.g., emergency, regular, specialized)
     */
    @JsonProperty("consult_type")
    private ConsultationType type;

    /**
     * The date and time the consultation took place
     */
    @JsonProperty("time")
    private LocalDateTime consultationTime;

    /**
     * The fee for the consultation
     */
    @JsonProperty("fee")
    private BigDecimal consultationFee;

    /**
     * The list of diagnostic codes associated with the consultation
     */
    @JsonProperty("diagnostic_codes")
    private List<DiagnosticCode> diagnosticCodes;

    /**
     * The list of procedure codes associated with the consultation
     */
    @JsonProperty("procedure_codes")
    private List<ProcedureCode> procedureCodes;


    @JsonSerialize(using = PrescriptionMapSerializer.class)
    @JsonDeserialize(using = PrescriptionMapDeserializer.class)
    @JsonProperty("prescriptions")
    private Map<Medication, Integer> prescriptions;

    /**
     * Additional notes regarding the consultation
     */
    private String notes;

    /**
     * Date of the outpatient appointment.
     */
    private LocalDateTime appointmentDate;

    /**
     * Medical history of the patient.
     */
    private String medicalHistory;

    /**
     * Current status of the outpatient case.
     */
    private ConsultationStatus status;

    /**
     * Department handling the outpatient case.
     */
    private HospitalDepartment department;

    /**
     * Diagnosis given for the outpatient case.
     */
    private String diagnosis;

    /**
     * Reason for the patient's visit.
     */
    private String visitReason;

    /**
     * Follow-up date for the patient.
     */
    private LocalDateTime followUpDate;

    /**
     * Instructions given for the outpatient case.
     */
    private String instructions;

    /**
     * Patient associated with this outpatient case.
     */
    private Patient patient;

    /**
     * Doctor handling the outpatient case.
     */
    private Doctor doctor;

    /**
     * List of treatments assigned to the patient.
     */
    private ArrayList<Treatment> treatments;

    /**
     * List of lab tests ordered for the patient.
     */
    private ArrayList<LabTest> labTests;


    /**
     * Creates a consultation with random data for testing purposes.
     * <p>
     * This method generates a instance with random values for various fields, including
     * consultation ID, type, doctor ID, consultation time, diagnostic codes, procedure codes, prescriptions, notes,
     * visit reason, diagnosis, instructions, and medical history.
     * </p>
     *
     * @return A randomly populated instance.
     */
    public static Consultation withRandomData() {
        Consultation consultation = new Consultation();

        Doctor doc = Doctor.builder().withRandomBaseData().build();
        Patient patient = Patient.builder()
                .withRandomData(DataGenerator.generatePatientId())
                .build();
        consultation.consultationId = "C" + System.currentTimeMillis() +
                String.format("%04d", DataGenerator.generateRandomInt(10000));
        consultation.type = DataGenerator.getRandomEnum(ConsultationType.class);
        consultation.consultationTime = LocalDateTime.now()
                .minusDays(DataGenerator.generateRandomInt(1, 30));
        consultation.consultationFee = new BigDecimal(DataGenerator.generateRandomInt(50, 300));

        consultation.doctor = doc;
        consultation.patient = patient;

        String[] reasons = {
                "Regular check-up",
                "Flu symptoms",
                "Headache",
                "Skin rash",
                "Fever",
                "Stomach pain",
                "Follow-up consultation",
                "Medication review",
                "Chronic condition management",
                "Mental health consultation"
        };
        consultation.visitReason = DataGenerator.getRandomElement(reasons);

        String[] diagnoses = {
                "Common cold",
                "Seasonal allergies",
                "Hypertension",
                "Type 2 Diabetes",
                "Migraine",
                "Anxiety disorder",
                "Gastritis",
                "Dermatitis",
                "Respiratory infection",
                "Vitamin D deficiency"
        };
        consultation.diagnosis = DataGenerator.getRandomElement(diagnoses);

        String[] instructionOptions = {
                "Take medication as prescribed. Rest for 2-3 days.",
                "Increase fluid intake. Monitor symptoms.",
                "Avoid strenuous activity for one week.",
                "Follow the diet plan provided. Schedule follow-up in 2 weeks.",
                "Apply cream twice daily. Return if symptoms worsen.",
                "Take blood pressure readings daily and log them.",
                "Continue with current treatment plan. No changes needed."
        };
        consultation.instructions = DataGenerator.getRandomElement(instructionOptions);

        String[] histories = {
                "No significant medical history.",
                "History of hypertension.",
                "Type 2 diabetes diagnosed 5 years ago.",
                "Previous appendectomy in 2018.",
                "Chronic asthma since childhood.",
                "Family history of cardiovascular disease.",
                "Previous allergic reaction to penicillin."
        };
        consultation.medicalHistory = DataGenerator.getRandomElement(histories);

        consultation.diagnosticCodes = new ArrayList<>();
        int diagCount = DataGenerator.generateRandomInt(1, 3);
        for (int i = 0; i < diagCount; i++) {
            consultation.diagnosticCodes.add(DiagnosticCode.getRandomCode());
        }

        consultation.procedureCodes = new ArrayList<>();
        int procCount = DataGenerator.generateRandomInt(0, 2);
        for (int i = 0; i < procCount; i++) {
            consultation.procedureCodes.add(ProcedureCode.getRandomCode());
        }

        consultation.prescriptions = new HashMap<>();
        int medCount = DataGenerator.generateRandomInt(1, 4);
        for (int i = 0; i < medCount; i++) {
            consultation.prescriptions.put(
                    DataGenerator.getRandomMedication(),
                    DataGenerator.generateRandomInt(1, 10)
            );
        }

        return consultation;
    }


    /**
     * Creates a consultation that is compatible with the given insurance policy coverage.
     * Similar to createCompatibleVisit in the Visit class.
     *
     * @param coverage         The insurance coverage
     * @param patient          The patient for this consultation
     * @param availableDoctors List of doctors to choose from
     * @return A consultation that will be covered by the policy
     */
    public static Consultation createCompatibleConsultation(Coverage coverage, Patient patient,
                                                            List<Doctor> availableDoctors) {
        Doctor doctor = null;
        if (availableDoctors != null && !availableDoctors.isEmpty()) {
            doctor = DataGenerator.getRandomElement(availableDoctors);
        }

        Consultation consultation = Consultation.withRandomData(patient, doctor);

        Set<BenefitType> coveredBenefits = coverage.getCoveredBenefits();

        consultation.clearDiagnosticCodes();
        consultation.clearProcedureCodes();
        consultation.clearPrescriptions();

        for (BenefitType benefitType : coveredBenefits) {
            try {
                DiagnosticCode code = DiagnosticCode.getRandomCodeForBenefitType(benefitType, false);
                consultation.diagnosticCodes.add(code);
                if (consultation.getDiagnosticCodes().size() >= 2) break;
            } catch (IllegalArgumentException ignored) {
            }
        }

        for (BenefitType benefitType : coveredBenefits) {
            try {
                ProcedureCode code = ProcedureCode.getRandomCodeForBenefitType(benefitType, false);
                consultation.procedureCodes.add(code);
                if (consultation.getProcedureCodes().size() >= 2) break;
            } catch (IllegalArgumentException ignored) {
            }
        }

        for (String category : Medication.getAllCategories()) {
            List<Medication> meds = Medication.getMedicationsByCategory(category, 5, true);
            for (Medication med : meds) {
                MedicationBillableItem item = new MedicationBillableItem(med, 1);
                if (coverage.isItemCovered(item, false)) {
                    consultation.addPrescription(med, 1 + DataGenerator.generateRandomInt(3));
                    if (consultation.getPrescriptions().size() >= 3) break;
                }
            }
            if (consultation.getPrescriptions().size() >= 3) break;
        }

        return consultation;
    }

    /**
     * Creates a consultation with randomized data for the specified patient
     *
     * @param patient The patient for the consultation
     * @param doctor The treating doctor
     * @return New consultation with random clinical data
     */
    public static Consultation withRandomData(Patient patient, Doctor doctor) {
        Consultation consultation = withRandomData();
        consultation.doctor = doctor;
        consultation.patient = patient;
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
    @JsonIgnore
    public List<BillableItem> getRelatedBillableItems() {
        List<BillableItem> items = new ArrayList<>();

        if (diagnosticCodes != null) {
            items.addAll(diagnosticCodes);
        }

        if (procedureCodes != null) {
            items.addAll(procedureCodes);
        }

        if (prescriptions != null) {
            prescriptions.forEach((medication, quantity) ->
                    items.add(new MedicationBillableItem(medication, quantity)));
        }

        return items;
    }

    /**
     * Gets the doctor attending thex` consultation
     *
     * @return The attending Doctor
     */
    public Doctor getDoctor() {
        return doctor;
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
    @JsonIgnore
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

    /**
     * Returns the category of the consultation.
     * <p>
     * This method returns the category of the consultation based on its type. The possible categories
     * are: "EMERGENCY_CONSULTATION", "REGULAR_CONSULTATION", "SPECIALIZED_CONSULTATION", and "FOLLOW_UP_CONSULTATION".
     * </p>
     *
     * @return A string representing the category of the consultation.
     */
    @JsonIgnore
    public String getCategory() {
        return switch (type) {
            case EMERGENCY -> "EMERGENCY_CONSULTATION";
            case REGULAR_CONSULTATION -> "REGULAR_CONSULTATION";
            case SPECIALIZED_CONSULTATION -> "SPECIALIZED_CONSULTATION";
            case FOLLOW_UP -> "FOLLOW_UP_CONSULTATION";
            case NEW_CONSULTATION -> "NEW_CONSULTATION";
            case ROUTINE_CHECKUP -> "ROUTINE_CHECKUP_CONSULTATION";
        };
    }

    /**
     * Gets the unique identifier for the consultation
     * @return The consultationId
     */
    public String getConsultationId() { return consultationId; }

    /**
     * Gets the patient associated with the consultation
     * @return The Patient object
     */
    public Patient getPatient() { return patient; }

    /**
     * Gets the diagnosis for the consultation
     * @return The diagnosis description
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Gets the type of consultation (ignored in JSON serialization)
     * @return The ConsultationType enum value
     * @see ConsultationType
     */
    @JsonIgnore
    public ConsultationType getConsultationType() {
        return type;
    }


    /**
     * Gets the scheduled date/time for the consultation
     * @return The appointment datetime
     */
    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    /**
    /**
     * Gets the time when the consultation occurred
     * @return LocalDateTime of the consultation
     */
    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    /**
     * Gets the current status of the consultation
     * @return ConsultationStatus
     */
    public ConsultationStatus getStatus() {
        return status;
    }

    /**
     * Gets the list of diagnostic codes for this consultation.
     * The returned list can be modified directly to add or remove codes.
     *
     * @return The list of diagnostic codes
     */
    public List<DiagnosticCode> getDiagnosticCodes() {
        if (this.diagnosticCodes == null) {
            this.diagnosticCodes = new ArrayList<>();
        }
        return this.diagnosticCodes;
    }

    /**
     * Adds a diagnostic code to this consultation.
     *
     * @param code The diagnostic code to add
     * @return true if the code was added successfully
     */
    public boolean addDiagnosticCode(DiagnosticCode code) {
        if (code == null) {
            return false;
        }
        if (this.diagnosticCodes == null) {
            this.diagnosticCodes = new ArrayList<>();
        }
        return this.diagnosticCodes.add(code);
    }

    /**
     * Removes a diagnostic code from this consultation.
     *
     * @param code The diagnostic code to remove
     * @return true if the code was removed successfully
     */
    public boolean removeDiagnosticCode(DiagnosticCode code) {
        if (this.diagnosticCodes == null || code == null) {
            return false;
        }
        return this.diagnosticCodes.remove(code);
    }

    /**
     * Clears all diagnostic codes from this consultation.
     */
    public void clearDiagnosticCodes() {
        if (this.diagnosticCodes != null) {
            this.diagnosticCodes.clear();
        }
    }

    /**
     * Gets the list of procedure codes for this consultation.
     * The returned list can be modified directly to add or remove codes.
     *
     * @return The list of procedure codes
     */
    public List<ProcedureCode> getProcedureCodes() {
        if (this.procedureCodes == null) {
            this.procedureCodes = new ArrayList<>();
        }
        return this.procedureCodes;
    }

    /**
     * Adds a procedure code to this consultation.
     *
     * @param code The procedure code to add
     * @return true if the code was added successfully
     */
    public boolean addProcedureCode(ProcedureCode code) {
        if (code == null) {
            return false;
        }
        if (this.procedureCodes == null) {
            this.procedureCodes = new ArrayList<>();
        }
        return this.procedureCodes.add(code);
    }

    /**
     * Removes a procedure code from this consultation.
     *
     * @param code The procedure code to remove
     * @return true if the code was removed successfully
     */
    public boolean removeProcedureCode(ProcedureCode code) {
        if (this.procedureCodes == null || code == null) {
            return false;
        }
        return this.procedureCodes.remove(code);
    }

    /**
     * Clears all procedure codes from this consultation.
     */
    public void clearProcedureCodes() {
        if (this.procedureCodes != null) {
            this.procedureCodes.clear();
        }
    }

    /**
     * Gets the map of prescriptions for this consultation.
     * The returned map can be modified directly to add or update prescriptions.
     *
     * @return The map of prescriptions
     */
    public Map<Medication, Integer> getPrescriptions() {
        if (this.prescriptions == null) {
            this.prescriptions = new HashMap<>();
        }
        return this.prescriptions;
    }

    /**
     * Adds or updates a prescription in this consultation.
     *
     * @param medication The medication to prescribe
     * @param quantity The quantity to prescribe
     */
    public void addPrescription(Medication medication, int quantity) {
        if (medication == null || quantity <= 0) {
            return;
        }
        if (this.prescriptions == null) {
            this.prescriptions = new HashMap<>();
        }
        this.prescriptions.put(medication, quantity);
    }

    /**
     * Removes a prescription from this consultation.
     *
     * @param medication The medication to remove
     * @return true if the prescription was removed successfully
     */
    public boolean removePrescription(Medication medication) {
        if (this.prescriptions == null || medication == null) {
            return false;
        }
        return this.prescriptions.remove(medication) != null;
    }

    /**
     * Clears all prescriptions from this consultation.
     */
    public void clearPrescriptions() {
        if (this.prescriptions != null) {
            this.prescriptions.clear();
        }
    }

    /**
     * Sets the doctor's notes for this consultation
     * @param notes Clinical notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Sets the patient's medical history to this consultation
     * @param medicalHistory Medical history summary
     */
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    /**
     * Sets the primary diagnosis for this consultation
     * @param diagnosis Clinical diagnosis
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Sets the reason for the patient's visit
     * @param visitReason Description of visit purpose
     */
    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }

    /**
     * Sets the follow-up date
     * @param followUpDate Future follow-up date
     */
    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    /**
     * Sets post-consultation instructions for the patient
     * @param instructions Care instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Sets the treatments prescribed during this consultation
     * @param treatments List of Treatment
     */
    public void setTreatments(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
    }

    /**
     * Sets the lab tests ordered during this consultation
     * @param labTests List of LabTest
     */
    public void setLabtests(ArrayList<LabTest> labTests) {
        this.labTests = labTests;
    }

    /**
     * Sets the unique identifier for this consultation
     * @param consultationId Consultation ID string
     */
    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    /**
     * Sets type of consultation
     * @param type ConsultationType enum value
     */
    public void setType(ConsultationType type) {
        this.type = type;
    }

    /**
     * Sets the date/time when consultation occurred
     * @param consultationTime Timestamp of consultation
     */
    public void setConsultationTime(LocalDateTime consultationTime) {
        this.consultationTime = consultationTime;
    }

    /**
     * Sets the consultation fee for this consultation
     * @param consultationFee Fee amount
     */
    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    /**
     * Sets the scheduled appointment date/time
     * @param appointmentDate Planned consultation time
     */
    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * Sets the current status of the consultation
     * @param status ConsultationStatus enum value
     */
    public void setStatus(ConsultationStatus status) {
        this.status = status;
    }

    /**
     * Sets the hospital department where consultation occurred
     * @param department HospitalDepartment enum value
     */
    public void setDepartment(HospitalDepartment department) {
        this.department = department;
    }

    /**
     * Sets the patient associated with this consultation.
     * @param patient Patient object
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Sets the doctor who conducted the consultation
     * @param doctor Doctor object
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Returns the list of treatments associated with this medical claim.
     *
     * @return a mutable {@code ArrayList} of treatments associated with this claim
     */
    public ArrayList<Treatment> getTreatments() {
        return treatments;
    }

    /**
     * Returns the list of lab tests associated with this medical claim.
     *
     * @return a mutable {@code ArrayList} of lab tests associated with this claim
     */
    public ArrayList<LabTest> getLabTests() {
        return labTests;
    }
}
