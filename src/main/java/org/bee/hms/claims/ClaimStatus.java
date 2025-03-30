package org.bee.hms.claims;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bee.utils.JSONSerializable;

/**
 * Defines the current status of an insurance claim in healthcare management system.
 * <br><br>A claim can have one of the following statuses:
 * <ul>
 *     <li>{@link #SUBMITTED} - Claim has been submitted.</li>
 *     <li>{@link #IN_REVIEW} - Claim is under review.</li>
 *     <li>{@link #PENDING_INFORMATION} - Additional information has been requested.</li>
 *     <li>{@link #APPROVED} - Claim has been approved.</li>
 *     <li>{@link #PARTIALLY_APPROVED} - Claim has been approved with adjustments.</li>
 *     <li>{@link #DENIED} - Claim has been denied.</li>
 *     <li>{@link #APPEALED} - Claim is under appeal.</li>
 *     <li>{@link #PAID} - Payment has been processed.</li>
 *     <li>{@link #CANCELLED} - Claim has been cancelled.</li>
 *     <li>{@link #EXPIRED} - Claim has expired.</li>
 * </ul>
 *
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ClaimStatus implements JSONSerializable {

    /**
     * Claim is in draft state, meaning it's not yet submitted
     */
    DRAFT("Draft", "Claim is draft"),

    /**
     * Claim has been submitted.
     */
    SUBMITTED("Submitted", "Claim has been submitted"),

    /**
     * Claim is under review.
     */
    IN_REVIEW("In Review", "Claim is under review"),

    /**
     * Additional information has been requested.
     */
    PENDING_INFORMATION("Pending Information", "Additional information requested"),

    /**
     * Claim has been approved.
     */
    APPROVED("Approved", "Claim has been approved"),

    /**
     * Claim has been approved with adjustments.
     */
    PARTIALLY_APPROVED("Partially Approved", "Claim approved with adjustments"),

    /**
     * Claim has been denied.
     */
    DENIED("Denied", "Claim has been denied"),

    /**
     * Claim is under appeal.
     */
    APPEALED("Appealed", "Claim is under appeal"),

    /**
     * Payment has been processed.
     */
    PAID("Paid", "Payment has been processed"),

    /**
     * Claim has been cancelled.
     */
    CANCELLED("Cancelled", "Claim has been cancelled"),

    /**
     * Claim has expired.
     */
    EXPIRED("Expired", "Claim has expired");

    private final String description;

    private final String displayName;

    /**
     * Constructor for ClaimStatus.
     *
     * @param description A brief description of the claim status.
     */
    ClaimStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Retrieves the description of the claim status.
     *
     * @return The description of the claim status.
     */
    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }
}