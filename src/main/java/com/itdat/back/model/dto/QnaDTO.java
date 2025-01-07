package com.itdat.back.model.dto;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.qna.QnaCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class QnaDTO {
    private String title;
    private String contents;
    private String loginedUserId;
    private LocalDateTime createDateAt;
    private LocalDateTime updateAt;
    private boolean isSecret;
    private String password;
    private QnaCategory category;

    public QnaDTO() {
    }

    public QnaDTO(String title, String contents, String loginedUserId, LocalDateTime createDateAt, LocalDateTime updateAt, boolean isSecret, String password, QnaCategory category) {
        this.title = title;
        this.contents = contents;
        this.loginedUserId = loginedUserId;
        this.createDateAt = createDateAt;
        this.updateAt = updateAt;
        this.isSecret = isSecret;
        this.password = password;
        this.category = category;
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

    public String getLoginedUserId() {
        return loginedUserId;
    }

    public void setLoginedUserId(String loginedUserId) {
        this.loginedUserId = loginedUserId;
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

    @Override
    public String toString() {
        return "QnaDTO{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", loginedUserId='" + loginedUserId + '\'' +
                ", createDateAt=" + createDateAt +
                ", updateAt=" + updateAt +
                ", isSecret=" + isSecret +
                ", password='" + password + '\'' +
                ", category=" + category +
                '}';
    }
}
