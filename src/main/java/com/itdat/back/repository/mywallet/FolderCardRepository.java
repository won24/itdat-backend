package com.itdat.back.repository.mywallet;

import com.itdat.back.entity.mywallet.FolderCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderCardRepository extends JpaRepository<FolderCard, Integer> {
    List<FolderCard> findByFolderId(Integer folderId);


    @Query("SELECT fc FROM FolderCard fc JOIN MyWallet mw ON fc.cardId = mw.id WHERE mw.myEmail = :myEmail AND mw.userEmail = :userEmail")
    List<FolderCard> findByCardIdAndUserEmail(@Param("myEmail") String myEmail, @Param("userEmail") String userEmail);

    @Modifying
    @Query("DELETE FROM FolderCard fc WHERE fc.cardId IN (SELECT mw.id FROM MyWallet mw WHERE mw.userEmail = :userEmail)")
    void deleteByUserEmail(@Param("userEmail") String userEmail);

    boolean existsByFolderIdAndCardId(Integer folderId, Integer cardId);

    boolean existsByCardId(Integer id);
}