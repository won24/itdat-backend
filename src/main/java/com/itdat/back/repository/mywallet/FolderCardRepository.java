package com.itdat.back.repository.mywallet;

import com.itdat.back.entity.mywallet.FolderCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderCardRepository extends JpaRepository<FolderCard, Integer> {
    List<FolderCard> findByFolderId(Integer folderId);
}