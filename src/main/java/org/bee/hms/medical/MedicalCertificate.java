package org.bee.hms.medical;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a medical certificate. Medical certificates in Singapore are usually simple.
 * Consisting of startDate, endDate, id and any additional remarks from the doctor.
 */
public class MedicalCertificate{
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String remarks;

    /**
     * Simple medical certificate object that contains all the fields required to generate an MC
     * @param startDate start date of the MC
     * @param endDate end date of the MC
     * @param remarks any doctor's remarks for MC
     */
    public MedicalCertificate(LocalDateTime startDate, LocalDateTime endDate, String remarks){
        this.startDate = startDate;
        this.endDate = endDate;
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
