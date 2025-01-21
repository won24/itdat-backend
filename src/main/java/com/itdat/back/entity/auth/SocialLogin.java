package com.itdat.back.entity.auth;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_logins")
public class SocialLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public SocialLogin() {
    }

    public SocialLogin(int id, User user, Provider provider, String providerId, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = createdAt;
    }

    public SocialLogin(User user, String provider, String providerId) {
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

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SocialLogin{" +
                "id=" + id +
                ", user=" + user +
                ", provider=" + provider +
                ", providerId='" + providerId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

enum Provider { KAKAO, GOOGLE, NAVER }
