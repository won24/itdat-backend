package com.itdat.back.entity.mywallet;

import jakarta.persistence.*;

@Entity
@Table(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "folder_name", nullable = false)
    private String folderName;

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
