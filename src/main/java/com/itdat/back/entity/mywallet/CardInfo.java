package com.itdat.back.entity.mywallet;

import com.itdat.back.entity.card.BusinessCard;

public class CardInfo {
    private String userName;
    private String companyName;
    private String userEmail;
    private int cardNo;
    private String phone;
    private String email;
    private String companyNumber;
    private String companyAddress;
    private String companyFax;
    private String department;
    private String position;
    private String appTemplate;
    private String cardSide;
    private String logoUrl;
    private boolean isPublic;

    public CardInfo(BusinessCard businessCard, int cardNo) {
        this.userName = businessCard.getUserName();
        this.companyName = businessCard.getCompanyName();
        this.userEmail = businessCard.getUserEmail();
        this.cardNo = cardNo;
        this.phone = businessCard.getPhone();
        this.email = businessCard.getEmail();
        this.companyNumber = businessCard.getCompanyNumber();
        this.companyAddress = businessCard.getCompanyAddress();
        this.companyFax = businessCard.getCompanyFax();
        this.department = businessCard.getDepartment();
        this.position = businessCard.getPosition();
        this.appTemplate = businessCard.getAppTemplate();
        this.cardSide = businessCard.getCardSide() != null ? businessCard.getCardSide().toString() : null;
        this.logoUrl = businessCard.getLogoUrl();
        this.isPublic = businessCard.isPublic();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getCardSide() {
        return cardSide;
    }

    public void setCardSide(String cardSide) {
        this.cardSide = cardSide;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}


