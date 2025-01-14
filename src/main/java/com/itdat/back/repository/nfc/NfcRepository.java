package com.itdat.back.repository.nfc;

import com.itdat.back.entity.nfc.MyWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NfcRepository extends JpaRepository<MyWallet, Integer> {

    MyWallet findByUserEmailAndCardNoAndMyEmail(String userEmail, int cardNo, String myEmail);
}