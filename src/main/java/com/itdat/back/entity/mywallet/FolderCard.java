package com.itdat.back.entity.mywallet;

import jakarta.persistence.*;

@Entity
@Table(name = "folder_cards")
public class FolderCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "folder_id", nullable = false)
    private Integer folderId;

    @Column(name = "card_id", nullable = false)
    private Integer cardId;

    public FolderCard() {}

    public FolderCard(Integer folderId, Integer cardId) {
        this.folderId = folderId;
        this.cardId = cardId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }
}
