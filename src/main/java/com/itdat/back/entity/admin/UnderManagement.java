package com.itdat.back.entity.admin;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "under_management")
public class UnderManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id"/*, referencedColumnName = "userId" // 컬럼명이 아닌 엔티티의 필드명을 써야 한다. */)
    private User user;

    @Column(name = "cumulative_count"/*, nullable = false*/)
    private Integer cumulativeCount = 0;

    @Column(name = "start_date_at")
    private LocalDateTime startDateAt;

    @Column(name = "end_date_at")
    private LocalDateTime endDateAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "reported_count"/*, nullable = false*/)
    private Integer reportedCount = 0;

    @Column(name = "banned_count"/*, nullable = false*/)
    private Integer bannedCount = 0;

    @Column(name = "demerit"/*, nullable = false*/)
    private Integer demerit = 0;

    @Column(name = "last_reported_date_at")
    private LocalDateTime lastReportedDateAt;

    public UnderManagement() {
    }

    public UnderManagement(int id, User user, Integer cumulativeCount, LocalDateTime startDateAt, LocalDateTime endDateAt, LocalDateTime updateAt, Integer reportedCount, Integer bannedCount, Integer demerit, LocalDateTime lastReportedDateAt) {
        this.id = id;
        this.user = user;
        this.cumulativeCount = cumulativeCount;
        this.startDateAt = startDateAt;
        this.endDateAt = endDateAt;
        this.updateAt = updateAt;
        this.reportedCount = reportedCount;
        this.bannedCount = bannedCount;
        this.demerit = demerit;
        this.lastReportedDateAt = lastReportedDateAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCumulativeCount() {
        return cumulativeCount;
    }

    public void setCumulativeCount(Integer cumulativeCount) {
        this.cumulativeCount = cumulativeCount;
    }

    public LocalDateTime getStartDateAt() {
        return startDateAt;
    }

    public void setStartDateAt(LocalDateTime startDateAt) {
        this.startDateAt = startDateAt;
    }

    public LocalDateTime getEndDateAt() {
        return endDateAt;
    }

    public void setEndDateAt(LocalDateTime endDateAt) {
        this.endDateAt = endDateAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getReportedCount() {
        return reportedCount;
    }

    public void setReportedCount(Integer reportedCount) {
        this.reportedCount = reportedCount;
    }

    public Integer getBannedCount() {
        return bannedCount;
    }

    public void setBannedCount(Integer bannedCount) {
        this.bannedCount = bannedCount;
    }

    public Integer getDemerit() {
        return demerit;
    }

    public void setDemerit(Integer demerit) {
        this.demerit = demerit;
    }

    public LocalDateTime getLastReportedDateAt() {
        return lastReportedDateAt;
    }

    public void setLastReportedDateAt(LocalDateTime lastReportedDateAt) {
        this.lastReportedDateAt = lastReportedDateAt;
    }

    @Override
    public String toString() {
        return "UnderManagement{" +
                "id=" + id +
                ", user=" + user +
                ", cumulativeCount=" + cumulativeCount +
                ", startDateAt=" + startDateAt +
                ", endDateAt=" + endDateAt +
                ", updateAt=" + updateAt +
                ", reportedCount=" + reportedCount +
                ", bannedCount=" + bannedCount +
                ", demerit=" + demerit +
                ", lastReportedDateAt=" + lastReportedDateAt +
                '}';
    }
}
