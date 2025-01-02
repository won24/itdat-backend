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
    private LocalDateTime create_date_at;

    @Column(name = "update_at")
    private LocalDateTime update_at;

    @Column(name = "is_secret")
    private boolean is_secret;

    @Column(name = "password")
    private String password;

    public Qna() {
    }

    public Qna(int id, String title, String contents, User user, LocalDateTime create_date_at, LocalDateTime update_at, boolean is_secret, String password) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.create_date_at = create_date_at;
        this.update_at = update_at;
        this.is_secret = is_secret;
        this.password = password;
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

    public LocalDateTime getCreate_date_at() {
        return create_date_at;
    }

    public void setCreate_date_at(LocalDateTime create_date_at) {
        this.create_date_at = create_date_at;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }

    public boolean isIs_secret() {
        return is_secret;
    }

    public void setIs_secret(boolean is_secret) {
        this.is_secret = is_secret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Qna{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", user=" + user +
                ", create_date_at=" + create_date_at +
                ", update_at=" + update_at +
                ", is_secret=" + is_secret +
                ", password='" + password + '\'' +
                '}';
    }
}
