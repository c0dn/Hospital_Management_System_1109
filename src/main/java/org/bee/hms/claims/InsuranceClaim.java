package org.bee.hms.claims;

import org.bee.hms.billing.Bill;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;
import org.bee.hms.humans.Patient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an insurance claim made.
 * <p>
 * This class includes details such as the claim amount, type of claim, submission date, and associated policy information.
 * </p>
 */

public class InsuranceClaim {

    /**
     * Unique identifier for the insurance claim.
     */
    private final String claimId;

    /**
     * Medical record associated with the claim.
     */
    private final Bill bill;

    /**
     * Name of the insurance provider handling the claim.
     */
    private final InsuranceProvider insuranceProvider;

    /**
     * The insurance policy under which the claim is made.
     */
    private final InsurancePolicy insurancePolicy;

    /**
     * The patient associated with the claim.
     */
    private final Patient patient;

    /**
     * The date when the claim was submitted.
     */
    private LocalDate submissionDate;

    /**
     * The current status of the claim.
     */
    private ClaimStatus claimStatus;

    /**
     * The total amount claimed.
     */
    private BigDecimal claimAmount;

    /**
     * Approved amount
     */
    private BigDecimal approvedAmount;


    /**
     * Reviewer comments
     */
    private String reviewerComments;


    /**
     * Additional comments related to the claim.
     */
    private String comments;


    private Map<LocalDateTime, String> supportingDocuments = new HashMap<>();

    private LocalDateTime lastUpdatedDate = LocalDateTime.now();


    /**
     * Private constructor for creating insurance claims.
     */
    private InsuranceClaim(String claimId, Bill bill, InsuranceProvider insuranceProvider,
                           InsurancePolicy insurancePolicy, Patient patient, LocalDate submissionDate,
                           ClaimStatus claimStatus, BigDecimal claimAmount, String comments) {
        this.claimId = claimId;
        this.bill = bill;
        this.insuranceProvider = insuranceProvider;
        this.insurancePolicy = insurancePolicy;
        this.patient = patient;
        this.submissionDate = submissionDate;
        this.claimStatus = claimStatus;
        this.claimAmount = claimAmount;
        this.comments = comments;
    }


    /**
     * Creates a new insurance claim with a generated claim ID.
     *
     * @param bill              The medical bill associated with the claim
     * @param insuranceProvider The insurance provider
     * @param insurancePolicy   The insurance policy
     * @param patient           The patient
     * @param claimAmount       The amount being claimed
     * @return A new InsuranceClaim instance
     */
    public static InsuranceClaim createNew(Bill bill, InsuranceProvider insuranceProvider,
                                           InsurancePolicy insurancePolicy, Patient patient,
                                           BigDecimal claimAmount) {
        return new InsuranceClaim(
                generateClaimId(),
                bill,
                insuranceProvider,
                insurancePolicy,
                patient,
                LocalDate.now(),
                ClaimStatus.DRAFT,
                claimAmount,
                ""
        );
    }

    /**
     * Creates an insurance claim with an existing claim ID.
     * Used when reconstructing claims from records.
     *
     * @param claimId           The existing claim ID
     * @param bill              The medical bill associated with the claim
     * @param insuranceProvider The insurance provider
     * @param insurancePolicy   The insurance policy
     * @param patient           The patient
     * @param submissionDate    The date the claim was submitted
     * @param claimStatus       The status of the claim
     * @param claimAmount       The amount being claimed
     * @param comments          Additional comments
     * @return An InsuranceClaim instance with the specified claim ID
     */
    public static InsuranceClaim fromExisting(String claimId, Bill bill, InsuranceProvider insuranceProvider,
                                              InsurancePolicy insurancePolicy, Patient patient, LocalDate submissionDate,
                                              ClaimStatus claimStatus, BigDecimal claimAmount, String comments) {
        return new InsuranceClaim(
                claimId,
                bill,
                insuranceProvider,
                insurancePolicy,
                patient,
                submissionDate,
                claimStatus,
                claimAmount,
                comments
        );
    }


    /**
     * Retrieves the unique identifier for the claim.
     *
     * @return The claim ID.
     */
    public String getClaimId() {
        return claimId;
    }

    /**
     * Retrieves the medical record associated with the claim.
     *
     * @return The medical record.
     */
    public Bill getBill() {
        return bill;
    }


