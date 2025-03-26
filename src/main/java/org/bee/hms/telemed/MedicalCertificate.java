package org.bee.hms.telemed;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.utils.JSONSerializable;

/**
 * Represents a medical certificate. Medical certificates in Singapore are usually simple.
 * Consisting of startDate, endDate, id and any additional remarks from the doctor.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MedicalCertificate implements JSONSerializable {
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String remarks;

    /**
     * Simple medical certificate object that contains all the fields required to generate an MC
     *
     * @param startDate start date of the MC (must not be null)
     * @param endDate end date of the MC (must not be null)
     * @param remarks any doctor's remarks for MC (can be null)
     * @throws NullPointerException if startDate or endDate is null
     */
    @JsonCreator
    public MedicalCertificate(
            @JsonProperty("startDate") LocalDateTime startDate,
            @JsonProperty("endDate") LocalDateTime endDate,
            @JsonProperty("remarks") String remarks) {
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        this.remarks = remarks;
        this.id = UUID.randomUUID().toString();
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getId() {
        return id;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
