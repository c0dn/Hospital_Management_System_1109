import humans.Patient;
import medical.MedicalRecord;

import java.time.LocalDate;

public abstract class InsuranceClaim {
    private String claimId;
    private MedicalRecord medicalRecord;
    private String insuranceProvider;
    private String policyId;
    private Patient patient;
    private LocalDate submissionDate;
    private TypeOfClaim typeOfClaim;
    private String claimStatus;
    private double claimAmount;
    private LocalDate followUpDate;
    private String notesComments;
    private String denialReason;
    private String adjustmentDetail;
    private LocalDate paymentReceiptDate;
    private String coverageType;
    private String eob;
    private double approvedAmount;
    private double coInsurance;

    public InsuranceClaim(String claimId, MedicalRecord medicalRecord, String insuranceProvider,
                          String policyId, Patient patient, LocalDate submissionDate, TypeOfClaim typeOfClaim,
                          String claimStatus, double claimAmount, LocalDate followUpDate, String notesComments,
                          String denialReason, String adjustmentDetail, LocalDate paymentReceiptDate,
                          String coverageType, String eob, double approvedAmount, double coInsurance) {
        this.claimId = claimId;
        this.medicalRecord = medicalRecord;
        this.insuranceProvider = insuranceProvider;
        this.policyId = policyId;
        this.patient = patient;
        this.submissionDate = submissionDate;
        this.typeOfClaim = typeOfClaim;
        this.claimStatus = claimStatus;
        this.claimAmount = claimAmount;
        this.followUpDate = followUpDate;
        this.notesComments = notesComments;
        this.denialReason = denialReason;
        this.adjustmentDetail = adjustmentDetail;
        this.paymentReceiptDate = paymentReceiptDate;
        this.coverageType = coverageType;
        this.eob = eob;
        this.approvedAmount = approvedAmount;
        this.coInsurance = coInsurance;
    }

    // getters
    public String getClaimId() { return claimId; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }

    public String getInsuranceProvider() { return insuranceProvider; }

    public String getPolicyId() { return policyId; }

    public Patient getPatient() { return patient; }

    public LocalDate getSubmissionDate() { return submissionDate; }

    public TypeOfClaim getTypeOfClaim() { return typeOfClaim; }

    public String getClaimStatus() { return claimStatus; }

    public double getClaimAmount() { return claimAmount; }

    public LocalDate getFollowUpDate() { return followUpDate; }

    public String getNotesComments() { return notesComments; }

    public String getDenialReason() { return denialReason; }

    public String getAdjustmentDetail() { return adjustmentDetail; }

    public LocalDate getPaymentReceiptDate() { return paymentReceiptDate; }

    public String getCoverageType() { return coverageType; }

    public String getEob() { return eob; }

    public double getApprovedAmount() { return approvedAmount; }

    public double getCoInsurance() { return coInsurance; }

}
