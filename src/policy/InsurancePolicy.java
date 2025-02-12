package policy;

import humans.Patient;
import insurance.InsuranceProvider;

import java.time.LocalDateTime;

public interface InsurancePolicy {
    String getPolicyNumber();
    Patient getPolicyHolder();
    String getPolicyName();
    InsuranceProvider getInsuranceProvider();
    Coverage getCoverage();
    boolean isActive();
    boolean isExpired(LocalDateTime now);
    boolean isCancelled();
    boolean isPending();
}
