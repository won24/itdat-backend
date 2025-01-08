package com.itdat.back.entity.mywallet;

public class CardInfo {
    private String userName;
    private String companyName;
    private String userEmail;
    private int cardNo;

    public CardInfo(String userName, String companyName, String userEmail, int cardNo) {
        this.userName = userName;
        this.companyName = companyName;
        this.userEmail = userEmail;
        this.cardNo = cardNo;
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
}
