package claims;

import humans.Patient;
import medical.MedicalRecord;
import policy.InsurancePolicy;

import java.time.LocalDate;

public class InsuranceClaim {

    /** Unique identifier for the insurance claim. */
    private final String claimId;

    /** Medical record associated with the claim. */
    private MedicalRecord medicalRecord;

    /** Name of the insurance provider handling the claim. */
    private String insuranceProvider;

    /** The insurance policy under which the claim is made. */
    private InsurancePolicy insurancePolicy;

    /** The patient associated with the claim. */
    private Patient patient;

    /** The date when the claim was submitted. */
    private LocalDate submissionDate;

    /** The current status of the claim. */
    private ClaimStatus claimStatus;

    /** The total amount claimed. */
    private double claimAmount;

    /** Additional comments related to the claim. */
    private String comments;

    public InsuranceClaim(String claimId, MedicalRecord medicalRecord, String insuranceProvider,
                          InsurancePolicy insurancePolicy, Patient patient, LocalDate submissionDate,
                          ClaimStatus claimStatus, double claimAmount, String comments) {
        this.claimId = claimId;
        this.medicalRecord = medicalRecord;
        this.insuranceProvider = insuranceProvider;
        this.insurancePolicy = insurancePolicy;
        this.patient = patient;
        this.submissionDate = submissionDate;
        this.claimStatus = claimStatus;
        this.claimAmount = claimAmount;
        this.comments = comments;
    }

    // getters

    public String getClaimId() { return claimId; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }

    public String getInsuranceProvider() { return insuranceProvider; }

    public InsurancePolicy getInsurancePolicy() { return insurancePolicy; }

    public Patient getPatient() { return patient; }

    public LocalDate getSubmissionDate() { return submissionDate; }

    public ClaimStatus getClaimStatus() { return claimStatus; }

    public double getClaimAmount() { return claimAmount; }

    public String getComments() { return comments; }

    public void setClaimStatus(ClaimStatus claimStatus) {
        this.claimStatus = claimStatus;
    } //setter, may change over time approved etc

    public void setComments(String comments) {
        this.comments = comments;
    }//setter, add comments overtime
}
