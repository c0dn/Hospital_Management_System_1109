package org.bee.hms.medical;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.ClaimableItem;
import org.bee.hms.wards.DaySurgeryWard;
import org.bee.hms.wards.ICUWard;
import org.bee.hms.wards.LabourWard;
import org.bee.hms.wards.Ward;

/**
 * Represents a patient's stay in a hospital ward, including the ward information and the duration
 * of the stay. This class is used to track ward-specific stays, including transfers to different wards.
 * It also calculates the charges for the stay and determines the type of benefit associated with the stay.
 */
public class WardStay implements ClaimableItem {

    private final Ward ward;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final boolean isAccident;

    /**
     * Constructs a WardStay instance with the specified ward, start date/time, end date/time,
     * and accident status.
     *
     * @param ward The ward the patient is assigned to during their stay.
     * @param startDateTime The start date and time of the patient's stay in the ward.
     * @param endDateTime The end date and time of the patient's stay in the ward.
     * @param isAccident Indicates whether the stay is due to an accident.
     */
    public WardStay(Ward ward, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isAccident) {
        this.ward = ward;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isAccident = isAccident;
    }

    /**
     * Calculates the charges for the patient's stay in the ward based on the ward's daily rate
     * and the number of days stayed.
     *
     * @return A BigDecimal representing the total charges for the ward stay.
     */
    public BigDecimal calculateCharges() {
        long daysStayed = getDaysStayed();
        return BigDecimal.valueOf(ward.getDailyRate())
                .multiply(BigDecimal.valueOf(Math.max(1, daysStayed)));
    }

    /**
     * Returns the number of days the patient stayed in the ward.
     *
     * @return The number of days stayed as a long value.
     */
    private long getDaysStayed() {
        return ChronoUnit.DAYS.between(
                startDateTime.toLocalDate(),
                endDateTime.toLocalDate());
    }

    /**
     * Returns the charges for the patient's stay.
     * This is an implementation of the {@link ClaimableItem#getCharges} method.
     *
     * @return A BigDecimal representing the total charges for the ward stay.
     */
    @Override
    public BigDecimal getCharges() {
        return this.calculateCharges();
    }

    /**
     * Resolves the appropriate benefit type based on the ward type and whether the patient is an inpatient.
     *
     * @param isInpatient A boolean value indicating whether the patient is an inpatient.
     * @return A {@link BenefitType} representing the type of benefit for the ward stay.
     */
    @Override
    public BenefitType resolveBenefitType(boolean isInpatient) {
        return switch (ward) {
            case LabourWard _d -> BenefitType.MATERNITY;
            case ICUWard _d -> BenefitType.HOSPITALIZATION;
            case DaySurgeryWard _d -> isInpatient ? BenefitType.SURGERY : BenefitType.OUTPATIENT_TREATMENTS;
            case null, default -> isInpatient ? BenefitType.HOSPITALIZATION : BenefitType.OUTPATIENT_TREATMENTS;
        };
    }

    /**
     * Returns a description of the benefit for the ward stay, including the ward name and the number of
     * days stayed.
     *
     * @param isInpatient A boolean value indicating whether the patient is an inpatient.
     * @return A string representing a description of the ward stay benefit.
     */
    @Override
    public String getBenefitDescription(boolean isInpatient) {
        return String.format("%s Ward Stay (%d days)",
                ward.getWardName(),
                getDaysStayed()
        );
    }
}
