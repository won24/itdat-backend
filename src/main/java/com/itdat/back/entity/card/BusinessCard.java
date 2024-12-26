package com.itdat.back.entity.card;

import com.itdat.back.entity.auth.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "business_card")
public class BusinessCard {

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false, name = "user_name")
    private String userName;

    @Column(nullable = false, name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_number")
    private String companyNumber;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "company_fax")
    private String companyFax;

    @Column(name = "department")
    private String department;

    @Column(name = "position")
    private String position;

    @Column(name = "logo_url")
    private String logoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false, name = "created_at")
    private Date createdAt = new Date();

    public int getCardId() {
        return cardId;
    }

    public User getUser() {
        return user;
    }

    public Template getTemplate() {
        return template;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public BusinessCard() {
    }

    public BusinessCard(int cardId, User user, Template template, String userName, String phone, String email, String companyName, String companyNumber, String companyAddress, String companyFax, String department, String position, String logoUrl, Date createdAt) {
        this.cardId = cardId;
        this.user = user;
        this.template = template;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.companyName = companyName;
        this.companyNumber = companyNumber;
        this.companyAddress = companyAddress;
        this.companyFax = companyFax;
        this.department = department;
        this.position = position;
        this.logoUrl = logoUrl;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BusinessCard{" +
                "cardId=" + cardId +
                ", user=" + user +
                ", template=" + template +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyNumber='" + companyNumber + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", companyFax='" + companyFax + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
