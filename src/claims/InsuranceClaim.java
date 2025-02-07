package claims;

import humans.Patient;
import medical.MedicalRecord;
import policy.InsurancePolicy;

import java.time.LocalDate;

/**
 * Represents an insurance claim made.
 * This class includes details such as claim amount, type of claim and submission date.
 */


public class InsuranceClaim {
    private String claimId;
    private MedicalRecord medicalRecord;
    private String insuranceProvider;
    private InsurancePolicy insurancePolicy;
    private Patient patient;
    private LocalDate submissionDate;
    private ClaimStatus claimStatus;
    private double claimAmount;
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

}
