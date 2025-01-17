package com.itdat.back.entity.mywallet;

import com.itdat.back.entity.card.BusinessCard;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "folder_name", nullable = false, length = 50)
    private String folderName;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<BusinessCard> businessCards;

    public Folder() {}

    public Folder(String userEmail, String folderName) {
        this.userEmail = userEmail;
        this.folderName = folderName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
