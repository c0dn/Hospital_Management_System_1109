package policy;

import humans.Patient;
import wards.WardClassType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an insurance policy with coverage details, status, and expiration information.
 * <p>
 *     This class provides functionality for managing the status of an insurance policy (active, expired, canceled),
 *     checking if a policy is still valid, and accessing the policy's coverage limits.
 * </p>
 * <ul>
 *     <li>{@link #getPolicyNumber()} - Retrieves the policy number of the insurance policy.</li>
 *     <li>{@link #getPolicyHolder()} - Retrieves the policyholder associated with this insurance policy.</li>
 *     <li>{@link #getCoverage()} - Retrieves the coverage details of the policy.</li>
 *     <li>{@link #isActive()} - Checks if the policy is currently active.</li>
 *     <li>{@link #isExpired(LocalDateTime)} - Checks if the policy has expired.</li>
 *     <li>{@link #isCancelled()} - Checks if the policy has been canceled.</li>
 *     <li>{@link #isPending()} - Checks if the policy status is pending.</li>
 *     <li>{@link #getCoverageLimit()} - Retrieves the coverage limits associated with this policy.</li>
 * </ul>
 */
public class BaseInsurancePolicy implements InsurancePolicy {
    /** The unique policy number for the insurance policy. */
    private final String policyNumber;

    /** The policyholder associated with this insurance policy. */
    private final Patient policyHolder;

    /** The coverage details associated with this insurance policy. */
    private final Coverage coverage;

    /** The expiration date of the insurance policy. */
    private LocalDateTime expirationDate;

    /** The cancellation date of the insurance policy. */
    private LocalDateTime cancellationDate;

    /** The current status of the insurance policy. */
    private InsuranceStatus status;

    /**
     * Constructs a BaseInsurancePolicy using the provided builder.
     *
     * @param builder The builder containing the details for creating the insurance policy.
     */
    protected BaseInsurancePolicy(Builder builder) {
        this.policyNumber = builder.policyNumber;
        this.policyHolder = builder.policyHolder;
        this.coverage = builder.coverage;
        LocalDateTime activationDate = builder.activationDate;
        this.expirationDate = builder.expirationDate;
        this.cancellationDate = builder.cancellationDate;
        this.status = builder.status;
    }

    /**
     * Builder class for constructing a {@link BaseInsurancePolicy}.
     */
    public static class Builder {
        private String policyNumber;
        private Patient policyHolder;
        private Coverage coverage;
        private LocalDateTime activationDate;
        private LocalDateTime expirationDate;
        private LocalDateTime cancellationDate;
        private InsuranceStatus status = InsuranceStatus.ACTIVE; // Default status

        /**
         * Constructs a Builder with the required fields.
         *
         * @param policyNumber The policy number for the insurance policy.
         * @param policyHolder The policyholder for the insurance policy.
         * @param coverage The coverage details for the insurance policy.
         */
        public Builder(String policyNumber, Patient policyHolder, Coverage coverage) {
            this.policyNumber = Objects.requireNonNull(policyNumber);
            this.policyHolder = Objects.requireNonNull(policyHolder);
            this.coverage = Objects.requireNonNull(coverage);
        }

        /**
         * Sets the status of the insurance policy.
         *
         * @param status The status to be assigned to the policy.
         * @return This Builder instance for method chaining.
         */
        public Builder withStatus(InsuranceStatus status) {
            this.status = Objects.requireNonNull(status);
            return this;
        }

        /**
         * Sets the activation date of the insurance policy.
         *
         * @param date The activation date to be assigned.
         * @return This Builder instance for method chaining.
         */
        public Builder withActivationDate(LocalDateTime date) {
            this.activationDate = date;
            return this;
        }

        /**
         * Sets the expiration date of the insurance policy.
         *
         * @param date The expiration date to be assigned.
         * @return This Builder instance for method chaining.
         */
        public Builder withExpirationDate(LocalDateTime date) {
            this.expirationDate = date;
            return this;
        }

        /**
         * Sets the cancellation date of the insurance policy.
         *
         * @param date The cancellation date to be assigned.
         * @return This Builder instance for method chaining.
         */
        public Builder withCancellationDate(LocalDateTime date) {
            this.cancellationDate = date;
            return this;
        }

        /**
         * Builds and returns a {@link BaseInsurancePolicy} instance.
         *
         * @return The constructed BaseInsurancePolicy.
         * @throws IllegalStateException If required fields are missing.
         */
        public BaseInsurancePolicy build() {
            validate();
            return new BaseInsurancePolicy(this);
        }

        /**
         * Validates the required fields before building the insurance policy.
         *
         * @throws IllegalStateException If required fields are not provided.
         */
        private void validate() {
            if (activationDate == null) {
                throw new IllegalStateException("Activation date is required");
            }
            if (status == null) {
                throw new IllegalStateException("Insurance status is required");
            }
        }
    }

    /**
     * Retrieves the policy number of the insurance policy.
     *
     * @return The policy number.
     */
    @Override
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Retrieves the policyholder of the insurance policy.
     *
     * @return The policyholder.
     */
    @Override
    public Patient getPolicyHolder() {
        return policyHolder;
    }

    /**
     * Retrieves the coverage details of the insurance policy.
     *
     * @return The coverage associated with the policy.
     */
    @Override
    public Coverage getCoverage() {
        return coverage;
    }

    /**
     * Checks if the policy is active, which means the policy is not expired or canceled.
     *
     * @return {@code true} if the policy is active, {@code false} otherwise.
     */
    @Override
    public boolean isActive() {
        return status == InsuranceStatus.ACTIVE &&
                !isExpired(LocalDateTime.now()) &&
                !isCancelled();
    }

    /**
     * Checks if the insurance policy has expired.
     *
     * @param now The current date and time to compare against the expiration date.
     * @return {@code true} if the policy has expired, {@code false} otherwise.
     */
    @Override
    public boolean isExpired(LocalDateTime now) {
        if (status == InsuranceStatus.EXPIRED) {
            return true;
        }
        if (expirationDate != null && now.isAfter(expirationDate)) {
            status = InsuranceStatus.EXPIRED;
            return true;
        }
        return false;
    }

    /**
     * Checks if the insurance policy has been canceled.
     *
     * @return {@code true} if the policy is canceled, {@code false} otherwise.
     */
    @Override
    public boolean isCancelled() {
        return status == InsuranceStatus.CANCELLED || cancellationDate != null;
    }

    /**
     * Checks if the insurance policy is in a pending state.
     *
     * @return {@code true} if the policy is pending, {@code false} otherwise.
     */
    @Override
    public boolean isPending() {
        return status == InsuranceStatus.PENDING;
    }

    /**
     * Retrieves the coverage limit associated with this insurance policy.
     *
     * @return The {@link CoverageLimit} object containing the annual, lifetime,
     *         benefit-specific, ward-specific, and accident-specific limits.
     */
    public CoverageLimit getCoverageLimit() {
        return this.coverage.getLimits();
    }
}