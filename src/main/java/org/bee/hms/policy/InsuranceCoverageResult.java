package org.bee.hms.policy;

import java.util.Optional;

import org.bee.hms.claims.InsuranceClaim;

public record InsuranceCoverageResult(
        Optional<InsuranceClaim> claim,
        String denialReason
) {
    public static InsuranceCoverageResult approved(InsuranceClaim claim) {
        return new InsuranceCoverageResult(Optional.of(claim), null);
    }

    public static InsuranceCoverageResult denied(String reason) {
        return new InsuranceCoverageResult(Optional.empty(), reason);
    }

    public Optional<String> getDenialReason() {
        return Optional.ofNullable(denialReason);
    }

    public boolean isApproved() {
        return claim.isPresent();
    }
}
