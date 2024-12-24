package com.itdat.back.entity.card;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_card")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Card {

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name = "user_id")
    private String userId;

    @Column(nullable = false, name = "template_id")
    private String templateId;

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


    public Card(Card cardDTO, Template template) {
    }
}
