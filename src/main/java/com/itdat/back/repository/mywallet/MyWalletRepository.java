package com.itdat.back.repository.mywallet;

import com.itdat.back.entity.mywallet.CardInfo;
import com.itdat.back.entity.nfc.MyWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyWalletRepository extends JpaRepository<MyWallet, Integer> {
    List<MyWallet> findByMyEmail(String myEmail);

    @Query("SELECT new com.itdat.back.entity.mywallet.CardInfo(b.userName, b.companyName, b.userEmail, m.cardNo) " +
            "FROM MyWallet m JOIN BusinessCard b " +
            "ON m.userEmail = b.userEmail AND m.cardNo = b.cardNo " +
            "WHERE m.myEmail = :myEmail")
    List<CardInfo> findAllCardsByUserEmail(@Param("myEmail") String myEmail);

}
