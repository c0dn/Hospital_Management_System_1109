package org.bee.hms.medical;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONReadable;
import org.bee.utils.JSONWritable;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Consultation implements JSONReadable, JSONWritable {

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

    /**
     * The list of medications prescribed during the consultation with their quantities
     */
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
     * This method generates a {@link Consultation} instance with random values for various fields, including
     * consultation ID, type, doctor ID, consultation time, diagnostic codes, procedure codes, prescriptions, notes,
     * visit reason, diagnosis, instructions, and medical history.
     * </p>
     *
     * @return A randomly populated {@link Consultation} instance.
     */
    public static Consultation withRandomData() {
        DataGenerator gen = DataGenerator.getInstance();
        Consultation consultation = new Consultation();

        Doctor doc = Doctor.builder().withRandomBaseData().build();
        Patient patient = Patient.builder()
                .withRandomData(gen.generatePatientId())
                .build();
        consultation.consultationId = "C" + System.currentTimeMillis() +
                String.format("%04d", gen.generateRandomInt(10000));
        consultation.type = gen.getRandomEnum(ConsultationType.class);
        consultation.consultationTime = LocalDateTime.now()
                .minusDays(gen.generateRandomInt(1, 30));
        consultation.consultationFee = new BigDecimal(gen.generateRandomInt(50, 300));

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
        consultation.visitReason = gen.getRandomElement(reasons);

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
        consultation.diagnosis = gen.getRandomElement(diagnoses);

        String[] instructionOptions = {
                "Take medication as prescribed. Rest for 2-3 days.",
                "Increase fluid intake. Monitor symptoms.",
                "Avoid strenuous activity for one week.",
                "Follow the diet plan provided. Schedule follow-up in 2 weeks.",
                "Apply cream twice daily. Return if symptoms worsen.",
                "Take blood pressure readings daily and log them.",
                "Continue with current treatment plan. No changes needed."
        };
        consultation.instructions = gen.getRandomElement(instructionOptions);

        String[] histories = {
                "No significant medical history.",
                "History of hypertension.",
                "Type 2 diabetes diagnosed 5 years ago.",
                "Previous appendectomy in 2018.",
                "Chronic asthma since childhood.",
                "Family history of cardiovascular disease.",
                "Previous allergic reaction to penicillin."
        };
        consultation.medicalHistory = gen.getRandomElement(histories);

        consultation.notes = "Consultation for " + consultation.visitReason.toLowerCase() +
                ". Diagnosed with " + consultation.diagnosis.toLowerCase() +
                ". " + gen.getRandomElement(instructionOptions);

        consultation.diagnosticCodes = new ArrayList<>();
        int diagCount = gen.generateRandomInt(1, 3);
        for (int i = 0; i < diagCount; i++) {
            consultation.diagnosticCodes.add(DiagnosticCode.getRandomCode());
        }

        consultation.procedureCodes = new ArrayList<>();
        int procCount = gen.generateRandomInt(0, 2);
        for (int i = 0; i < procCount; i++) {
            consultation.procedureCodes.add(ProcedureCode.getRandomCode());
        }

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
                    items.add(new MedicationBillableItem(medication, quantity)));
        }

        return items;
    }

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

    public String getConsultationId() { return consultationId; }

    public Patient getPatient() { return patient; }

    public String getDiagnosis() {
        return diagnosis;
    }

    @JsonIgnore
    public ConsultationType getConsultationType() {
        return type;
    }


    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void displayConsultation() {
        patient.displayHuman();
        System.out.printf("%n");
        System.out.println("CONSULTATION DETAILS");
        System.out.println("---------------------------------------------------------------------");

//        System.out.printf("%nName: " + name);
        System.out.println("Case ID: " + consultationId);
        System.out.println("Appointment Date: " + (appointmentDate != null ? appointmentDate : "-"));
        System.out.println("Type: " + type);
        System.out.println("Status: " + (status != null ? status : "-"));
        System.out.println("Medical History: " + (medicalHistory != null ? medicalHistory : "-"));
        System.out.println("Visit Reason: " + (visitReason != null ? visitReason : "-"));

        System.out.print("Diagnostic Code: ");
        if (diagnosticCodes != null && !diagnosticCodes.isEmpty()) {
            System.out.println();
            for (int i = 0; i < diagnosticCodes.size(); i++) {
                System.out.print("    ");
                System.out.print(i + 1);
                System.out.print(". ");
                System.out.println(diagnosticCodes.get(i).getDCode());
            }
        } else {
            System.out.println("-");
        }

        System.out.println("Diagnosis: " + (diagnosis != null ? diagnosis : "-"));

        System.out.print("Procedure Code: ");
        if (procedureCodes != null && !procedureCodes.isEmpty()) {
            System.out.println();
            for (int i = 0; i < procedureCodes.size(); i++) {
                System.out.print("    ");
                System.out.print(i + 1);
                System.out.print(". ");
                System.out.println(procedureCodes.get(i).getPCode());
            }
        } else {
            System.out.println("-");
        }

        System.out.print("Lab Test: ");
        if (labTests != null && !labTests.isEmpty()) {
            System.out.println();
            for (int i = 0; i < labTests.size(); i++) {
                System.out.print("    ");
                System.out.print(i + 1);
                System.out.print(". ");
                System.out.println(labTests.get(i).getLabTestInfo());
            }
        } else {
            System.out.println("-");
        }

        System.out.print("Treatment: ");
        if (treatments != null && !treatments.isEmpty()) {
            System.out.println();
            for (int i = 0; i < treatments.size(); i++) {
                System.out.print("    ");
                System.out.print(i + 1);
                System.out.print(". ");
                System.out.println(treatments.get(i).getTreatmentInfo());
            }
        } else {
            System.out.println("-");
        }

        System.out.println("Prescription: ");
        int i = 1;
        if (prescriptions != null && !prescriptions.isEmpty()) {
            System.out.println();
            for (Medication drugCode : prescriptions.keySet()) {
                System.out.print("    ");
                System.out.print(i);
                System.out.print(". ");
                System.out.println(drugCode.getDrugCode() + ", " + drugCode.name);
                i++;
            }
        } else {
            System.out.println("-");
        }

        System.out.println("Follow Up Date: " + (followUpDate != null ? followUpDate : "-"));
        System.out.println("Doctor's Notes: " + (notes != null ? notes : "-"));
        System.out.println("Instructions: " + (instructions != null ? instructions : "-"));
        System.out.printf("Doctor Name: " + doctor.getName());
        System.out.println();
    }

    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    public ConsultationStatus getStatus() {
        return status;
    }

    public void setDiagnosticCodes(List<DiagnosticCode> diagnosticCodes) {
        this.diagnosticCodes = diagnosticCodes;
    }

    public void setProcedureCodes(List<ProcedureCode> procedureCodes) {
        this.procedureCodes = procedureCodes;
    }

    public void setPrescriptions(Map<Medication, Integer> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setTreatments(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
    }

    public void setLabtests(ArrayList<LabTest> labTests) {
        this.labTests = labTests;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setType(ConsultationType type) {
        this.type = type;
    }

    public void setConsultationTime(LocalDateTime consultationTime) {
        this.consultationTime = consultationTime;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setStatus(ConsultationStatus status) {
        this.status = status;
    }

    public void setDepartment(HospitalDepartment department) {
        this.department = department;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

}
