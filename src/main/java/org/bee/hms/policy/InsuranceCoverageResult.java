package org.bee.hms.policy;

import java.util.Optional;

import org.bee.hms.claims.InsuranceClaim;

/**
 * Result of insurance coverage, whether approved/denied
 * @param claim Approved claim if present
 * @param denialReason Denial explanation if claim absent
 */
public record InsuranceCoverageResult(

        Optional<InsuranceClaim> claim,
        String denialReason
) {
    /**
     * Creates an approved coverage result with the specified claim
     *
     * @param claim The approved insurance claim
     * @return New InsuranceCoverageResult representing an approved claim
     */
    public static InsuranceCoverageResult approved(InsuranceClaim claim) {
        return new InsuranceCoverageResult(Optional.of(claim), null);
    }

    /**
     * Creates a denied coverage result with the specified reason
     *
     * @param reason The denial reason
     * @return New InsuranceCoverageResult representing a denied claim
     */
    public static InsuranceCoverageResult denied(String reason) {
        return new InsuranceCoverageResult(Optional.empty(), reason);
    }

    /**
     * Returns the denial reason wrapped in an Optional
     *
     * @return Optional containing the denial reason if present, empty otherwise
     */
    public Optional<String> getDenialReason() {
        return Optional.ofNullable(denialReason);
    }

    /**
     * Checks if the coverage result represents an approved claim
     *
     * @return true if the claim is present, false otherwise
     */
    public boolean isApproved() {
        return claim.isPresent();
    }
}
