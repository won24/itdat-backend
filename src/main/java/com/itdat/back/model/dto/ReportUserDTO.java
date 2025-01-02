package com.itdat.back.model.dto;

import java.util.Date;

public class ReportUserDTO {
    private String reportedUserId;
    private String description;
    private String userId;
    private Date reportDateAt;

    public ReportUserDTO() {
    }

    public ReportUserDTO(String reportedUserId, String description, String userId, Date reportDateAt) {
        this.reportedUserId = reportedUserId;
        this.description = description;
        this.userId = userId;
        this.reportDateAt = reportDateAt;
    }

    public String getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(String reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getReportDateAt() {
        return reportDateAt;
    }

    public void setReportDateAt(Date reportDateAt) {
        this.reportDateAt = reportDateAt;
    }

    @Override
    public String toString() {
        return "ReportUserDTO{" +
                "reportedUserId='" + reportedUserId + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", reportDateAt=" + reportDateAt +
                '}';
    }
}
