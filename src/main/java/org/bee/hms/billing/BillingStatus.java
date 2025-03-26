package org.bee.hms.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Represents the various statuses a bill can have throughout its lifecycle.
 * Each status includes a display name and a description.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BillingStatus {
    /** The bill is being prepared and has not yet been finalized. */
    DRAFT("Draft", "Bill is being prepared"),
    /** The bill has been generated but not yet processed. */
    PENDING("Pending", "Bill has been generated but not yet processed"),
    /** The bill has been submitted for processing. */
    SUBMITTED("Submitted", "Bill has been submitted for processing"),
    /** The bill is waiting for insurance approval. */
    INSURANCE_PENDING("Insurance Pending", "Waiting for insurance approval"),
    /** The insurance claim for the bill has been approved. */
    INSURANCE_APPROVED("Insurance Approved", "Insurance claim has been approved"),
    /** The insurance claim for the bill has been rejected. */
    INSURANCE_REJECTED("Insurance Rejected", "Insurance claim has been rejected"),
    /** The bill has been partially paid. */
    PARTIALLY_PAID("Partially Paid", "Payment has been partially received"),
    /** The bill has been fully paid. */
    PAID("Paid", "Bill has been fully paid"),
    /** The bill payment is past due. */
    OVERDUE("Overdue", "Payment is past due"),
    /** The bill has been cancelled and is no longer active. */
    CANCELLED("Cancelled", "Bill has been cancelled"),
    /** The bill is under dispute and requires resolution. */
    IN_DISPUTE("In Dispute", "Bill is under dispute"),
    /** A refund for the bill is currently being processed. */
    REFUND_PENDING("Refund Pending", "Refund is being processed"),
    /** The refund for the bill has been processed successfully. */
    REFUNDED("Refunded", "Refund has been processed");

    /** The display name of the billing status. */
    private final String displayName;
    /** A brief description of what the status represents. */
    private final String description;

    /**
     * Constructs a billing status with a display name and description.
     *
     * @param displayName The user-friendly name of the status.
     * @param description A short description of what the status means.
     */
    BillingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the display name of the billing status.
     *
     * @return The display name of the status.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description of the billing status.
     *
     * @return The description of the status.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determines if the billing status represents a finalized state.
     * Finalized statuses include:
     * <ul>
     *   <li>{@code PAID}</li>
     *   <li>{@code CANCELLED}</li>
     *   <li>{@code REFUNDED}</li>
     * </ul>
     *
     * @return {@code true} if the status is finalized, otherwise {@code false}.
     */
    public boolean isFinalized() {
        return this == PAID || this == CANCELLED || this == REFUNDED;
    }

    /**
     * Determines if the billing status requires further action.
     * Action-required statuses include:
     * <ul>
     *   <li>{@code INSURANCE_REJECTED}</li>
     *   <li>{@code OVERDUE}</li>
     *   <li>{@code IN_DISPUTE}</li>
     * </ul>
     *
     * @return {@code true} if the status requires further action, otherwise {@code false}.
     */
    public boolean requiresAction() {
        return this == INSURANCE_REJECTED || this == OVERDUE || this == IN_DISPUTE;
    }

    /**
     * Determines if the billing status is related to insurance processing.
     * Insurance-related statuses include:
     * <ul>
     *   <li>{@code INSURANCE_PENDING}</li>
     *   <li>{@code INSURANCE_APPROVED}</li>
     *   <li>{@code INSURANCE_REJECTED}</li>
     * </ul>
     *
     * @return {@code true} if the status is insurance-related, otherwise {@code false}.
     */
    public boolean isInsuranceRelated() {
        return this == INSURANCE_PENDING || this == INSURANCE_APPROVED || this == INSURANCE_REJECTED;
    }

    @JsonCreator
    public static BillingStatus fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (BillingStatus status : values()) {
                if (status.displayName.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown BillingStatus: " + value);
        }
    }

    /**
     * Returns the display name of the billing status as a string.
     *
     * @return The display name of the status.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
