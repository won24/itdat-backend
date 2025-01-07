package com.itdat.back.repository.card;

import com.itdat.back.entity.nfc.MyWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardBookRepository extends JpaRepository<MyWallet, Integer> {

    // 내 이메일(myEmail)을 제외한 명함 가져오기
    List<MyWallet> findByMyEmailNot(String myEmail);
}
