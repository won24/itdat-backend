package com.itdat.back.entity.mywallet;

public class FolderUpdateRequest {
    private String userEmail;
    private String oldFolderName;
    private String newFolderName;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getOldFolderName() {
        return oldFolderName;
    }

    public void setOldFolderName(String oldFolderName) {
        this.oldFolderName = oldFolderName;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }
}

