package claims;

import humans.Patient;
import medical.MedicalRecord;
import policy.InsurancePolicy;

import java.time.LocalDate;

/**
 * Represents an insurance claim made.
 * This class includes details such as claim amount, type of claim, submission date and associated policy information.
 */


public class InsuranceClaim {

    /** Unique identifier for the insurance claim. */
    private String claimId;

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

    /**
     * Constructs an InsuranceClaim with the given details.
     *
     * @param claimId Unique identifier for the insurance claim.
     * @param medicalRecord Medical record associated with the claim.
     * @param insuranceProvider Name of the insurance provider handling the claim.
     * @param insurancePolicy The insurance policy under which the claim is made.
     * @param patient The patient associated with the claim.
     * @param submissionDate The date when the claim was submitted.
     * @param claimStatus The current status of the claim.
     * @param claimAmount The total amount claimed.
     * @param comments Additional comments related to the claim.
     */
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

    /**
     * Retrieves the unique identifier for the claim.
     *
     * @return The claim ID.
     */
    public String getClaimId() { return claimId; }

    /**
     * Retrieves the medical record associated with the claim.
     *
     * @return The medical record.
     */
    public MedicalRecord getMedicalRecord() { return medicalRecord; }

    /**
     * Retrieve the name of the insurance provider handling the claim.
     *
     * @return The insurance provider name.
     */
    public String getInsuranceProvider() { return insuranceProvider; }

    /**
     * Retrieves the insurance policy under which the claim is made.
     *
     * @return The insurance policy.
     */
    public InsurancePolicy getInsurancePolicy() { return insurancePolicy; }

    /**
     * Retrieves the patient associated with the claim.
     *
     * @return The patient.
     */
    public Patient getPatient() { return patient; }

    /**
     * Retrieves the date when the claim was submitted.
     *
     * @return The submission date.
     */
    public LocalDate getSubmissionDate() { return submissionDate; }

    /**
     * Retrieves the current status of the claim.
     *
     * @return The claim status.
     */
    public ClaimStatus getClaimStatus() { return claimStatus; }

    /**
     * Retrieves the total amount claimed.
     *
     * @return The claim amount.
     */
    public double getClaimAmount() { return claimAmount; }

    /**
     * Retrieves any additional comments related to the claim.
     *
     * @return The comments.
     */
    public String getComments() { return comments; }

}
