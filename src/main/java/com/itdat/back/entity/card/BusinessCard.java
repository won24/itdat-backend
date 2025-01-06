package com.itdat.back.entity.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "business_card")
public class BusinessCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cardId;


    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "card_no", nullable = false)
    private int cardNo;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "phone", nullable = false)
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

    @Column(name = "app_template")
    private String appTemplate;

    @Column(name = "web_template")
    private String webTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_side")
    @JsonProperty("cardSide")
    private CardSide cardSide;

    @Column(name = "logo_path")
    private String logoPath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public BusinessCard() {
    }

    public BusinessCard(Integer cardId, String userEmail, int cardNo, String userName, String phone, String email, String companyName, String companyNumber, String companyAddress, String companyFax, String department, String position, String appTemplate, String webTemplate, CardSide cardSide, String logoPath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cardId = cardId;
        this.userEmail = userEmail;
        this.cardNo = cardNo;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.companyName = companyName;
        this.companyNumber = companyNumber;
        this.companyAddress = companyAddress;
        this.companyFax = companyFax;
        this.department = department;
        this.position = position;
        this.appTemplate = appTemplate;
        this.webTemplate = webTemplate;
        this.cardSide = cardSide;
        this.logoPath = logoPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getCardNo() {
        return cardNo;
    }

    public void setCardNo(int cardNo) {
        this.cardNo = cardNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAppTemplate() {
        return appTemplate;
    }

    public void setAppTemplate(String appTemplate) {
        this.appTemplate = appTemplate;
    }

    public String getWebTemplate() {
        return webTemplate;
    }

    public void setWebTemplate(String webTemplate) {
        this.webTemplate = webTemplate;
    }

    public CardSide getCardSide() {
        return cardSide;
    }

    public void setCardSide(CardSide cardSide) {
        this.cardSide = cardSide;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}