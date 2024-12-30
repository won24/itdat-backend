package com.itdat.back.entity.card;

import jakarta.persistence.*;


@Entity
@Table(name = "business_card")
public class BusinessCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cardId;

    @Column(name = "user_id", nullable = false)
    private String userId;

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

    public BusinessCard() {
    }

    public BusinessCard(Integer cardId, String userId, String userName, String phone, String email, String companyName, String companyNumber, String companyAddress, String companyFax, String department, String position, String appTemplate, String webTemplate) {
        this.cardId = cardId;
        this.userId = userId;
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
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
