package org.bee.hms.medical;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.hms.billing.BillableItem;
import org.bee.hms.policy.BenefitType;
import org.bee.hms.policy.ClaimableItem;
import org.bee.hms.wards.*;
import org.bee.utils.JSONSerializable;

/**
 * Represents a patient's stay in a hospital ward, including the ward information and the duration
 * of the stay. This class is used to track ward-specific stays, including transfers to different wards.
 * It also calculates the charges for the stay and determines the type of benefit associated with the stay.
 */
public class WardStay implements ClaimableItem, BillableItem, JSONSerializable {

    /**
     * The hospital ward where the patient was admitted
     * Represents the physical location and specialized unit
     * where the patient received care during their stay
     */
    private final Ward ward;

    /**
     * The date and time when the ward stay began
     * Must be before {@code endDateTime} for completed stays
     */
    private final LocalDateTime startDateTime;

    /**
     * The date and time when the ward stay concluded
     * Recorded in the local timezone of the healthcare facility
     * For current admissions, this field remains null until discharge
     */
    private final LocalDateTime endDateTime;

    /**
     * Constructs a WardStay instance with the specified ward, start date/time, end date/time,
     * and accident status.
     *
     * @param ward The ward the patient is assigned to during their stay.
     * @param startDateTime The start date and time of the patient's stay in the ward.
     * @param endDateTime The end date and time of the patient's stay in the ward.
     */
    @JsonCreator
    public WardStay(
            @JsonProperty("ward") Ward ward,
            @JsonProperty("startDateTime") LocalDateTime startDateTime,
            @JsonProperty("endDateTime") LocalDateTime endDateTime) {
        this.ward = ward;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
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
     * This is an implementation of the ClaimableItem#getCharges method.
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

    /**
     * Calculates and returns the unsubsidized charges for the ward stay
     * @return The total unsubsidized charges as a {@link BigDecimal}
     */
    @Override
    public BigDecimal getUnsubsidisedCharges() {
        return calculateCharges();
    }

    /**
     * Generates a descriptive text for billing statements
     * @return Formatted billing description string
     */
    @Override
    public String getBillItemDescription() {
        return getBenefitDescription(true);
    }

    /**
     * Returns the category classification for this billing item
     * @return Constant string "WARD"
     */
    @Override
    public String getBillItemCategory() {
        return "WARD";
    }

    /**
     * Generates a standardized billing code for the ward stay
     * <p>The code format consists of three parts:</p>
     * <ul>
     *   <li>Ward type prefix (LBR, ICU, DSG, or GEN)</li>
     *   <li>Ward class identifier</li>
     *   <li>Duration in days</li>
     * </ul>
     *
     * @return Formatted billing code string
     */
    @Override
    public String getBillingItemCode() {
        String wardTypePart = switch (ward) {
            case LabourWard _d -> "LBR";
            case ICUWard _d -> "ICU";
            case DaySurgeryWard _d -> "DSG";
            case null, default -> "GEN";
        };

        String classTypePart = "";
        for (WardClassType type : WardClassType.values()) {
            assert ward != null;
            if (Math.abs(type.getDailyRate() - ward.getDailyRate()) < 0.001) {
                String[] nameParts = type.name().split("_CLASS_");
                if (nameParts.length > 1) {
                    classTypePart = nameParts[1];
                } else if (type == WardClassType.ICU) {
                    classTypePart = "ICU";
                } else {
                    // Handle day surgery specific codes
                    String[] parts = type.name().split("_");
                    if (parts.length > 2) {
                        classTypePart = parts[2];
                    }
                }
                break;
            }
        }

        // Format: WARD_TYPE-CLASS-DAYS
        return String.format("%s-%s-%d", wardTypePart, classTypePart, getDaysStayed());
    }
}
