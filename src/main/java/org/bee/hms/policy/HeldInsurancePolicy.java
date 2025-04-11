package org.bee.hms.policy;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.humans.Patient;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.utils.JSONSerializable;

/**
 * Represents an insurance policy that is actively held by a policyholder. This class extends {@link BaseInsurancePolicy}
 * and implements the {@link InsurancePolicy} interface to define the behavior of an insurance policy in effect.
 * <p>
 * It includes attributes such as policy number, policyholder details, expiration date, cancellation date, and the policy's status.
 * </p>
 */
public class HeldInsurancePolicy extends BaseInsurancePolicy implements InsurancePolicy, JSONSerializable {

    /** Unique policy ID */
    private final String policyNumber;

    /** Insured patient */
    private final Patient policyHolder;

    /** Policy name */
    private final String name;

    /** When coverage ends  */
    private final LocalDateTime expirationDate;

    /** When policy was cancelled  */
    private final LocalDateTime cancellationDate;

    /** Current policy status (ACTIVE/EXPIRED/CANCELLED) */
    private InsuranceStatus status;

    /**
     * Creates a HeldInsurancePolicy instance with the specified parameters.
     *
     * @param builder The builder used to create the insurance policy.
     */
    protected HeldInsurancePolicy(Builder builder) {
        super(builder.coverage, builder.provider);
        this.policyNumber = Objects.requireNonNull(builder.policyNumber);
        this.policyHolder = Objects.requireNonNull(builder.policyHolder);
        this.expirationDate = builder.expirationDate;
        this.name = builder.name;
        this.cancellationDate = builder.cancellationDate;
        this.status = Objects.requireNonNull(builder.status);
    }


    /**
     * Creates a HeldInsurancePolicy instance from JSON deserialization
     * <p>
     * This factory method constructs a policy using the builder pattern and handles:
     * <ul>
     * <li>Optional expiration and cancellation dates </li>
     * <li>Default status (ACTIVE) if status is null</li>
     * <li>All required policy fields</li>
     * </ul>
     *
     * @param policyNumber The unique policy identifier
     * @param policyHolder The patient who holds this policy
     * @param coverage The coverage details of the policy
     * @param provider The insurance provider
     * @param name The display name of the policy
     * @param expirationDate When the policy expires (optional)
     * @param cancellationDate When the policy was cancelled (optional)
     * @param status The current status of the policy (defaults to ACTIVE if null)
     * @return A fully constructed HeldInsurancePolicy
     */
    @JsonCreator
    public static HeldInsurancePolicy create(
            @JsonProperty("policyNumber") String policyNumber,
            @JsonProperty("policyHolder") Patient policyHolder,
            @JsonProperty("coverage") Coverage coverage,
            @JsonProperty("provider") InsuranceProvider provider,
            @JsonProperty("name") String name,
            @JsonProperty("expirationDate") LocalDateTime expirationDate,
            @JsonProperty("cancellationDate") LocalDateTime cancellationDate,
            @JsonProperty("status") InsuranceStatus status
    ) {
        Builder builder = new Builder(
                policyNumber,
                policyHolder,
                coverage,
                provider,
                name
        );

        if (expirationDate != null) {
            builder.withExpirationDate(expirationDate);
        }

        if (cancellationDate != null) {
            builder.withCancellationDate(cancellationDate);
        }

        builder.withStatus(Objects.requireNonNullElse(status, InsuranceStatus.ACTIVE));

        return builder.build();
    }

    /**
     * Returns the policy number of the held insurance policy.
     *
     * @return The policy number.
     */
    @Override
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Returns the policyholder associated with this insurance policy.
     *
     * @return The policyholder.
     */
    @Override
    public Patient getPolicyHolder() {
        return policyHolder;
    }

    /**
     * Returns the name of the insurance policy.
     *
     * @return The name of the policy.
     */
    @Override
    public String getPolicyName() {
        return name;
    }

    /**
     * Returns the insurance provider associated with this policy.
     *
     * @return The insurance provider.
     */
    @Override
    public InsuranceProvider getInsuranceProvider() {
        return this.provider;
    }

    /**
     * Determines if the insurance policy is currently active.
     * A policy is active if its status is {@link InsuranceStatus#ACTIVE}, it is not expired, and it is not cancelled.
     *
     * @return true if the policy is active, false otherwise.
     */
    @Override
    public boolean isActive() {
        return status == InsuranceStatus.ACTIVE &&
                !isExpired(LocalDateTime.now()) &&
                !isCancelled();
    }

    /**
     * Determines if the insurance policy is expired.
     *
     * @param now The current date and time.
     * @return true if the policy is expired, false otherwise.
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
     * Returns the policy type identifier for JSON serialization
     * Always returns "held" to indicate this is an active insurance policy instance
     *
     * @return Constant string "held" representing this policy type
     */

    @JsonProperty("policyType")
    public String getPolicyType() {
        return "held";
    }

    /**
     * Determines if the insurance policy is cancelled.
     *
     * @return true if the policy is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return status == InsuranceStatus.CANCELLED || cancellationDate != null;
    }

    /**
     * Determines if the insurance policy is pending activation.
     *
     * @return true if the policy is pending, false otherwise.
     */
    @Override
    public boolean isPending() {
        return status == InsuranceStatus.PENDING;
    }

    /**
     * Builder pattern to create a {@link HeldInsurancePolicy}.
     */
    public static class Builder {
        /** Unique policy ID */
        private final String policyNumber;

        /** Insured patient */
        private final Patient policyHolder;

        /** Insurance company */
        private final InsuranceProvider provider;

        /** Plan name */
        private final String name;

        /** Coverage rules */
        private final Coverage coverage;

        /** Expiration date */
        private LocalDateTime expirationDate;

        /** Cancellation date */
        private LocalDateTime cancellationDate;

        /** Current status, default is ACTIVE */
        private InsuranceStatus status = InsuranceStatus.ACTIVE;

        /**
         * Creates a builder for constructing a {@link HeldInsurancePolicy}.
         *
         * @param policyNumber The policy number.
         * @param policyHolder The policyholder associated with the policy.
         * @param coverage The coverage associated with the policy.
         * @param provider The insurance provider for the policy.
         * @param name The name of the insurance policy.
         */
        public Builder(String policyNumber, Patient policyHolder, Coverage coverage, InsuranceProvider provider, String name) {
            this.policyNumber = policyNumber;
            this.policyHolder = policyHolder;
            this.provider = provider;
            this.name = name;
            this.coverage = coverage;
        }

        /**
         * Sets the status of the insurance policy.
         *
         * @param status The status of the insurance policy.
         * @return The current builder instance.
         */
        public Builder withStatus(InsuranceStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the expiration date of the insurance policy.
         *
         * @param date The expiration date.
         * @return The current builder instance.
         */
        public Builder withExpirationDate(LocalDateTime date) {
            this.expirationDate = date;
            return this;
        }

        /**
         * Sets the cancellation date of the insurance policy.
         *
         * @param date The cancellation date.
         * @return The current builder instance.
         */
        public Builder withCancellationDate(LocalDateTime date) {
            this.cancellationDate = date;
            return this;
        }

        /**
         * Builds the {@link HeldInsurancePolicy} instance.
         *
         * @return The constructed insurance policy.
         */
        public HeldInsurancePolicy build() {
            validate();
            return new HeldInsurancePolicy(this);
        }

        /**
         * Validates the required fields before building the insurance policy.
         *
         * @throws IllegalStateException If any required field is missing.
         */
        private void validate() {
            if (status == null) {
                throw new IllegalStateException("Insurance status is required");
            }
        }
    }
}