    /**
     * Retrieves the patient associated with the claim.
     *
     * @return The patient.
     */
    public Patient getPatient() {
        return patient;
    }


    /**
     * Validates if the status transition is allowed.
     *
     * @param newStatus The new status to transition to
     * @throws IllegalStateException if the transition is not allowed
     */
    private void validateStatusTransition(ClaimStatus newStatus) {
        if (this.claimStatus == null && newStatus != ClaimStatus.DRAFT) {
            throw new IllegalStateException("New claim must start as DRAFT");
        }

        switch (this.claimStatus) {
            case DRAFT -> {
                if (newStatus != ClaimStatus.SUBMITTED) {
                    throw new IllegalStateException("Draft claim can only be submitted");
                }
            }
            case SUBMITTED -> {
                if (newStatus != ClaimStatus.IN_REVIEW && newStatus != ClaimStatus.CANCELLED) {
                    throw new IllegalStateException("Submitted claim can only move to review or be cancelled");
                }
            }
            case IN_REVIEW -> {
                if (newStatus != ClaimStatus.APPROVED &&
                        newStatus != ClaimStatus.PARTIALLY_APPROVED &&
                        newStatus != ClaimStatus.DENIED &&
                        newStatus != ClaimStatus.PENDING_INFORMATION) {
                    throw new IllegalStateException("Invalid transition from IN_REVIEW status");
                }
            }
            case PENDING_INFORMATION -> {
                if (newStatus != ClaimStatus.IN_REVIEW && newStatus != ClaimStatus.EXPIRED) {
                    throw new IllegalStateException("Pending information can only return to review or expire");
                }
            }
            case DENIED -> {
                if (newStatus != ClaimStatus.APPEALED) {
                    throw new IllegalStateException("Denied claim can only be appealed");
                }
            }
            case APPROVED, PARTIALLY_APPROVED -> {
                if (newStatus != ClaimStatus.PAID) {
                    throw new IllegalStateException("Approved claim can only move to paid status");
                }
            }
            case PAID, CANCELLED, EXPIRED ->
                    throw new IllegalStateException("Cannot change status of a " + this.claimStatus + " claim");
            case APPEALED -> {
                if (newStatus != ClaimStatus.IN_REVIEW) {
                    throw new IllegalStateException("Appealed claim must go back to review");
                }
            }
            case null -> throw new IllegalStateException("Null claim status");
        }
    }


    /**
     * Processes a partial approval for the claim.
     *
     * @param approvedAmount The approved amount
     * @param reason         The reason for partial approval
     * @throws IllegalStateException if the claim is not in review or the amount is invalid
     */
    public void processPartialApproval(BigDecimal approvedAmount, String reason) {
        if (claimStatus != ClaimStatus.IN_REVIEW) {
            throw new IllegalStateException("Claim must be under review to process partial approval");
        }

        if (approvedAmount == null || approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Approved amount must be greater than zero");
        }

        if (approvedAmount.compareTo(claimAmount) >= 0) {
            throw new IllegalArgumentException("Partial approval amount must be less than claimed amount");
        }

        this.approvedAmount = approvedAmount;
        this.reviewerComments = reason;
        updateStatus(ClaimStatus.PARTIALLY_APPROVED);
    }


    /**
     * Gets the most recent supporting document.
     *
     * @return The most recent document description, or null if no documents exist
     */
    public String getMostRecentDocument() {
        if (supportingDocuments.isEmpty()) {
            return null;
        }

        LocalDateTime mostRecent = supportingDocuments.keySet()
                .stream()
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return supportingDocuments.get(mostRecent);
    }


    /**
     * Adds a supporting document to the claim.
     *
     * @param documentDescription Description of the supporting document
     * @throws IllegalStateException if the claim is not in a state that accepts documents
     */
    public void addSupportingDocument(String documentDescription) {
        if (!isActionable()) {
            throw new IllegalStateException("Cannot add documents to a closed claim");
        }

        if (documentDescription == null || documentDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Document description cannot be empty");
        }

        supportingDocuments.put(LocalDateTime.now(), documentDescription);
    }


