package policy;

import humans.Patient;
import wards.WardClassType;

import java.time.LocalDateTime;
import java.util.Objects;

public class BaseInsurancePolicy implements InsurancePolicy {
    private final String policyNumber;
    private final Patient policyHolder;
    private final Coverage coverage;
    private LocalDateTime expirationDate;
    private LocalDateTime cancellationDate;
    private InsuranceStatus status;

    protected BaseInsurancePolicy(Builder builder) {
        this.policyNumber = builder.policyNumber;
        this.policyHolder = builder.policyHolder;
        this.coverage = builder.coverage;
        LocalDateTime activationDate = builder.activationDate;
        this.expirationDate = builder.expirationDate;
        this.cancellationDate = builder.cancellationDate;
        this.status = builder.status;
    }


    public static class Builder {
        private String policyNumber;
        private Patient policyHolder;
        private Coverage coverage;
        private LocalDateTime activationDate;
        private LocalDateTime expirationDate;
        private LocalDateTime cancellationDate;
        private InsuranceStatus status = InsuranceStatus.ACTIVE; // Default status

        public Builder(String policyNumber, Patient policyHolder, Coverage coverage) {
            this.policyNumber = Objects.requireNonNull(policyNumber);
            this.policyHolder = Objects.requireNonNull(policyHolder);
            this.coverage = Objects.requireNonNull(coverage);
        }

        public Builder withStatus(InsuranceStatus status) {
            this.status = Objects.requireNonNull(status);
            return this;
        }

        public Builder withActivationDate(LocalDateTime date) {
            this.activationDate = date;
            return this;
        }

        public Builder withExpirationDate(LocalDateTime date) {
            this.expirationDate = date;
            return this;
        }

        public Builder withCancellationDate(LocalDateTime date) {
            this.cancellationDate = date;
            return this;
        }
        
        public BaseInsurancePolicy build() {
            validate();
            return new BaseInsurancePolicy(this);
        }

        private void validate() {
            if (activationDate == null) {
                throw new IllegalStateException("Activation date is required");
            }
            if (status == null) {
                throw new IllegalStateException("Insurance status is required");
            }
        }
    }

    @Override
    public String getPolicyNumber() {
        return policyNumber;
    }

    @Override
    public Patient getPolicyHolder() {
        return policyHolder;
    }

    @Override
    public Coverage getCoverage() {
        return coverage;
    }

    @Override
    public boolean isActive() {
        return status == InsuranceStatus.ACTIVE &&
                !isExpired(LocalDateTime.now()) &&
                !isCancelled();
    }


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

    @Override
    public boolean isCancelled() {
        return status == InsuranceStatus.CANCELLED || cancellationDate != null;
    }


    @Override
    public boolean isPending() {
        return status == InsuranceStatus.PENDING;
    }

    /**
     * Retrieves the coverage limit associated with this insurance policy.
     *
     * @return the {@link CoverageLimit} object containing the annual, lifetime,
     *         benefit-specific, ward-specific, and accident-specific limits. Used for limit calculations
     */
    public CoverageLimit getCoverageLimit() {
        return this.coverage.getLimits();
    }

}