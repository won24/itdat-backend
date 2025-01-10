package com.itdat.back.entity.admin;

import com.itdat.back.entity.auth.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "report_user")
public class ReportUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reported_user_id") // 신고를 당한 유저
    private String reportedUserId;

    @Column(name = "description")
    private String description;

    @Column(name = "user_id") // 신고를 한 유저
    private String userId;

    @Column(name = "report_date_at")
    private Date reportDateAt;

    public ReportUser() {
    }

    public ReportUser(int id, String reportedUserId, String description, String userId, Date reportDateAt) {
        this.id = id;
        this.reportedUserId = reportedUserId;
        this.description = description;
        this.userId = userId;
        this.reportDateAt = reportDateAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "ReportUser{" +
                "id=" + id +
                ", reportedUserId='" + reportedUserId + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", reportDateAt=" + reportDateAt +
                '}';
    }
}
