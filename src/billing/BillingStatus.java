package billing;

public enum BillingStatus {
    DRAFT("Draft", "Bill is being prepared"),
    PENDING("Pending", "Bill has been generated but not yet processed"),
    SUBMITTED("Submitted", "Bill has been submitted for processing"),
    INSURANCE_PENDING("Insurance Pending", "Waiting for insurance approval"),
    INSURANCE_APPROVED("Insurance Approved", "Insurance claim has been approved"),
    INSURANCE_REJECTED("Insurance Rejected", "Insurance claim has been rejected"),
    PARTIALLY_PAID("Partially Paid", "Payment has been partially received"),
    PAID("Paid", "Bill has been fully paid"),
    OVERDUE("Overdue", "Payment is past due"),
    CANCELLED("Cancelled", "Bill has been cancelled"),
    IN_DISPUTE("In Dispute", "Bill is under dispute"),
    REFUND_PENDING("Refund Pending", "Refund is being processed"),
    REFUNDED("Refunded", "Refund has been processed");

    private final String displayName;
    private final String description;

    BillingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinalized() {
        return this == PAID || this == CANCELLED || this == REFUNDED;
    }

    public boolean requiresAction() {
        return this == INSURANCE_REJECTED || this == OVERDUE || this == IN_DISPUTE;
    }

    public boolean isInsuranceRelated() {
        return this == INSURANCE_PENDING || this == INSURANCE_APPROVED || this == INSURANCE_REJECTED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}