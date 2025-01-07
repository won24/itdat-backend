package com.itdat.back.entity.nfc;

import jakarta.persistence.*;

@Entity
@Table(name = "my_wallet")
public class NfcEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "my_email", nullable = false)
    private String myEmail;

    @Column(name = "card_no", nullable = false)
    private int cardNo;

    @Column(name = "description")
    private String description;

    public NfcEntity() {}

    public NfcEntity(String userEmail, String myEmail, int cardNo, String description) {
        this.userEmail = userEmail;
        this.myEmail = myEmail;
        this.cardNo = cardNo;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getMyEmail() {
        return myEmail;
    }

    public void setMyEmail(String myEmail) {
        this.myEmail = myEmail;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}