package com.itdat.back.entity.qna;

import com.itdat.back.entity.auth.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna_answer")
public class QnaAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "qna_id", nullable = false)
    private Qna qna;

    @Column(name = "contents", nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "userId", nullable = false)
    private User user;

    @Column(name = "create_date_at", nullable = false)
    private LocalDateTime createDateAt;

    public QnaAnswer() {
    }

    public QnaAnswer(int id, Qna qna, String contents, User user, LocalDateTime createDateAt) {
        this.id = id;
        this.qna = qna;
        this.contents = contents;
        this.user = user;
        this.createDateAt = createDateAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Qna getQna() {
        return qna;
    }

    public void setQna(Qna qna) {
        this.qna = qna;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreateDateAt() {
        return createDateAt;
    }

    public void setCreateDateAt(LocalDateTime createDateAt) {
        this.createDateAt = createDateAt;
    }

    @Override
    public String toString() {
        return "QnaAnswer{" +
                "id=" + id +
                ", qna=" + qna +
                ", contents='" + contents + '\'' +
                ", user=" + user +
                ", createDateAt=" + createDateAt +
                '}';
    }
}
