package com.itdat.back.entity.card;


import com.itdat.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "business_card")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BusinessCard {

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cardId;

    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
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

}
