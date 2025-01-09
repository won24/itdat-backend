package com.itdat.back.repository.mywallet;

import com.itdat.back.entity.mywallet.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    Optional<Folder> findByUserEmailAndFolderName(String userEmail, String folderName);

    List<Folder> findByUserEmail(String userEmail);

    Optional<Object> findByFolderName(String folderName);

    @Modifying
    @Query("UPDATE BusinessCard c SET c.folder = (SELECT f FROM Folder f WHERE f.id = :folderId) WHERE c.userEmail = :userEmail")
    void moveCardToFolder(@Param("userEmail") String userEmail, @Param("folderId") Integer folderId);

    @Modifying
    @Query("UPDATE BusinessCard c SET c.folder = NULL WHERE c.userEmail = :userEmail")
    void moveCardToAllCards(@Param("userEmail") String userEmail);

    @Modifying
    @Query("UPDATE BusinessCard c SET c.folder.id = :id WHERE c.userEmail = :userEmail")
    int updateFolderId(@Param("userEmail") String userEmail, @Param("id") Integer id);
}