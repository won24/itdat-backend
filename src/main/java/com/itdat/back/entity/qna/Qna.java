package com.itdat.back.entity.qna;

import com.itdat.back.entity.auth.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "contents")
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @Column(name = "create_date_at")
    private LocalDateTime createDateAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_secret")
    private boolean isSecret;

    @Column(name = "password")
    private String password;

    @Column(name = "category", columnDefinition = "ENUM('ETC', 'NFC', 'MERCHANDISE', 'APP', 'ACCOUNT')")
    @Enumerated(EnumType.STRING)
    private QnaCategory category;

    @Column(name = "is_answered")
    private boolean isAnswered = false;

    public Qna() {
    }

    public Qna(int id, String title, String contents, User user, LocalDateTime createDateAt, LocalDateTime updateAt, boolean isSecret, String password, QnaCategory category, boolean isAnswered) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.createDateAt = createDateAt;
        this.updateAt = updateAt;
        this.isSecret = isSecret;
        this.password = password;
        this.category = category;
        this.isAnswered = isAnswered;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public boolean isSecret() {
        return isSecret;
    }

    public void setSecret(boolean secret) {
        isSecret = secret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public QnaCategory getCategory() {
        return category;
    }

    public void setCategory(QnaCategory category) {
        this.category = category;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    @Override
    public String toString() {
        return "Qna{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", user=" + user +
                ", createDateAt=" + createDateAt +
                ", updateAt=" + updateAt +
                ", isSecret=" + isSecret +
                ", password='" + password + '\'' +
                ", category=" + category +
                ", isAnswered=" + isAnswered +
                '}';
    }
}
