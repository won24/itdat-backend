package com.itdat.back.entity.card;

public class BusinessCardDTO {
    private String userName;
    private String phone;
    private String email;
    private String companyName;
    private String companyAddress;
    private String companyFax;
    private String department;
    private String position;
    private String appTemplate;
    private String logoUrl;
    private String cardSide;

    public BusinessCardDTO(BusinessCard businessCard) {
        this.userName = businessCard.getUserName();
        this.phone = businessCard.getPhone();
        this.email = businessCard.getEmail();
        this.companyName = businessCard.getCompanyName();
        this.companyAddress = businessCard.getCompanyAddress();
        this.companyFax = businessCard.getCompanyFax();
        this.department = businessCard.getDepartment();
        this.position = businessCard.getPosition();
        this.appTemplate = businessCard.getAppTemplate();
        this.logoUrl = businessCard.getLogoUrl();
        this.cardSide = businessCard.getCardSide().toString();
    }

    // Getters and Setters
}
