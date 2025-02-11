package medical;

import policy.BenefitType;
import policy.ClaimableItem;
import wards.DaySurgeryWard;
import wards.ICUWard;
import wards.LabourWard;
import wards.Ward;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; 

/**
 * Represents a patient's stay in a hospital ward, including the ward information and the duration
 * of the stay. This class is used to track ward-specific stays, including transfers to different wards.
 */
public class WardStay implements ClaimableItem {
    private final Ward ward;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final boolean isAccident;

    public WardStay(Ward ward, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isAccident) {
        this.ward = ward;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isAccident = isAccident;
    }
    

    public BigDecimal calculateCharges() {
        long daysStayed = getDaysStayed();
        return BigDecimal.valueOf(ward.getDailyRate())
                .multiply(BigDecimal.valueOf(Math.max(1, daysStayed)));
    }

    private long getDaysStayed() {
        return ChronoUnit.DAYS.between(
                startDateTime.toLocalDate(),
                endDateTime.toLocalDate());
    }

    @Override
    public BigDecimal getCharges() {
        return this.calculateCharges();
    }

    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        return switch (ward) {
            case LabourWard _ -> BenefitType.MATERNITY;
            case ICUWard _ -> BenefitType.HOSPITALIZATION;
            case DaySurgeryWard _ -> isInpatient ? BenefitType.SURGERY : BenefitType.OUTPATIENT_TREATMENTS;
            case null, default -> isInpatient ? BenefitType.HOSPITALIZATION : BenefitType.OUTPATIENT_TREATMENTS;
        };
    }


    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return String.format("%s Ward Stay (%d days)",
                ward.getWardName(),
                getDaysStayed()
        );

    }
}
