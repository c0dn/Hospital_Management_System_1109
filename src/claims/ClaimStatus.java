package claims;

public enum ClaimStatus {
    SUBMITTED("Claim has been submitted"),
    IN_REVIEW("Claim is under review"),
    PENDING_INFORMATION("Additional information requested"),
    APPROVED("Claim has been approved"),
    PARTIALLY_APPROVED("Claim approved with adjustments"),
    DENIED("Claim has been denied"),
    APPEALED("Claim is under appeal"),
    PAID("Payment has been processed"),
    CANCELLED("Claim has been cancelled"),
    EXPIRED("Claim has expired");

    private final String description;

    ClaimStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}