    /**
     * Gets the approved amount for the claim.
     *
     * @return The approved amount, or null if not yet approved
     */
    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }


    /**
     * Updates the status of the claim.
     *
     * @param newStatus The new status to set
     * @throws IllegalStateException if the status transition is not allowed
     */
    public void updateStatus(ClaimStatus newStatus) {
        validateStatusTransition(newStatus);
        this.claimStatus = newStatus;
    }

    /**
     * Submits the claim, changing its status to SUBMITTED.
     *
     * @throws IllegalStateException if the claim is not in DRAFT status
     */
    public void submit() {
        validateStatusTransition(ClaimStatus.SUBMITTED);
        this.claimStatus = ClaimStatus.SUBMITTED;
    }


    /**
     * Checks if the claim is in draft status.
     *
     * @return true if the claim is in draft status
     */
    public boolean isDraft() {
        return claimStatus == ClaimStatus.DRAFT;
    }

    /**
     * Checks if the claim has been submitted.
     *
     * @return true if the claim has been submitted
     */
    public boolean isSubmitted() {
        return claimStatus == ClaimStatus.SUBMITTED;
    }

    /**
     * Checks if the claim requires action from the claimant.
     *
     * @return true if additional information is needed
     */
    public boolean requiresAction() {
        return claimStatus == ClaimStatus.PENDING_INFORMATION;
    }

    /**
     * Checks if the claim is under review.
     *
     * @return true if the claim is being reviewed
     */
    public boolean isUnderReview() {
        return claimStatus == ClaimStatus.IN_REVIEW;
    }

    /**
     * Checks if the claim has been approved (fully or partially).
     *
     * @return true if the claim is approved
     */
    public boolean isApproved() {
        return claimStatus == ClaimStatus.APPROVED ||
                claimStatus == ClaimStatus.PARTIALLY_APPROVED;
    }

    /**
     * Checks if the claim has been denied.
     *
     * @return true if the claim was denied
     */
    public boolean isDenied() {
        return claimStatus == ClaimStatus.DENIED;
    }

    /**
     * Checks if the claim is currently under appeal.
     *
     * @return true if the claim is being appealed
     */
    public boolean isUnderAppeal() {
        return claimStatus == ClaimStatus.APPEALED;
    }

    /**
     * Checks if the claim has been paid.
     *
     * @return true if payment has been processed
     */
    public boolean isPaid() {
        return claimStatus == ClaimStatus.PAID;
    }

    /**
     * Checks if the claim is closed (paid, cancelled, or expired).
     *
     * @return true if the claim is in a final state
     */
    public boolean isClosed() {
        return claimStatus == ClaimStatus.PAID ||
                claimStatus == ClaimStatus.CANCELLED ||
                claimStatus == ClaimStatus.EXPIRED;
    }

    /**
     * Checks if any action can be taken on the claim.
     *
     * @return true if the claim can be modified
     */
    public boolean isActionable() {
        return !isClosed();
    }


    /**
     * Sets the comments of the claim.
     *
     * @param comments The comments.
     */
    public void updateComments(String comments) {
        this.comments = comments;
    }

    private static String generateClaimId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = DataGenerator.getInstance().generateRandomString(4);

        return String.format("CLM-%s-%s", datePart, randomPart);
    }

    public void displayClaimInfo() {
        printHeader();

        printBasicClaimInfo();

        printFooter();
    }

    private void printHeader() {
        System.out.println("\n====================================================");
        System.out.println("                INSURANCE CLAIM RECORD               ");
        System.out.println("====================================================");
    }

    private void printBasicClaimInfo() {
        System.out.printf("%-15s: %s%n", "Claim ID", claimId);
        System.out.printf("%-15s: %s%n", "Status", claimStatus);
        System.out.printf("%-15s: %s%n", "Submitted On", submissionDate);
        System.out.printf("%-15s: %.2f%n", "Amount", claimAmount);
        System.out.printf("%-15s: %s%n", "Provider", insuranceProvider.getProviderName());
        System.out.printf("%-15s: %s%n", "Policy Number", insurancePolicy.getPolicyNumber());
    }



    private void printFooter() {
        System.out.println("\n====================================================");
        System.out.printf("Last Updated: %s%n", lastUpdatedDate);
        if (comments != null && !comments.isEmpty()) {
            System.out.println("\nComments:");
            System.out.println(comments);
        }
        System.out.println("====================================================\n");
    }



}
