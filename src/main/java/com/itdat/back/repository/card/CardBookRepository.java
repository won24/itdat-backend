package com.itdat.back.repository.card;

import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.entity.nfc.NfcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardBookRepository extends JpaRepository<NfcEntity, Integer> {

    // 내 이메일(myEmail)을 제외한 명함 가져오기
    List<NfcEntity> findByMyEmailNot(String myEmail);
}
