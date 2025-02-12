package policy;

import humans.Patient;

import java.time.LocalDateTime;
import java.util.Objects;

public class HeldInsurancePolicy extends BaseInsurancePolicy implements InsurancePolicy {

    private final String policyNumber;
    private final Patient policyHolder;
    private LocalDateTime expirationDate;
    private LocalDateTime cancellationDate;
    private InsuranceStatus status;

    protected HeldInsurancePolicy(Builder builder) {
        super(builder.coverage);
        this.policyNumber = Objects.requireNonNull(builder.policyNumber);
        this.policyHolder = Objects.requireNonNull(builder.policyHolder);
        this.expirationDate = builder.expirationDate;
        this.cancellationDate = builder.cancellationDate;
        this.status = Objects.requireNonNull(builder.status);
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

    public static class Builder {
        private String policyNumber;
        private Patient policyHolder;
        private Coverage coverage;
        private LocalDateTime expirationDate;
        private LocalDateTime cancellationDate;
        private InsuranceStatus status = InsuranceStatus.ACTIVE;

        public Builder(String policyNumber, Patient policyHolder, Coverage coverage) {
            this.policyNumber = policyNumber;
            this.policyHolder = policyHolder;
            this.coverage = coverage;
        }

        public Builder withStatus(InsuranceStatus status) {
            this.status = status;
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

        public HeldInsurancePolicy build() {
            validate();
            return new HeldInsurancePolicy(this);
        }

        private void validate() {
            if (status == null) {
                throw new IllegalStateException("Insurance status is required");
            }
        }
    }
}