package com.itdat.back.entity.admin;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "under_management")
public class UnderManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uerId;

    @Column(name = "cumulative_count")
    private int cumulativeCount;

    @Column(name = "start_date_at")
    private Date startDateAt;

    @Column(name = "end_date_at")
    private Date endDateAt;

    @Column(name = "update_at")
    private Date updateAt;

    public UnderManagement() {
    }

    public UnderManagement(int id, User uerId, int cumulativeCount, Date startDateAt, Date endDateAt, Date updateAt, UserStatus status) {
        this.id = id;
        this.uerId = uerId;
        this.cumulativeCount = cumulativeCount;
        this.startDateAt = startDateAt;
        this.endDateAt = endDateAt;
        this.updateAt = updateAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUerId() {
        return uerId;
    }

    public void setUerId(User uerId) {
        this.uerId = uerId;
    }

    public int getCumulativeCount() {
        return cumulativeCount;
    }

    public void setCumulativeCount(int cumulativeCount) {
        this.cumulativeCount = cumulativeCount;
    }

    public Date getStartDateAt() {
        return startDateAt;
    }

    public void setStartDateAt(Date startDateAt) {
        this.startDateAt = startDateAt;
    }

    public Date getEndDateAt() {
        return endDateAt;
    }

    public void setEndDateAt(Date endDateAt) {
        this.endDateAt = endDateAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }



    @Override
    public String toString() {
        return "UnderManagement{" +
                "id=" + id +
                ", uerId=" + uerId +
                ", cumulativeCount=" + cumulativeCount +
                ", startDateAt=" + startDateAt +
                ", endDateAt=" + endDateAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
