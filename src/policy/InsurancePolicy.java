package policy;

import humans.Patient;

import java.time.LocalDateTime;

public interface InsurancePolicy {
    String getPolicyNumber();
    Patient getPolicyHolder();
    String getPolicyName();
    Coverage getCoverage();
    boolean isActive();
    boolean isExpired(LocalDateTime now);
    boolean isCancelled();
    boolean isPending();
}
